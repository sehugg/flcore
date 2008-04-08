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

import java.util.StringTokenizer;

import com.fasterlight.util.Util;

/**
  * An interface for a 1-d function in the form f(x)=y
  */
public abstract class AbstractBSPWFunc1d
implements Func1d
{
	protected double minvalue, maxvalue;
	protected double[] coeff;

	/**
	  * Init with an array. The array is copied
	  */
	public AbstractBSPWFunc1d(double[] arr)
	{
		this.coeff = new double[arr.length];
		System.arraycopy(arr, 0, coeff, 0, arr.length);
		init();
	}

	/**
	  * Initialize with a string of coefficents.
	  */
	public AbstractBSPWFunc1d(String tmp)
	{
		StringTokenizer st = new StringTokenizer(tmp, " \t\n\r,");

		int bs = getBlockSize();
		int hs = getHeaderSize();
		int is = getInputSize();
		int nt = st.countTokens();
		if (nt < 1)
			throw new IllegalArgumentException("Too few tokens, got " + nt);
		if (is==0 || (nt % is) != 0)
			throw new IllegalArgumentException("Inappropriate number of tokens -- expected multiple of " +
				is + ", got " + nt);
		coeff = new double[(nt/is)*bs + hs];
		int i = hs;
		int j=0;
		try {
			while (st.hasMoreTokens())
			{
				String s = st.nextToken();
				coeff[i] = Util.parseDouble(s);
				i++;
				// skip elements that are "for internal use only"
				if (is<bs && ++j >= is)
				{
					i += (bs-is);
					j=0;
				}
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("Error in format string '" + tmp +
				"': " + ex);
		}
		init();
	}

	/**
	  * Called after initialization -- should set up minvalue, maxvalue
	  */
	protected void init()
	{
		// override me
	}

	/**
	  * Get the offset in the coeff[] array for a given
	  * value of x, using linear search.
	  * First value in each block is the x-lower bound for
	  * that block.
	  */
	public int getCoeffIndex(double x)
	{
		int bs = getBlockSize();
		// do search
		int i = getHeaderSize();
		do {
			if (x < coeff[i])
				return Math.max(0,i-bs);
			i += bs;
		} while (i < coeff.length);
		return i-bs;
	}
	/**
	  * Returns the size of each coefficient block
	  */
	protected abstract int getBlockSize();
	/**
	  * Returns the # of components input in each block
	  */
	protected abstract int getInputSize();
	/**
	  * Returns the size of the 'header' doubles
	  */
	protected abstract int getHeaderSize();
}
