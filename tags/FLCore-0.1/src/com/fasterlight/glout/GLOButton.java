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
  * A pushable button -- in actuality, just a framed component
  * that contains a label.
  * todo: push ti
  */
public class GLOButton
extends GLOFramedComponent
{
	protected boolean depressed = false;
	protected Object actionobj = this;
	protected PropertyEvaluator prop_bool,prop_value;
	protected String btntext;

	protected int flags;

	public static final int NEGATE     = 1;
	public static final int TOGGLE     = 2;
	public static final int PUSHBUTTON = 4;
	public static final int ERRORON    = 8;
	public static final int INHIBIT    = 16;
	public static final int CLEARABLE  = 32;
	public static final int CONTINUOUS = 64;

	public static final String onupshader = "button-on-up";
	public static final String offupshader = "button-off-up";
	public static final String ondownshader = "button-on-down";
	public static final String offdownshader = "button-off-down";

	public GLOButton()
	{
	}

	public GLOButton(String text)
	{
		setText(text);
	}

	public GLOButton(String text, Object action)
	{
		this(text);
		setActionObject(action);
	}

	// pushbutton means it delivers an event when pressed,
	// and a null action when released
	public GLOButton(String text, Object action, boolean pushbutton)
	{
		this(text, action);
		if (pushbutton)
			flags |= PUSHBUTTON;
	}

	public void setActionObject(Object o)
	{
		this.actionobj = o;
	}

	public Object getActionObject()
	{
		return actionobj;
	}

	public String getBooleanProperty()
	{
		return getKey(prop_bool);
	}

	public void setBooleanProperty(String prop_bool)
	{
		this.prop_bool = new PropertyEvaluator(prop_bool);
	}

	public String getValueProperty()
	{
		return getKey(prop_value);
	}

	public void setValueProperty(String prop_value)
	{
		this.prop_value = new PropertyEvaluator(prop_value);
	}

	public boolean getState()
	{
		boolean b=false;
		if (prop_bool != null)
		{
			Object o = getForPropertyKey(prop_bool);
			if (o == null && (flags&ERRORON) != 0)
				b = true;
			else
				b = PropertyUtil.toBoolean(o);
		}
		else if (prop_value != null)
		{
			Object o = getForPropertyKey(prop_value);
			b = PropertyUtil.equals(o, actionobj);
		}
		b ^= ((flags&NEGATE) != 0);
		return b;
	}

	public void setState(boolean b)
	{
		if ((flags&NEGATE) != 0 ^ (flags&CLEARABLE) != 0)
			b = !b;
		if (prop_bool != null)
		{
			setForPropertyKey(prop_bool, b ? Boolean.TRUE : Boolean.FALSE);
		}
		else if (prop_value != null && b)
		{
			setForPropertyKey(prop_value, actionobj);
		}
	}


	public int getFlags()
	{
		return flags;
	}

	public void setFlags(int flags)
	{
		this.flags = flags;
	}

	public void setText(String text)
	{
		this.btntext = text;
		GLOLabel label = new GLOLabel(text);
		setContent(label);
	}

	public String getText()
	{
		return btntext;
	}

	public GLOShader getFrameShader()
	{
		return getShader(getState() ? (depressed ? ondownshader : onupshader) : (depressed ? offdownshader : offupshader));
	}

	public void render(GLOContext ctx)
	{
		// if CONTINUOUS, send events
		if (depressed && (flags & CONTINUOUS) != 0)
		{
			notifyAction(actionobj);
			setState(true);
		}

		super.render(ctx);
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			if (mbe.isPressed(1))
			{
				if ((flags & PUSHBUTTON) != 0)
				{
					notifyAction(actionobj);
					setState(true);
				}
				event.getContext().requestFocus(this);
				depressed = true;
				beginDrag(event);
				return true;
			}
			else if (mbe.isReleased(1))
			{
				endDrag(event);
				if (depressed)
				{
					depressed = false;
					if ((flags & PUSHBUTTON) != 0)
					{
						notifyAction(null);
						setState(false);
						return true;
					}
					if (this.containsPoint(mbe.x, mbe.y))
					{
						notifyAction(actionobj);
						if ((flags & TOGGLE) != 0)
							setState(!getState());
						else
							setState(true);
						return true;
					}
				}
			}
		}

		return super.handleEvent(event);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOButton.class);

	static {
		prophelp.registerGetSet("text", "Text", String.class);
		prophelp.registerGetSet("action", "ActionObject", Object.class);
		prophelp.registerGetSet("state", "State", boolean.class);
		prophelp.registerGetSet("bool_prop", "BooleanProperty", String.class);
		prophelp.registerGetSet("value_prop", "ValueProperty", String.class);
		prophelp.registerGetSet("flags", "Flags", int.class);
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
