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

import java.awt.Point;
import java.io.IOException;
import java.util.*;

import com.fasterlight.spif.*;

/**
  * A specialized sort of table that implements a menu (horizontal or vertical)
  */
public class GLOMenuTable
extends GLOTableContainer
{
	protected GLOMenu menu;
	protected boolean horiz;
	protected int selindex = -1;
	protected GLOComponent openmenu;
	protected GLOMenuTable submenutab;
	protected GLOMenuTable parentmenu;

	// todo: kinda wank
	protected static boolean menudragging;

	//

	public GLOMenuTable()
	{
		super();
	}

	public GLOMenuTable(GLOMenu menu, GLOMenuTable parent)
	{
		this(menu);
		this.parentmenu = parent;
	}

	public GLOMenuTable(GLOMenu menu)
	{
		this();
		this.menu = menu;
	}

	public GLOMenu getMenu()
	{
		return menu;
	}

	public void loadMenu(String path)
	throws IOException
	{
		menu = new GLOMenu();
		menu.load(path);
	}

	public GLOContainer getMenuContainer()
	{
		GLOComponent cmpt = this.getParent();
		while (cmpt != null)
		{
			if ((cmpt instanceof GLOContext) || (cmpt instanceof GLOVanishingPane))
				return (GLOContainer)cmpt;
			cmpt = cmpt.getParent();
		}
		throw new RuntimeException(this + " is not in a context");
	}

	public GLOShader getShader(String name)
	{
		return (parentmenu != null) ? parentmenu.getShader(name) : super.getShader(name);
	}

	public boolean isHorizontal()
	{
		return horiz;
	}

	public void setHorizontal(boolean horiz)
	{
		this.horiz = horiz;
	}

	private int getRowOffset(int row)
	{
		GLOComponent child = getChild(row*cols);
		if (child == null)
			return -1;
		return child.getY();
	}

	private int getRowHeight(int row)
	{
		GLOComponent child = getChild(row*cols);
		if (child == null)
			return 0;
		return child.getHeight();
	}

	private int getColOffset(int col)
	{
		GLOComponent child = getChild(col);
		if (child == null)
			return -1;
		return child.getX();
	}

	private int getColWidth(int col)
	{
		GLOComponent child = getChild(col);
		if (child == null)
			return 0;
		return child.getWidth();
	}

	public int getNumItems()
	{
		return horiz ? getChildCount()/rows : getChildCount()/cols;
	}

	public int getSelectedIndex()
	{
		return selindex;
	}

	public void setSelectedIndex(int i)
	{
		if (i >= getNumItems())
			i = 0;
		else if (i < 0)
			i = getNumItems()-1;

		if (i != selindex)
		{
			selindex = i;
			if (isOpened())
			{
				closeMenu();
				openMenu(i);
			}
		}
	}

	public void activateItem(GLOMenuItem item)
	{
		if (item.getAction() != null)
		{
			notifyAction(item.getAction());
		}
	}

	public GLOMenuItem getSelectedItem()
	{
		return getMenuItem(getSelectedIndex());
	}

	public GLOMenuItem getMenuItem(int i)
	{
		if (menu == null)
			return null;
		List items = menu.getItems();
		if (i < 0 || i >= items.size())
			return null;
		return (GLOMenuItem)items.get(i);
	}

	public boolean isOpened()
	{
		return (openmenu != null);
	}

	public void closeAllMenus()
	{
		closeMenu();
		if (parentmenu != null)
			parentmenu.closeAllMenus();
		if (getParent() instanceof GLOMenuPopup)
			getParent().setVisible(false);
	}

	public void closeMenu()
	{
		if (openmenu != null)
		{
			submenutab.closeMenu();
			ctx.requestFocus(this);
			getMenuContainer().remove(openmenu);
			openmenu = null;
			submenutab = null;
		}
	}

	public void openMenu()
	{
		openMenu(getSelectedIndex());
	}

	public void openMenu(int i)
	{
		GLOMenuItem item = getMenuItem(i);
		if (item == null)
			return;

		closeMenu();

		GLOMenu submenu = item.submenu;
		if (submenu == null)
			return;

		submenutab = new GLOMenuTable(submenu, this);
		openmenu = new GLOFramedComponent(submenutab);
		openmenu.addShader("frame", this.getShader("frame"));

		Point o = getOrigin();
		if (horiz)
		{
			openmenu.setPosition(getColOffset(i)+o.x, o.y+h1);
		} else {
			openmenu.setPosition(o.x+w1, getRowOffset(i)+o.y);
		}
		getMenuContainer().add(openmenu);
		openmenu.layout();

		ctx.requestFocus(submenutab);
	}

	//

	protected void buildMenu()
	{
		if (menu == null)
			return;

		GLOCommandManager cmdmgr = getContext().getCommandManager();

		removeAllChildren();

		List items = menu.getItems();
		if (horiz)
		{
			setNumColumns(items.size());
		} else {
			setNumColumns(2);
			setNumRows(items.size());
			setColumnFlags(0, HALIGN_LEFT);
			setColumnFlags(1, HALIGN_RIGHT);
			setColumnPadding(12);
		}

		Iterator it = items.iterator();
		while (it.hasNext())
		{
			GLOMenuItem item = (GLOMenuItem)it.next();
			GLOMenuLabel label = new GLOMenuLabel();
			label.setText(item.text);
			this.add(label);
			// for vertical menus, add menu shortcut label
			if (!horiz)
			{
				String text = (item.getSubMenu() == null) ? "" : "\u00bb";
				if (cmdmgr != null && item.getAction() instanceof String)
				{
					int keycode = cmdmgr.getKeyCodeForCommand(item.getAction().toString());
					if (keycode >= 0)
					{
						text = cmdmgr.keyCodeToString(keycode);
					}
				}
				GLOLabel sclab = new GLOLabel();
				sclab.setText(text);
				this.add(sclab);
			}
		}
	}

	public void layout()
	{
		buildMenu();
		super.layout();
	}

	//

	public void render(GLOContext ctx)
	{
		super.render(ctx);

		// draw invert box
		if (selindex >= 0)
		{
			int x,y,w,h;
			setShader("menusel");
			Point o = getOrigin();
			if (horiz)
			{
				x = getColOffset(selindex) + o.x;
				w = getColWidth(selindex);
				y = o.y;
				h = h1;
			} else {
				x = o.x;
				w = w1;
				y = getRowOffset(selindex) + o.y;
				h = getRowHeight(selindex);
			}
			drawBox(ctx, x, y, w, h);
		}
	}

	//

	public int getMenuItemIndex(int x, int y)
	{
		// todo: not right, need to take into account entire row or col
		for (int i=0; i<getChildCount(); i++)
		{
			GLOComponent cmpt = getChild(i);
			if (cmpt.containsPoint(x,y))
				return horiz ? i : i/cols;
		}
		return -1;
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOFocusEvent)
		{
			GLOFocusEvent focev = (GLOFocusEvent)event;
			return true;
		}
		else if (event instanceof GLOKeyEvent)
		{
			GLOKeyEvent keyev = (GLOKeyEvent)event;
			if (keyev.pressed)
			{
				switch (keyev.keycode)
				{
					// todo
					case GLOKeyEvent.VK_LEFT:
						if (horiz)
						{
							setSelectedIndex(getSelectedIndex()-1);
							return true;
						} else {
							if (isOpened())
							{
								closeMenu();
								return true;
							} else {
								if (parentmenu != null)
								{
									return parentmenu.handleEvent(event);
								}
							}
						}
						break;
					case GLOKeyEvent.VK_RIGHT:
						if (horiz)
						{
							setSelectedIndex(getSelectedIndex()+1);
							return true;
						} else {
							if (!isOpened())
							{
								openMenu();
								if (isOpened())
									return true;
							}
							if (parentmenu != null)
							{
								return parentmenu.handleEvent(event);
							}
						}
					case GLOKeyEvent.VK_UP:
						if (!horiz) {
							setSelectedIndex(getSelectedIndex()-1);
							return true;
						}
						break;
					case GLOKeyEvent.VK_DOWN:
						if (!horiz)
						{
							setSelectedIndex(getSelectedIndex()+1);
							return true;
						} else {
							openMenu();
							return true;
						}
					case GLOKeyEvent.VK_ESCAPE:
						if (parentmenu != null)
						{
							parentmenu.closeMenu();
							return true;
						}
						break;
					case GLOKeyEvent.VK_ENTER:
						GLOMenuItem sel = getSelectedItem();
						if (sel != null)
						{
							openMenu();
							activateItem(sel);
							return true;
						}
						break;
				}
			}
		}
		else if (event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			int sel = getMenuItemIndex(mbe.getX(), mbe.getY());
			GLOMenuItem selitem = getMenuItem(sel);

			if ( mbe.isPressed(1) )
			{
				event.getContext().requestFocus(this);
				menudragging = true;
				if (sel >= 0 && (sel != selindex || !isOpened()))
				{
					setSelectedIndex(sel);
					openMenu(sel);
				} else
					closeAllMenus();
				return true;
			}
			else if ( mbe.isReleased(1) )
			{
				menudragging = false;
				System.out.println("selitem = " + selitem);
				if (selitem != null)
				{
					System.out.println("submenu = " + selitem.getSubMenu());
					if (selitem.getSubMenu() == null)
					{
						activateItem(selitem);
						closeAllMenus();
					}
				} else {
					closeAllMenus();
				}
				return true;
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

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOMenuTable.class);

	static {
		prophelp.registerGet("horiz", "isHorizontal");
		prophelp.registerSet("horiz", "setHorizontal", boolean.class);
		prophelp.registerSet("loadmenu", "loadMenu", String.class);
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
