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
  * Pops up a menu, and then gets out when it loses focus
  */
public class GLOMenuPopup
extends GLOFramedComponent
{
	protected GLOMenu menu;
	protected GLOMenuTable menutab;
	protected boolean temporary = false;

	protected static GLOMenuPopup current_popup; //todo: static crap

	//

	public GLOMenuPopup()
	{
		super();
	}

	public void setMenu(GLOMenu menu)
	{
		this.menu = menu;
	}

	public GLOMenu getMenu()
	{
		return menu;
	}

	public boolean isTemporary()
	{
		return temporary;
	}

	public void setTemporary(boolean temporary)
	{
		this.temporary = temporary;
	}

	public GLOShader getFrameShader()
	{
		return getShader("menuframe");
	}

	public void setVisible(boolean b)
	{
		if (b)
			closePopup();
		super.setVisible(b);
		if (b)
			current_popup = this;
	}

	void closeInternal()
	{
		if (temporary)
		{
			if (menutab != null)
			{
				if (getParent() != null)
					((GLOContainer)getParent()).remove(this);
				this.remove(menutab);
			}
			menutab = null;
		} else {
			setVisible(false);
		}
	}

	public static void closePopup()
	{
		if (current_popup != null)
		{
			current_popup.closeInternal();
			current_popup = null;
		}
	}

	public void open(GLOContext ctx, int x, int y)
	{
		closePopup();
		if (menu == null)
			return;
		setTemporary(true);
		current_popup = this;
		if (menutab == null)
		{
			menutab = new GLOMenuTable(menu);
			this.add(menutab);
			layout();
		}
		ctx.add(this);
		this.setPosition(x, y);
		ctx.requestFocus(menutab);
	}

	//

/*
	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOFocusEvent)
		{
			GLOFocusEvent focev = (GLOFocusEvent)event;
			return true;
		}
		else if (event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			int sel = getMenuItemIndex(mbe.getX(), mbe.getY());
			if ( mbe.isPressed(1) )
			{
				event.getContext().requestFocus(this);
				//event.getContext().beginEventCapture(this, GLOMouseButtonEvent.class);
				menudragging = true;
				if (sel >= 0)
				{
					setSelectedIndex(sel);
					openMenu(sel);
				}
				return true;
			}
			else if ( mbe.isReleased(1) )
			{
				//event.getContext().endEventCapture(this, GLOMouseButtonEvent.class);
				menudragging = false;
				GLOMenuItem selitem = getMenuItem(sel);
				if (selitem != null)
				{
					activateItem(selitem);
					closeAllMenus();
					return true;
				} else
					closeAllMenus();
			}
		}
		else if (event instanceof GLOMouseMovedEvent && menudragging)
		{
			GLOMouseMovedEvent mbe = (GLOMouseMovedEvent)event;
			int sel = getMenuItemIndex(mbe.getX(), mbe.getY());
			if (sel >= 0)
			{
				setSelectedIndex(sel);
				return true;
			}
		}

		return super.handleEvent(event);
	}
*/

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOMenuPopup.class);

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
