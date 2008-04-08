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

import com.fasterlight.util.Util;

/**
  * Implements an n-order polynomial (n>1)
  * Later will make a lookup table
  */
public class PolynomialFunc1d
extends AbstractBSPWFunc1d
{
	public PolynomialFunc1d(double[] arr)
	{
		super(arr);
	}
	public PolynomialFunc1d(String tmp)
	{
		super(tmp);
	}

	public double minValue()
	{
		return coeff[0];
	}
	public double maxValue()
	{
		return coeff[1];
	}

	public double f(double x)
	{
		x = Math.min(Math.max(x, minValue()), maxValue());
		double t = 0;
		for (int i=coeff.length-1; i>2; i--)
		{
			t += coeff[i];
			t *= x;
		}
		t += coeff[2];
		return t;
	}

	public double fp(double x)
	{
		return Double.NaN;
	}

	public double fpp(double x)
	{
		return Double.NaN;
	}

	public int getBlockSize()
	{
		return 1;
	}

	public int getInputSize()
	{
		return 1;
	}

	public int getHeaderSize()
	{
		return 0;
	}

	//

	public static void main(String[] args)
	{
		PolynomialFunc1d poly = new PolynomialFunc1d(args[0]);
		double lo = Util.parseDouble(args[1]);
		double hi = Util.parseDouble(args[2]);
		double step = Util.parseDouble(args[3]);
		double x = lo;
		while (x <= hi)
		{
			System.out.println(x + "\t" + poly.f(x));
			x += step;
		}
	}

}
