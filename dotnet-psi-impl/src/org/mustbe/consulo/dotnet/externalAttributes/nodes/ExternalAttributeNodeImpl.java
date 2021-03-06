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

package org.mustbe.consulo.dotnet.externalAttributes.nodes;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.externalAttributes.ExternalAttributeArgumentNode;
import org.mustbe.consulo.dotnet.externalAttributes.ExternalAttributeNode;
import com.intellij.util.SmartList;

/**
 * @author VISTALL
 * @since 03.09.14
 */
public class ExternalAttributeNodeImpl implements ExternalAttributeNode
{
	private List<ExternalAttributeArgumentNode> myArguments = new SmartList<ExternalAttributeArgumentNode>();
	private final String myName;

	public ExternalAttributeNodeImpl(String name)
	{
		myName = name;
	}

	public void addArgument(@NotNull ExternalAttributeArgumentNode attributeArgumentNode)
	{
		myArguments.add(attributeArgumentNode);
	}

	@NotNull
	@Override
	public List<ExternalAttributeArgumentNode> getArguments()
	{
		return myArguments;
	}

	@NotNull
	@Override
	public String getName()
	{
		return myName;
	}
}

