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

package test;

import org.mustbe.consulo.csharp.cfs.lang.lexer.CfsLexer;
import com.intellij.psi.tree.IElementType;

/**
 * @author VISTALL
 * @since 31.08.14
 */
public class MyLexerTest
{
	public static void main(String[] args)
	{
		CfsLexer lexer = new CfsLexer();
		lexer.start("{0,10:dd} {1} {2} {3} {-4} {5} ff hello world");

		System.out.println("TEST " + lexer.getBufferSequence());
		IElementType tokenType;
		while((tokenType = lexer.getTokenType()) != null)
		{
			System.out.println(tokenType + " : '" + lexer.getTokenText() + "'");
			lexer.advance();
		}

		lexer.start("{0,-10:dd} {1} {2} {3} {-4} {5} ff hello world");
		System.out.println("-----------------------------------------------");
		System.out.println("TEST " + lexer.getBufferSequence());
		while((tokenType = lexer.getTokenType()) != null)
		{
			System.out.println(tokenType + " : '" + lexer.getTokenText() + "'");
			lexer.advance();
		}
	}
}
