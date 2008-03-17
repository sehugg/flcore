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

import com.fasterlight.vecmath.Vector3d;

/**
  * Implements a 3-d cubic spline
  * @see CubicSpline
  */
public class CubicSpline3d
{
	public CubicSpline xs = new CubicSpline();
	public CubicSpline ys = new CubicSpline();
	public CubicSpline zs = new CubicSpline();

	//

	public CubicSpline3d()
	{
	}

   public CubicSpline3d(Vector3d r0, Vector3d r1, Vector3d v0, Vector3d v1)
   {
   	set(r0,r1,v0,v1);
   }

	public void set(Vector3d r0, Vector3d r1, Vector3d v0, Vector3d v1)
	{
   	xs = new CubicSpline(r0.x, r0.x+v0.x, r1.x-v1.x, r1.x);
   	ys = new CubicSpline(r0.y, r0.y+v0.y, r1.y-v1.y, r1.y);
   	zs = new CubicSpline(r0.z, r0.z+v0.z, r1.z-v1.z, r1.z);
   }

   public Vector3d f(double t)
   {
   	return new Vector3d(xs.f(t), ys.f(t), zs.f(t));
   }

   public Vector3d fp(double t)
   {
   	return new Vector3d(xs.fp(t), ys.fp(t), zs.fp(t));
   }

   public Vector3d fpp(double t)
   {
   	return new Vector3d(xs.fpp(t), ys.fpp(t), zs.fpp(t));
   }

   //

   public static void main(String[] args)
   {
   	CubicSpline3d cs3 = new CubicSpline3d(
   		new Vector3d(0,0,0),
   		new Vector3d(100,100,0),
   		new Vector3d(25,0,0),
   		new Vector3d(0,25,0)
   	);
   	for (double t=0; t<2; t+=0.125)
   	{
   		System.out.println(t + ":\t" + cs3.f(t));
   	}
   }
}
