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

package org.mustbe.consulo.msil.lang.psi.impl.elementType.stub;

import org.mustbe.consulo.dotnet.resolve.DotNetPsiSearcher;
import org.mustbe.consulo.msil.lang.psi.MsilUserType;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilReferenceTypeStub extends StubBase<MsilUserType>
{
	private final DotNetPsiSearcher.TypeResoleKind myTypeResoleKind;
	private final StringRef myReferenceText;
	private final StringRef myNestedClassText;

	public MsilReferenceTypeStub(
			StubElement parent,
			IStubElementType elementType,
			DotNetPsiSearcher.TypeResoleKind typeResoleKind,
			String referenceText,
			String nestedClassText)
	{
		super(parent, elementType);
		myTypeResoleKind = typeResoleKind;
		myReferenceText = StringRef.fromNullableString(referenceText);
		myNestedClassText = StringRef.fromNullableString(nestedClassText);
	}

	public MsilReferenceTypeStub(
			StubElement parent,
			IStubElementType elementType,
			DotNetPsiSearcher.TypeResoleKind typeResoleKind,
			StringRef referenceText,
			StringRef nestedClassText)
	{
		super(parent, elementType);
		myTypeResoleKind = typeResoleKind;
		myReferenceText = referenceText;
		myNestedClassText = nestedClassText;
	}

	public String getReferenceText()
	{
		return StringRef.toString(myReferenceText);
	}

	public String getNestedClassText()
	{
		return StringRef.toString(myNestedClassText);
	}

	public DotNetPsiSearcher.TypeResoleKind getTypeResoleKind()
	{
		return myTypeResoleKind;
	}
}
