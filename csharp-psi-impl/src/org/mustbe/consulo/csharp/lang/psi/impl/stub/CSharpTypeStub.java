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

package org.mustbe.consulo.csharp.lang.psi.impl.stub;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public class CSharpTypeStub extends MemberStub<CSharpTypeDeclaration>
{
	public static final byte TYPE = 0;
	public static final byte STRUCT = 1;
	public static final byte INTERFACE = 1;
	public static final byte ENUM = 2;

	private final byte myType;

	public CSharpTypeStub(StubElement parent, @Nullable StringRef name, @Nullable StringRef parentQName, int modifierMask, byte type)
	{
		super(parent, CSharpStubElements.TYPE_DECLARATION, name, parentQName, modifierMask, 0);
		myType = type;
	}

	public byte getType()
	{
		return myType;
	}

	public static byte getType(CSharpTypeDeclaration typeDeclaration)
	{
		if(typeDeclaration.isInterface())
		{
			return INTERFACE;
		}
		else if(typeDeclaration.isEnum())
		{
			return ENUM;
		}
		else if(typeDeclaration.isStruct())
		{
			return STRUCT;
		}
		else
		{
			return TYPE;
		}
	}
}
