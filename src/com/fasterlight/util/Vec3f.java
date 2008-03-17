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

import com.fasterlight.spif.*;
import com.fasterlight.vecmath.*;

/**
  * An extension of Vector3f that supports the PropertyAware
  * interface.  This object is immutable with respect to the
  * PropertyAware.setProp() method.
  */
public class Vec3f extends Vector3f
implements PropertyAware
{
    /**
      * Constructs and initializes a Vector3f from the specified xyz coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      * @param z the z coordinate
      */
    public Vec3f(float x, float y, float z) {
		super(x, y, z);
    }

    /**
      * Constructs and initializes a Vector3f from the specified array of length 3.
      * @param v the array of length 3 containing xyz in order
      */
    public Vec3f(float v[]) {
		super(v);
    }

    /**
      * Constructs and initializes a Vector3f from the specified Vector3f.
      * @param v1 the Vector3f containing the initialization x y z data
      */
    public Vec3f(Vector3f v1) {
		super(v1);
    }

    /**
      * Constructs and initializes a Vector3f from the specified Vector3f.
      * @param v1 the Vector3f containing the initialization x y z data
      */
    public Vec3f(Vector3d v1) {
		super(v1);
    }

    /**
      * Constructs and initializes a Vector3f from the specified Tuple3f.
      * @param t1 the Tuple3f containing the initialization x y z data
      */
    public Vec3f(Tuple3f t1) {
		super(t1);
    }

    /**
      * Constructs and initializes a Vector3f from the specified Tuple3f.
      * @param t1 the Tuple3f containing the initialization x y z data
      */
    public Vec3f(Tuple3d t1) {
		super(t1);
    }

    /**
      * Constructs and initializes a Vector3f to (0,0,0).
      */
    public Vec3f() {
		super();
    }

    public final boolean isValid()
    {
    	return isValid(x) && isValid(y) && isValid(z);
    }

    public static boolean isValid(float a)
    {
    	return !(Float.isNaN(a) || Float.isInfinite(a));
    }


    /// PROPERTY STUFF

   public Object getProp(String s)
   {
		switch (s.charAt(0))
		{
			case 'x' : return new Float(x);
			case 'y' : return new Float(y);
			case 'z' : return new Float(z);
		}
		if ("length".equals(s))
			return new Double(length());
		else if ("length^2".equals(s))
			return new Double(lengthSquared());
		else
			return null;
   }

   public void setProp(String s, Object o)
   {
   	throw new PropertyRejectedException("Vec3f is immutable");
   }

}
