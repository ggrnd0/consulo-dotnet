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

package org.mustbe.consulo.csharp.lang.parser.exp;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.parser.CSharpBuilderWrapper;
import org.mustbe.consulo.csharp.lang.parser.SharingParsingHelpers;
import org.mustbe.consulo.csharp.lang.parser.stmt.StatementParsing;
import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import lombok.val;

public class ExpressionParsing extends SharingParsingHelpers
{
	private enum ExprType
	{
		CONDITIONAL_OR, CONDITIONAL_AND, OR, XOR, AND, EQUALITY, RELATIONAL, SHIFT, ADDITIVE, MULTIPLICATIVE, UNARY, TYPE
	}

	private static final TokenSet ASSIGNMENT_OPS = TokenSet.create(EQ/*, ASTERISKEQ, DIVEQ, PERCEQ, PLUSEQ, MINUSEQ,
			LTLTEQ, GTGTEQ, GTGTGTEQ, ANDEQ, OREQ, XOREQ*/);

	private static final TokenSet CONDITIONAL_OR_OPS = TokenSet.create(OROR);
	private static final TokenSet CONDITIONAL_AND_OPS = TokenSet.create(ANDAND);
	private static final TokenSet OR_OPS = TokenSet.create(OR);
	private static final TokenSet XOR_OPS = TokenSet.create(XOR);
	private static final TokenSet AND_OPS = TokenSet.create(AND);
	private static final TokenSet EQUALITY_OPS = TokenSet.create(EQEQ, NTEQ);
	private static final TokenSet RELATIONAL_OPS = TokenSet.create(LT, GT, LTEQ, GTEQ);
	private static final TokenSet SHIFT_OPS = TokenSet.create(LTLT, GTGT);
	private static final TokenSet ADDITIVE_OPS = TokenSet.create(PLUS, MINUS);
	private static final TokenSet MULTIPLICATIVE_OPS = TokenSet.create(MUL, DIV, PERC);
	private static final TokenSet POSTFIX_OPS = TokenSet.create(PLUSPLUS, MINUSMINUS);
	private static final TokenSet PREF_ARITHMETIC_OPS = TokenSet.orSet(POSTFIX_OPS, TokenSet.create(PLUS, MINUS));
	private static final TokenSet PREFIX_OPS = TokenSet.orSet(PREF_ARITHMETIC_OPS, TokenSet.create(TILDE, EXCL));
	private static final TokenSet ARGS_LIST_CONTINUE = TokenSet.create(IDENTIFIER, TokenType.BAD_CHARACTER, COMMA, INTEGER_LITERAL, STRING_LITERAL);
	private static final TokenSet ARGS_LIST_END = TokenSet.create(RPAR, RBRACE, RBRACKET);
	private static final TokenSet ID_OR_SUPER = TokenSet.create(IDENTIFIER, BASE_KEYWORD);
	private static final TokenSet THIS_OR_BASE = TokenSet.create(THIS_KEYWORD, BASE_KEYWORD);


	@Nullable
	public static PsiBuilder.Marker parse(final CSharpBuilderWrapper builder)
	{
		return parseAssignment(builder);
	}

	@Nullable
	private static PsiBuilder.Marker parseAssignment(final CSharpBuilderWrapper builder)
	{
		final PsiBuilder.Marker left = parseConditional(builder);
		if(left == null)
		{
			return null;
		}

		final IElementType tokenType = builder.getTokenTypeGGLL();

		if(ASSIGNMENT_OPS.contains(tokenType) && tokenType != null)
		{
			final PsiBuilder.Marker assignment = left.precede();
			builder.advanceLexerGGLL();

			final PsiBuilder.Marker right = parse(builder);
			if(right == null)
			{
				builder.error("Expression expected");
			}

			assignment.done(ASSIGNMENT_EXPRESSION);
			return assignment;
		}

		return left;
	}

