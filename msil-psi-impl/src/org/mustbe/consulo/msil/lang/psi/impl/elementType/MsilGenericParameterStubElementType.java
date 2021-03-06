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

package org.mustbe.consulo.msil.lang.psi.impl.elementType;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.msil.lang.psi.impl.MsilGenericParameterImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilGenericParameterStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilGenericParameterStubElementType extends AbstractMsilStubElementType<MsilGenericParameterStub, DotNetGenericParameter>
{
	public MsilGenericParameterStubElementType()
	{
		super("MSIL_GENERIC_PARAMETER");
	}

	@NotNull
	@Override
	public DotNetGenericParameter createElement(@NotNull ASTNode astNode)
	{
		return new MsilGenericParameterImpl(astNode);
	}

	@NotNull
	@Override
	public DotNetGenericParameter createPsi(@NotNull MsilGenericParameterStub msilGenericParameterStub)
	{
		return new MsilGenericParameterImpl(msilGenericParameterStub, this);
	}

	@Override
	public MsilGenericParameterStub createStub(@NotNull DotNetGenericParameter parameter, StubElement stubElement)
	{
		String name = parameter.getName();
		return new MsilGenericParameterStub(stubElement, this, name);
	}

	@Override
	public void serialize(@NotNull MsilGenericParameterStub msilGenericParameterStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilGenericParameterStub.getName());
	}

	@NotNull
	@Override
	public MsilGenericParameterStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef ref = inputStream.readName();
		return new MsilGenericParameterStub(stubElement, this, ref);
	}
}
