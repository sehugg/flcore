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

import com.fasterlight.vecmath.*;

/**
  * A simple array that holds floats.
  */
public class FloatList
implements java.io.Serializable
{
	private float[] arr;
	private int size;
	private int inc = 16;

   static final long serialVersionUID = 3003950008823557991L;

	//

	public FloatList()
	{
		this(16);
	}

	public FloatList(int cap)
	{
		arr = new float[cap];
	}

	public int size()
	{
		return size;
	}

	protected void ensureCapacity(int cap)
	{
		if (arr.length < cap)
		{
			float[] newarr = new float[arr.length+inc];
			System.arraycopy(arr, 0, newarr, 0, arr.length);
			arr = newarr;
		}
	}

	public void add(float f)
	{
		ensureCapacity(size+1);
		arr[size++] = f;
	}

	public void add(Tuple2f v)
	{
		add(v.x);
		add(v.y);
	}

	public void add(Tuple3f v)
	{
		add(v.x);
		add(v.y);
		add(v.z);
	}

	public float get(int i)
	{
		return arr[i];
	}

	public Vector2f getVector2f(int i)
	{
		i *= 2;
		return new Vector2f(arr[i], arr[i+1]);
	}

	public Vector3f getVector3f(int i)
	{
		i *= 3;
		return new Vector3f(arr[i], arr[i+1], arr[i+2]);
	}

	public int indexOf(Vector2f v)
	{
		for (int i=0; i<size; i+=2)
		{
			if (arr[i]==v.x && arr[i+1]==v.y)
				return i/2;
		}
		return -1;
	}

	public int indexOf(Vector3f v)
	{
		for (int i=0; i<size; i+=3)
		{
			if (arr[i]==v.x && arr[i+1]==v.y && arr[i+2]==v.z)
				return i/3;
		}
		return -1;
	}

}
