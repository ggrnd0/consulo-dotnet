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

package org.mustbe.consulo.dotnet.module;

import java.util.Map;

import org.consulo.annotations.Immutable;
import org.consulo.util.pointers.Named;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.Key;

/**
 * @author VISTALL
 * @since 01.02.14
 */
public interface ConfigurationProfile extends Named
{
	boolean isActive();

	void setActive(boolean v);

	@NotNull
	<T extends ConfigurationProfileEx<T>> T getExtension(@NotNull Key<T> clazz);

	@NotNull
	@Immutable
	Map<String, ConfigurationProfileEx<?>> getExtensions();

	@NotNull
	ConfigurationProfile clone();
}