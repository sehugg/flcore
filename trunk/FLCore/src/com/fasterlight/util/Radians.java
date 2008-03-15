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
package com.fasterlight.util;

import com.fasterlight.vecmath.*;

public class Radians
{
	public static final int IR_PI = 32768;
	public static final float M_PI = 3.141592f;

	public static void rotate(Tuple2f v, float rad)
	{
		float s1 = (float)Math.sin(rad);
		float c1 = (float)Math.cos(rad);
		float xx = v.x*c1 + v.y*s1;
		float yy = v.y*c1 - v.x*s1;
		v.set(xx,yy);
	}
	public static void rotate(Tuple3f v, float rad)
	{
		float s1 = (float)Math.sin(rad);
		float c1 = (float)Math.cos(rad);
		float xx = v.x*c1 + v.y*s1;
		float yy = v.y*c1 - v.x*s1;
		v.set(xx,yy,v.z);
	}
	public static float shortToRad(short s)
	{
		return s*(M_PI/IR_PI);
	}
	public static short radToShort(float rad)
	{
		return (short)(rad*(IR_PI/M_PI));
	}
}
