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

import java.util.Vector;

import com.fasterlight.vecmath.Vector2f;

/**
  * points are ordered clockwise
  */
public final class Polygon2f
implements java.io.Serializable
{
   Vector2f pts[];

	public Polygon2f(Polygon2f poly)
	{
		this(poly.pts);
	}

   public Polygon2f(Vector2f[] points)
   {
      pts = new Vector2f[points.length];
      for (int i=0; i<points.length; i++)
      {
      	pts[i] = new Vector2f(points[i]);
      }
   }

   public int numPoints()
   {
   	return pts.length;
   }

   public Vector2f getPoint(int i)
   {
   	return pts[i];
   }

   public Polygon2f rotate(Vector2f sv, Vector2f tv, float rad)
   {
      int l = pts.length;
      Vector2f[] newpts = new Vector2f[l];
      double s1 = Math.sin(rad);
      double c1 = Math.cos(rad);
      for (int i=0; i<l; i++)
      {
         float x = pts[i].x*sv.x;
         float y = pts[i].y*sv.y;
         float xx = (float)(x*c1 + y*s1) + tv.x;
         float yy = (float)(y*c1 - x*s1) + tv.y;
         newpts[i] = new Vector2f(xx, yy);
      }
      return new Polygon2f(newpts);
   }

   public Polygon2f scale(Vector2f a)
   {
      int l = pts.length;
      Vector2f[] newpts = new Vector2f[l];
      for (int i=0; i<l; i++)
      {
         newpts[i] = new Vector2f(pts[i].x*a.x, pts[i].y*a.y);
      }
      return new Polygon2f(newpts);
   }

   public Polygon2f trans(Vector2f a)
   {
      int l = pts.length;
      Vector2f[] newpts = new Vector2f[l];
      for (int i=0; i<l; i++)
      {
         newpts[i] = new Vector2f(pts[i].x+a.x, pts[i].y+a.y);
      }
      return new Polygon2f(newpts);
   }

   public Polygon2f rotate(float rad)
   {
      int l = pts.length;
      Vector2f[] newpts = new Vector2f[l];
      double s1 = Math.sin(rad);
      double c1 = Math.cos(rad);
      for (int i=0; i<l; i++)
      {
         newpts[i] = new Vector2f((float)(pts[i].x*c1 + pts[i].y*s1),
            (float)(pts[i].y*c1 - pts[i].x*s1));
      }
      return new Polygon2f(newpts);
   }

/*
            (Ay-Cy)(Dx-Cx)-(Ax-Cx)(Dy-Cy)
        r = -----------------------------  (eqn 1)
            (Bx-Ax)(Dy-Cy)-(By-Ay)(Dx-Cx)
            (Ay-Cy)(Bx-Ax)-(Ax-Cx)(By-Ay)
        s = -----------------------------  (eqn 2)
            (Bx-Ax)(Dy-Cy)-(By-Ay)(Dx-Cx)
*/
   Vector2f intersect(Vector2f a, Vector2f b, Vector2f c, Vector2f d)
   {
      float r,s;
      r=((a.y-c.y)*(d.x-c.x)-(a.x-c.x)*(d.y-c.y))/
        ((b.x-a.x)*(d.y-c.y)-(b.y-a.y)*(d.x-c.x));
      if (r < 0 || r > 1)
         return null;
      s=((a.y-c.y)*(b.x-a.x)-(a.x-c.x)*(b.y-a.y))/
        ((b.x-a.x)*(d.y-c.y)-(b.y-a.y)*(d.x-c.x));
      if (s >= 0 && s <= 1)
         return new Vector2f(a.x+r*(b.x-a.x), a.y+r*(b.y-a.y));
      else
         return null;
   }

   public boolean intersects(Polygon2f a)
   {
   	// todo: check case where polygon completely inside
   	return (getIntersectPoints(a).size() > 0);
   }

   public Vector getIntersectPoints(Polygon2f a)
   {
   	Vector v = new Vector();
      int l1 = pts.length;
      int l2 = a.pts.length;
      for (int i=0; i<l1; i++)
      {
         Vector2f p1 = pts[i];
         Vector2f lp1 = pts[i==0?l1-1:i-1];
         for (int j=0; j<l2; j++)
         {
            Vector2f lp2 = a.pts[j==0?l2-1:j-1];
            Vector2f p2 = a.pts[j];
            Vector2f ip = intersect(lp1, p1, lp2, p2);
            if (ip != null)
            	v.addElement(ip);
         }
      }
      return v;
   }

   public boolean contains(Vector2f point)
   {
      float xnew,ynew,xold,yold,x1,y1,x2,y2;
      int i;
      boolean inside = false;
      int npts = pts.length;
      if (npts < 3)
         return false;
      xold = pts[npts-1].x;
      yold = pts[npts-1].y;
      for (i=0; i<npts; i++)
      {
         xnew = pts[i].x;
         ynew = pts[i].y;
         if (xnew > xold) {
            x1=xold;
            x2=xnew;
            y1=yold;
            y2=ynew;
         } else {
            x1=xnew;
            x2=xold;
            y1=ynew;
            y2=yold;
         }
         if ((xnew < point.x) == (point.x <= xold)
            && (point.y-y1)*(x2-x1) < (y2-y1)*(point.x-x1))
         {
            inside = !inside;
         }
         xold=xnew;
         yold=ynew;
      }
      return inside;
   }

}
