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

public class CurveParser
{
/**
  * Parses a string, tries to convert it to a Func1d.
  * Now supports these types:
  * <pre>
  * PWL:x1,y1 x2,y2 ...
  * -- a piecewise linear function with bounds.
  *
  * PN:xlow,xhigh k0,k1,k2...
  * -- a polynomial function with bounds.
  * </pre>
  */
	public static Func1d parseCurve1d(String fmt)
	{
		if (fmt.startsWith("PWL:"))
			return new PWLinearFunc1d(fmt.substring(4));
		else if (fmt.startsWith("PN:"))
			return new PolynomialFunc1d(fmt.substring(3));
		else if (fmt.startsWith("PWEXP:"))
			return new PWExponentialFunc1d(fmt.substring(6));
		else
			try {
				return new ConstFunc1d(Util.parseDouble(fmt));
			} catch (NumberFormatException nfe) {
				throw new IllegalArgumentException("Format string invalid: " + fmt);
			}
	}
}
