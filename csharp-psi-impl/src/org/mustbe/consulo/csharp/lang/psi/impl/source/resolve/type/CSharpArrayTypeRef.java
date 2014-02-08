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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterListOwner;
import org.mustbe.consulo.dotnet.resolve.DotNetArrayTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpArrayTypeRef extends DotNetTypeRef.Adapter implements DotNetArrayTypeRef
{
	private final DotNetTypeRef myInnerType;

	public CSharpArrayTypeRef(DotNetTypeRef innerType)
	{
		myInnerType = innerType;
	}

	@Nullable
	@Override
	public String getPresentableText()
	{
		return myInnerType.getPresentableText() + "[]";
	}

	@Nullable
	@Override
	public String getQualifiedText()
	{
		return myInnerType.getQualifiedText() + "[]";
	}

	@Nullable
	@Override
	public PsiElement resolve(@NotNull PsiElement scope)
	{
		Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(scope);
		if(moduleForPsiElement == null)
		{
			return null;
		}
		return CSharpModuleTypeHelper.getInstance(moduleForPsiElement).getArrayType();
	}

	@NotNull
	@Override
	public DotNetGenericExtractor getGenericExtractor(@NotNull PsiElement resolved, @NotNull PsiElement scope)
	{
		if(!(resolved instanceof DotNetGenericParameterListOwner))
		{
			return DotNetGenericExtractor.EMPTY;
		}
		return new CSharpGenericExtractor(((DotNetGenericParameterListOwner) resolved).getGenericParameters(), new DotNetTypeRef[]{getInnerType()});
	}

	@Override
	@NotNull
	public DotNetTypeRef getInnerType()
	{
		return myInnerType;
	}
}
