package org.mustbe.consulo.dotnet.debugger.nodes.logicView.enumerator;

import java.util.Iterator;

import org.mustbe.consulo.dotnet.debugger.DotNetDebugContext;
import org.mustbe.consulo.dotnet.debugger.nodes.logicView.MdbDebuggerUtil;
import com.intellij.openapi.util.Pair;
import mono.debugger.BooleanValueMirror;
import mono.debugger.InvokeFlags;
import mono.debugger.MethodMirror;
import mono.debugger.ThreadMirror;
import mono.debugger.ThrowValueException;
import mono.debugger.TypeMirror;
import mono.debugger.Value;

/**
 * @author VISTALL
 * @since 20.09.14
 */
public class DictionaryEnumeratorAsIterator implements Iterator<Pair<Value<?>, Value<?>>>
{
	private ThreadMirror myThreadMirror;
	private Value<?> myValue;
	private MethodMirror myMoveNextMethod;
	private MethodMirror myKeyMethod;
	private MethodMirror myValueMethod;

	public DictionaryEnumeratorAsIterator(ThreadMirror threadMirror, Value<?> value, DotNetDebugContext debugContext) throws CantCreateException
	{
		myThreadMirror = threadMirror;
		myValue = value;

		TypeMirror typeMirror = myValue.type();

		myMoveNextMethod = MdbDebuggerUtil.findMethod("MoveNext", typeMirror);
		if(myMoveNextMethod == null)
		{
			throw new CantCreateException();
		}

		myKeyMethod = MdbDebuggerUtil.findGetterForProperty("Key", typeMirror);
		if(myKeyMethod == null)
		{
			throw new CantCreateException();
		}
		myValueMethod = MdbDebuggerUtil.findGetterForProperty("Value", typeMirror);
		if(myValueMethod == null)
		{
			throw new CantCreateException();
		}
	}

	@Override
	public boolean hasNext()
	{
		try
		{
			Value<?> invoke = myMoveNextMethod.invoke(myThreadMirror, InvokeFlags.DISABLE_BREAKPOINTS, myValue);
			return invoke instanceof BooleanValueMirror && ((BooleanValueMirror) invoke).value();
		}
		catch(ThrowValueException e)
		{
			//Value<?> throwExceptionValue = e.getThrowExceptionValue();
			//TypeMirror type = throwExceptionValue.type();

			//MethodMirror toString = type.findMethodByName("ToString", true);
			//System.out.println(toString.invoke(myThreadMirror, throwExceptionValue));
			return false;
		}
	}

	@Override
	public Pair<Value<?>, Value<?>> next()
	{
		try
		{
			Value<?> keyValue = myKeyMethod.invoke(myThreadMirror, InvokeFlags.DISABLE_BREAKPOINTS, myValue);
			Value<?> valueValue = myValueMethod.invoke(myThreadMirror, InvokeFlags.DISABLE_BREAKPOINTS, myValue);
			return Pair.<Value<?>, Value<?>>create(keyValue, valueValue);
		}
		catch(ThrowValueException e)
		{
			//Value<?> throwExceptionValue = e.getThrowExceptionValue();
			//TypeMirror type = throwExceptionValue.type();

			//MethodMirror toString = type.findMethodByName("ToString", true);
			//System.out.println(toString.invoke(myThreadMirror, throwExceptionValue));
		}
		return Pair.create(null, null);
	}

	@Override
	public void remove()
	{

	}
}
