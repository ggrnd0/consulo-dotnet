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

import org.mustbe.consulo.msil.lang.psi.MsilParameter;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilParameterStub extends StubBase<MsilParameter>
{
	private final StringRef myName;

	public MsilParameterStub(StubElement parent, IStubElementType elementType, StringRef ref)
	{
		super(parent, elementType);
		myName = ref;
	}

	public MsilParameterStub(StubElement parent, IStubElementType elementType, String name)
	{
		super(parent, elementType);
		myName = StringRef.fromNullableString(name);
	}

	public String getName()
	{
		return StringRef.toString(myName);
	}
}