	@Nullable
	public static PsiBuilder.Marker parseConditional(final CSharpBuilderWrapper builder)
	{
		final PsiBuilder.Marker condition = parseExpression(builder, ExprType.CONDITIONAL_OR);
		if(condition == null)
		{
			return null;
		}

		if(builder.getTokenType() == QUEST)
		{
			final PsiBuilder.Marker ternary = condition.precede();
			builder.advanceLexer();

			final PsiBuilder.Marker truePart = parse(builder);
			if(truePart == null)
			{
				builder.error("Expression expected");
				ternary.done(CONDITIONAL_EXPRESSION);
				return ternary;
			}

			if(builder.getTokenType() != COLON)
			{
				builder.error("Expected colon");
				ternary.done(CONDITIONAL_EXPRESSION);
				return ternary;
			}
			builder.advanceLexer();

			final PsiBuilder.Marker falsePart = parseConditional(builder);
			if(falsePart == null)
			{
				builder.error("Expression expected");
				ternary.done(CONDITIONAL_EXPRESSION);
				return ternary;
			}

			ternary.done(CONDITIONAL_EXPRESSION);
			return ternary;
		}
		else if(builder.getTokenType() == NULL_COALESCING)
		{
			final PsiBuilder.Marker nullCoalescing = condition.precede();
			builder.advanceLexer();

			final PsiBuilder.Marker ifNullPart = parse(builder);
			if(ifNullPart == null)
			{
				builder.error("Expression expected");
			}
			nullCoalescing.done(NULL_COALESCING_EXPRESSION);
			return nullCoalescing;
		}
		else
		{
			return condition;
		}
	}

	@Nullable
	private static PsiBuilder.Marker parseExpression(final CSharpBuilderWrapper builder, final ExprType type)
	{
		switch(type)
		{
			case CONDITIONAL_OR:
				return parseBinary(builder, ExprType.CONDITIONAL_AND, CONDITIONAL_OR_OPS);

			case CONDITIONAL_AND:
				return parseBinary(builder, ExprType.OR, CONDITIONAL_AND_OPS);

			case OR:
				return parseBinary(builder, ExprType.XOR, OR_OPS);

			case XOR:
				return parseBinary(builder, ExprType.AND, XOR_OPS);

			case AND:
				return parseBinary(builder, ExprType.EQUALITY, AND_OPS);

			case EQUALITY:
				return parseBinary(builder, ExprType.RELATIONAL, EQUALITY_OPS);

			case RELATIONAL:
				return parseRelational(builder);

			case SHIFT:
				return parseBinary(builder, ExprType.ADDITIVE, SHIFT_OPS);

			case ADDITIVE:
				return parseBinary(builder, ExprType.MULTIPLICATIVE, ADDITIVE_OPS);

			case MULTIPLICATIVE:
				return parseBinary(builder, ExprType.UNARY, MULTIPLICATIVE_OPS);

			case UNARY:
				return parseUnary(builder);

			case TYPE:
				TypeInfo typeInfo = parseType(builder);
				return typeInfo == null ? null : typeInfo.marker;
			default:
				assert false : "Unexpected type: " + type;
				return null;
		}
	}

