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

public class GLOScrollBox
extends GLOTableContainer
{
	GLOVirtualBox box;
	GLOScrollBar hbar,vbar;

	public GLOScrollBox()
	{
		super(2,2);
		box = new GLOVirtualBox();
		add(box);
	}

	public GLOScrollBox(boolean hashbar, boolean hasvbar)
	{
		this();
		setHasHorizBar(hashbar);
		setHasVertBar(hasvbar);
	}

	public void setHasHorizBar(boolean b)
	{
		if (b != getHasHorizBar())
		{
			removeScrollBars();
			if (b)
				hbar = new GLOScrollBar(false);
			else
				hbar = null;
			redoScrollBars();
		}
	}

	public boolean getHasHorizBar()
	{
		return (hbar != null);
	}

	public void setHasVertBar(boolean b)
	{
		if (b != getHasVertBar())
		{
			removeScrollBars();
			if (b)
				vbar = new GLOScrollBar(true);
			else
				vbar = null;
			redoScrollBars();
		}
	}

	public boolean getHasVertBar()
	{
		return (vbar != null);
	}

	private void redoScrollBars()
	{
		if (vbar != null)
			add(vbar);
		if (hbar != null)
			add(hbar);
	}

	private void removeScrollBars()
	{
		if (vbar != null)
			remove(vbar);
		if (hbar != null)
			remove(hbar);
	}

	public GLOVirtualBox getBox()
	{
		return box;
	}

	public int getBoxWidth()
	{
		return box.getWidth();
	}

	public void setBoxWidth(int boxwidth)
	{
		box.setSize(boxwidth, box.getHeight());
	}

	public int getBoxHeight()
	{
		return box.getHeight();
	}

	public void setBoxHeight(int boxheight)
	{
		box.setSize(box.getWidth(), boxheight);
	}

	public void layout()
	{
		if (hbar != null)
		{
			hbar.setSize(box.getWidth(), 0);
		}
		if (vbar != null)
		{
			vbar.setSize(0,box.getHeight());
		}
		super.layout();
	}

	protected void updateScrollBars()
	{
		if (hbar != null)
		{
			hbar.setRange(0, box.getXRange());
			hbar.setValue(box.getXOffset());
		}
		if (vbar != null)
		{
			vbar.setRange(0, box.getYRange());
			vbar.setValue(box.getYOffset());
		}
	}

	public void render(GLOContext ctx)
	{
		updateScrollBars();
		super.render(ctx);
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOActionEvent)
		{
			GLOActionEvent ace = (GLOActionEvent)event;
			if (ace.getAction() == hbar)
			{
				box.setXOffset(hbar.getValue());
				return true;
			}
			else if (ace.getAction() == vbar)
			{
				box.setYOffset(vbar.getValue());
				return true;
			}
			else if (ace.getAction() == box)
			{
				updateScrollBars();
				return true;
			}
		}

		return super.handleEvent(event);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOScrollBox.class);

	static {
		prophelp.registerGet("box", "getBox");
		prophelp.registerGetSet("hbar", "HasHorizBar", boolean.class);
		prophelp.registerGetSet("vbar", "HasVertBar", boolean.class);
		prophelp.registerGetSet("boxwidth", "BoxWidth", int.class);
		prophelp.registerGetSet("boxheight", "BoxHeight", int.class);
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
