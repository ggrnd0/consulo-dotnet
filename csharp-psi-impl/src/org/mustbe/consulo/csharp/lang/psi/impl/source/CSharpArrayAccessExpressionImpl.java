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

package org.mustbe.consulo.csharp.lang.psi.impl.source;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpElementVisitor;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.dotnet.psi.DotNetArrayMethodDeclaration;
import org.mustbe.consulo.dotnet.psi.DotNetExpression;
import org.mustbe.consulo.dotnet.resolve.DotNetTypeRef;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.MultiRangeReference;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedReference;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.SmartList;

/**
 * @author VISTALL
 * @since 04.01.14.
 */
public class CSharpArrayAccessExpressionImpl extends CSharpElementImpl implements DotNetExpression, MultiRangeReference,
		CSharpExpressionWithParameters, PsiQualifiedReference
{
	public CSharpArrayAccessExpressionImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	@Override
	public void accept(@NotNull CSharpElementVisitor visitor)
	{
		visitor.visitArrayAccessExpression(this);
	}

	@NotNull
	@Override
	public DotNetTypeRef toTypeRef(boolean resolveFromParent)
	{
		PsiElement resolve = resolve();
		if(resolve instanceof DotNetArrayMethodDeclaration)
		{
			return ((DotNetArrayMethodDeclaration) resolve).getReturnTypeRef();
		}
		return DotNetTypeRef.ERROR_TYPE;
	}

	@Override
	public List<TextRange> getRanges()
	{
		PsiElement l = findChildByType(CSharpTokens.LBRACKET);
		PsiElement r = findChildByType(CSharpTokens.RBRACKET);

		List<TextRange> list = new SmartList<TextRange>();
		if(l != null)
		{
			list.add(new TextRange(l.getStartOffsetInParent(), l.getStartOffsetInParent() + 1));
		}

		if(r != null)
		{
			list.add(new TextRange(r.getStartOffsetInParent(), r.getStartOffsetInParent() + 1));
		}

		return list;
	}

	@Override
	public PsiElement getElement()
	{
		return this;
	}

	@Override
	public TextRange getRangeInElement()
	{
		List<TextRange> ranges = getRanges();
		return ranges.isEmpty() ? TextRange.EMPTY_RANGE : ranges.get(0);
	}

	@Nullable
	@Override
	public PsiElement resolve()
	{
		ResolveResult[] resolveResults = CSharpReferenceExpressionImpl.multiResolve0(true, CSharpReferenceExpressionImpl.ResolveToKind.ARRAY_METHOD,
				this, this);
		for(ResolveResult resolveResult : resolveResults)
		{
			if(resolveResult.isValidResult())
			{
				return resolveResult.getElement();
			}
		}
		return null;
	}

	@Override
	@NotNull
	public DotNetExpression getQualifier()
	{
		return (DotNetExpression) getFirstChild();
	}

	@Nullable
	@Override
	public String getReferenceName()
	{
		return getQualifier().getText();
	}

	@Nullable
	public DotNetExpression getParameterValue()
	{
		for(PsiElement element : getChildren())
		{
			if(element != getFirstChild() && element instanceof DotNetExpression)
			{
				return (DotNetExpression) element;
			}
		}
		return null;
	}

	@NotNull
	@Override
	public String getCanonicalText()
	{
		return "array";
	}

	@Override
	public PsiElement handleElementRename(String s) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
	{
		return null;
	}

	@Override
	public boolean isReferenceTo(PsiElement element)
	{
		return element == resolve();
	}

	@NotNull
	@Override
	public Object[] getVariants()
	{
		return new Object[0];
	}

	@Override
	public boolean isSoft()
	{
		return false;
	}

	@NotNull
	@Override
	public DotNetExpression[] getParameterExpressions()
	{
		return new DotNetExpression[]{getParameterValue()};
	}
}