	@Nullable
	private static PsiBuilder.Marker parseUnary(final CSharpBuilderWrapper builder)
	{
		final IElementType tokenType = builder.getTokenType();

		if(PREFIX_OPS.contains(tokenType))
		{
			final PsiBuilder.Marker unary = builder.mark();
			builder.advanceLexer();

			final PsiBuilder.Marker operand = parseUnary(builder);
			if(operand == null)
			{
				builder.error("Expression expected");
			}

			unary.done(PREFIX_EXPRESSION);
			return unary;
		}
		else if(tokenType == LPAR)
		{
			final PsiBuilder.Marker typeCast = builder.mark();
			builder.advanceLexer();

			val typeInfo = parseType(builder);
			if(typeInfo == null || !expect(builder, RPAR, null))
			{
				typeCast.rollbackTo();
				return parsePostfix(builder);
			}

			if(PREF_ARITHMETIC_OPS.contains(builder.getTokenType()) && !typeInfo.isNative)
			{
				typeCast.rollbackTo();
				return parsePostfix(builder);
			}

			final PsiBuilder.Marker expr = parseUnary(builder);
			if(expr == null)
			{
				if(!typeInfo.isParameterized)
				{  // cannot parse correct parenthesized expression after correct parameterized type
					typeCast.rollbackTo();
					return parsePostfix(builder);
				}
				else
				{
					builder.error("Expression expected");
				}
			}

			typeCast.done(TYPE_CAST_EXPRESSION);
			return typeCast;
		}
		else
		{
			return parsePostfix(builder);
		}
	}

	@Nullable
	private static PsiBuilder.Marker parsePostfix(final CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker operand = parsePrimary(builder, null, -1);
		if(operand == null)
		{
			return null;
		}

		while(POSTFIX_OPS.contains(builder.getTokenType()))
		{
			final PsiBuilder.Marker postfix = operand.precede();
			builder.advanceLexer();
			postfix.done(POSTFIX_EXPRESSION);
			operand = postfix;
		}

		return operand;
	}

	@Nullable
	private static PsiBuilder.Marker parseBinary(final CSharpBuilderWrapper builder, final ExprType type, final TokenSet ops)
	{
		PsiBuilder.Marker result = parseExpression(builder, type);
		if(result == null)
		{
			return null;
		}
		int operandCount = 1;

		IElementType tokenType = builder.getTokenTypeGGLL();
		IElementType currentExprTokenType = tokenType;
		while(true)
		{
			if(tokenType == null || !ops.contains(tokenType))
			{
				break;
			}

			builder.advanceLexerGGLL();

			final PsiBuilder.Marker right = parseExpression(builder, type);
			operandCount++;
			tokenType = builder.getTokenType();
			if(tokenType == null || !ops.contains(tokenType) || tokenType != currentExprTokenType || right == null)
			{
				// save
				result = result.precede();
				if(right == null)
				{
					builder.error("Expression expected");
				}
				result.done(operandCount > 2 ? POLYADIC_EXPRESSION : BINARY_EXPRESSION);
				if(right == null)
				{
					break;
				}
				currentExprTokenType = tokenType;
				operandCount = 1;
			}
		}

		return result;
	}

	@Nullable
	private static PsiBuilder.Marker parseRelational(final CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker left = parseExpression(builder, ExprType.SHIFT);
		if(left == null)
		{
			return null;
		}

		IElementType tokenType;
		while((tokenType = builder.getTokenTypeGGLL()) != null)
		{
			final IElementType toCreate;
			final ExprType toParse;
			if(RELATIONAL_OPS.contains(tokenType))
			{
				toCreate = BINARY_EXPRESSION;
				toParse = ExprType.SHIFT;
			}
			else if(tokenType == IS_KEYWORD)
			{
				toCreate = IS_EXPRESSION;
				toParse = ExprType.TYPE;
			}
			else if(tokenType == AS_KEYWORD)
			{
				toCreate = AS_EXPRESSION;
				toParse = ExprType.TYPE;
			}
			else
			{
				break;
			}

			final PsiBuilder.Marker expression = left.precede();
			builder.advanceLexerGGLL();

			final PsiBuilder.Marker right = parseExpression(builder, toParse);
			if(right == null)
			{
				builder.error(toParse == ExprType.TYPE ? "Type expected" : "Expression expected");
				expression.done(toCreate);
				return expression;
			}

			expression.done(toCreate);
			left = expression;
		}

		return left;
	}

	private static enum BreakPoint
	{
		P1, P2, P3, P4
	}

