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

package org.mustbe.consulo.msil.lang.psi.impl;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameter;
import org.mustbe.consulo.dotnet.psi.DotNetGenericParameterList;
import org.mustbe.consulo.msil.lang.psi.MsilStubElements;
import org.mustbe.consulo.msil.lang.psi.impl.elementType.stub.MsilGenericParameterListStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;

/**
 * @author VISTALL
 * @since 23.05.14
 */
public class MsilGenericParameterListImpl extends MsilStubElementImpl<MsilGenericParameterListStub> implements DotNetGenericParameterList
{
	public MsilGenericParameterListImpl(@NotNull ASTNode node)
	{
		super(node);
	}

	public MsilGenericParameterListImpl(@NotNull MsilGenericParameterListStub stub, @NotNull IStubElementType nodeType)
	{
		super(stub, nodeType);
	}

	@Override
	public void accept(MsilVisitor visitor)
	{

	}

	@NotNull
	@Override
	public DotNetGenericParameter[] getParameters()
	{
		return getStubOrPsiChildren(MsilStubElements.GENERIC_PARAMETER, DotNetGenericParameter.ARRAY_FACTORY);
	}

	@Override
	public int getGenericParametersCount()
	{
		return getParameters().length;
	}
}
