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
  */
public class GLOBooleanLight
extends GLOFramedComponent
{
	private String boolprop;
	private boolean negate, clearable, erroron;
	private boolean inhibited;

	public GLOBooleanLight()
	{
	}

	public String getBooleanProperty()
	{
		return boolprop;
	}

	public void setBooleanProperty(String boolprop)
	{
		this.boolprop = boolprop;
	}

	public boolean getNegate()
	{
		return negate;
	}

	public void setNegate(boolean negate)
	{
		this.negate = negate;
	}

	public boolean getClearable()
	{
		return clearable;
	}

	public void setClearable(boolean clearable)
	{
		this.clearable = clearable;
	}

	public boolean getErrorOn()
	{
		return erroron;
	}

	public void setErrorOn(boolean erroron)
	{
		this.erroron = erroron;
	}

	public void clear()
	{
		inhibited = true;
	}

	public boolean getValue()
	{
		if (boolprop != null)
		{
			Object o = getForPropertyKey(boolprop);
			boolean b;
			if (o == null && erroron)
				b = true;
			else
				b = PropertyUtil.toBoolean(o) ^ negate;
			// if light is inhibited, we return false if condition is positive
			// and we uninhibit when the condition goes to negative
			if (inhibited)
			{
				if (b)
					b = false;
				else
					inhibited = false;
			}
			return b;
		} else
			return false;
	}

	public GLOShader getFrameShader()
	{
		return getShader(getValue() ? "light-on" : "light-off");
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			if (mbe.isPressed(1) && clearable)
			{
				clear();
			}
		}

		return super.handleEvent(event);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOBooleanLight.class);

	static {
		prophelp.registerGetSet("bool_prop", "BooleanProperty", String.class);
		prophelp.registerGetSet("negate", "Negate", boolean.class);
		prophelp.registerGetSet("erroron", "ErrorOn", boolean.class);
		prophelp.registerGetSet("clearable", "Clearable", boolean.class);
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
