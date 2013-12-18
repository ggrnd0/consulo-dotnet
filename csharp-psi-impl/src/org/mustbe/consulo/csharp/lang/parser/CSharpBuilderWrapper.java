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

package org.mustbe.consulo.csharp.lang.parser;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpSoftTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class CSharpBuilderWrapper extends PsiBuilderAdapter
{
	private static Map<String, IElementType> myTextToSoftKeywords = new HashMap<String, IElementType>();

	static
	{
		for(IElementType o : CSharpSoftTokens.ALL.getTypes())
		{
			String keyword = o.toString().replace("_KEYWORD", "").toLowerCase();
			myTextToSoftKeywords.put(keyword, o);
		}
	}

	private TokenSet mySoftSet = TokenSet.EMPTY;

	public CSharpBuilderWrapper(PsiBuilder delegate)
	{
		super(delegate);
	}

	public void enableSoftKeywords(@NotNull TokenSet tokenSet)
	{
		mySoftSet = TokenSet.orSet(mySoftSet, tokenSet);
	}

	public void disableSoftKeywords(@NotNull TokenSet tokenSet)
	{
		mySoftSet = TokenSet.andNot(mySoftSet, tokenSet);
	}

	public void enableSoftKeyword(@NotNull IElementType elementType)
	{
		mySoftSet = TokenSet.orSet(mySoftSet, TokenSet.create(elementType));
	}

	public void disableSoftKeyword(@NotNull IElementType elementType)
	{
		mySoftSet = TokenSet.andNot(mySoftSet, TokenSet.create(elementType));
	}

	@Nullable
	@Override
	public IElementType getTokenType()
	{
		IElementType tokenType = super.getTokenType();
		if(tokenType == CSharpTokens.IDENTIFIER)
		{
			IElementType elementType = myTextToSoftKeywords.get(getTokenText());
			if(elementType != null && mySoftSet.contains(elementType))
			{
				remapCurrentToken(elementType);
				return elementType;
			}
		}
		return tokenType;
	}

	@Override
	public void advanceLexer()
	{
		getTokenType();  // remap if getTokenType not called

		super.advanceLexer();
	}
}