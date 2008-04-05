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

import java.lang.reflect.Method;
import java.util.*;

import com.fasterlight.math.*;

/**
  * This class can help you build a PropertyAware object
  * by introspecting certain methods.
  */
public class PropertyHelper
{
	Map gets = new HashMap();
	Map puts = new HashMap();
	Class clazz;

	static final Class[] GET_PARAMS = { };
	static final Object[] EMPTY_PARAMS = { };

	//

	class MethodRec
	{
		Method m;
		Class c;
	}

	public PropertyHelper(Class clazz)
	{
		this.clazz = clazz;
	}

	public void registerGet(String propname, String methodname)
	{
		try {
			Method m = clazz.getMethod(methodname, GET_PARAMS);
			gets.put(propname, m);
		} catch (NoSuchMethodException nsme) {
			throw new RuntimeException("Method " + methodname + "() not found in class " + clazz);
		}
	}

	public void registerSet(String propname, String methodname, Class paramtype)
	{
		try {
			Class[] paramtypes = { paramtype };
			Method m = clazz.getMethod(methodname, paramtypes);
			MethodRec mr = new MethodRec();
			mr.m = m;
			mr.c = paramtype;
			puts.put(propname, mr);
		} catch (NoSuchMethodException nsme) {
			throw new RuntimeException("Method " + methodname + "(" + paramtype.getName() + ") not found in class " + clazz);
		}
	}

	public void registerGetSet(String propname, String methodname, Class paramtype)
	{
		registerGet(propname, "get" + methodname);
		registerSet(propname, "set" + methodname, paramtype);
	}

	public Object getProp(Object obj, String key)
	throws PropertyNotFoundException, PropertyInvokeException
	{
		Method m = (Method)gets.get(key);
		if (m == null)
			return null;
		try {
			Object o = m.invoke(obj, EMPTY_PARAMS);
			return o;
		} catch (Exception exc) {
			System.out.println("Error getting property " + key + ": " + exc);
			throw new PropertyInvokeException(exc);
		}
	}

	public void setProp(Object obj, String key, Object value)
	throws PropertyNotFoundException, PropertyInvokeException
	{
		MethodRec mr = (MethodRec)puts.get(key);
		if (mr == null)
			throw new PropertyRejectedException(key);
		try {
			if (value != null)
			{
				if (mr.c == int.class)
					value = new Integer(PropertyUtil.toInt(value));
				else if (mr.c == long.class)
					value = new Long(PropertyUtil.toLong(value));
				else if (mr.c == float.class)
					value = new Float(PropertyUtil.toFloat(value));
				else if (mr.c == double.class)
					value = new Double(PropertyUtil.toDouble(value));
				else if (mr.c == boolean.class)
					value = PropertyUtil.toBoolean(value) ? Boolean.TRUE : Boolean.FALSE;
				else if (mr.c == String.class)
					value = value.toString();
				else if (mr.c == Func1d.class)
					value = CurveParser.parseCurve1d(value.toString());
			}
			Object[] arr = { value };
			mr.m.invoke(obj, arr);
		} catch (Exception exc) {
			System.out.println("Error setting property " + key + " to " + value);
			exc.printStackTrace();
			throw new PropertyInvokeException(exc);
		}
	}
}
