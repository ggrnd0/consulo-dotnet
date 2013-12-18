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

package org.mustbe.consulo.csharp.lang.psi.impl.stub;

import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpStubElements;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpNamespaceDeclarationImpl;
import com.intellij.psi.stubs.NamedStubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author VISTALL
 * @since 15.12.13.
 */
public class CSharpNamespaceStub extends NamedStubBase<CSharpNamespaceDeclarationImpl>
{
	private StringRef myQName;

	public CSharpNamespaceStub(StubElement parent, @Nullable StringRef name, @Nullable StringRef qname)
	{
		super(parent, CSharpStubElements.NAMESPACE_DECLARATION, name);
		myQName = qname;
	}

	@Nullable
	public String getQName()
	{
		return StringRef.toString(myQName);
	}}