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

package org.mustbe.consulo.csharp.lang.psi.impl.stub.elementTypes;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpPropertyDeclarationImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.stub.CSharpPropertyStub;
import org.mustbe.consulo.dotnet.psi.DotNetPropertyDeclaration;
import org.mustbe.consulo.dotnet.psi.stub.index.DotNetIndexKeys;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 21.12.13.
 */
public class CSharpPropertyElementType extends CSharpAbstractStubElementType<CSharpPropertyStub, DotNetPropertyDeclaration>
{
	public CSharpPropertyElementType()
	{
		super("PROPERTY_DECLARATION");
	}

	@Override
	public DotNetPropertyDeclaration createPsi(@NotNull ASTNode astNode)
	{
		return new CSharpPropertyDeclarationImpl(astNode);
	}

	@Override
	public DotNetPropertyDeclaration createPsi(@NotNull CSharpPropertyStub cSharpPropertyStub)
	{
		return new CSharpPropertyDeclarationImpl(cSharpPropertyStub);
	}

	@Override
	public CSharpPropertyStub createStub(@NotNull DotNetPropertyDeclaration dotNetPropertyDeclaration, StubElement stubElement)
	{
		return new CSharpPropertyStub(stubElement, StringRef.fromNullableString(dotNetPropertyDeclaration.getName()),
				StringRef.fromNullableString(dotNetPropertyDeclaration.getPresentableParentQName()));
	}

	@Override
	public void serialize(@NotNull CSharpPropertyStub cSharpPropertyStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(cSharpPropertyStub.getName());
		stubOutputStream.writeName(cSharpPropertyStub.getParentQName());
	}

	@NotNull
	@Override
	public CSharpPropertyStub deserialize(@NotNull StubInputStream stubInputStream, StubElement stubElement) throws IOException
	{
		StringRef name = stubInputStream.readName();
		StringRef parentQName = stubInputStream.readName();
		return new CSharpPropertyStub(stubElement, name, parentQName);
	}

	@Override
	public void indexStub(@NotNull CSharpPropertyStub cSharpPropertyStub, @NotNull IndexSink indexSink)
	{
		String name = cSharpPropertyStub.getName();
		if(!StringUtil.isEmpty(name))
		{
			indexSink.occurrence(DotNetIndexKeys.PROPERTY_INDEX, name);
		}
	}
}