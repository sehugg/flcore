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
  * Shows a bitmap.
  */
public class GLOBooleanSwitch
extends GLOBitmap
{
	private PropertyEvaluator boolprop;
	private String up_bitmap = "switch-up.png";
	private String mid_bitmap = "switch-mid.png";
	private String down_bitmap = "switch-down.png";
	private boolean clickToggle = false;

	//

	public GLOBooleanSwitch()
	{
	}

	public String getTextureName()
	{
		Object b = getForPropertyKey(boolprop);
		if (Boolean.TRUE.equals(b))
			return up_bitmap;
		else if (Boolean.FALSE.equals(b))
			return down_bitmap;
		else
			return mid_bitmap;
	}

	public String getBooleanProperty()
	{
		return getKey(boolprop);
	}

	public void setBooleanProperty(String boolprop)
	{
		this.boolprop = new PropertyEvaluator(boolprop);
	}


	public String getTrueBitmap()
	{
		return up_bitmap;
	}

	public void setTrueBitmap(String up_bitmap)
	{
		this.up_bitmap = up_bitmap;
	}

	public String getFalseBitmap()
	{
		return down_bitmap;
	}

	public void setFalseBitmap(String down_bitmap)
	{
		this.down_bitmap = down_bitmap;
	}

	public String getMaybeBitmap()
	{
		return mid_bitmap;
	}

	public void setMaybeBitmap(String mid_bitmap)
	{
		this.mid_bitmap = mid_bitmap;
	}

	public boolean getValue()
	{
		return PropertyUtil.toBoolean(getForPropertyKey(boolprop));
	}

	public void setValue(boolean b)
	{
		if (boolprop != null)
			setForPropertyKey(boolprop, b ? Boolean.TRUE : Boolean.FALSE);
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			if (mbe.isPressed(1))
			{
				if (clickToggle)
				{
					// toggle with each click
					setValue(!getValue());
				} else {
					// if clicked upper 1/2, set to TRUE
					setValue (mbe.getY() < getOrigin().y+getHeight()/2);
					return true;
				}
			}
		}
		return super.handleEvent(event);
	}

	public boolean getClickToggle()
	{
		return clickToggle;
	}

	public void setClickToggle(boolean clickToggle)
	{
		this.clickToggle = clickToggle;
	}


	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOBooleanSwitch.class);

	static {
		prophelp.registerGetSet("bool_prop", "BooleanProperty", String.class);
		prophelp.registerGetSet("true_bitmap", "TrueBitmap", String.class);
		prophelp.registerGetSet("false_bitmap", "FalseBitmap", String.class);
		prophelp.registerGetSet("maybe_bitmap", "MaybeBitmap", String.class);
		prophelp.registerGetSet("clicktoggle", "ClickToggle", boolean.class);
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
