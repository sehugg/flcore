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

import java.io.*;
import java.util.*;

public final class Range
{

   private static RangeNode NIL = new RangeNode();
   static {
      NIL.left = NIL;
      NIL.right = NIL;
   }

   private RangeNode root;

   class NodeEnum implements Enumeration
   {
      RangeNode curnode = firstNode();
      public boolean hasMoreElements()
      {
         return curnode != null;
      }
      public Object nextElement()
      {
         Object o = curnode;
         curnode = nextNode(curnode);
         return o;
      }
   }

   class ObjEnum implements Enumeration
   {
      RangeNode curnode = firstNode();
      public boolean hasMoreElements()
      {
         return curnode != null;
      }
      public Object nextElement()
      {
         Object o = curnode.obj;
         curnode = nextNode(curnode);
         return o;
      }
   }

   public Range()
   {
      root = NIL;
   }

   public Enumeration enumNodes()
   {
      return new NodeEnum();
   }

   public Enumeration enumObjects()
   {
      return new ObjEnum();
   }

   private final int compareNodes(RangeNode a, RangeNode b)
   {
      int x = a.lo-b.lo;
      if (x == 0)
         x = a.hi-b.hi;
      return x;
   }

   private final RangeNode firstNode()
   {
      RangeNode n = root;
      while (n != null && n.left != NIL)
      {
         n = n.left;
      }
      return n;
   }

   private final RangeNode lastNode()
   {
      RangeNode n = root;
      while (n != null && n.right != NIL)
      {
         n = n.right;
      }
      return n;
   }

   private final RangeNode nextNode(RangeNode n)
   {
      if (n.right != NIL)
      {
         n = n.right;
         while (n.left != NIL)
         {
            n = n.left;
         }
      } else {
         RangeNode prev = n;
         n = n.parent;
         while (n != null && prev == n.right)
         {
            prev = n;
            n = n.parent;
         }
      }
      return (n != NIL) ? n : null;
   }

