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

import java.lang.reflect.InvocationTargetException;

public class Util
{
	public static Throwable getBaseException(Throwable ex)
	{
		if (ex instanceof InvocationTargetException)
		{
			return ((InvocationTargetException)ex).getTargetException();
		}
		else
			return ex;
	}
	public static float parseFloat(String s)
	{
//		return new Float(s).floatValue();
		return Float.parseFloat(s);
	}
	public static double parseDouble(String s)
	{
//		return new Double(s).doubleValue();
		return Double.parseDouble(s);
	}
	public static double toRadians(double x)
	{
		return x*(Math.PI/180d);
	}
	public static double toDegrees(double x)
	{
		return x*(180d/Math.PI);
	}
	public static final int sign2(float x)
	{
		return (x >= 0) ? 1 : -1;
	}
}
