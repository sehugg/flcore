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

/**
  * A framed component that can be moved around with the mouse.
  */
public class GLOWindow
extends GLOFramedComponent
{
	protected int dragq; // quadrant being dragged
	protected int flags;
	protected boolean maximized;
	protected Point stdpos;
	protected Dimension stdsize;
	protected GLOComponent resizechild;

	public static final int RESIZEABLE = 1;
	public static final int RESIZE_CHILD = 2;
	public static final int NOLAYOUT = 4;

	// todo: resize arbitrary child

	public void setFlags(int flags)
	{
		this.flags = flags;
	}

	// don't allow it to go off-screen
	public void setPosition(int x, int y)
	{
      if (getParent() != null)
      {
   		int parw = getParent().getWidth();
	   	int parh = getParent().getHeight();
		   if (parw > 0 && parh > 0)
   		{
	   		if (x < bxs-getWidth()) x = bxs-getWidth();
		   	if (x > parw-bxs) x = parw-bxs;
   			if (y < bys-getHeight()) y = bys-getHeight();
	   		if (y > parh-bys) y = parh-bys;
		   }
      }
		super.setPosition(x,y);
	}

	public void setResizeChild(GLOComponent child)
	{
		resizechild = child;
		if (child != null)
			flags |= RESIZE_CHILD;
		else
			flags &= ~RESIZE_CHILD;
	}

	protected int getQuadrant(int x, int y)
	{
		if (!containsPoint(x,y))
			return -1;
		Point o = getOrigin();
		x -= o.x;
		y -= o.y;
		int q = 0;
		if (x >= bxs && x < w1-bxs)
			q += 1;
		else if (x >= w1-bxs)
			q += 2;
		if (y >= bys && y < h1-bys)
			q += 3;
		else if (y >= h1-bys)
			q += 6;
		return q;
	}

	public void layout()
	{
		// if maximized, set to fill entire screen
		if (maximized && getContent() != null)
		{
			getContent().setPosition(0,0);
			getContent().setSize(getParent().getWidth(), getParent().getHeight());
		}
		super.layout();
	}

	// todo??
	public void setMaximized(boolean max)
	{
		if (max == maximized)
			return;
		maximized = max;
		if (max)
		{
			// save old position, then call layout() to maximize
			stdpos = getPosition();
			stdsize = getSize();
			layout();
		} else {
			// set content size back to what it was
			if (getContent() != null)
			{
				getContent().setPosition(stdpos);
				getContent().setSize(stdsize);
			}
		}
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			if (mbe.isPressed(1))
			{
				event.getContext().requestFocus(this);
				// figure out what part of frame clicked
				int q = getQuadrant(mbe.x, mbe.y);
				dragq = q;
//				System.out.println(event + ", q=" + q);
				switch (q)
				{
					case 1 : // top border
					case 3 : // left side
					case 5 : // right side
					case 7 : // bottom border
						beginDrag(event);
						return true;
					case 8 : // lower-right
						if ( (flags & RESIZEABLE) != 0 )
						{
							beginDrag(event);
							return true;
						}
				}
			}
			else if (mbe.isReleased(1))
			{
				if (isDragging())
				{
					endDrag(event);
					return true;
				}
			}
		}
		else if (event instanceof GLOMouseMovedEvent)
		{
			if (isDragging())
			{
				GLOMouseMovedEvent mme = (GLOMouseMovedEvent)event;
				Point ofs = mme.getOffset();
				if (dragq == 8)
				{
					// window resize
					GLOComponent c = this;
					if ( (flags & RESIZE_CHILD) !=0 )
						c = (resizechild == null) ? getContent() : resizechild;
					if (c != null)
						c.setSize(c.getWidth() + ofs.x, c.getHeight() + ofs.y);
					if ( (flags & NOLAYOUT) == 0)
					{
						this.layout();
					}
				} else {
					// window drag
					setPosition(ofs.x+x1, ofs.y+y1);
					return true;
				}
			}
		}
		else if (event instanceof GLOFocusEvent)
		{
			if ( ((GLOFocusEvent)event).isGained() )
			{
				raise();
				return true;
			}
		}
		else if (event instanceof GLOActionEvent)
		{
			Object action = ((GLOActionEvent)event).getAction();
			if ("Close".equals(action))
			{
				close();
				return true;
			}
		}

		return super.handleEvent(event);
	}

}
