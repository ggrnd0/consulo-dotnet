/*
 * Copyright 2013 must-be.org
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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpTypeStub;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import org.mustbe.consulo.dotnet.psi.DotNetNamedElement;
import org.mustbe.consulo.dotnet.psi.DotNetNamespaceDeclaration;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpTypeDeclarationImpl extends CSharpStubMemberImpl<CSharpTypeStub> implements CSharpTypeDeclaration
{
	public CSharpTypeDeclarationImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpTypeDeclarationImpl(@NotNull CSharpTypeStub stub)
	{
		super(stub, CSharpStubElements.TYPE_DECLARATION);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitTypeDeclaration(this);
	}

	@Override
	public PsiElement getLeftBrace()
	{
		return findChildByType(CSharpTokens.LBRACE);
	}

	@Override
	public PsiElement getRightBrace()
	{
		return findChildByType(CSharpTokens.RBRACE);
	}

	@Nullable
	@Override
	public DotNetGenericParameterList getGenericParameterList()
	{
		return findChildByClass(DotNetGenericParameterList.class);
	}

	@NotNull
	@Override
	public DotNetGenericParameter[] getGenericParameters()
	{
		DotNetGenericParameterList genericParameterList = getGenericParameterList();
		return genericParameterList == null ? DotNetGenericParameter.EMPTY_ARRAY : genericParameterList.getParameters();
	}

	@NotNull
	@Override
	public DotNetNamedElement[] getMembers()
	{
		return findChildrenByClass(DotNetNamedElement.class);
	}

	@Override
	public boolean isInterface()
	{
		return findChildByType(CSharpTokens.INTERFACE_KEYWORD) != null;
	}

	@Override
	public boolean isStruct()
	{
		return findChildByType(CSharpTokens.STRUCT_KEYWORD) != null;
	}

	@Override
	public boolean isEnum()
	{
		return findChildByType(CSharpTokens.ENUM_KEYWORD) != null;
	}

	@Nullable
	@Override
	public String getQName()
	{
		CSharpTypeStub stub = getStub();
		if(stub != null)
		{
			return stub.getQName();
		}

		PsiElement parent = getParent();
		if(parent instanceof DotNetNamespaceDeclaration)
		{
			return ((DotNetNamespaceDeclaration) parent).getQName() + "." + getName();
		}
		return getName();
	}
}
