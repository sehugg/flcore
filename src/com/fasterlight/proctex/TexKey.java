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
package com.fasterlight.proctex;

public class TexKey
{
	public int x,y,level;

	//

	public TexKey()
	{
	}

	public TexKey(int x, int y, int level)
	{
		this.x = x;
		this.y = y;
		this.level = level;
	}

	public TexKey(TexKey key)
	{
		this(key.x, key.y, key.level);
	}

	public void set(int x, int y, int level)
	{
		this.x = x;
		this.y = y;
		this.level = level;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof TexKey))
			return false;
		TexKey tk = (TexKey)o;
		return (tk.x==x) && (tk.y==y) && (tk.level==level);
	}

	public int hashCode()
	{
		return x ^ (y<<8) ^ (level<<16);
	}

	public String toString()
	{
		return level + "-" + x + "-" + y;
	}

}

