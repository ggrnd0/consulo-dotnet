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
import org.mustbe.consulo.msil.lang.psi.MsilFieldEntry;
import org.mustbe.consulo.msil.lang.psi.impl.MsilFieldEntryImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilVariableEntryStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilFieldStubElementType extends AbstractMsilStubElementType<MsilVariableEntryStub,MsilFieldEntry>
{
	public MsilFieldStubElementType()
	{
		super("MSIL_FIELD_ENTRY");
	}

	@NotNull
	@Override
	public MsilFieldEntry createElement(@NotNull ASTNode astNode)
	{
		return new MsilFieldEntryImpl(astNode);
	}

	@NotNull
	@Override
	public MsilFieldEntry createPsi(@NotNull MsilVariableEntryStub msilVariableEntryStub)
	{
		return new MsilFieldEntryImpl(msilVariableEntryStub, this);
	}

	@Override
	public MsilVariableEntryStub createStub(@NotNull MsilFieldEntry msilFieldEntry, StubElement stubElement)
	{
		String name = msilFieldEntry.getNameFromBytecode();
		return new MsilVariableEntryStub(stubElement, this, name);
	}

	@Override
	public void serialize(@NotNull MsilVariableEntryStub msilVariableEntryStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeName(msilVariableEntryStub.getNameFromBytecode());
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
