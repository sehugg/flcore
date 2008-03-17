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
public class PWLinearFunc1d
extends AbstractBSPWFunc1d
{
	public PWLinearFunc1d(double[] arr)
	{
		super(arr);
	}
	public PWLinearFunc1d(String tmp)
	{
		super(tmp);
	}

	protected void init()
	{
		super.init();

		int bs = getBlockSize();

		// setup 1st block (zero slope)
		coeff[0] = 0;
		coeff[1] = coeff[4];
		coeff[2] = 0;

		// setup slopes
		for (int i=bs; i<coeff.length-bs; i+=bs)
		{
			double x1 = coeff[i];
			double x2 = coeff[i+bs];
			double y1 = coeff[i+1];
			double y2 = coeff[i+bs+1];
			coeff[i+2] = (y2-y1)/(x2-x1);
		}
	}

	public double minValue()
	{
		return coeff[3];
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
		x -= coeff[i];
		return coeff[i+1] + coeff[i+2]*x;
	}
	/**
	  * 1st derivative of f(x), or NaN if not supported
	  */
	public double fp(double x)
	{
		int i = getCoeffIndex(x);
		return coeff[i+2];
	}
	/**
	  * 2nd derivative of f(x), or NaN if not supported
	  */
	public double fpp(double x)
	{
		return 0;
	}

	/**
	  * The block-format is:
	  * 0 = first x coord of seg
	  * 1 = first y coord of seg
	  * 2 = slope of seg
	  */
	protected int getBlockSize()
	{
		return 3;
	}

	protected int getInputSize()
	{
		return 2;
	}

	protected int getHeaderSize()
	{
		return 3;
	}

	//

	public static void main(String[] args)
	{
		PWLinearFunc1d func = new PWLinearFunc1d("1,1 10,5");
		System.out.println(func.f(-11));
		System.out.println(func.f(0));
		System.out.println(func.f(2.5f));
		System.out.println(func.f(10));
		System.out.println(func.f(11));
	}
}
