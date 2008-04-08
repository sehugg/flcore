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
package com.fasterlight.testing;

import com.fasterlight.vecmath.*;

/**
  * A testing case that specializes in testing numeric routines.
  */
public abstract class NumericTestCase
extends FLTestCase
{
	public double THRESHOLD = 1e-6;
	public static boolean num_debug = true;

	//

	public NumericTestCase(String name)
	{
		super(name);
	}

	public boolean compare(double a, double b)
	{
		if (a==b)
			return true;
		if (Double.isNaN(a) && Double.isNaN(b))
			return true;
		if (a*b < 0)
			return false; // make sure both are same sign
		a = Math.abs(a);
		b = Math.abs(b);
		if (a+b < THRESHOLD*2)
			return true;
//		double factor = 1 - (a>b ? b/a : a/b);
		double factor = Math.abs((a/b)-1);
		boolean res = (factor < THRESHOLD);
		if (!res)
		{
			double diff = Math.abs(a-b);
			res = (diff < THRESHOLD);
		}
		if (!res && num_debug) {
			System.out.println(a + " " + b + " " + factor);
		}
		return res;
	}

	public boolean compare(double[] arr1, double[] arr2)
	{
		if (arr1==null && arr2==null)
			return true;
		if (arr1.length != arr2.length || arr1==null || arr2==null)
			return false;

		for (int i=0; i<arr1.length; i++)
		{
			if (!compare(arr1[i], arr2[i]))
			{
				System.out.println("compare() failed on element " + i);
				return false;
			}
		}
		return true;
	}

	public boolean compare(Vector3d a, Vector3d b)
	{
		double[] arr1 = new double[] { a.x, a.y, a.z };
		double[] arr2 = new double[] { b.x, b.y, b.z };
		return compare(arr1, arr2);
	}

	public boolean compare(Vector3f a, Vector3f b)
	{
		double[] arr1 = new double[] { a.x, a.y, a.z };
		double[] arr2 = new double[] { b.x, b.y, b.z };
		return compare(arr1, arr2);
	}

	public void compareAssert(double a, double b)
	{
		if (!compare(a, b))
		{
			assertTrue("compare failed: " + a + " != " + b, false);
		}
	}

	public void compareAssert(double[] aarr, double[] barr)
	{
		if (!compare(aarr, barr))
		{
			assertTrue("compare failed: " + aarr + " != " + barr, false);
		}
	}

	public void compareAssert(Vector3d a, Vector3d b)
	{
		if (!compare(a, b))
		{
			assertTrue("compare failed: " + a + " != " + b, false);
		}
	}

	public void compareAssert(Vector3f a, Vector3f b)
	{
		if (!compare(a, b))
		{
			assertTrue("compare failed: " + a + " != " + b, false);
		}
	}
}
