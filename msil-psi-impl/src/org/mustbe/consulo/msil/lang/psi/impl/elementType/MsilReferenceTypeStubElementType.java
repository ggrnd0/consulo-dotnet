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
import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.msil.lang.psi.MsilUserType;
import org.mustbe.consulo.msil.lang.psi.impl.MsilUserTypeImpl;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilReferenceTypeStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeStubElementType extends AbstractMsilStubElementType<MsilReferenceTypeStub, MsilUserType>
{
	public MsilReferenceTypeStubElementType()
	{
		super("MSIL_USER_TYPE");
	}

	@NotNull
	@Override
	public MsilUserType createElement(@NotNull ASTNode astNode)
	{
		return new MsilUserTypeImpl(astNode);
	}

	@NotNull
	@Override
	public MsilUserType createPsi(@NotNull MsilReferenceTypeStub msilReferenceTypeStub)
	{
		return new MsilUserTypeImpl(msilReferenceTypeStub, this);
	}

	@Override
	public MsilReferenceTypeStub createStub(@NotNull MsilUserType dotNetReferenceType, StubElement stubElement)
	{
		DotNetPsiSearcher.TypeResoleKind typeResoleKind = dotNetReferenceType.getTypeResoleKind();
		String referenceText = dotNetReferenceType.getReferenceText();
		String nestedClassName = dotNetReferenceType.getNestedClassName();
		return new MsilReferenceTypeStub(stubElement, this, typeResoleKind, referenceText, nestedClassName);
	}

	@Override
	public void serialize(
			@NotNull MsilReferenceTypeStub msilReferenceTypeStub, @NotNull StubOutputStream stubOutputStream) throws IOException
	{
		stubOutputStream.writeByte(msilReferenceTypeStub.getTypeResoleKind().ordinal());
		stubOutputStream.writeName(msilReferenceTypeStub.getReferenceText());
		stubOutputStream.writeName(msilReferenceTypeStub.getNestedClassText());
	}

	@NotNull
	@Override
	public MsilReferenceTypeStub deserialize(
			@NotNull StubInputStream inputStream, StubElement stubElement) throws IOException
	{
		DotNetPsiSearcher.TypeResoleKind typeResoleKind = DotNetPsiSearcher.TypeResoleKind.VALUES[inputStream.readByte()];
		StringRef referenceText = inputStream.readName();
		StringRef nestedClassText = inputStream.readName();
		return new MsilReferenceTypeStub(stubElement, this, typeResoleKind, referenceText, nestedClassText);
	}
}
