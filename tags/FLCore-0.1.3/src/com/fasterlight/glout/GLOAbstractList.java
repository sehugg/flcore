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

/**
  * A class that may be extended to display items in a list format,
  * optionally contained in a scroll box.
  * @see GLOStringList
  * @see GLOConsole
  */
public abstract class GLOAbstractList
extends GLOComponent
{
	public abstract boolean isSelected(int row);

	public abstract void selectRow(int row);

	public abstract int getRowHeight();

	public abstract int getRowWidth();

	public abstract int getRowCount();

	public abstract void drawRow(GLOContext ctx, int row, int xpos, int ypos, boolean selected);

	public boolean isClickable()
	{
		return true;
	}

	public void layout()
	{
		super.layout();
		setScrollBoxSize();
	}

	protected void setScrollBoxSize()
	{
		if (getParent() instanceof GLOVirtualBox)
		{
			GLOVirtualBox vb = (GLOVirtualBox)getParent();
			int width = getRowWidth();
			int height = getRowHeight()*getRowCount();
			this.setSize(width, height);
			vb.setVirtualSize(width, height);
		}
	}

/***
	public void makeRowVisible(int row)
	{
		Point o = getOrigin();
		Point po = getParent().getOrigin();
		int rh = getRowHeight();
		// todo
	}
***/

	public void centerOnRow(int row)
	{
		if (getParent() instanceof GLOVirtualBox)
		{
			GLOVirtualBox vb = (GLOVirtualBox)getParent();
			int rh = getRowHeight();
			int newy = row*rh - (vb.getHeight()+rh)/2;
			int oldy = vb.getYOffset();
			vb.setYOffset( oldy + (newy-oldy)/2 );
			// todo:  make this work
		}
	}

	public void render(GLOContext ctx)
	{
		setScrollBoxSize();

		Point o = getOrigin();
		int y = o.y;
		int py = getParent().getOrigin().y;
		int ph = getParent().getHeight();
		int rh = getRowHeight();

		int lorow = (py-y)/rh;
		int hirow = (py-y+ph+rh)/rh;
		hirow = Math.min(hirow, getRowCount());

		y += lorow*rh;
		y += getRowHeight();
		for (int i=lorow; i<hirow; i++)
		{
			boolean selected = isSelected(i);
			drawRow(ctx, i, o.x, y, selected);
			y += rh;
		}
	}

	public int getRow(int x, int y)
	{
		int row = (y/getRowHeight());
		if (row >= getRowCount())
			row = -1;
		return row;
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (isClickable() && event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			if (mbe.isReleased(1))
			{
				event.getContext().requestFocus(this);
				Point p = new Point(mbe.x, mbe.y);
				scrn2local(p);
				System.out.println("Clicked " + p);
				// figure out what row clicked
				int row = getRow(p.x, p.y);
				if (row >= 0)
				{
					selectRow(row);
					notifyDataChanged();
					return true;
				}
			}
		}
		else if (isClickable() && event instanceof GLOKeyEvent
				&& ((GLOKeyEvent) event).isPressed())
		{
			GLOKeyEvent ev = (GLOKeyEvent)event;
			switch (ev.getKeyCode())
			{
			case GLOKeyEvent.VK_UP:
			{
				for (int i=1; i<getRowCount(); i++)
				{
					if (isSelected(i))
					{
						selectRow(i-1);
						centerOnRow(i-1);
						notifyDataChanged();
						return true;
					}
				}
				break;
			}
			case GLOKeyEvent.VK_DOWN:
			{
				for (int i=0; i<getRowCount()-1; i++)
				{
					if (isSelected(i))
					{
						selectRow(i+1);
						centerOnRow(i+1);
						notifyDataChanged();
						return true;
					}
				}
				break;
			}
			}
		}
		else if (event instanceof GLOFocusEvent)
		{
			return true;
		}

		return super.handleEvent(event);
	}

}
