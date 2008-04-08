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
package com.fasterlight.glout;

import java.awt.Point;

/**
  */
public class GLOMouseEvent
extends GLOEvent
{
	public int x,y;

	/**
	  * Creates a mouse event that takes place at a given x and y.
	  * Coordinates are scaled to the GLOContext's world coords.
	  */
	public GLOMouseEvent(GLOContext ctx, int x, int y)
	{
		super(ctx);
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public Point getOffset()
	{
		return new Point(x-ctx.mousex, y-ctx.mousey);
	}

	public String toString()
	{
		return getClass().getName() + ":x=" + x + ",y=" + y;
	}
}
