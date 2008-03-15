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

import java.awt.Dimension;
import java.util.Iterator;

import com.fasterlight.spif.*;

/**
  * A container that arranges components like the pages of
  * some wacky book, and shows one at a time.
  */
public class GLOPageStack
extends GLOContainer
{
	protected int cur_page = 0;

	public int getPageNum()
	{
		return cur_page;
	}

	public void setPageNum(int i)
	{
		this.cur_page = i;
	}

	public void addPageNum(int di)
	{
		this.cur_page += di;
		if (cur_page >= getChildCount())
			cur_page = 0;
		else if (cur_page < 0)
			cur_page = getChildCount()-1;
	}

	public void nextPage()
	{
		addPageNum(1);
	}

	public void prevPage()
	{
		addPageNum(-1);
	}

	public GLOComponent getPageComponent()
	{
		if (cur_page >= 0 && cur_page < getChildCount())
			return getChild(cur_page);
		else
			return null;
	}

	public boolean shows(GLOComponent cmpt)
	{
		return (cmpt != null && cmpt == getPageComponent());
	}

	public Dimension getMinimumSize()
	{
		Iterator it = getChildren();
		Dimension mind = new Dimension();
		while (it.hasNext())
		{
			GLOComponent child = (GLOComponent)it.next();
			Dimension d = child.getMinimumSize();
			if (d.width > mind.width)
				mind.width = d.width;
			if (d.height > mind.height)
				mind.height = d.height;
		}
		return mind;
	}

	public GLOComponent getComponentAt(int x, int y)
	{
		GLOComponent cmpt = getPageComponent();
		if (cmpt != null && cmpt.isVisible())
			return cmpt.getComponentAt(x,y);
		else
			return this;
	}

	public void layout()
	{
		// layout all the children, and determine the
		// size of this page stack (it must contain all components)
		Iterator it = getChildren();
		Dimension mind = new Dimension();
		while (it.hasNext())
		{
			GLOComponent child = (GLOComponent)it.next();
			child.layout();
			Dimension d = child.getSize();
			if (d.width > mind.width)
				mind.width = d.width;
			if (d.height > mind.height)
				mind.height = d.height;
		}
		this.setSize(mind.width, mind.height);

		// it might be nice to center all the children
		it = getChildren();
		while (it.hasNext())
		{
			GLOComponent child = (GLOComponent)it.next();
			child.setPosition((mind.width-child.getWidth())/2,
				(mind.height-child.getHeight())/2);
		}
	}

	public void render(GLOContext ctx)
	{
		GLOComponent cmpt = getPageComponent();
		if (cmpt != null)
		{
			ctx.renderComponent(cmpt);
		}
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOPageStack.class);

	static {
		prophelp.registerGetSet("pagenum", "PageNum", int.class);
		prophelp.registerSet("addpagenum", "addPageNum", int.class);
		prophelp.registerGet("pagecmpt", "getPageComponent");
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
