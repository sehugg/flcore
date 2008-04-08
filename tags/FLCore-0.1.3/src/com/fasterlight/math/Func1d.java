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
  * An interface for a 1-d function in the form f(x) = y
  */
public interface Func1d
{
	/**
	  * Minimum valid value of x, or Double.MIN_VALUE
	  */
	public double minValue();
	/**
	  * Maximum valid value of x, or Double.MAX_VALUE
	  */
	public double maxValue();
	/**
	  * Value of f(x)
	  */
	public double f(double x);
	/**
	  * 1st derivative of f(x), or NaN if not supported
	  */
	public double fp(double x);
	/**
	  * 2nd derivative of f(x), or NaN if not supported
	  */
	public double fpp(double x);
}
