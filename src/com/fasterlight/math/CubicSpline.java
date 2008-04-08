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
package com.fasterlight.math;


/**
  * Implements a 1-d cubic spline
  * @see CubicSpline3D
  */
public class CubicSpline
{
	public double A,B,C,D;

	//

	public CubicSpline()
	{
	}

   public CubicSpline(double x, double y, double z, double w)
   {
   	set(x,y,z,w);
   }

   public void set(double x, double y, double z, double w)
   {
		A = w - 3*z + 3*y - x;
	   B = 3*z - 6*y + 3*x;
	   C = 3*y - 3*x;
	   D = x;
   }

   public double f(double t)
   {
   	return A*t*t*t + B*t*t + C*t + D;
   }

   public double fp(double t)
   {
   	return 3*A*t*t + 2*B*t + C;
   }

   public double fpp(double t)
   {
   	return 6*A*t + 2*B;
   }
}
