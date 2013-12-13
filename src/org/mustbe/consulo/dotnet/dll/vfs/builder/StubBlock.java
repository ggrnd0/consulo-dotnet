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

package org.mustbe.consulo.dotnet.dll.vfs.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 12.12.13.
 */
public class StubBlock
{
	private String myStartText;
	private String myInnerText;
	private char[] myIndents;

	private List<StubBlock> myBlocks = new ArrayList<StubBlock>(2);

	public StubBlock(String startText, String innerText, char[] indents)
	{
		myStartText = startText;
		myInnerText = innerText;
		myIndents = indents;
	}

	public List<StubBlock> getBlocks()
	{
		return myBlocks;
	}

	public char[] getIndents()
	{
		return myIndents;
	}

	public String getStartText()
	{
		return myStartText;
	}

	public String getInnerText()
	{
		return myInnerText;
	}
}
