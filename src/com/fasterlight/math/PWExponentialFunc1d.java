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
  * Piecewise-linear unary function
  */
public class PWExponentialFunc1d
extends AbstractBSPWFunc1d
{
	public PWExponentialFunc1d(double[] arr)
	{
		super(arr);
	}
	public PWExponentialFunc1d(String tmp)
	{
		super(tmp);
	}

	protected void init()
	{
		super.init();
	}

	public double minValue()
	{
		return coeff[0];
	}

	public double maxValue()
	{
		return coeff[coeff.length-3];
	}

	/**
	  * Value of f(x)
	  */
	public double f(double x)
	{
		int i = getCoeffIndex(x);
		// y = scale * EXP((x - x0)/scaleheight);
		return coeff[i+1] * Math.exp( (x-coeff[i])/coeff[i+2] );
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
	  * The block-format is:
	  * 0 = first x coord of seg
	  * 1 = first y coord of seg
	  * 2 = scale height
	  */
	protected int getBlockSize()
	{
		return 3;
	}

	protected int getInputSize()
	{
		return 3;
	}

	/**
	  * Header is:
	  * 0 = lo range
	  * 1 = hi range
	  * 2 = scale factor
	  */
	protected int getHeaderSize()
	{
		return 0;
	}

	//

	public static void main(String[] args)
	{
		PWExponentialFunc1d func = new PWExponentialFunc1d("0 1.225 -7.249  25 0 0 ");
		System.out.println(func.f(-11));
		System.out.println(func.f(0));
		System.out.println(func.f(2.5f));
		System.out.println(func.f(10));
		System.out.println(func.f(11));
	}
}
