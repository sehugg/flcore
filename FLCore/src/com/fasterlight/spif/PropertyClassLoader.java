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

/**
  * A class that loads new instance of classes
  * in the format 'com/foo/bar'
  */
public class PropertyClassLoader
implements PropertyAware
{
	public Object getProp(String key)
	{
		try {
			String name = key.replace('/','.');
			return Class.forName(name).newInstance();
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}

	public void setProp(String key, Object value)
	{
		throw new PropertyRejectedException("This object cannot be set");
	}
}
