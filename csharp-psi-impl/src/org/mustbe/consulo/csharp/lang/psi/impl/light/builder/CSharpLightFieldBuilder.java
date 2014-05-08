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

package org.mustbe.consulo.csharp.lang.psi.impl.light.builder;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpFieldDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetModifierWithMask;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;

/**
 * @author VISTALL
 * @since 08.05.14
 */
public class CSharpLightFieldBuilder extends CSharpLightVariableBuilder<CSharpLightFieldBuilder> implements CSharpFieldDeclaration
{
	private List<DotNetModifierWithMask> myModifiers = new ArrayList<DotNetModifierWithMask>();
	private PsiElement myNameIdentifier;

	public CSharpLightFieldBuilder(PsiElement element)
	{
		super(element);
	}

	public CSharpLightFieldBuilder(Project manager, Language language)
	{
		super(manager, language);
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		PsiElement parent = getParent();
		if(parent instanceof DotNetQualifiedElement)
		{
			return ((DotNetQualifiedElement) parent).getPresentableQName();
		}
		return "";
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		String parentQName = getPresentableParentQName();
		if(StringUtil.isEmpty(parentQName))
		{
			return getName();
		}
		return parentQName + "." + getName();
	}

	@Nullable
	@Override
	public PsiElement getNameIdentifier()
	{
		return myNameIdentifier;
	}

	@Override
	public int getTextOffset()
	{
		PsiElement nameIdentifier = getNameIdentifier();
		return nameIdentifier == null ? super.getTextOffset() : nameIdentifier.getTextOffset();
	}

	@Override
	public String getName()
	{
		PsiElement nameIdentifier = getNameIdentifier();
		if(nameIdentifier != null)
		{
			return nameIdentifier.getText();
		}
		return super.getName();
	}

	public void withNameIdentifier(@NotNull PsiElement element)
	{
		myNameIdentifier = element;
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifierWithMask modifier)
	{
		return myModifiers.contains(modifier);
	}

	public void addModifier(DotNetModifierWithMask modifierWithMask)
	{
		myModifiers.add(modifierWithMask);
	}
}
