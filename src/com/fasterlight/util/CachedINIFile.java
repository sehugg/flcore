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

import java.io.*;
import java.util.*;

public class CachedINIFile extends INIFile
{
	protected Map cache = new HashMap();

	public CachedINIFile(String filename)
	{
		super(filename);
	}

	public CachedINIFile(File file)
	{
		super(file);
	}

	/**
	 * Copies an InputStream to a temporary file and uses that
	 */
	public CachedINIFile(InputStream in) throws IOException
	{
		super(in);
	}

	public String setString(String section, String key, String newvalue) throws IOException
	{
		cache.remove(section);
		return super.setString(section, key, newvalue);
	}

	public String getString(String section, String key, String defvalue) throws IOException
	{
		String tmp = getSection(section).getProperty(key);
		if (tmp == null)
			tmp = defvalue;
		return tmp;
	}

	public Properties getSection(String section) throws IOException
	{
		Properties p;
		if (cache.containsKey(section))
		{
			p = (Properties) cache.get(section);
		} else
		{
			p = super.getSection(section);
			cache.put(section, p);
		}
		try
		{
			p = (Properties) p.clone();
		} catch (Exception e)
		{
		}
		return p;
	}

	/*
	   public Vector getSectionNames()
	   throws IOException
	   {
	   }
	*/

}
