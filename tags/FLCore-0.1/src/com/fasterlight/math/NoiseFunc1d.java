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

/**
  * An interface for a 1-d noise function.
  * Interpolates linearly between function
  * f(x) = R(floor(x*k))
  * where R is a pseudorandom function returning -1..1
  */
public class NoiseFunc1d
implements Func1d
{
	private double k;

	public NoiseFunc1d(double k)
	{
		this.k = k;
	}

	/**
	  * Minimum valid value of x, or Double.MIN_VALUE
	  */
	public double minValue()
	{
		return Double.MIN_VALUE;
	}
	/**
	  * Maximum valid value of x, or Double.MAX_VALUE
	  */
	public double maxValue()
	{
		return Double.MAX_VALUE;
	}
	/**
	  * Value of f(x)
	  */
	public double f(double x)
	{
		x *= k;
		int x1 = (int)Math.floor(x);
		double i = (x-x1);
		return R(x1)*(1-i) + R(x1+1)*i;
	}
	/**
	  * 1st derivative of f(x), or NaN if not supported
	  */
	public double fp(double x)
	{
		return Double.NaN;
	}
	/**
	  * 2nd derivative of f(x), or NaN if not supported
	  */
	public double fpp(double x)
	{
		return Double.NaN;
	}

	/**
	  * The random function R(x)
	  */
	protected double R(int x)
	{
		x = (x<<13) ^ x;
		return ( 1.0 - ( (x * (x * x * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);
	}
}
