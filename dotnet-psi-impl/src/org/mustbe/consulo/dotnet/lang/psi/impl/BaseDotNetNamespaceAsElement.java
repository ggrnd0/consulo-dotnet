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

package org.mustbe.consulo.dotnet.lang.psi.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.resolve.DotNetNamespaceAsElement;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 23.09.14
 */
public abstract class BaseDotNetNamespaceAsElement extends LightElement implements DotNetNamespaceAsElement
{
	public static Key<GlobalSearchScope> RESOLVE_SCOPE = Key.create("resolve.scope");
	public static Key<Boolean> WITH_CHILD_NAMESPACES = Key.create("with.child.namespaces");

	protected Project myProject;
	protected String myQName;

	protected BaseDotNetNamespaceAsElement(@NotNull Project project, @NotNull Language language, @NotNull String qName)
	{
		super(PsiManager.getInstance(project), language);
		myQName = qName;
		myProject = project;
	}

	@Override
	public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement
			place)
	{
		GlobalSearchScope globalSearchScope = state.get(RESOLVE_SCOPE);
		if(globalSearchScope == null)
		{
			throw new IllegalArgumentException("Please specify RESOLVE_SCOPE");
		}

		boolean withChildNamespaces = state.get(WITH_CHILD_NAMESPACES) == Boolean.TRUE;

		for(PsiElement element : getChildren(globalSearchScope, withChildNamespaces))
		{
			if(!processor.execute(element, state))
			{
				return false;
			}
		}

		return true;
	}

	@NotNull
	@Override
	public PsiElement[] findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, boolean withChildNamespaces)
	{
		List<PsiElement> list = new SmartList<PsiElement>();
		for(PsiElement element : getChildren(globalSearchScope, withChildNamespaces))
		{
			if(element instanceof PsiNamedElement && Comparing.equal(name, ((PsiNamedElement) element).getName()))
			{
				list.add(element);
			}
		}
		return list.isEmpty() ? PsiElement.EMPTY_ARRAY : ContainerUtil.toArray(list, PsiElement.ARRAY_FACTORY);
	}

	@Override
	@NotNull
	public Collection<? extends PsiElement> getChildren(@NotNull GlobalSearchScope globalSearchScope, boolean withChildNamespaces)
	{
		return Collections.emptyList();
	}

	@Nullable
	@Override
	public String getPresentableParentQName()
	{
		return StringUtil.getPackageName(myQName);
	}

	@Nullable
	@Override
	public String getPresentableQName()
	{
		return myQName;
	}

	@Override
	public String getName()
	{
		return StringUtil.getShortName(myQName);
	}

	@Override
	public String toString()
	{
		return null;
	}

	@Override
	public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException
	{
		return null;
	}
}
