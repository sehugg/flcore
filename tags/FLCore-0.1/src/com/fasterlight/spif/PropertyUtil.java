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
package com.fasterlight.spif;

import java.util.*;

import com.fasterlight.util.Util;

/**
  * Various routines that will help you in your Property Quest.
  */
public class PropertyUtil
{
	public static int toInt(Object o)
	{
		return (int)toLong(o);
	}

	public static long toLong(Object o)
	{
		if (o == null)
			return 0;
		try {
			if (o instanceof Number)
				return ((Number)o).longValue();
			else if (o instanceof Boolean)
				return ((Boolean)o).booleanValue() ? 1 : 0;
			else
				return Long.parseLong(o.toString());
		} catch (Exception e) {
			throw new PropertyRejectedException("Can't convert " + o + " to int");
		}
	}

	public static float toFloat(Object o)
	{
		if (o == null)
			return 0;
		try {
			if (o instanceof Number)
				return ((Number)o).floatValue();
			else if (o instanceof Boolean)
				return toInt(o);
			else
				return Util.parseFloat(o.toString());
		} catch (Exception e) {
			return Float.NaN;
//			throw new PropertyRejectedException("Can't convert " + o + " to float");
		}
	}

	public static double toDouble(Object o)
	{
		if (o == null)
			return 0;
		try {
			if (o instanceof Number)
				return ((Number)o).doubleValue();
			else if (o instanceof Boolean)
				return toInt(o);
			else
				return Util.parseDouble(o.toString());
		} catch (Exception e) {
			return Double.NaN;
//			throw new PropertyRejectedException("Can't convert " + o + " to double");
		}
	}

	public static boolean toBoolean(Object o)
	{
		if (o == null)
			return false;
		if (o instanceof Boolean)
			return ((Boolean)o).booleanValue();
		try {
			if (o instanceof Number)
				return ((Number)o).floatValue() != 0;
			else
				return o.toString().trim().equalsIgnoreCase("true");
		} catch (Exception e) {
			throw new PropertyRejectedException("Can't convert " + o + " to double");
		}
	}

	public static String toString(Object o)
	{
		return (o != null) ? o.toString() : "";
	}

	public static void setFromProps(PropertyAware obj, Properties props)
	{
		Enumeration e = props.propertyNames();
		while (e.hasMoreElements())
		{
			String name = (String)e.nextElement();
			try {
				Object value = parseValue(props.getProperty(name));
				PropertyEvaluator.set(obj, name, value);
			} catch (PropertyRejectedException pre) {
				System.out.println(pre);
			}
		}
	}

	public static boolean equals(Object a, Object b)
	{
		if (a == null)
			return (b == null);
		else if (b == null)
			return false;
		else if (a instanceof Number && b instanceof Number)
			return ((Number)a).floatValue() == ((Number)b).floatValue();
		else
			return a.equals(b);
	}

	public static Object parseValue(Object value)
	{
		if (value instanceof String)
		{
			String s = (String)value;
			try {
				return new Integer(s);
			} catch (Exception e1) {
			}
			try {
				return new Float(s);
			} catch (Exception e1) {
			}
			if ("true".equals(s))
				return Boolean.TRUE;
			else if ("false".equals(s))
				return Boolean.FALSE;
			else if ("null".equals(s))
				return null;
		}
		return value;
	}
}
