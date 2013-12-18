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

package org.mustbe.consulo.csharp.lang.parser.decl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokenSets;
import com.intellij.lang.PsiBuilder;
import com.intellij.util.NotNullFunction;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
public class DeclarationParsing extends SharingParsingHelpers
{
	public static boolean parse(@NotNull CSharpBuilderWrapper builder, boolean inner)
	{
		if(inner && builder.getTokenType() == RBRACE)
		{
			return false;
		}

		val marker = builder.mark();

		val modifierListBuilder = parseWithSoftElements(new NotNullFunction<CSharpBuilderWrapper, PsiBuilder.Marker>()
		{
			@NotNull
			@Override
			public PsiBuilder.Marker fun(CSharpBuilderWrapper builderWrapper)
			{
				return parseModifierList(builderWrapper);
			}
		}, builder, PARTIAL_KEYWORD, ASYNC_KEYWORD);

		assert modifierListBuilder != null;

		val tokenType = builder.getTokenType();
		if(tokenType == NAMESPACE_KEYWORD)
		{
			NamespaceDeclarationParsing.parse(builder, marker);
		}
		else if(CSharpTokenSets.TYPE_DECLARATION_START.contains(tokenType))
		{
			TypeDeclarationParsing.parse(builder, marker);
		}
		else if(tokenType == EVENT_KEYWORD)
		{
			builder.advanceLexer();

			EventParsing.parse(builder, marker);
		}
		else if(tokenType == DELEGATE_KEYWORD)
		{
			builder.advanceLexer();

			MethodParsing.parseMethodStartAtType(builder, marker);
		}
		else if(tokenType == CONST_KEYWORD)
		{
			builder.advanceLexer();

			FieldOrPropertyParsing.parseFieldBeforeName(builder, marker);
		}
		else
		{
			// MODIFIER_LIST IDENTIFIER LPAR -> CONSTRUCTOR
			if(tokenType == IDENTIFIER && builder.lookAhead(1) == LPAR)
			{
				MethodParsing.parseMethodStartAfterType(builder, marker, true);
			}
			else
			{
				if(parseType(builder) == null)
				{
					builder.error("Type expected " + builder.getTokenType());

					modifierListBuilder.drop();
					marker.drop();
					return false;
				}
				else if(expect(builder, IDENTIFIER, "Name expected"))
				{
					// MODIFIER_LIST TYPE IDENTIFIER LPAR -> METHOD
					if(builder.getTokenType() == LPAR || builder.getTokenType() == LT)
					{
						MethodParsing.parseMethodStartAfterName(builder, marker, false);
					}
					else
					{
						FieldOrPropertyParsing.parseFieldOrPropertyAfterName(builder, marker);
					}
				}
				else
				{
					modifierListBuilder.drop();
					marker.drop();
					return false;
				}
			}
		}
		return true;
	}
}