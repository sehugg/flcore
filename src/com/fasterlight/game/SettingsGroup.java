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

import java.util.*;

/**
  * A class that helps other classes manage their static values.
  * Create a SettingsGroup in a static initializer, then implement
  * the updateSettings() method to retrieve your values when they change.
  */
public abstract class SettingsGroup
extends Observable
{
	Class clazz;
	String section;

	//

	/**
	  * Associate with a Class (clazz),
	  * then immediately call updateSettings().
	  */
	protected SettingsGroup(Class clazz)
	{
		this(clazz, clazz.getName());
	}

	/**
	  * Associate with a Class (clazz) and section,
	  * then immediately call updateSettings().
	  */
	protected SettingsGroup(Class clazz, String section)
	{
		this.clazz = clazz;
		this.section = section;
		register();
		updateSettings();
	}

	public abstract void updateSettings();

	public String getString(String section, String name, String defvalue)
	{
		return Settings.getString(section, name, defvalue);
	}

	public int getInt(String section, String name, int defvalue)
	{
		return Settings.getInt(section, name, defvalue);
	}

	public long getLong(String section, String name, long defvalue)
	{
		return Settings.getLong(section, name, defvalue);
	}

	public float getFloat(String section, String name, float defvalue)
	{
		return Settings.getFloat(section, name, defvalue);
	}

	public double getDouble(String section, String name, double defvalue)
	{
		return Settings.getDouble(section, name, defvalue);
	}

	public boolean getBoolean(String section, String name, boolean defvalue)
	{
		return Settings.getBoolean(section, name, defvalue);
	}

	public String getString(String name, String defvalue)
	{
		return Settings.getString(section, name, defvalue);
	}

	public int getInt(String name, int defvalue)
	{
		return Settings.getInt(section, name, defvalue);
	}

	public long getLong(String name, long defvalue)
	{
		return Settings.getLong(section, name, defvalue);
	}

	public float getFloat(String name, float defvalue)
	{
		return Settings.getFloat(section, name, defvalue);
	}

	public double getDouble(String name, double defvalue)
	{
		return Settings.getDouble(section, name, defvalue);
	}

	public boolean getBoolean(String name, boolean defvalue)
	{
		return Settings.getBoolean(section, name, defvalue);
	}

	//

	private void register()
	{
		if (groups.get(clazz) != null)
			System.out.println("Warning, already have group registered for class \"" + clazz.getName() + "\"");
		groups.put(clazz, this);
	}

	private static HashMap groups = new HashMap();

	/**
	  * Calls updateSettings() for all registered
	  * SettingsGroup objects.
	  */
	public static void updateAll()
	{
		Iterator it = groups.values().iterator();
		while (it.hasNext())
		{
			SettingsGroup group = (SettingsGroup)it.next();
			group.updateSettings();
			group.notifyObservers();
		}
	}
}