   private final RangeNode prevNode(RangeNode n)
   {
      if (n.left != NIL)
      {
         n = n.left;
         while (n.right != NIL)
         {
            n = n.right;
         }
      } else {
         RangeNode prev = n;
         n = n.parent;
         while (n != null && prev == n.left)
         {
            prev = n;
            n = n.parent;
         }
      }
      return (n != NIL) ? n : null;
   }

private void insertFixup(RangeNode X)
{

   /*************************************
    *  maintain red-black tree balance  *
    *  after inserting node X           *
    *************************************/

    /* check red-black properties */
    while (X != root && X.parent.isred) {
        /* we have a violation */
        if (X.parent == X.parent.parent.left) {
            RangeNode Y = X.parent.parent.right;
            if (Y.isred) {

                /* uncle is red */
                X.parent.isred = false;
                Y.isred = false;
                X.parent.parent.isred = true;
                X = X.parent.parent;
            } else {

                /* uncle is black */
                if (X == X.parent.right) {
                    /* make X a left child */
                    X = X.parent;
                    rotateleft(X);
                }

                /* recolor and rotate */
                X.parent.isred = false;
                X.parent.parent.isred = true;
                rotateright(X.parent.parent);
            }
        } else {

            /* mirror image of above code */
            RangeNode Y = X.parent.parent.left;
            if (Y.isred) {

                /* uncle is red */
                X.parent.isred = false;
                Y.isred = false;
                X.parent.parent.isred = true;
                X = X.parent.parent;
            } else {

                /* uncle is black */
                if (X == X.parent.left) {
                    X = X.parent;
                    rotateright(X);
                }
                X.parent.isred = false;
                X.parent.parent.isred = true;
                rotateleft(X.parent.parent);
            }
        }
    }
    root.isred = false;
}

private RangeNode insertNode(RangeNode X)
{
    RangeNode current, parent;
    /* find where node belongs */
    current = root;
    parent = null;
    while (current != NIL)
    {
        int comp = compareNodes(X, current);
        if (comp == 0)
        {
           current.obj = X.obj;
           return current;
        }
        parent = current;
        current = (comp < 0) ? current.left : current.right;
    }

    /* insert node in tree */
    if(parent != null)
    {
        //System.out.println("X      = " + X);
        //System.out.println("parent = " + parent);
        if(compareNodes(X, parent) < 0)
            parent.left = X;
        else
            parent.right = X;
    } else {
        root = X;
    }

    X.parent = parent;
    X.left = NIL;
    X.right = NIL;
    X.isred = true;

    insertFixup(X);
    return X;
}

void rotateleft(RangeNode X)
{

   /**************************
    *  rotate RangeNode X to left *
    **************************/

    RangeNode Y = X.right;

    /* establish X.right link */
    X.right = Y.left;
    if (Y.left != NIL) Y.left.parent = X;

    /* establish Y.parent link */
    if (Y != NIL) Y.parent = X.parent;
    if (X.parent != null) {
        if (X == X.parent.left)
            X.parent.left = Y;
        else
            X.parent.right = Y;
    } else {
        root = Y;
    }

    /* link X and Y */
    Y.left = X;
    if (X != NIL) X.parent = Y;
}

void rotateright(RangeNode X)
{

   /****************************
    *  rotate RangeNode X to right  *
    ****************************/

    RangeNode Y = X.left;

    /* establish X.left link */
    X.left = Y.right;
    if (Y.right != NIL) Y.right.parent = X;

    /* establish Y.parent link */
    if (Y != NIL) Y.parent = X.parent;
    if (X.parent != null) {
        if (X == X.parent.right)
            X.parent.right = Y;
        else
            X.parent.left = Y;
    } else {
        root = Y;
    }

    /* link X and Y */
    Y.right = X;
    if (X != NIL) X.parent = Y;
}

RangeNode deleteNode(RangeNode Z, RangeNode anode)
{
    RangeNode X, Y;

   /*****************************
    *  delete node Z from tree  *
    *****************************/

    if (Z == null || Z == NIL)
      return anode;

    if (Z.left == NIL || Z.right == NIL)
    {
        /* Y has a NIL node as a child */
        Y = Z;
    } else {
        /* find tree successor with a NIL node as a child */
        Y = Z.right;
        while (Y.left != NIL)
           Y = Y.left;
    }

    /* X is Y's only child */
    if (Y.left != NIL)
        X = Y.left;
    else
        X = Y.right;

    /* remove Y from the parent chain */
    X.parent = Y.parent;
    if (Y.parent != null)
    {
        if (Y == Y.parent.left)
            Y.parent.left = X;
        else
            Y.parent.right = X;
    } else {
        root = X;
    }

    if (Y != Z)
    {
      Z.lo = Y.lo;
      Z.hi = Y.hi;
      Z.obj = Y.obj;
    }

    if (!Y.isred)
        deleteFixup (X);

    return (anode == Y ? Z : anode);
}

void deleteFixup(RangeNode X)
{

   /*************************************
    *  maintain red-black tree balance  *
    *  after deleting node X            *
    *************************************/

    while (X != root && !X.isred) {
        if (X == X.parent.left) {
            RangeNode W = X.parent.right;
            if (W.isred) {
                W.isred = false;
                X.parent.isred = true;
                rotateleft (X.parent);
                W = X.parent.right;
            }
            if (!W.left.isred && !W.right.isred) {
                W.isred = true;
                X = X.parent;
            } else {
                if (!W.right.isred) {
                    W.left.isred = false;
                    W.isred = true;
                    rotateright (W);
                    W = X.parent.right;
                }
                W.isred = X.parent.isred;
                X.parent.isred = false;
                W.right.isred = false;
                rotateleft (X.parent);
                X = root;
            }
        } else {
            RangeNode W = X.parent.left;
            if (W.isred) {
                W.isred = false;
                X.parent.isred = true;
                rotateright (X.parent);
                W = X.parent.left;
            }
            if (!W.right.isred && !W.left.isred) {
                W.isred = true;
                X = X.parent;
            } else {
                if (!W.left.isred) {
                    W.right.isred = false;
                    W.isred = true;
                    rotateleft (W);
                    W = X.parent.left;
                }
                W.isred = X.parent.isred;
                X.parent.isred = false;
                W.left.isred = false;
                rotateright (X.parent);
                X = root;
            }
        }
    }
    X.isred = false;
}

/*
RangeNode findRangeNode(T obj)
{
    RangeNode current = root;
    while(current != NIL)
        if(CompEQ(obj, current.obj))
            return (current);
        else
            current = CompLT (obj, current.obj) ?
                current.left : current.right;
    return(0);
}
*/

