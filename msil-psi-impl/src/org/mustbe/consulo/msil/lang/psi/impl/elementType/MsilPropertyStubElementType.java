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
import org.mustbe.consulo.msil.lang.psi.MsilPropertyEntry;
import org.mustbe.consulo.msil.lang.psi.impl.MsilPropertyEntryImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilVariableEntryStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 21.05.14
 */
public class MsilPropertyStubElementType extends AbstractMsilStubElementType<MsilVariableEntryStub, MsilPropertyEntry>
{
	public MsilPropertyStubElementType()
	{
		super("MSIL_PROPERTY_ENTRY");
	}

	@NotNull
	@Override
	public MsilPropertyEntry createElement(@NotNull ASTNode astNode)
	{
		return new MsilPropertyEntryImpl(astNode);
	}

	@NotNull
	@Override
	public MsilPropertyEntry createPsi(@NotNull MsilVariableEntryStub msilPropertyEntryStub)
	{
		return new MsilPropertyEntryImpl(msilPropertyEntryStub, this);
	}

	@Override
	public MsilVariableEntryStub createStub(@NotNull MsilPropertyEntry msilPropertyEntry, StubElement stubElement)
	{
		String nameFromBytecode = msilPropertyEntry.getNameFromBytecode();
		return new MsilVariableEntryStub(stubElement, this, nameFromBytecode);
	}

	@Override
	public void serialize(@NotNull MsilVariableEntryStub msilPropertyEntryStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilPropertyEntryStub.getNameFromBytecode());
	}

	@NotNull
	@Override
	public MsilVariableEntryStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		StringRef ref = inputStream.readName();
		return new MsilVariableEntryStub(stubElement, this, ref);
	}
}
