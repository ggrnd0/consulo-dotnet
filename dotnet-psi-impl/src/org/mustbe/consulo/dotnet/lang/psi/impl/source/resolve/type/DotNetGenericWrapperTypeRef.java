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

package org.mustbe.consulo.dotnet.lang.psi.impl.source.resolve.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterListOwner;
import org.mustbe.consulo.dotnet.resolve.DotNetGenericExtractor;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRefWithInnerTypeRef;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 04.01.14.
 */
public class DotNetGenericWrapperTypeRef implements DotNetTypeRef, DotNetTypeRefWithInnerTypeRef
{
	private final DotNetTypeRef myInnerTypeRef;
	private final DotNetTypeRef[] myArguments;

	public DotNetGenericWrapperTypeRef(DotNetTypeRef innerTypeRef, DotNetTypeRef[] rArguments)
	{
		myInnerTypeRef = innerTypeRef;
		myArguments = rArguments;
	}

	@NotNull
	@Override
	public String getPresentableText()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getInnerTypeRef().getPresentableText());
		builder.append("<");
		for(int i = 0; i < getArgumentTypeRefs().length; i++)
		{
			if(i != 0)
			{
				builder.append(", ");
			}
			DotNetTypeRef argument = getArgumentTypeRefs()[i];
			builder.append(argument.getPresentableText());
		}
		builder.append(">");
		return builder.toString();
	}

	@NotNull
	@Override
	public String getQualifiedText()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getInnerTypeRef().getQualifiedText());
		builder.append("<");
		for(int i = 0; i < getArgumentTypeRefs().length; i++)
		{
			if(i != 0)
			{
				builder.append(", ");
			}
			DotNetTypeRef argument = getArgumentTypeRefs()[i];
			builder.append(argument.getQualifiedText());
		}
		builder.append(">");
		return builder.toString();
	}

	@Override
	public boolean isNullable()
	{
		return getInnerTypeRef().isNullable();
	}

	@Nullable
	@Override
	public PsiElement resolve(@NotNull PsiElement element)
	{
		return getInnerTypeRef().resolve(element);
	}

	@NotNull
	@Override
	public DotNetGenericExtractor getGenericExtractor(@NotNull PsiElement resolved, @NotNull PsiElement scope)
	{
		if(!(resolved instanceof DotNetGenericParameterListOwner))
		{
			return DotNetGenericExtractor.EMPTY;
		}

		DotNetGenericParameter[] genericParameters = ((DotNetGenericParameterListOwner) resolved).getGenericParameters();
		if(genericParameters.length != getArgumentTypeRefs().length)
		{
			return DotNetGenericExtractor.EMPTY;
		}
		return new SimpleGenericExtractorImpl(genericParameters, getArgumentTypeRefs());
	}

	@Override
	@NotNull
	public DotNetTypeRef getInnerTypeRef()
	{
		return myInnerTypeRef;
	}

	@NotNull
	public DotNetTypeRef[] getArgumentTypeRefs()
	{
		return myArguments;
	}
}