	@Nullable
	private static PsiBuilder.Marker parsePrimary(final CSharpBuilderWrapper builder, @Nullable final BreakPoint breakPoint, final int breakOffset)
	{
		PsiBuilder.Marker startMarker = builder.mark();

		PsiBuilder.Marker expr = parsePrimaryExpressionStart(builder);
		if(expr == null)
		{
			startMarker.drop();
			return null;
		}

		while(true)
		{
			final IElementType tokenType = builder.getTokenType();
			if(tokenType == DOT)
			{
				final PsiBuilder.Marker dotPos = builder.mark();
				final int dotOffset = builder.getCurrentOffset();
				builder.advanceLexer();

				IElementType dotTokenType = builder.getTokenType();

				if(dotTokenType == NEW_KEYWORD)
				{
					dotPos.drop();
					expr = parseNewExpression(builder, expr);
				}
			/*	else if(THIS_OR_SUPER.contains(dotTokenType) && exprType(expr) == REFERENCE_EXPRESSION)
				{
					if(breakPoint == BreakPoint.P2 && builder.getCurrentOffset() == breakOffset)
					{
						dotPos.rollbackTo();
						startMarker.drop();
						return expr;
					}

					final PsiBuilder.Marker copy = startMarker.precede();
					final int offset = builder.getCurrentOffset();
					startMarker.rollbackTo();

					final PsiBuilder.Marker ref = myParser.getReferenceParser().parseJavaCodeReference(builder, false, true, false, false);
					if(ref == null || builder.getTokenType() != DOT || builder.getCurrentOffset() != dotOffset)
					{
						copy.rollbackTo();
						return parsePrimary(builder, BreakPoint.P2, offset);
					}
					builder.advanceLexer();

					if(builder.getTokenType() != dotTokenType)
					{
						copy.rollbackTo();
						return parsePrimary(builder, BreakPoint.P2, offset);
					}
					builder.advanceLexer();

					startMarker = copy;
					expr = ref.precede();
					expr.done(dotTokenType == THIS_KEYWORD ? THIS_EXPRESSION : SUPER_EXPRESSION);
				} */
				else if(dotTokenType == BASE_KEYWORD)
				{
					dotPos.drop();
					final PsiBuilder.Marker refExpr = expr.precede();
					builder.advanceLexer();
					refExpr.done(REFERENCE_EXPRESSION);
					expr = refExpr;
				}
				else
				{
					dotPos.drop();
					final PsiBuilder.Marker refExpr = expr.precede();
					//myParser.getReferenceParser().parseReferenceParameterList(builder, false, false);

					if(!expect(builder, ID_OR_SUPER, "expected.identifier"))
					{
						refExpr.done(REFERENCE_EXPRESSION);
						startMarker.drop();
						return refExpr;
					}

					refExpr.done(REFERENCE_EXPRESSION);
					expr = refExpr;
				}
			}
			else if(tokenType == LPAR)
			{
				if(exprType(expr) != REFERENCE_EXPRESSION)
				{
					startMarker.drop();
					return expr;
				}

				final PsiBuilder.Marker callExpr = expr.precede();
				parseArgumentList(builder);
				callExpr.done(METHOD_CALL_EXPRESSION);
				expr = callExpr;
			}
			else if(tokenType == LBRACKET)
			{
				if(breakPoint == BreakPoint.P4)
				{
					startMarker.drop();
					return expr;
				}

				builder.advanceLexer();

				{
					final PsiBuilder.Marker arrayAccess = expr.precede();

					final PsiBuilder.Marker index = parse(builder);
					if(index == null)
					{
						builder.error("Expression expected");
						arrayAccess.done(ARRAY_ACCESS_EXPRESSION);
						startMarker.drop();
						return arrayAccess;
					}

					if(builder.getTokenType() != RBRACKET)
					{
						builder.error("']' expected");
						arrayAccess.done(ARRAY_ACCESS_EXPRESSION);
						startMarker.drop();
						return arrayAccess;
					}
					builder.advanceLexer();

					arrayAccess.done(ARRAY_ACCESS_EXPRESSION);
					expr = arrayAccess;
				}
			}
			else
			{
				startMarker.drop();
				return expr;
			}
		}
	}

