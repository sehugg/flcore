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
  * A pushable button that can display one of two bitmaps.
  * todo: up & down bitmaps
  */
public class GLOBitmapButton
extends GLOBitmap
{
	protected boolean depressed = false;
	protected Object actionobj = this;
	protected boolean pushbutton = false;

	public GLOBitmapButton()
	{
	}

	public GLOBitmapButton(String texname)
	{
		setTextureName(texname);
	}

	public GLOBitmapButton(String texname, Object action)
	{
		this(texname);
		setActionObject(action);
	}

	// pushbutton means it delivers an event when pressed,
	// and a null action when released
	public GLOBitmapButton(String text, Object action, boolean pushbutton)
	{
		this(text, action);
		this.pushbutton = pushbutton;
	}

	public void setActionObject(Object o)
	{
		this.actionobj = o;
	}

	public Object getActionObject()
	{
		return actionobj;
	}

	public boolean isDepressed()
	{
		return depressed;
	}

	// todo: gotta sync GLOButton and GLOBitmapButton
	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			if (mbe.isPressed(1))
			{
				if (pushbutton)
				{
					notifyAction(actionobj);
				}
				getContext().requestFocus(this);
				depressed = true;
				getContext().beginEventCapture(this, GLOMouseEvent.class);
				return true;
			}
			else if (mbe.isReleased(1))
			{
				getContext().endEventCapture(this, GLOMouseEvent.class);
				if (depressed)
				{
					depressed = false;
					if (pushbutton)
					{
						notifyAction(null);
						return true;
					}
					if (this.containsPoint(mbe.x, mbe.y))
					{
						notifyAction(actionobj);
						return true;
					}
				}
			}
		}

		return super.handleEvent(event);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOBitmapButton.class);

	static {
		prophelp.registerGetSet("action", "ActionObject", Object.class);
		prophelp.registerGet("depressed", "isDepressed");
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
