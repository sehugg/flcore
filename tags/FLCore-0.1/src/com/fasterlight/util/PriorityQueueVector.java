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

public class PriorityQueueVector
implements java.io.Serializable
{
    protected Vector data;

    public PriorityQueueVector()
    // post: constructs a new priority queue.
    {
        data = new Vector();
    }

    public PriorityQueueVector(Vector v)
    // post: constructs a new priority queue from an unordered vector.
    {
        int i;
        data = new Vector(v.size()); // we know ultimate size
        for (i = 0; i < v.size(); i++)
        {   // add elements to heap
            add((Comparable)v.elementAt(i));
        }
    }

    protected static int parentOf(int i)
    // post: returns index of parent of value at i
    {
        return (i-1)/2;
    }

    protected static int leftChildOf(int i)
    // post: returns index of left child of value at i
    {
        return 2*i+1;
    }

    protected static int rightChildOf(int i)
    // post: returns index of right child of value at i
    {
        return 2*(i+1);
    }

    public Comparable peek()
    // pre: !isEmpty()
    // post: returns minimum value in queue
    {
        return (Comparable)data.elementAt(0);
    }

    public Comparable remove()
    // pre: !isEmpty()
    // post: removes and returns minimum value in queue
    {
        Comparable minVal = peek();
        data.setElementAt(data.elementAt(data.size()-1),0);
        data.setSize(data.size()-1);
        if (data.size() > 1) pushDownRoot(0);
        return minVal;
    }

    public void add(Comparable value)
    // pre: value is non-null comparable object
    // post: adds value to priority queue
    {
        data.addElement(value);
        percolateUp(data.size()-1);
    }

    public boolean isEmpty()
    // post: returns true iff queue has no values
    {
        return data.size() == 0;
    }

    protected void percolateUp(int leaf)
    // pre: 0 <= leaf < size
    // post: takes value at leaf in near-heap,
    //       and pushes up to correct location
    {
        int parent = parentOf(leaf);
        Comparable value = (Comparable)(data.elementAt(leaf));
        while (leaf > 0 &&
          (value.compareTo((data.elementAt(parent))) < 0))
        {
            data.setElementAt(data.elementAt(parent),leaf);
            leaf = parent;
            parent = parentOf(leaf);
        }
        data.setElementAt(value,leaf);
    }

    protected void pushDownRoot(int root)
    // pre: 0 <= root < size
    // post: pushes root down into near-heap
    //       constructing heap
    {
        int heapSize = data.size();
        Comparable value = (Comparable)data.elementAt(root);
        while (root < heapSize) {
            int childpos = leftChildOf(root);
            if (childpos < heapSize)
            {
                if ((rightChildOf(root) < heapSize) &&
                  (((Comparable)(data.elementAt(childpos+1))).compareTo
                   ((data.elementAt(childpos))) < 0))
                {
                    childpos++;
                }
                // Assert: childpos indexes smaller of two children
                if (((Comparable)(data.elementAt(childpos))).compareTo
                    (value) < 0)
                {
                    data.setElementAt(data.elementAt(childpos),root);
                    root = childpos; // keep moving down
                } else { // found right location
                    data.setElementAt(value,root);
                    return;
                }
            } else { // at a leaf! insert and halt
                data.setElementAt(value,root);
                return;
            }
        }
    }

    public int size()
    // post: returns number of values in queue
    {
        return data.size();
    }

    public void clear()
    // post: removes all values from queue
    {
        data.setSize(0);
    }

    public String toString()
    // post: returns string representation of heap
    {
        return "<PriorityQueueVector: "+data+">";
    }
}