	@NotNull
	public static PsiBuilder.Marker parseArgumentList(final CSharpBuilderWrapper builder)
	{
		final PsiBuilder.Marker list = builder.mark();
		builder.advanceLexer();

		boolean first = true;
		while(true)
		{
			final IElementType tokenType = builder.getTokenType();
			if(first && (ARGS_LIST_END.contains(tokenType) || builder.eof()))
			{
				break;
			}
			if(!first && !ARGS_LIST_CONTINUE.contains(tokenType))
			{
				break;
			}

			boolean hasError = false;
			if(!first)
			{
				if(builder.getTokenType() == COMMA)
				{
					builder.advanceLexer();
				}
				else
				{
					hasError = true;
					builder.error("Expected ',' or ')'");
					emptyExpression(builder);
				}
			}
			first = false;

			final PsiBuilder.Marker arg = parse(builder);
			if(arg == null)
			{
				if(!hasError)
				{
					builder.error("Expression expected");
					emptyExpression(builder);
				}
				if(!ARGS_LIST_CONTINUE.contains(builder.getTokenType()))
				{
					break;
				}
				if(builder.getTokenType() != COMMA && !builder.eof())
				{
					builder.advanceLexer();
				}
			}
		}

		final boolean closed = expect(builder, RPAR, "expected.rparen");

		list.done(METHOD_CALL_PARAMETER_LIST);
		if(!closed)
		{
			list.setCustomEdgeTokenBinders(null, GREEDY_RIGHT_EDGE_PROCESSOR);
		}
		return list;
	}

	@Nullable
	private static PsiBuilder.Marker parsePrimaryExpressionStart(final CSharpBuilderWrapper builder)
	{
		IElementType tokenType = builder.getTokenType();

		if(LITERALS.contains(tokenType))
		{
			final PsiBuilder.Marker literal = builder.mark();
			builder.advanceLexer();
			literal.done(CONSTANT_EXPRESSION);
			return literal;
		}

	/*	if(tokenType == LBRACE)
		{
			return parseArrayInitializer(builder);
		}      */

		if(tokenType == NEW_KEYWORD)
		{
			return parseNewExpression(builder, null);
		}

		if(tokenType == TYPEOF_KEYWORD)
		{
			return parseExpressionWithTypeInLParRPar(builder, null, TYPE_OF_EXPRESSION);
		}

		if(tokenType == DEFAULT_KEYWORD)
		{
			return parseExpressionWithTypeInLParRPar(builder, null, DEFAULT_EXPRESSION);
		}

		if(tokenType == SIZEOF_KEYWORD)
		{
			return parseExpressionWithTypeInLParRPar(builder, null, SIZE_OF_EXPRESSION);
		}

		if(tokenType == LPAR)
		{
			final PsiBuilder.Marker lambda = parseLambdaAfterParenth(builder, null);
			if(lambda != null)
			{
				return lambda;
			}

			final PsiBuilder.Marker parenth = builder.mark();
			builder.advanceLexer();

			final PsiBuilder.Marker inner = parse(builder);
			if(inner == null)
			{
				builder.error("Expression expected");
			}

			if(!expect(builder, RPAR, null))
			{
				if(inner != null)
				{
					builder.error("')' expected");
				}
			}

			parenth.done(PARENTHESES_EXPRESSION);
			return parenth;
		}

		if(tokenType == FROM_KEYWORD)
		{
			return LinqParsing.parseLinqExpression(builder);
		}

		if(tokenType == IDENTIFIER)
		{
			if(builder.lookAhead(1) == DARROW)
			{
				return parseLambdaExpression(builder, false, null);
			}

			val refExpr = builder.mark();

			builder.advanceLexer();
			refExpr.done(REFERENCE_EXPRESSION);
			return refExpr;
		}

		if(NATIVE_TYPES.contains(tokenType))
		{
			val refExpr = builder.mark();

			builder.advanceLexer();
			refExpr.done(REFERENCE_EXPRESSION);
			return refExpr;
		}

		if(THIS_OR_BASE.contains(tokenType))
		{
			val expr = builder.mark();
			builder.advanceLexer();
			expr.done(REFERENCE_EXPRESSION);
			return expr;
		}

		return null;
	}

