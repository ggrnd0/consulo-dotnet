/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mustbe.consulo.dotnet.documentation;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.*;
import org.mustbe.consulo.dotnet.resolve.DotNetArrayTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetNativeTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetPointerTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiFacade;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.ArrayUtil;
import com.intellij.util.Function;
import lombok.val;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public class DotNetDocumentationProvider implements DocumentationProvider
{
	private static final String TYPE_PREFIX = "type::";

	@Nullable
	@Override
	public String getQuickNavigateInfo(PsiElement element, PsiElement element2)
	{
		return null;
	}

	@Nullable
	@Override
	public List<String> getUrlFor(PsiElement element, PsiElement element2)
	{
		return null;
	}

	@Nullable
	@Override
	public String generateDoc(PsiElement element, @Nullable PsiElement element2)
	{
		PsiFile containingFile = element.getContainingFile();
		if(containingFile == null)
		{
			return null;
		}
		VirtualFile virtualFile = containingFile.getVirtualFile();
		if(virtualFile == null)
		{
			return null;
		}
		List<OrderEntry> orderEntriesForFile = ProjectRootManager.getInstance(element.getProject()).getFileIndex().getOrderEntriesForFile
				(virtualFile);

		String docQName = getDocName(element);

		PsiManager manager = PsiManager.getInstance(element.getProject());
		for(OrderEntry orderEntry : orderEntriesForFile)
		{
			for(VirtualFile docVirtualFile : orderEntry.getFiles(OrderRootType.DOCUMENTATION))
			{
				if(docVirtualFile.getFileType() != XmlFileType.INSTANCE)
				{
					continue;
				}

				PsiFile psiFile = manager.findFile(docVirtualFile);
				if(psiFile instanceof XmlFile)
				{
					XmlTag rootTag = ((XmlFile) psiFile).getRootTag();
					if(rootTag == null)
					{
						continue;
					}

					XmlTag membersTag = rootTag.findFirstSubTag("members");

					if(membersTag != null)
					{
						XmlTag[] members = membersTag.findSubTags("member");
						for(XmlTag member : members)
						{
							String name = member.getAttributeValue("name");
							if(Comparing.equal(docQName, name))
							{
								return generate(member, (DotNetQualifiedElement) element);
							}
						}
					}
				}
			}
		}
		return null;
	}

	private String generate(XmlTag xmlTag, DotNetQualifiedElement psiElement)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><body>");

		String presentableParentQName = psiElement.getPresentableParentQName();
		if(!StringUtil.isEmpty(presentableParentQName))
		{
			builder.append("<b><small>").append(presentableParentQName).append("</small></b>");
			builder.append("<br>");
		}

		builder.append("<code>");
		if(psiElement instanceof DotNetModifierListOwner)
		{
			appendModifiers((DotNetModifierListOwner) psiElement, builder);
		}

		if(psiElement instanceof DotNetTypeDeclaration)
		{
			DotNetTypeDeclaration typeDeclaration = (DotNetTypeDeclaration) psiElement;
			if(typeDeclaration.isInterface())
			{
				builder.append("interface");
			}
			else if(typeDeclaration.isEnum())
			{
				builder.append("enum");
			}
			else if(typeDeclaration.isStruct())
			{
				builder.append("struct");
			}
			else
			{
				builder.append("class");
			}
			builder.append(" ");
		}
		else if(psiElement instanceof DotNetPropertyDeclaration)
		{
			builder.append(generateLinksForType(((DotNetPropertyDeclaration) psiElement).toTypeRef(), psiElement)).append(" ");
		}
		else if(psiElement instanceof DotNetLikeMethodDeclaration)
		{
			builder.append(generateLinksForType(((DotNetLikeMethodDeclaration) psiElement).getReturnTypeRef(), psiElement)).append(" ");
		}

		builder.append(psiElement.getName());

		if(psiElement instanceof DotNetLikeMethodDeclaration)
		{
			builder.append("(");
			builder.append(StringUtil.join(((DotNetLikeMethodDeclaration) psiElement).getParameters(), new Function<DotNetParameter, String>()
			{
				@Override
				public String fun(DotNetParameter dotNetParameter)
				{
					return generateLinksForType(dotNetParameter.toTypeRef(), dotNetParameter) + " " + dotNetParameter.getName();
				}
			}, ", "));
			builder.append(")");
		}

		builder.append("</code><br><br>");

		XmlTag summaryElement = xmlTag.findFirstSubTag("summary");
		if(summaryElement != null)
		{
			builder.append("<b><big>").append("Summary").append("</big></b><br>");
			builder.append(xmlTag.getSubTagText("summary"));
		}
		builder.append("</body></html>");
		return builder.toString();
	}

	private void appendModifiers(DotNetModifierListOwner owner, StringBuilder builder)
	{
		DotNetModifierList modifierList = owner.getModifierList();
		if(modifierList == null)
		{
			return;
		}

		for(DotNetModifierWithMask modifier : modifierList.getModifiers())
		{
			builder.append(modifier.name().toLowerCase()).append(" ");
		}
	}

	private String getDocName(PsiElement element)
	{
		String docQName = getDocName0(element);
		if(element instanceof DotNetPropertyDeclaration)
		{
			return "P:" + docQName;
		}
		else if(element instanceof DotNetEventDeclaration)
		{
			return "E:" + docQName;
		}
		else if(element instanceof DotNetTypeDeclaration)
		{
			return "T:" + docQName;
		}
		else if(element instanceof DotNetFieldDeclaration)
		{
			return "F:" + docQName;
		}
		else if(element instanceof DotNetLikeMethodDeclaration)
		{
			return "M:" + docQName;
		}
		return docQName;
	}

	private String getDocName0(PsiElement element)
	{
		if(element instanceof DotNetPropertyDeclaration ||
				element instanceof DotNetEventDeclaration ||
				element instanceof DotNetFieldDeclaration)
		{
			return getQNameForDoc(element, ((DotNetNamedElement) element).getName());
		}
		else if(element instanceof DotNetTypeDeclaration)
		{
			String name = ((DotNetTypeDeclaration) element).getName();
			int genericParametersCount = ((DotNetTypeDeclaration) element).getGenericParametersCount();
			if(genericParametersCount > 0)
			{
				name = name + '`' + genericParametersCount;
			}

			String presentableParentQName = ((DotNetTypeDeclaration) element).getPresentableParentQName();
			if(StringUtil.isEmpty(presentableParentQName))
			{
				return name;
			}
			else
			{
				return presentableParentQName + "." + name;
			}
		}
		else if(element instanceof DotNetLikeMethodDeclaration)
		{
			String name = ((DotNetLikeMethodDeclaration) element).getName();
			int genericParametersCount = ((DotNetLikeMethodDeclaration) element).getGenericParametersCount();
			if(genericParametersCount > 0)
			{
				name = name + '`' + genericParametersCount;
			}

			String fullName = getQNameForDoc(element, name);

			DotNetParameter[] parameters = ((DotNetLikeMethodDeclaration) element).getParameters();
			if(parameters.length > 0)
			{
				fullName = fullName + "(";
				fullName = fullName + StringUtil.join(parameters, new Function<DotNetParameter, String>()
				{
					@Override
					public String fun(DotNetParameter dotNetParameter)
					{
						DotNetTypeRef dotNetTypeRef = dotNetParameter.toTypeRef();

						return typeToDocName(dotNetParameter, dotNetTypeRef);
					}
				}, ",");
				fullName = fullName + ")";
			}
			return fullName;
		}
		else
		{
			return "test";
		}
	}

	private String getQNameForDoc(PsiElement element, String name)
	{
		String fullName;
		PsiElement parent = element.getParent();
		if(parent instanceof DotNetTypeDeclaration)
		{
			fullName = getDocName0(parent) + "." + name;
		}
		else
		{
			String presentableParentQName = ((DotNetQualifiedElement) element).getPresentableParentQName();
			if(StringUtil.isEmpty(presentableParentQName))
			{
				fullName = name;
			}
			else
			{
				fullName = presentableParentQName + "." + name;
			}
		}
		return fullName;
	}

	private String typeToDocName(PsiElement element, DotNetTypeRef typeRef)
	{
		if(typeRef instanceof DotNetArrayTypeRef)
		{
			return typeToDocName(element, ((DotNetArrayTypeRef) typeRef).getInnerType()) + "[]";
		}
		else if(typeRef instanceof DotNetPointerTypeRef)
		{
			return typeToDocName(element, ((DotNetPointerTypeRef) typeRef).getInnerType()) + "*";
		}
		else if(typeRef instanceof DotNetNativeTypeRef)
		{
			return ((DotNetNativeTypeRef) typeRef).getWrapperQualifiedClass();
		}
		else
		{
			PsiElement resolve = typeRef.resolve(element);
			if(resolve == null)
			{
				return "<error>";
			}

			if(resolve instanceof DotNetGenericParameter)
			{
				DotNetGenericParameterListOwner generic = PsiTreeUtil.getParentOfType(resolve, DotNetGenericParameterListOwner.class);
				if(element == generic)
				{
					return "`" + ArrayUtil.indexOf(generic.getGenericParameters(), resolve);
				}
				else
				{
					// handle class parameter - in method
					return ((DotNetGenericParameter) resolve).getName();
				}
			}
			else
			{
				return getDocName0(resolve);
			}
		}
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object o, PsiElement element)
	{
		return null;
	}

	@Nullable
	@Override
	public PsiElement getDocumentationElementForLink(PsiManager psiManager, String s, PsiElement element)
	{
		if(s.startsWith(TYPE_PREFIX))
		{
			String qName = s.substring(TYPE_PREFIX.length(), s.length());

			int genericCount = -1;
			int indexOfG = qName.indexOf('`');
			if(indexOfG != -1)
			{
				val oldQName = qName;

				qName = oldQName.substring(0, indexOfG);
				genericCount = Integer.parseInt(oldQName.substring(indexOfG + 1, oldQName.length()));
			}
			return DotNetPsiFacade.getInstance(element.getProject()).findType(qName, element.getResolveScope(), genericCount);
		}
		return null;
	}

	private static String generateLinksForType(DotNetTypeRef dotNetTypeRef, PsiElement element)
	{
		StringBuilder builder = new StringBuilder();
		if(dotNetTypeRef instanceof DotNetArrayTypeRef)
		{
			builder.append(generateLinksForType(((DotNetArrayTypeRef) dotNetTypeRef).getInnerType(), element));
			builder.append("[]");
		}
		else if(dotNetTypeRef instanceof DotNetPointerTypeRef)
		{
			builder.append(generateLinksForType(((DotNetPointerTypeRef) dotNetTypeRef).getInnerType(), element));
			builder.append("*");
		}
		else if(dotNetTypeRef instanceof DotNetNativeTypeRef)
		{
			wrapToLink(dotNetTypeRef, ((DotNetNativeTypeRef) dotNetTypeRef).getWrapperQualifiedClass(), 0, builder);
		}
		else
		{
			PsiElement resolve = dotNetTypeRef.resolve(element);
			if(resolve instanceof DotNetQualifiedElement)
			{
				if(resolve instanceof DotNetGenericParameterListOwner)
				{
					wrapToLink(dotNetTypeRef, ((DotNetQualifiedElement) resolve).getPresentableQName(), ((DotNetGenericParameterListOwner) resolve)
							.getGenericParametersCount(), builder);
				}
				else
				{
					builder.append(((DotNetQualifiedElement) resolve).getName());
				}
			}
			else
			{
				wrapToLink(dotNetTypeRef, "<unknown>", 0, builder);
			}
		}

		return builder.toString();
	}

	private static void wrapToLink(DotNetTypeRef dotNetTypeRef, String qName, int genericCount, StringBuilder builder)
	{
		builder.append("<a href=\"psi_element://").append(TYPE_PREFIX);
		builder.append(qName);
		if(genericCount > 0)
		{
			builder.append("`").append(genericCount);
		}
		builder.append("\">").append(dotNetTypeRef.getPresentableText()).append("</a>");
	}
}
