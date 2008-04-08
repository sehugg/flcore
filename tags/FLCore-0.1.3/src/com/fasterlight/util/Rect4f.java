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

import com.fasterlight.vecmath.Tuple2f;

public class Rect4f
implements java.io.Serializable
{
   public float x1,y1,x2,y2;

   public Rect4f(Rect4f r)
   {
   		this(r.x1, r.y1, r.x2, r.y2);
   }

   public Rect4f(float x1, float y1, float x2, float y2)
   {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
   }

   public Rect4f(Tuple2f a, Tuple2f b)
   {
      this(a.x,a.y,b.x,b.y);
   }

   public final boolean contains(Tuple2f p)
   {
      return (p.x >= x1 && p.x < x2 && p.y >= y1 && p.y < y2);
   }

   public final boolean contains(Rect4f r)
   {
      //return (r.x1 >= x1 && r.x2 < x2 && r.y1 >= y1 && r.y2 < y2);
      return (r.x1 >= x1 && r.x2 < x2 && r.x1 >= x1 && r.x2 < x2);
   }

   public final boolean intersects(Rect4f r)
   {
      //return !(r.x2 < x1 || r.y2 < y1 || r.x1 >= x2 || r.y1 >= y2);
      return !(r.x2 < x1 || r.y2 < y1 || r.x1 >= x2 || r.y1 >= y2);
   }

   public final float width()
   {
      return (x2-x1);
   }

   public final float height()
   {
      return (y2-y1);
   }

   public final String toString()
   {
      return "[" + x1 + ',' + y1 + ',' + x2 + ',' + y2 + ']';
   }

public void scale(float xs, float ys)
{
	x1 *= xs;
	x2 *= xs;
	y1 *= ys;
	y2 *= ys;
}

public void translate(double xx, double yy)
{
	x1 += xx;
	x2 += xx;
	y1 += yy;
	y2 += yy;
}

   /*
   public static void main(String[] args)
   {
      Rect4f r = new Rect4f(0,0,10,10);
      Rect4f r2 = new Rect4f(5,5,15,15);
      Rect4f r3 = new Rect4f(12,5,25,25);
      System.out.println(r.intersects(r2));
      System.out.println(r2.intersects(r));
      System.out.println(r.intersects(r3));
      System.out.println(r3.intersects(r));
      System.out.println(r2.intersects(r3));
   }
   */
}
