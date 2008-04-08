/********************************************************************
    Copyright (c) 2000-2008 Steven E. Hugg.

    This file is part of FLCore.

    FLCore is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FLCore is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FLCore.  If not, see <http://www.gnu.org/licenses/>.
*********************************************************************/
package com.fasterlight.io;


public class IFFChunk
{
	int type, len;
	int startpos;

	public IFFChunk(int type, int len, int curpos)
	{
		this.type = type;
		this.len = len;
		this.startpos = curpos;
	}

	public int getTypeInt()
	{
		return type;
	}

	public String getTypeStr()
	{
		return IFFReader.int2str4(type);
	}

	public int getLength()
	{
		return len;
	}

	public String toString()
	{
		if ((type & 0xff000000) != 0)
			return "[" + getTypeStr() + "," + getLength() + "]";
		else
			return "[0x" + Integer.toString(getTypeInt(),16) + "," + getLength() + "]";
	}

}
