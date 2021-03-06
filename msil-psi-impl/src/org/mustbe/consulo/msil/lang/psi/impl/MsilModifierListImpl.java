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

package org.mustbe.consulo.msil.lang.psi.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.psi.DotNetAttribute;
import org.mustbe.consulo.dotnet.psi.DotNetModifier;
import org.mustbe.consulo.msil.lang.psi.MsilClassEntry;
import org.mustbe.consulo.msil.lang.psi.MsilMethodEntry;
import org.mustbe.consulo.msil.lang.psi.MsilModifierElementType;
import org.mustbe.consulo.msil.lang.psi.MsilModifierList;
import org.mustbe.consulo.msil.lang.psi.MsilParameter;
import org.mustbe.consulo.msil.lang.psi.MsilParameterList;
import org.mustbe.consulo.msil.lang.psi.MsilTokenSets;
import org.mustbe.consulo.msil.lang.psi.MsilTokens;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilModifierListStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.ArrayUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilModifierListImpl extends MsilStubElementImpl<MsilModifierListStub> implements MsilModifierList
{
	private static Map<DotNetModifier, MsilModifierElementType> ourReplaceMap = new HashMap<DotNetModifier, MsilModifierElementType>()
	{
		{
			put(DotNetModifier.STATIC, MsilTokens.STATIC_KEYWORD);
			put(DotNetModifier.PRIVATE, MsilTokens.PRIVATE_KEYWORD);
			put(DotNetModifier.PUBLIC, MsilTokens.PUBLIC_KEYWORD);
			put(DotNetModifier.PROTECTED, MsilTokens.PROTECTED_KEYWORD);
			put(DotNetModifier.INTERNAL, MsilTokens.ASSEMBLY_KEYWORD);
			put(DotNetModifier.ABSTRACT, MsilTokens.ABSTRACT_KEYWORD);
			put(DotNetModifier.SEALED, MsilTokens.SEALED_KEYWORD);
		}
	};

	public MsilModifierListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilModifierListImpl(@NotNull MsilModifierListStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{
		visitor.visitModifierList(this);
	}

	@Override
	public void addModifier(@NotNull DotNetModifier modifier)
	{

	}

	@Override
	public void removeModifier(@NotNull DotNetModifier modifier)
	{

	}

	@NotNull
	@Override
	public DotNetModifier[] getModifiers()
	{
		val modifiers = new ArrayList<DotNetModifier>();
		for(MsilModifierElementType modifierElementType : MsilTokenSets.MODIFIERS_AS_ARRAY)
		{
			if(hasModifier(modifierElementType))
			{
				modifiers.add(modifierElementType);
			}
		}
		return modifiers.toArray(new DotNetModifier[modifiers.size()]);
	}

	@NotNull
	@Override
	public DotNetAttribute[] getAttributes()
	{
		PsiElement parentByStub = getParentByStub();
		if(parentByStub instanceof MsilClassEntry)
		{
			return ((MsilClassEntry) parentByStub).getAttributes();
		}
		else if(parentByStub instanceof MsilMethodEntry)
		{
			return ((MsilMethodEntry) parentByStub).getAttributes();
		}
		else if(parentByStub instanceof MsilParameter)
		{
			MsilParameterList parameterList = getStubOrPsiParentOfType(MsilParameterList.class);
			MsilMethodEntry methodEntry = getStubOrPsiParentOfType(MsilMethodEntry.class);

			assert parameterList != null;
			assert methodEntry != null;
			int i = ArrayUtil.indexOf(parameterList.getParameters(), parentByStub);
			assert i != -1;
			return methodEntry.getParameterAttributes(i + 1);
		}

		return DotNetAttribute.EMPTY_ARRAY;
	}

	@Override
	public boolean hasModifier(@NotNull DotNetModifier modifier)
	{
		MsilModifierElementType elementType = asMsilModifier(modifier);
		if(elementType == null)
		{
			return false;
		}

		MsilModifierListStub stub = getStub();
		if(stub != null)
		{
			return stub.hasModififer(elementType);
		}
		return hasModifierInTree(elementType);
	}

	@Override
	public boolean hasModifierInTree(@NotNull DotNetModifier modifier)
	{
		MsilModifierElementType elementType = asMsilModifier(modifier);
		if(elementType == null)
		{
			return false;
		}
		return findChildByType(elementType) != null;
	}

	@Nullable
	@Override
	public PsiElement getModifierElement(DotNetModifier modifier)
	{
		MsilModifierElementType elementType = asMsilModifier(modifier);
		if(elementType == null)
		{
			return null;
		}
		return findChildByType(elementType);
	}

	@NotNull
	@Override
	public List<PsiElement> getModifierElements(@NotNull DotNetModifier modifier)
	{
		MsilModifierElementType elementType = asMsilModifier(modifier);
		if(elementType == null)
		{
			return Collections.emptyList();
		}
		return findChildrenByType(elementType);
	}

	@Nullable
	private static MsilModifierElementType asMsilModifier(DotNetModifier modifier)
	{
		if(modifier instanceof MsilModifierElementType)
		{
			return (MsilModifierElementType) modifier;
		}
		else
		{
			MsilModifierElementType msilModifierElementType = ourReplaceMap.get(modifier);
			if(msilModifierElementType != null)
			{
				return msilModifierElementType;
			}
			return null;
		}
	}
}
