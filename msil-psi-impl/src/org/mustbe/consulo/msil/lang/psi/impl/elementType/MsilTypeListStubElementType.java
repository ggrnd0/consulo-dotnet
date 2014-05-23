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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetTypeList;
import org.mustbe.consulo.msil.lang.psi.impl.MsilTypeListImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilTypeListStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilTypeListStubElementType extends AbstractMsilStubElementType<MsilTypeListStub, DotNetTypeList>
{
	public MsilTypeListStubElementType(@NotNull @NonNls String debugName)
	{
		super(debugName);
	}

	@NotNull
	@Override
	public DotNetTypeList createPsi(@NotNull ASTNode astNode)
	{
		return new MsilTypeListImpl(astNode);
	}

	@NotNull
	@Override
	public DotNetTypeList createPsi(@NotNull MsilTypeListStub msilTypeListStub)
	{
		return new MsilTypeListImpl(msilTypeListStub, this);
	}

	@Override
	public MsilTypeListStub createStub(
			@NotNull DotNetTypeList dotNetTypeList, StubElement stubElement)
	{
		return new MsilTypeListStub(stubElement, this);
	}

	@Override
	public void serialize(
			@NotNull MsilTypeListStub msilTypeListStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{

	}

	@NotNull
	@Override
	public MsilTypeListStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		return new MsilTypeListStub(stubElement, this);
	}
}