   private void printRangeNodes(RangeNode n, StringBuffer st)
   {
      if (n == null || n == NIL)
         return;
      printRangeNodes(n.left, st);
      st.append(n);
      printRangeNodes(n.right, st);
   }

   public String toString()
   {
      StringBuffer st = new StringBuffer();
      st.append("[Range:");
      RangeNode n = firstNode();
      while (n != null)
      {
         st.append(n);
         n = nextNode(n);
      }
//      printRangeNodes(root, st);
      st.append(']');
      return st.toString();
   }

   private RangeNode insertNode(int lo, int hi, Object obj)
   {
      RangeNode n = new RangeNode();
      n.lo = lo;
      n.hi = hi;
      n.obj = obj;
      return insertNode(n);
   }

   public final void set(int x, Object obj)
   {
      set(x, x, obj);
   }

   public final void set(int lo, int hi, Object obj)
   {
      if (lo > hi)
         throw new IllegalArgumentException("lo > hi");

      RangeNode n = insertNode(lo, hi, obj);

      RangeNode x;
      // look at next nodes
      x = nextNode(n);
      while (x != null)
      {
         // ASSERT x.lo >= lo
         // if subset of new node, erase it & continue
         if (x.lo <= hi && x.hi <= hi)
         {
            RangeNode xx = nextNode(x);
            xx = deleteNode(x, xx);
            x = xx;
         }
         else
         {
            // if just lower range, set it to our hi
            if (x.lo <= hi)
            {
               x.lo = hi+1;
            }
            break;
         }
      }

      // now look at previous nodes
      x = prevNode(n);
      while (x != null)
      {
         // ASSERT lo >= x.lo
         // if subset, erase & continue
         if (x.lo == lo && x.hi <= hi)
         {
            RangeNode xx = prevNode(x);
            xx = deleteNode(x, xx);
            x = xx;
         }
         else
         {
            if (x.hi >= lo)
            {
               RangeNode xx = prevNode(x);
               int xhi = x.hi;
               x.hi = lo-1;
               if (xhi > hi)
               {
                  insertNode(hi+1, xhi, x.obj);
               }
               x = xx;
            } else {
               break;
            }
         }
      }
   }

   static void superTest(Range r, int lo, int hi)
   {
      Random rnd = new Random();
      while (hi-- > 0)
      {
         int x = Math.abs(rnd.nextInt()) % lo;
         int y = Math.abs(rnd.nextInt()) % lo;
         if (x > y)
         {
            int z = x;
            x = y;
            y = z;
         }
         System.out.print("set " + lo + "," + hi + " - ");
         r.set(x,y,hi+"");
         System.out.println(r);
      }
   }

   static void superBench()
   {
      Random rnd = new Random(0);
      long t1 = System.currentTimeMillis();
      Range r = new Range();
      int lo = 10000;
      int iters = 100000;
      for (int i=0; i<iters; i++)
      {
         int x = Math.abs(rnd.nextInt()) % lo;
         int y = Math.abs(rnd.nextInt()) % lo;
         if (x > y)
         {
            int z = x;
            x = y;
            y = z;
         }
         r.set(x,y,rnd);
      }
      long t2 = System.currentTimeMillis();
      System.out.println("Test took " + (t2-t1) + " ms, " + iters*1000.0/(t2-t1) +
         " sets/sec");
   }

   public static void main(String[] args)
   {
      Range r = new Range();
      System.out.println("Testing: " + r.NIL);
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      String line = null;
      do {
         try {
            line = br.readLine();
            if (line == null)
               break;
            StringTokenizer st = new StringTokenizer(line);
            int lo = Integer.parseInt(st.nextToken());
            int hi = Integer.parseInt(st.nextToken());
            String obj = st.nextToken();
            if (obj.equalsIgnoreCase("XYZZY"))
               superTest(r,lo,hi);
            else if (obj.equalsIgnoreCase("BENCHIT"))
               superBench();
            else
               r.set(lo,hi,obj);
            System.out.println(r);
         } catch (Exception e) {
            e.printStackTrace();
         }
      } while (true);
   }
}


