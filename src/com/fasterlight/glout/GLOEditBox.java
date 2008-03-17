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
  * A frame around an editable label.
  */
public class GLOEditBox
extends GLOFramedComponent
{
	protected Object actionobj = this;
	protected GLOLabel input;

	public GLOEditBox()
	{
		input = makeEditLabel();
		this.setContent(input);
	}

	public GLOEditBox(int minchars)
	{
		this();
		setMinChars(minchars);
	}

	public GLOEditBox(int minchars, String text)
	{
		this(minchars);
		input.setText(text);
	}

	protected GLOLabel makeEditLabel()
	{
		GLOLabel input = new GLOLabel();
		input.setEditable(true);
		return input;
	}

	public int getMinChars()
	{
		return input.getMinChars();
	}

	public void setMinChars(int minchars)
	{
		input.setMinChars(minchars);
	}

	public GLOLabel getInputLabel()
	{
		return input;
	}

	public void setText(String text)
	{
		input.setText(text);
	}

	public String getText()
	{
		return input.getText();
	}

	public void setPropertyForText(String text)
	{
		input.setPropertyForText(text);
	}

	public String getPropertyForText()
	{
		return input.getPropertyForText();
	}

	public Object getActionObject()
	{
		return actionobj;
	}

	public void setActionObject(Object o)
	{
		this.actionobj = o;
	}

	public GLOShader getFrameShader()
	{
		return getShader("editbox");
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOMouseButtonEvent)
		{
			if ( ((GLOMouseButtonEvent)event).isPressed(1))
			{
				event.getContext().requestFocus(input);
				return true;
			}
		}
		else if (event instanceof GLOActionEvent)
		{
			if (actionobj != null && ((GLOActionEvent)event).getAction() == input )
			{
				ctx.deliverEvent(new GLOActionEvent(ctx, actionobj), this);
				return true;
			}
		}
		return super.handleEvent(event);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOEditBox.class);

	static {
		prophelp.registerGetSet("text", "Text", String.class);
		prophelp.registerGetSet("text_prop", "PropertyForText", String.class);
		prophelp.registerGetSet("minchars", "MinChars", int.class);
		prophelp.registerGet("inputlabel", "getInputLabel");
		prophelp.registerGetSet("action", "ActionObject", Object.class);
		// todo: minchars
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
