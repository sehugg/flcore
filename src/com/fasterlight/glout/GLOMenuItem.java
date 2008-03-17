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


/**
  * Data definition of a menu item.
  * @see GLOMenu
  */
public class GLOMenuItem
{
	String text;
	GLOMenu submenu;
	Object action;

	public GLOMenuItem()
	{
	}
  	public GLOMenuItem(String text, Object action, GLOMenu submenu)
  	{
  		this.text = text;
  		this.action = action;
  		this.submenu = submenu;
  	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public Object getAction()
	{
		return action;
	}

	public void setAction(Object action)
	{
		this.action = action;
	}

	public GLOMenu getSubMenu()
	{
		return submenu;
	}

	public void setSubMenu(GLOMenu submenu)
	{
		this.submenu = submenu;
	}

	public String toString()
	{
		return "[" + text + ", " + action + ", " + submenu + "]";
	}
}
