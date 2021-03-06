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

import org.emonic.base.codehierarchy.CodeHierarchyHelper;
import org.emonic.base.documentation.IDocumentation;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.*;
import org.mustbe.consulo.dotnet.resolve.DotNetArrayTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetPointerTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.Function;
import com.intellij.xml.util.XmlStringUtil;

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
		if(element instanceof DotNetTypeDeclaration)
		{
			return generateQuickTypeDeclarationInfo((DotNetTypeDeclaration) element);
		}
		else if(element instanceof DotNetVariable)
		{
			return generateQuickVariableInfo((DotNetVariable) element);
		}
		else if(element instanceof DotNetLikeMethodDeclaration)
		{
			return generateQuickLikeMethodDeclarationInfo((DotNetLikeMethodDeclaration) element);
		}
		return null;
	}

	private static String generateQuickLikeMethodDeclarationInfo(DotNetLikeMethodDeclaration element)
	{
		StringBuilder builder = new StringBuilder();

		appendModifiers(element, builder);

		if(element instanceof DotNetConstructorDeclaration)
		{
			if(((DotNetConstructorDeclaration) element).isDeConstructor())
			{
				builder.append("~");
			}
			builder.append(element.getName());
		}
		else
		{
			builder.append(generateLinksForType(element.getReturnTypeRef(), element));
			builder.append(" ");
			builder.append(XmlStringUtil.escapeString(element.getName()));
		}
		builder.append("(");
		builder.append(StringUtil.join(element.getParameters(), new Function<DotNetParameter, String>()
		{
			@Override
			public String fun(DotNetParameter dotNetParameter)
			{
				return generateLinksForType(dotNetParameter.toTypeRef(true), dotNetParameter) + " " + dotNetParameter.getName();
			}
		}, ", "));
		builder.append(")");
		return builder.toString();
	}

	private static String generateQuickVariableInfo(DotNetVariable element)
	{
		StringBuilder builder = new StringBuilder();

		appendModifiers(element, builder);
		builder.append(generateLinksForType(element.toTypeRef(true), element));
		builder.append(" ");
		builder.append(element.getName());
		DotNetExpression initializer = element.getInitializer();
		if(initializer != null)
		{
			builder.append(" = ");
			builder.append(initializer.getText());
		}
		builder.append(";");
		return builder.toString();
	}

	private static String generateQuickTypeDeclarationInfo(DotNetTypeDeclaration element)
	{
		StringBuilder builder = new StringBuilder();

		PsiFile containingFile = element.getContainingFile();
		final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(element.getProject()).getFileIndex();
		VirtualFile vFile = containingFile == null ? null : containingFile.getVirtualFile();
		if(vFile != null && (fileIndex.isInLibrarySource(vFile) || fileIndex.isInLibraryClasses(vFile)))
		{
			final List<OrderEntry> orderEntries = fileIndex.getOrderEntriesForFile(vFile);
			if(orderEntries.size() > 0)
			{
				final OrderEntry orderEntry = orderEntries.get(0);
				builder.append("[").append(StringUtil.escapeXml(orderEntry.getPresentableName())).append("] ");
			}
		}
		else
		{
			final Module module = containingFile == null ? null : ModuleUtil.findModuleForPsiElement(containingFile);
			if(module != null)
			{
				builder.append('[').append(module.getName()).append("] ");
			}
		}

		String presentableParentQName = element.getPresentableParentQName();
		if(!StringUtil.isEmpty(presentableParentQName))
		{
			builder.append(presentableParentQName);
		}

		if(builder.length() > 0)
		{
			builder.append("<br>");
		}

		appendModifiers(element, builder);

		appendTypeDeclarationType(element, builder);

		builder.append(" ").append(element.getName());

		return builder.toString();
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
		IDocumentation documentation = DotNetDocumentationCache.getInstance().findDocumentation(element);
		if(documentation == null)
		{
			return null;
		}
		return CodeHierarchyHelper.getFormText(documentation);
	}

	private static void appendTypeDeclarationType(DotNetTypeDeclaration psiElement, StringBuilder builder)
	{
		if(psiElement.isInterface())
		{
			builder.append("interface");
		}
		else if(psiElement.isEnum())
		{
			builder.append("enum");
		}
		else if(psiElement.isStruct())
		{
			builder.append("struct");
		}
		else
		{
			builder.append("class");
		}
	}

	private static void appendModifiers(DotNetModifierListOwner owner, StringBuilder builder)
	{
		DotNetModifierList modifierList = owner.getModifierList();
		if(modifierList == null)
		{
			return;
		}

		for(DotNetModifier modifier : modifierList.getModifiers())
		{
			builder.append(modifier.getPresentableText()).append(" ");
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

			return DotNetPsiSearcher.getInstance(element.getProject()).findType(qName, element.getResolveScope());
		}
		return null;
	}

	private static String generateLinksForType(DotNetTypeRef dotNetTypeRef, PsiElement element)
	{
		StringBuilder builder = new StringBuilder();
		if(dotNetTypeRef == DotNetTypeRef.AUTO_TYPE)
		{
			builder.append("var");
		}
		else if(dotNetTypeRef instanceof DotNetArrayTypeRef)
		{
			builder.append(generateLinksForType(((DotNetArrayTypeRef) dotNetTypeRef).getInnerTypeRef(), element));
			builder.append("[]");
		}
		else if(dotNetTypeRef instanceof DotNetPointerTypeRef)
		{
			builder.append(generateLinksForType(((DotNetPointerTypeRef) dotNetTypeRef).getInnerTypeRef(), element));
			builder.append("*");
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
