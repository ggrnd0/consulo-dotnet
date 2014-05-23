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

package org.mustbe.consulo.csharp.lang.psi.impl.msil;

import com.intellij.psi.PsiElement;
import com.intellij.util.Processor;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public abstract class MsilWrapperProcessor<T> implements Processor<T>
{
	@Override
	public final boolean process(T t)
	{
		if(t instanceof PsiElement)
		{
			return processImpl((T) MsilToCSharpUtil.wrap((PsiElement) t));
		}
		return processImpl(t);
	}

	public abstract boolean processImpl(T t);
}
