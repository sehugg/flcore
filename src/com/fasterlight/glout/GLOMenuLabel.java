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
package com.fasterlight.glout;

import com.fasterlight.spif.*;

/**
  * A label that knows its a menu item.
  * todo
  */
public class GLOMenuLabel
extends GLOLabel
{
	public GLOMenuLabel()
	{
	}

	public GLOMenuLabel(String name)
	{
		setText(name);
	}

	public GLOMenuLabel(String name, int alignment)
	{
		setText(name);
		setAlignment(alignment);
	}

	public GLOMenuLabel(int minchars)
	{
		setMinChars(minchars);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOMenuLabel.class);

	static {
	}

	public Object getProp(String key)
	{
		Object o = prophelp.getProp(this, key);
		if (o == null)
			o = super.getProp(key);
		return o;
	}

	public void setProp(String key, Object value)
	{
		try {
			prophelp.setProp(this, key, value);
		} catch (PropertyRejectedException e) {
			super.setProp(key, value);
		}
	}

}
