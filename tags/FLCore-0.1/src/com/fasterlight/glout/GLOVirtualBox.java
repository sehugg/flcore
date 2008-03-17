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

import java.awt.*;

import com.fasterlight.spif.*;


public class GLOVirtualBox
extends GLOContainer
{
	int vxs,vys; // virtual screen area
	int xofs,yofs; // scroll offset
	boolean draggable = false;

	//

	public void setVirtualSize(int vx, int vy)
	{
		this.vxs = vx;
		this.vys = vy;
		setOffset(xofs, yofs);
	}

	public void setVirtualSize(Dimension dim)
	{
		setVirtualSize(dim.width, dim.height);
	}

	public void setVirtualWidth(int w)
	{
		this.vxs = w;
	}

	public int getVirtualWidth()
	{
		return vxs;
	}

	public void setVirtualHeight(int h)
	{
		this.vys = h;
	}

	public int getVirtualHeight()
	{
		return vys;
	}

	public Dimension getVirtualSize()
	{
		return new Dimension(vxs, vys);
	}

	public void setDraggable(boolean drag)
	{
		draggable = drag;
	}

	public boolean getDraggable()
	{
		return draggable;
	}

	public int getXRange()
	{
		return vxs-getWidth();
	}

	public int getYRange()
	{
		return vys-getHeight();
	}

	public int getXOffset()
	{
		return xofs;
	}

	public int getYOffset()
	{
		return yofs;
	}

	public void setOffset(int xo, int yo)
	{
		setXOffset(xo);
		setYOffset(yo);
	}

	public void setOffset(Point ofs)
	{
		setOffset(ofs.x, ofs.y);
	}

	public void setXOffset(int xo)
	{
		if (xo > getXRange()) xo = getXRange();
		if (xo < 0) xo = 0;
		if (xo == xofs)
			return;
		xofs = xo;
		computeOrigin();
		notifyDataChanged();
	}

	public void setYOffset(int yo)
	{
		if (yo > getYRange()) yo = getYRange();
		if (yo < 0) yo = 0;
		if (yo == yofs)
			return;
		yofs = yo;
		computeOrigin();
		notifyDataChanged();
	}

	public boolean needsClipping()
	{
		return true;
	}

	protected void offsetPoint(Point p)
	{
		p.x -= xofs;
		p.y -= yofs;
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (draggable && event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			if (mbe.isPressed(1))
			{
				beginDrag(event);
				return true;
			}
			else if (mbe.isReleased(1))
			{
				endDrag(event);
				return true;
			}
		}
		else if (event instanceof GLOMouseMovedEvent)
		{
			if (isDragging())
			{
				GLOMouseMovedEvent mme = (GLOMouseMovedEvent)event;
				Point ofs = mme.getOffset();
				setOffset(xofs-ofs.x, yofs-ofs.y);
				return true;
			}
		}

		return super.handleEvent(event);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOVirtualBox.class);

	static {
		prophelp.registerGetSet("draggable", "Draggable", boolean.class);
		//todo
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