	@Nullable
	private static PsiBuilder.Marker parseLambdaAfterParenth(final CSharpBuilderWrapper builder, @Nullable final PsiBuilder.Marker typeList)
	{
		final boolean isLambda;
		final boolean isTyped;

		final IElementType nextToken1 = builder.lookAhead(1);
		final IElementType nextToken2 = builder.lookAhead(2);
		if(nextToken1 == RPAR && nextToken2 == DARROW)
		{
			isLambda = true;
			isTyped = false;
		}
		else if(nextToken1 == IDENTIFIER)
		{
			if(nextToken2 == COMMA || nextToken2 == RPAR && builder.lookAhead(3) == DARROW)
			{
				isLambda = true;
				isTyped = false;
			}
			else if(nextToken2 == DARROW)
			{
				isLambda = false;
				isTyped = false;
			}
			else
			{
				boolean arrow = false;

				final PsiBuilder.Marker marker = builder.mark();
				while(!builder.eof())
				{
					builder.advanceLexer();
					final IElementType tokenType = builder.getTokenType();
					if(tokenType == DARROW)
					{
						arrow = true;
						break;
					}
					if(tokenType == RPAR)
					{
						arrow = builder.lookAhead(1) == DARROW;
						break;
					}
					else if(tokenType == LPAR || tokenType == SEMICOLON ||
							tokenType == LBRACE || tokenType == RBRACE)
					{
						break;
					}
				}
				marker.rollbackTo();

				isLambda = arrow;
				isTyped = true;
			}
		}
		else
		{
			isLambda = false;
			isTyped = false;
		}

		return isLambda ? parseLambdaExpression(builder, isTyped, typeList) : null;
	}

	@Nullable
	private static PsiBuilder.Marker parseLambdaExpression(final CSharpBuilderWrapper builder, final boolean typed, @Nullable final PsiBuilder.Marker typeList)
	{
		final PsiBuilder.Marker start = typeList != null ? typeList.precede() : builder.mark();

		//myParser.getDeclarationParser().parseLambdaParameterList(builder, typed);

		if(!expect(builder, DARROW, null))
		{
			start.rollbackTo();
			return null;
		}

		final PsiBuilder.Marker body;
		if(builder.getTokenType() == LBRACE)
		{
			body = StatementParsing.parse(builder);
		}
		else
		{
			body = parse(builder);
		}

		if(body == null)
		{
			builder.error("'}' expected");
		}

		start.done(LAMBDA_EXPRESSION);
		return start;
	}

	private static void emptyExpression(final PsiBuilder builder)
	{
		emptyElement(builder, EMPTY_EXPRESSION);
	}

	public static void parseParameterList(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		if(builder.getTokenType() == RPAR)
		{
			builder.advanceLexer();
			mark.done(METHOD_CALL_PARAMETER_LIST);
			return;
		}

		boolean empty = true;
		while(!builder.eof())
		{
			PsiBuilder.Marker marker = parse(builder);
			if(marker == null)
			{
				if(!empty)
				{
					builder.error("Expression expected");
				}
				break;
			}

			empty = false;

			if(builder.getTokenType() == COMMA)
			{
				builder.advanceLexer();
			}
			else if(builder.getTokenType() == RPAR)
			{
				break;
			}
			else
			{
				break;
			}
		}
		expect(builder, RPAR, "')' expected");
		mark.done(METHOD_CALL_PARAMETER_LIST);
	}

