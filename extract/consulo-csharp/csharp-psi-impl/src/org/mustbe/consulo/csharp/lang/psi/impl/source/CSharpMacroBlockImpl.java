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

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.CSharpMacroElementVisitor;
import com.intellij.lang.ASTNode;

/**
 * @author VISTALL
 * @since 18.12.13.
 */
public class CSharpMacroBlockImpl extends CSharpMacroElementImpl
{
	public CSharpMacroBlockImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public CSharpMacroBlockStartImpl getStartElement()
	{
		return findChildByClass(CSharpMacroBlockStartImpl.class);
	}

	public CSharpMacroBlockStopImpl getStopElement()
	{
		return findChildByClass(CSharpMacroBlockStopImpl.class);
	}

	@Override
	public void accept(@NotNull CSharpMacroElementVisitor visitor)
	{
		visitor.visitMacroBlock(this);
	}
}