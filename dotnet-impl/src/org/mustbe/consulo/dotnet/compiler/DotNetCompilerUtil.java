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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.DotNetTarget;
import org.mustbe.consulo.dotnet.module.extension.DotNetModuleExtension;
import org.mustbe.consulo.dotnet.module.roots.DotNetLibraryOrderEntryImpl;
import org.mustbe.consulo.dotnet.module.roots.DotNetRootPolicy;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.types.BinariesOrderRootType;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.util.ArchiveVfsUtil;
import lombok.val;

/**
 * @author VISTALL
 * @since 02.01.14.
 */
public class DotNetCompilerUtil
{
	@NotNull
	public static Set<File> collectDependencies(@NotNull final Module module, @NotNull DotNetTarget target, final boolean includeStdLibraries)
	{
		return collectDependencies(module, target, false, includeStdLibraries);
	}

	@NotNull
	public static Set<File> collectDependencies(@NotNull final Module module, @NotNull final DotNetTarget target, final boolean debugSymbol,
			final boolean includeStdLibraries)
	{
		val set = new HashSet<File>();

		val processed = new HashSet<Object>();

		processed.add(module);

		ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
		moduleRootManager.processOrder(new DotNetRootPolicy<Object>()
		{
			@Override
			public Object visitDotNetLibrary(DotNetLibraryOrderEntryImpl orderEntry, Object value)
			{
				if(!includeStdLibraries)
				{
					return null;
				}
				collectFromRoot(orderEntry);
				return null;
			}

			@Override
			public Object visitLibraryOrderEntry(LibraryOrderEntry libraryOrderEntry, Object value)
			{
				Library library = libraryOrderEntry.getLibrary();
				if(library == null || processed.contains(library))
				{
					return null;
				}

				processed.add(library);

				collectFromRoot(libraryOrderEntry);
				return null;
			}

			private void collectFromRoot(OrderEntry orderEntry)
			{
				for(VirtualFile virtualFile : orderEntry.getFiles(BinariesOrderRootType.getInstance()))
				{
					VirtualFile virtualFileForArchive = ArchiveVfsUtil.getVirtualFileForArchive(virtualFile);
					if(virtualFileForArchive != null)
					{
						if(Comparing.equal(virtualFileForArchive.getExtension(), target.getExtension()))
						{
							set.add(VfsUtil.virtualToIoFile(virtualFileForArchive));
						}
					}
				}
			}

			@Override
			public Object visitModuleOrderEntry(ModuleOrderEntry moduleOrderEntry, Object value)
			{
				Module depModule = moduleOrderEntry.getModule();
				if(depModule == null)
				{
					return null;
				}

				if(processed.contains(depModule))
				{
					return null;
				}

				processed.add(depModule);

				DotNetModuleExtension dependencyExtension = ModuleUtilCore.getExtension(depModule, DotNetModuleExtension.class);
				if(dependencyExtension != null && dependencyExtension.getTarget() == target)
				{
					set.add(new File(DotNetMacroUtil.expandOutputFile(dependencyExtension)));
					if(debugSymbol)
					{
						set.add(new File(DotNetMacroUtil.expandOutputFile(dependencyExtension, true)));
					}

					set.addAll(collectDependencies(depModule, target, false));
				}

				return null;
			}
		}, null);

		return set;
	}

}
