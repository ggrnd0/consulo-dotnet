package org.mustbe.consulo.dotnet.resolve;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.dotnet.psi.DotNetQualifiedElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;

/**
 * @author VISTALL
 * @since 11.02.14
 */
public interface DotNetNamespaceAsElement extends DotNetQualifiedElement
{
	@NotNull
	Collection<? extends PsiElement> getChildren(@NotNull GlobalSearchScope globalSearchScope, boolean withChildNamespaces);

	@NotNull
	PsiElement[] findChildren(@NotNull String name, @NotNull GlobalSearchScope globalSearchScope, boolean withChildNamespaces);
}
