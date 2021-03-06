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

import org.mustbe.consulo.dotnet.psi.DotNetNativeType;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 22.05.14
 */
public class MsilNativeTypeStub extends StubBase<DotNetNativeType>
{
	private final IElementType myType;

	public MsilNativeTypeStub(StubElement parent, IStubElementType elementType, IElementType type)
	{
		super(parent, elementType);
		myType = type;
	}

	public IElementType getTypeElementType()
	{
		return myType;
	}
}
