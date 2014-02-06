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

package org.mustbe.consulo.dotnet.dll.vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.consulo.lombok.annotations.Logger;
import org.consulo.vfs.ArchiveFileSystemBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.dll.DotNetDllFileType;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.vfs.ArchiveEntry;
import com.intellij.openapi.vfs.ArchiveFile;
import com.intellij.openapi.vfs.ArchiveFileSystem;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.impl.archive.ArchiveHandler;
import com.intellij.openapi.vfs.impl.archive.ArchiveHandlerBase;
import com.intellij.util.containers.EmptyIterator;
import com.intellij.util.messages.MessageBus;
import edu.arizona.cs.mbel.mbel.ModuleParser;
import lombok.val;

/**
 * @author VISTALL
 * @since 28.11.13.
 */
@Logger
public class DotNetArchiveFileSystem extends ArchiveFileSystemBase implements ApplicationComponent
{
	@NotNull
	public static DotNetArchiveFileSystem getInstance()
	{
		return (DotNetArchiveFileSystem) VirtualFileManager.getInstance().getFileSystem(DotNetDllFileType.PROTOCOL);
	}

	public DotNetArchiveFileSystem(MessageBus bus)
	{
		super(bus);
	}

	@Override
	public ArchiveHandler createHandler(ArchiveFileSystem archiveFileSystem, String s)
	{
		return new ArchiveHandlerBase(archiveFileSystem, s)
		{
			@Nullable
			@Override
			protected ArchiveFile createArchiveFile()
			{
				val originalFile = getOriginalFile();
				try
				{
					File mirrorFile = getMirrorFile(originalFile);
					ModuleParser parser = new ModuleParser(new FileInputStream(mirrorFile));

					return new DotNetArchiveFile(parser.parseModule(), mirrorFile.lastModified());
				}
				catch(Exception e)
				{
					LOGGER.warn(originalFile.getPath(), e);
				}

				return new ArchiveFile()
				{
					@Nullable
					@Override
					public ArchiveEntry getEntry(String s)
					{
						return null;
					}

					@Nullable
					@Override
					public InputStream getInputStream(ArchiveEntry archiveEntry) throws IOException
					{
						return null;
					}

					@NotNull
					@Override
					public Iterator<? extends ArchiveEntry> entries()
					{
						return EmptyIterator.getInstance();
					}

					@Override
					public int getSize()
					{
						return 0;
					}
				};
			}
		};
	}

	@Override
	public void setNoCopyJarForPath(String s)
	{

	}

	@NotNull
	@Override
	public String getProtocol()
	{
		return DotNetDllFileType.PROTOCOL;
	}

	@Override
	public void initComponent()
	{

	}

	@Override
	public void disposeComponent()
	{

	}

	@NotNull
	@Override
	public String getComponentName()
	{
		return "DotNetArchiveFileSystem";
	}
}
