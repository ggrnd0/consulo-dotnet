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

package org.mustbe.consulo.dotnet.compiler;

import org.consulo.compiler.ModuleCompilerPathsManager;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.module.ConfigurationProfile;
import org.mustbe.consulo.dotnet.module.MainConfigurationProfileEx;
import org.mustbe.consulo.roots.impl.ProductionContentFolderTypeProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;

/**
 * @author VISTALL
 * @since 27.11.13.
 */
public class DotNetMacros
{
	public static final String MODULE_OUTPUT_DIR = "${module-output-dir}";

	public static final String CONFIGURATION = "${configuration}";

	public static final String MODULE_NAME = "${module-name}";

	public static final String OUTPUT_FILE_EXT = "${output-file-ext}";

	@NotNull
	public static String extract(@NotNull Module module, ConfigurationProfile profile)
	{
		ModuleCompilerPathsManager compilerPathsManager = ModuleCompilerPathsManager.getInstance(module);

		MainConfigurationProfileEx mainConfigurationProfileEx = profile.getExtension(MainConfigurationProfileEx.KEY);
		String fileExtension = null;
		switch(mainConfigurationProfileEx.getTarget())
		{
			case EXECUTABLE:
				fileExtension = "exe";
				break;
			case LIBRARY:
				fileExtension = "dll";
				break;
		}

		String path = DotNetCompilerConfiguration.getInstance(module.getProject()).getOutputFile();
		path = StringUtil.replace(path, MODULE_OUTPUT_DIR, compilerPathsManager.getCompilerOutputUrl(ProductionContentFolderTypeProvider.getInstance
				()));

		path = StringUtil.replace(path, CONFIGURATION, profile.getName());
		path = StringUtil.replace(path, MODULE_NAME, module.getName());
		path = StringUtil.replace(path, OUTPUT_FILE_EXT, fileExtension);
		return FileUtil.toSystemDependentName(VfsUtil.urlToPath(path));
	}

	public static String getModuleOutputDirUrl(@NotNull Module module, ConfigurationProfile p)
	{
		String path = DotNetCompilerConfiguration.getInstance(module.getProject()).getOutputDir();
		return extractLikeWorkDir(module, path, p, true);
	}

	public static String extractLikeWorkDir(@NotNull Module module, @NotNull String path, ConfigurationProfile p, boolean url)
	{
		ModuleCompilerPathsManager compilerPathsManager = ModuleCompilerPathsManager.getInstance(module);
		String compilerOutputUrl = compilerPathsManager.getCompilerOutputUrl(ProductionContentFolderTypeProvider.getInstance());

		path = StringUtil.replace(path, MODULE_OUTPUT_DIR, url ? compilerOutputUrl : VfsUtil.urlToPath(compilerOutputUrl));
		path = StringUtil.replace(path, CONFIGURATION, p.getName());
		path = StringUtil.replace(path, MODULE_NAME, module.getName());
		return path;
	}
}