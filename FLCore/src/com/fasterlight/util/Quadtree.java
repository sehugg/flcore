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

import java.util.*;

import com.fasterlight.vecmath.*;

// todo: inaccuracy in FP when lots of rects

public class Quadtree
implements java.io.Serializable
{
   Rect4f r; // boundaries
   Point2f c; // center
   Quadtree[] quads;
   HashMap buckets;
   Quadtree parent;
   boolean issplit;
   //int ndescendants;
   static final int maxbuckets = 5;
   static final int minbuckets = 1;

   public Quadtree(Rect4f bounds)
   {
      this.r = bounds;
      this.c = new Point2f((r.x1+r.x2)/2, (r.y1+r.y2)/2);
      quads = new Quadtree[4];
   }

   Quadtree(Rect4f bounds, Quadtree parent)
   {
      this(bounds);
      this.parent = parent;
   }

   public Rect4f getBounds()
   {
   	return r;
   }

   private int getQuadOfPoint(Tuple2f p)
   {
      return (p.x < c.x ? 0 : 1) + (p.y < c.y ? 0 : 2);
   }

   public Quadtree getQuad(int quadindex)
   {
      return quads[quadindex];
   }

   public Quadtree getTreeForPoint(Tuple2f p)
   {
      Quadtree q = quads[getQuadOfPoint(p)];
      if (q == null)
         return this;
      else
         return q.getTreeForPoint(p);
   }

   public boolean isInBounds(Tuple2f p)
   {
      return r.contains(p);
   }

   public int getDescendantCount()
   {
      if (!issplit) {
         return (buckets == null) ? 0 : buckets.size();
      } else {
         int total = 0;
         for (int i=0; i<4; i++)
         {
            total += quads[i].getDescendantCount();
         }
         return total;
      }
   }

   private boolean removeObj(Object o)
   {
      if (buckets == null)
         return false;
      Object oo = buckets.remove(o);
      if (oo != null)
      {
         Quadtree qp = parent;
         while (qp != null && qp.issplit &&
                qp.getDescendantCount() <= minbuckets)
         {
            qp.consolidate();
            qp = qp.parent;
         }
         return true;
      } else {
         return false;
      }
   }

   private void consolidate()
   {
      for (int i=0; i<4; i++)
      {
         Quadtree q = quads[i];
         if (q.buckets != null)
         {
            Iterator it = q.buckets.entrySet().iterator();
            while (it.hasNext())
            {
            	Map.Entry entry = (Map.Entry)it.next();
               this.putObj(entry.getKey(), (Tuple2f)entry.getValue());
            }
         }
         quads[i] = null;
      }
      issplit = false;
   }

   private void split()
   {
   	// if quad is too small already, don't split (todo)
   	if (r.x1==c.x || r.x2==c.x || r.y1==c.y || r.y2==c.y)
   	{
//   		System.out.println("QUAD TOO SMALL: " +this);
   		return;
   	}
      quads[0] = new Quadtree(new Rect4f(r.x1, r.y1, c.x, c.y), this);
      quads[1] = new Quadtree(new Rect4f(c.x, r.y1, r.x2, c.y), this);
      quads[2] = new Quadtree(new Rect4f(r.x1, c.y, c.x, r.y2), this);
      quads[3] = new Quadtree(new Rect4f(c.x, c.y, r.x2, r.y2), this);
      Iterator it = buckets.entrySet().iterator();
      while (it.hasNext())
      {
       	Map.Entry entry = (Map.Entry)it.next();
       	Tuple2f p = (Tuple2f)entry.getValue();
//       	System.out.println(p + "\t" + r + "\t" + c);
       	quads[getQuadOfPoint(p)].putObj(entry.getKey(), p);
      }
      buckets = null;
      issplit = true;
   }

   private Object putObj(Object o, Tuple2f p)
   {
      if (buckets == null)
         buckets = new HashMap(maxbuckets);
      Object lo = buckets.put(o, p);
      if (buckets.size() > maxbuckets)
      {
         split();
      }
      return lo;
   }

   public void update(Object obj, Tuple2f oldpos, Tuple2f newpos)
   {
      Quadtree q = getTreeForPoint(oldpos);
      if (q.isInBounds(newpos))
      {
         // can take shortcut, just a hard update
         q.putObj(obj, new Point2f(newpos.x, newpos.y));
      } else {
         // gotta do it the hard way
         q.removeObj(obj);
         Quadtree q2 = getTreeForPoint(newpos);
         q2.putObj(obj, new Point2f(newpos.x, newpos.y));
      }
   }

   public Object get(Tuple2f pos)
   {
   	Quadtree q;
   	q = getTreeForPoint(pos);
      if (q.buckets != null)
      {
         Iterator it = q.buckets.entrySet().iterator();
         while (it.hasNext())
         {
         	Map.Entry entry = (Map.Entry)it.next();
          	if (entry.getValue().equals(pos))
          		return entry.getKey();
         }
      }
      return null;
   }

   public void put(Object obj, Tuple2f pos)
   {
      Quadtree q;
      q = getTreeForPoint(pos);
      q.putObj(obj, new Point2f(pos.x, pos.y));
   }

   public void remove(Object obj, Tuple2f pos)
   {
      Quadtree q;
      q = getTreeForPoint(pos);
      q.removeObj(obj);
   }

   public void addElementsInRect(Rect4f a, Vector v)
   {
      if (!r.intersects(a))
         return;
      if (!issplit)
      {
         if (buckets == null)
            return;
	      Iterator it = buckets.entrySet().iterator();
   	   while (it.hasNext())
      	{
       		Map.Entry entry = (Map.Entry)it.next();
       		if (a.contains((Tuple2f)entry.getValue()))
               v.addElement(entry.getKey());
         }
      } else {
         for (int i=0; i<4; i++)
            quads[i].addElementsInRect(a, v);
      }
   }

   public void addEntriesInRect(Rect4f a, Vector v)
   {
      if (!r.intersects(a))
         return;
      if (!issplit)
      {
         if (buckets == null)
            return;
	      Iterator it = buckets.entrySet().iterator();
   	   while (it.hasNext())
      	{
       		Map.Entry entry = (Map.Entry)it.next();
       		if (a.contains((Tuple2f)entry.getValue()))
               v.addElement(entry);
         }
      } else {
         for (int i=0; i<4; i++)
            quads[i].addEntriesInRect(a, v);
      }
   }

   public Vector getElementsInRect(Rect4f a)
   {
      Vector v = new Vector();
      addElementsInRect(a, v);
      return v;
   }

   public Vector getEntriesInRect(Rect4f a)
   {
      Vector v = new Vector();
      addEntriesInRect(a, v);
      return v;
   }

   public Enumeration enumElementsInRect(Rect4f a)
   {
      Vector v = new Vector();
      addElementsInRect(a, v);
      return v.elements();
   }

   public String toString()
   {
   	return "[Quadtree@"+r+":"+buckets+"]";
   }

   public static void main(String[] args)
   {
      Vector active = new Vector();
      int niters = 50000;
      Random rnd = new Random();
      Quadtree qt = new Quadtree(new Rect4f(0, 0, 1, 1));
      float scale = 1e-5f;
      // add lots of elements
      for (int i=0; i<niters; i++)
      {
         Point2f v = new Point2f(rnd.nextFloat()*scale, rnd.nextFloat()*scale);
         qt.put(v, v);
         active.addElement(v);
      }
      System.out.println("Quadtree now contains " +
         qt.getDescendantCount() + " elements");
      // count 'em
      Vector v2 = qt.getElementsInRect(new Rect4f(0, 0, 0.5f, 0.5f));
      System.out.println("Found " + v2.size() + " elements in upper-left quad");
      // now remove 'em
      for (int i=0; i<active.size(); i++)
      {
         Point2f v = (Point2f)active.elementAt(i);
         qt.remove(v, v);
      }
      System.out.println("Quadtree now contains " +
         qt.getDescendantCount() + " elements");
      try {
         Thread.sleep(1000);
      } catch (Exception e) {
      }
   }
}
