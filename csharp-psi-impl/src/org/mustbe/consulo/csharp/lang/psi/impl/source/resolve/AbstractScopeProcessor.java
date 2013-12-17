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

package org.mustbe.consulo.csharp.lang.psi.impl.source.resolve;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.scope.PsiScopeProcessor;

/**
 * @author VISTALL
 * @since 17.12.13.
 */
public abstract class AbstractScopeProcessor extends UserDataHolderBase implements PsiScopeProcessor
{
	protected final List<PsiElement> myElements = new ArrayList<PsiElement>();

	@Nullable
	@Override
	public <T> T getHint(@NotNull Key<T> tKey)
	{
		return getUserData(tKey);
	}

	@NotNull
	public List<PsiElement> getElements()
	{
		return myElements;
	}

	@NotNull
	public ResolveResult[] toResolveResults()
	{
		if(myElements.isEmpty())
		{
			return ResolveResult.EMPTY_ARRAY;
		}
		ResolveResult[] k = new ResolveResult[myElements.size()];
		for(int i = 0; i < myElements.size(); i++)
		{
			PsiElement psiElement = myElements.get(i);
			k[i] = new PsiElementResolveResult(psiElement);
		}
		return k;
	}

	@Override
	public void handleEvent(Event event, @Nullable Object o)
	{

	}
}
