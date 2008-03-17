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

import java.io.Serializable;

import com.fasterlight.vecmath.*;

/**
  * A plane object represented by a normal and a distance
  */
public class Plane4f
extends Vector4f
implements Serializable
{
	public Plane4f(Vector4f v)
	{
		super(v);
	}

	public Plane4f(Vector3f nml, float d)
	{
		super(nml.x, nml.y, nml.z, d);
	}

	public Plane4f(float x, float y, float z, float w)
	{
		super(x,y,z,w);
	}

	public Plane4f(Vector3f nml, Vector3f p)
	{
		set(nml, p);
	}

	public Plane4f(Vector3f p1, Vector3f p2, Vector3f p3)
	{
		set(p1,p2,p3);
	}

	public void set(Vector3f p1, Vector3f p2, Vector3f p3)
	{
		// find normal
		Vector3f nml = new Vector3f(p2);
		nml.sub(p1);
		Vector3f nml2 = new Vector3f(p3);
		nml2.sub(p1);
		nml.cross(nml, nml2);
		set(nml, p1);
	}

	public void set(Vector3f nml, Vector3f p)
	{
		this.x = nml.x;
		this.y = nml.y;
		this.z = nml.z;
		this.w = -nml.dot(p);
	}

	public float distFromPt(Tuple3f p)
	{
		return (x*p.x + y*p.y + z*p.z + w);
	}

	public boolean contains(Tuple3f p)
	{
		return distFromPt(p) >= 0;
	}
}