	public static PsiBuilder.Marker parseParenthesesExpression(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();
		if(expect(builder, LPAR, "'(' expected"))
		{
			parse(builder);
			expect(builder, RPAR, "')' expected");
			mark.done(PARENTHESES_EXPRESSION);
			return mark;
		}
		else
		{
			mark.drop();
			return null;
		}
	}

	private static PsiBuilder.Marker parseNewExpression(CSharpBuilderWrapper builder, PsiBuilder.Marker mark)
	{
		PsiBuilder.Marker newExpr = (mark != null ? mark.precede() : builder.mark());

		builder.advanceLexer();

		IElementType elementType = null;
		val typeMarker = parseType(builder);
		if(typeMarker == null)
		{
			builder.error("Type expected");
		}
		else
		{
			elementType = ((LighterASTNode) typeMarker.marker).getTokenType();
		}

		if(elementType == ARRAY_TYPE)
		{
			if(builder.getTokenType() == LBRACKET)
			{

			}
		}
		else
		{
			if(builder.getTokenType() == LPAR)
			{
				parseArgumentList(builder);
			}

			if(builder.getTokenType() == LBRACE)
			{
				parseFieldOrPropertySetBlock(builder);
			}
		}

		newExpr.done(NEW_EXPRESSION);
		return newExpr;
	}

	private static void parseFieldOrPropertySetBlock(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		builder.advanceLexer();

		if(!expect(builder, RBRACE, null))
		{
			while(!builder.eof())
			{
				if(parseFieldOrPropertySet(builder) == null)
				{
					break;
				}

				if(builder.getTokenType() == COMMA)
				{
					builder.advanceLexer();
				}
				else
				{
					break;
				}
			}
			expect(builder, RBRACE, "'}' expected");
		}

		mark.done(FIELD_OR_PROPERTY_SET_BLOCK);
	}

	private static PsiBuilder.Marker parseFieldOrPropertySet(CSharpBuilderWrapper builder)
	{
		PsiBuilder.Marker mark = builder.mark();

		if(doneOneElement(builder, IDENTIFIER, REFERENCE_EXPRESSION, "Identifier expected"))
		{
			if(expect(builder, EQ, "'=' expected"))
			{
				if(ExpressionParsing.parse(builder) == null)
				{
					builder.error("Expression expected");
				}
			}
			mark.done(FIELD_OR_PROPERTY_SET);
			return mark;
		}
		else
		{
			mark.drop();
			return null;
		}
	}

	private static PsiBuilder.Marker parseExpressionWithTypeInLParRPar(CSharpBuilderWrapper builder, PsiBuilder.Marker mark, IElementType to)
	{
		PsiBuilder.Marker newMarker = mark == null ? builder.mark() : mark.precede();
		builder.advanceLexer();

		if(expect(builder, LPAR, "'(' expected"))
		{
			if(parseType(builder) == null)
			{
				builder.error("Type expected");
			}
			expect(builder, RPAR, "')' expected");
		}
		newMarker.done(to);
		return newMarker;
	}

	public static PsiBuilder.Marker parseQualifiedReference(@NotNull PsiBuilder builder, @Nullable PsiBuilder.Marker prevMarker)
	{
		if(prevMarker != null)
		{
			builder.advanceLexer(); // skip dot
		}
		PsiBuilder.Marker marker = prevMarker == null ? builder.mark() : prevMarker;

		if(expect(builder, IDENTIFIER, "Identifier expected"))
		{
			marker.done(REFERENCE_EXPRESSION);

			if(builder.getTokenType() == DOT)
			{
				marker = parseQualifiedReference(builder, marker.precede());
			}
		}
		else
		{
			marker.drop();
			marker = null;
		}

		return marker;
	}
}
