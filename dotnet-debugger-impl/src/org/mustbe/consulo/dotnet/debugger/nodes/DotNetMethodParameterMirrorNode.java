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

package org.mustbe.consulo.dotnet.debugger.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.DotNetDebuggerUtil;
import org.mustbe.consulo.dotnet.psi.DotNetCodeBlockOwner;
import org.mustbe.consulo.dotnet.psi.DotNetParameter;
import org.mustbe.consulo.dotnet.psi.DotNetParameterListOwner;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.frame.XNavigatable;
import mono.debugger.LocalVariableOrParameterMirror;
import mono.debugger.MethodParameterMirror;
import mono.debugger.StackFrameMirror;
import mono.debugger.TypeMirror;
import mono.debugger.Value;
import mono.debugger.util.ImmutablePair;

/**
 * @author VISTALL
 * @since 11.04.14
 */
public class DotNetMethodParameterMirrorNode extends DotNetAbstractVariableMirrorNode
{
	private final MethodParameterMirror myParameter;
	private final StackFrameMirror myFrame;

	public DotNetMethodParameterMirrorNode(DotNetDebugContext debuggerContext, MethodParameterMirror parameter, StackFrameMirror frame)
	{
		super(debuggerContext, parameter.name(), frame.thread());
		myParameter = parameter;
		myFrame = frame;
	}

	@Override
	public void computeSourcePosition(@NotNull XNavigatable navigatable)
	{
		String sourcePath = myFrame.location().sourcePath();
		if(sourcePath == null)
		{
			return;
		}
		VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(sourcePath);
		if(fileByPath == null)
		{
			return;
		}
		PsiElement psiElement = DotNetDebuggerUtil.findPsiElement(myDebugContext.getProject(), fileByPath, myFrame.location().lineNumber() - 1);
		if(psiElement == null)
		{
			return;
		}
		DotNetCodeBlockOwner codeBlockOwner = PsiTreeUtil.getParentOfType(psiElement, DotNetCodeBlockOwner.class);
		if(codeBlockOwner == null)
		{
			return;
		}

		// search parameterlist owner
		DotNetParameterListOwner parameterListOwner = PsiTreeUtil.getParentOfType(codeBlockOwner, DotNetParameterListOwner.class, false);
		if(parameterListOwner == null)
		{
			return;
		}

		DotNetParameter[] psiParameters = parameterListOwner.getParameters();

		MethodParameterMirror[] parameterMirrors = myFrame.location().method().parameters();

		int i1 = ArrayUtil.indexOf(parameterMirrors, myParameter);

		DotNetParameter parameter = psiParameters[i1];
		PsiElement nameIdentifier = parameter.getNameIdentifier();
		if(nameIdentifier == null)
		{
			return;
		}
		navigatable.setSourcePosition(XDebuggerUtil.getInstance().createPositionByOffset(parameter.getContainingFile().getVirtualFile(),
				nameIdentifier.getTextOffset()));
	}

	@NotNull
	@Override
	public TypeMirror getTypeOfVariable()
	{
		return myParameter.type();
	}

	@Nullable
	@Override
	public Value<?> getValueOfVariable()
	{
		return myFrame.localOrParameterValue(myParameter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValueForVariable(@NotNull Value<?> value)
	{
		myFrame.setLocalOrParameterValues(new ImmutablePair<LocalVariableOrParameterMirror, Value<?>>(myParameter, value));
	}
}
