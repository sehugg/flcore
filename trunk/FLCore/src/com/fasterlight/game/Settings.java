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
package com.fasterlight.game;

import java.io.IOException;

import com.fasterlight.spif.*;
import com.fasterlight.util.*;

/**
  * A static class that manages static data in other classes.
  * You can assign an .INI file to the Settings object on startup,
  * and other classes call the static methods in Settings to get
  * and set settings.
  */
public class Settings implements PropertyAware
{
	private static INIFile settings;
	private static boolean writeable = false;

	//

	/**
	  * The only reason you'd want to create a Settings object
	  * is to have an object that is PropertyAware
	  */
	public Settings()
	{
	}

	public static void setFilename(String fn)
	{
		settings = new CachedINIFile(fn);
	}

	public static void setWriteable(boolean b)
	{
		writeable = b;
	}

	public static String getString(String section, String name, String defvalue)
	{
		if (settings == null)
			return defvalue;
		try
		{
			String s = settings.getString(section, name, null);
			if (writeable && s == null && defvalue != null)
			{
				settings.setString(section, name, defvalue);
			}
			return (s != null) ? s : defvalue;
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			return defvalue;
		}
	}

	public static int getInt(String section, String name, int defvalue)
	{
		if (settings == null)
			return defvalue;
		try
		{
			return PropertyUtil.toInt(getString(section, name, Integer.toString(defvalue)));
		}
		catch (PropertyRejectedException nfe)
		{
			return defvalue;
		}
	}

	public static long getLong(String section, String name, long defvalue)
	{
		if (settings == null)
			return defvalue;
		try
		{
			return PropertyUtil.toLong(getString(section, name, Long.toString(defvalue)));
		}
		catch (PropertyRejectedException nfe)
		{
			return defvalue;
		}
	}

	public static float getFloat(String section, String name, float defvalue)
	{
		if (settings == null)
			return defvalue;
		try
		{
			return PropertyUtil.toFloat(getString(section, name, Float.toString(defvalue)));
		}
		catch (PropertyRejectedException nfe)
		{
			return defvalue;
		}
	}

	public static double getDouble(String section, String name, double defvalue)
	{
		if (settings == null)
			return defvalue;
		try
		{
			return PropertyUtil.toDouble(getString(section, name, Double.toString(defvalue)));
		}
		catch (PropertyRejectedException nfe)
		{
			return defvalue;
		}
	}

	public static boolean getBoolean(String section, String name, boolean defvalue)
	{
		if (settings == null)
			return defvalue;
		try
		{
			return PropertyUtil.toBoolean(getString(section, name, defvalue ? "true" : "false"));
		}
		catch (PropertyRejectedException nfe)
		{
			return defvalue;
		}
	}

	public static void setString(String section, String name, String newvalue)
	{
		try
		{
			String oldval = getString(section, name, null);
			if (oldval == null || !oldval.equals(newvalue))
			{
				settings.setString(section, name, newvalue);
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			throw new PropertyRejectedException(ioe.toString());
		}
	}

	public static int getDebugFlags(Class clazz)
	{
		return getInt("Debug", clazz.getName(), 0);
	}

	public static boolean getDebug(Class clazz)
	{
		return getDebugFlags(clazz) != 0;
	}

	// PROPERTIES

	public static String[] parseSettingPair(String key) throws PropertyRejectedException
	{
		int pos = key.indexOf('/');
		if (pos < 0)
			throw new PropertyNotFoundException("Pair must have / separating Section/Key: " + key);
		return new String[] { key.substring(0, pos), key.substring(pos + 1)};
	}

	public Object getProp(String key)
	{
		String[] sect_key = parseSettingPair(key);
		String valstr = getString(sect_key[0], sect_key[1], null);
		if (valstr == null)
			return null;
		return PropertyUtil.parseValue(valstr);
	}

	public void setProp(String key, Object value)
	{
		String[] sect_key = parseSettingPair(key);
		String newvalstr = (value != null) ? PropertyUtil.toString(value) : null;
		setString(sect_key[0], sect_key[1], newvalstr);
	}

}
