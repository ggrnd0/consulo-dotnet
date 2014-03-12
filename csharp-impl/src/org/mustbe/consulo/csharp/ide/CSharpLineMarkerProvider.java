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

package org.mustbe.consulo.csharp.ide;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.csharp.lang.psi.CSharpTokens;
import org.mustbe.consulo.csharp.lang.psi.CSharpTypeDeclaration;
import org.mustbe.consulo.csharp.lang.psi.CSharpModifier;
import org.mustbe.consulo.dotnet.psi.DotNetTypeDeclaration;
import org.mustbe.consulo.dotnet.psi.search.searches.ClassInheritorsSearch;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.search.PsiElementProcessorAdapter;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Function;
import lombok.val;

/**
 * @author VISTALL
 * @since 29.12.13.
 */
public class CSharpLineMarkerProvider implements LineMarkerProvider, DumbAware
{
	@Nullable
	@Override
	public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element)
	{
		return null;
	}

	@Override
	public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> lineMarkerInfos)
	{
		ApplicationManager.getApplication().assertReadAccessAllowed();

		if(elements.isEmpty() || DumbService.getInstance(elements.get(0).getProject()).isDumb())
		{
			return;
		}

		for(PsiElement psiElement : elements)
		{
			if(psiElement.getParent() instanceof CSharpTypeDeclaration && psiElement.getNode().getElementType() == CSharpTokens.IDENTIFIER)
			{
				CSharpTypeDeclaration parent = (CSharpTypeDeclaration) psiElement.getParent();
				boolean b = hasChild(parent);
				if(b)
				{
					val icon = parent.isInterface() ? AllIcons.Gutter.ImplementedMethod : AllIcons.Gutter.OverridenMethod;
					val lineMarkerInfo = new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), icon, Pass.UPDATE_OVERRIDEN_MARKERS,
							new Function<PsiElement, String>()
					{
						@Override
						public String fun(PsiElement element)
						{
							return "Searching for overriding";
						}
					}, new GutterIconNavigationHandler<PsiElement>()
					{
						@Override
						public void navigate(MouseEvent mouseEvent, PsiElement element)
						{
							val typeDeclaration = (DotNetTypeDeclaration) element.getParent();
							val collectProcessor = new PsiElementProcessor.CollectElements<DotNetTypeDeclaration>();
							if(!ProgressManager.getInstance().runProcessWithProgressSynchronously(new Runnable()
							{
								@Override
								public void run()
								{
									ClassInheritorsSearch.search(typeDeclaration,
											true).forEach(new PsiElementProcessorAdapter<DotNetTypeDeclaration>(collectProcessor));
								}
							}, "Searching for overriding", true, typeDeclaration.getProject(), (JComponent) mouseEvent.getComponent()))
							{
								return;
							}

							DotNetTypeDeclaration[] inheritors = collectProcessor.toArray(DotNetTypeDeclaration.EMPTY_ARRAY);

							JBPopup popup = NavigationUtil.getPsiElementPopup(inheritors, "Open types (" + inheritors.length + " items)");
							popup.show(new RelativePoint(mouseEvent));
						}
					}, GutterIconRenderer.Alignment.LEFT
					);
					lineMarkerInfos.add(lineMarkerInfo);
				}
			}
		}
	}

	private static boolean hasChild(final CSharpTypeDeclaration type)
	{
		if(type.hasModifier(CSharpModifier.SEALED))
		{
			return false;
		}

		return ClassInheritorsSearch.search(type, false).findFirst() != null;
	}
}
