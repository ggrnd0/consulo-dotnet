package org.mustbe.consulo.dotnet.module;

import java.util.List;

import org.consulo.annotations.InheritImmutable;
import org.consulo.module.extension.MutableModuleInheritableNamedPointer;
import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.DotNetTarget;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.Key;

/**
 * @author VISTALL
 * @since 03.02.14
 */
public interface MainConfigurationProfileEx<T extends MainConfigurationProfileEx<T>> extends ConfigurationProfileEx<T>
{
	public static final Key<MainConfigurationProfileEx> KEY = Key.create("main");

	@NotNull
	DotNetTarget getTarget();

	boolean isAllowDebugInfo();

	@InheritImmutable
	List<String> getVariables();

	@NotNull
	MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk();
}