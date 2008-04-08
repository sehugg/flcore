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

import com.fasterlight.vecmath.Vector3f;

public class Cube6f
{
	public float x1,y1,z1,x2,y2,z2;

	public Cube6f(Vector3f a, Vector3f b)
	{
		x1 = a.x;
		y1 = a.y;
		z1 = a.z;
		x2 = b.x;
		y2 = b.y;
		z2 = b.z;
	}
}
