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

package org.mustbe.consulo.csharp.ide.liveTemplates;

import org.jetbrains.annotations.Nullable;
import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;
import com.intellij.util.ArrayUtil;

/**
 * @author VISTALL
 * @since 11.06.14
 */
public class CSharpLiveTemplatesProvider implements DefaultLiveTemplatesProvider
{
	@Override
	public String[] getDefaultLiveTemplateFiles()
	{
		return new String[] {"/liveTemplates/output"};
	}

	@Nullable
	@Override
	public String[] getHiddenLiveTemplateFiles()
	{
		return ArrayUtil.EMPTY_STRING_ARRAY;
	}
}