/* MBEL: The Microsoft Bytecode Engineering Library
 * Copyright (C) 2003 The University of Arizona
 * http://www.cs.arizona.edu/mbel/license.html
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */


package edu.arizona.cs.mbel.instructions;

import java.io.IOException;

import edu.arizona.cs.mbel.mbel.ModuleParser;

/**
 * Exclusive OR instruction.<br>
 * Stack transition: <br>
 * ..., value1, value2 --> result
 *
 * @author Michael Stepp
 */
public class XOR extends Instruction
{
	public static final int XOR = 0x61;
	protected static final int OPCODE_LIST[] = {XOR};

	/**
	 * Creates a new XOR object
	 */
	public XOR() throws InstructionInitException
	{
		super(XOR, OPCODE_LIST);
	}

	public String getName()
	{
		return "xor";
	}

	public XOR(int opcode, ModuleParser parse) throws IOException, InstructionInitException
	{
		super(opcode, OPCODE_LIST);
	}

	public boolean equals(Object o)
	{
		return (super.equals(o) && (o instanceof XOR));
	}
}
