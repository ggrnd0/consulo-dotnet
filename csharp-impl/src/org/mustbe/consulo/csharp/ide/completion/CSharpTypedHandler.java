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

package org.mustbe.consulo.csharp.ide.completion;

import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpFileImpl;
import org.mustbe.consulo.csharp.lang.psi.impl.source.CSharpReferenceExpressionImpl;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author VISTALL
 * @since 06.01.14.
 */
public class CSharpTypedHandler extends TypedHandlerDelegate
{
	@Override
	public Result beforeCharTyped(char c, Project project, Editor editor, PsiFile file, FileType fileType)
	{
		if(!(file instanceof CSharpFileImpl))
		{
			return Result.CONTINUE;
		}

		autoPopupMemberLookup(project, editor);
		return Result.CONTINUE;
	}

	private static void autoPopupMemberLookup(Project project, final Editor editor)
	{
		AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, new Condition<PsiFile>()
		{
			@Override
			public boolean value(final PsiFile file)
			{
				int offset = editor.getCaretModel().getOffset();

				PsiElement lastElement = file.findElementAt(offset - 1);
				if(lastElement == null)
				{
					return false;
				}

				final PsiElement prevSibling = PsiTreeUtil.prevVisibleLeaf(lastElement);
				if(prevSibling == null || ".".equals(prevSibling.getText()))
				{
					return false;
				}
				PsiElement parent = prevSibling;
				do
				{
					parent = parent.getParent();
				}
				while(parent instanceof CSharpReferenceExpressionImpl);

				return true;
			}
		});
	}
}
