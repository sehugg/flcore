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

/**
  * This class encapsulates an Array object.
  * It is meant to be used as a key in a Map object, since
  * it returns a hashCode().
  */
public class ArrayKey
implements java.io.Serializable
{
   private Object[] arr;

   public ArrayKey(Object[] arr)
   {
      this.arr = arr;
   }

   public ArrayKey(int len)
   {
      this.arr = new Object[len];
   }

   public Object[] getArray()
   {
   	return arr;
   }

   public Object get(int i)
   {
      return arr[i];
   }

   public void set(int i, Object o)
   {
      arr[i] = o;
   }

   public int size()
   {
      return arr.length;
   }

   public int hashCode()
   {
      int t = 0;
      for (int i=0; i<arr.length; i++)
      {
         Object o = arr[i];
         if (o != null)
            t ^= o.hashCode();
         else
         	t = (t<<1);
      }
      return t;
   }

   public boolean equals(Object obj)
   {
      if (obj == null || !(obj instanceof ArrayKey))
         return false;

      ArrayKey arr2 = (ArrayKey) obj;
      if (arr2.size() != this.size()) // check for size mismatch
         return false;

      // compare each element of the array
      for (int i=0; i<size(); i++)
      {
         Object o1 = arr[i];
         Object o2 = arr2.get(i);
         if (o1 == null)
         {
         	if (o2 != null) // if both are null, it's ok
         		return false;
         } else {
            if (!o1.equals(o2))
               return false;
         }
      }
      return true;
   }

   public String toString()
   {
      StringBuffer st = new StringBuffer();
      st.append("{");
      for (int i=0; i<arr.length; i++)
      {
         if (i > 0)
            st.append(",");
         st.append(arr[i]);
      }
      st.append("}");
      return st.toString();
   }

}
