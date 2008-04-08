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
  * An extension of Vector3d that supports the PropertyAware
  * interface.  This object is immutable with respect to the
  * PropertyAware.setProp() method.
  */
public class Vec3d extends Vector3d
implements PropertyAware
{
    /**
      * Constructs and initializes a Vector3d from the specified xyz coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      * @param z the z coordinate
      */
    public Vec3d(double x, double y, double z) {
		super(x, y, z);
    }

    /**
      * Constructs and initializes a Vector3d from the specified array of length 3.
      * @param v the array of length 3 containing xyz in order
      */
    public Vec3d(double v[]) {
		super(v);
    }

    /**
      * Constructs and initializes a Vector3d from the specified Vector3f.
      * @param v1 the Vector3d containing the initialization x y z data
      */
    public Vec3d(Vector3f v1) {
		super(v1);
    }

    /**
      * Constructs and initializes a Vector3d from the specified Vector3d.
      * @param v1 the Vector3d containing the initialization x y z data
      */
    public Vec3d(Vector3d v1) {
		super(v1);
    }

    /**
      * Constructs and initializes a Vector3d from the specified Tuple3d.
      * @param t1 the Tuple3d containing the initialization x y z data
      */
    public Vec3d(Tuple3d t1) {
		super(t1);
    }

    /**
      * Constructs and initializes a Vector3d from the specified Tuple3f.
      * @param t1 the Tuple3f containing the initialization x y z data
      */
    public Vec3d(Tuple3f t1) {
		super(t1);
    }

    /**
      * Constructs and initializes a Vector3d to (0,0,0).
      */
    public Vec3d() {
		super();
    }

    public final boolean isValid()
    {
    	return isValid(x) && isValid(y) && isValid(z);
    }

    public static boolean isValid(double a)
    {
    	return !(Double.isNaN(a) || Double.isInfinite(a));
    }

    /// PROPERTY STUFF

   public Object getProp(String s)
   {
		switch (s.charAt(0))
		{
			case 'x' : return new Double(x);
			case 'y' : return new Double(y);
			case 'z' : return new Double(z);
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
   	throw new PropertyRejectedException("Vec3d is immutable");
   }

}
