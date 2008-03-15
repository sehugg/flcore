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

import java.util.StringTokenizer;

/**
  * Evaluates a property string like foo.bar
  * todo: fix type casting?
  */
public class PropertyEvaluator
{
	private String key;
	private String[] strarr;
	private boolean local;

	//

	public PropertyEvaluator(String s)
	{
		setKey(s);
	}

	public boolean isLocal()
	{
		return local;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
		local = key.startsWith(".");
		StringTokenizer st = new StringTokenizer(key, ".");
		strarr = new String[st.countTokens()];
		int i=0;
		while (st.hasMoreTokens())
		{
			strarr[i++] = st.nextToken().intern();
		}
	}

	public Object get(Object o)
	{
		for (int i=0; i<strarr.length; i++)
		{
			if (o == null)
			{
				throw new PropertyNotFoundException(toString());
			}
			o = ((PropertyAware)o).getProp(strarr[i]);
		}
		return o;
	}

	public Object set(Object o, Object v)
	{
		for (int i=0; i<strarr.length; i++)
		{
			if (o == null)
			{
				throw new PropertyNotFoundException(toString());
			}
			String n = strarr[i];
			if (i==strarr.length-1)
				((PropertyAware)o).setProp(n, v);
			else
				o = ((PropertyAware)o).getProp(n);
		}
		return o;
	}

	public String toString()
	{
		return key;
	}

	// STATIC METHODS

	public static Object get(Object o, String s)
	{
		StringTokenizer st = new StringTokenizer(s, ".");
		while (st.hasMoreTokens())
		{
			if (o == null)
			{
				throw new PropertyNotFoundException(s);
			}
			String n = st.nextToken();
			o = ((PropertyAware)o).getProp(n);
		}
		return o;
	}

	public static Object set(Object o, String s, Object v)
	{
		StringTokenizer st = new StringTokenizer(s, ".");
		while (st.hasMoreTokens())
		{
			if (o == null)
			{
				throw new PropertyNotFoundException(s);
			}
			String n = st.nextToken();
			if (!st.hasMoreTokens())
				((PropertyAware)o).setProp(n, v);
			else
				o = ((PropertyAware)o).getProp(n);
		}
		return o;
	}
}
