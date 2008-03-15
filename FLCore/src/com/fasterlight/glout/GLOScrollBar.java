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

import javax.media.opengl.GL;

import com.fasterlight.spif.*;

/**
  * A scroll bar, for scrolling
  */
public class GLOScrollBar
extends GLOComponent
{
	protected boolean vert = true;
	protected int loval, hival;
	protected int curval;

	//

	public GLOScrollBar()
	{
	}

	public GLOScrollBar(boolean isvert)
	{
		this.vert = isvert;
	}

	public GLOScrollBar(boolean isvert, int lo, int hi)
	{
		this(isvert);
		setRange(lo, hi);
	}

	public boolean getVertical()
	{
		return vert;
	}

	public void setVertical(boolean vert)
	{
		this.vert = vert;
	}

	public void setRange(int lo, int hi)
	{
		this.loval = lo;
		this.hival = hi;
		fixValue();
	}

	public int getValue()
	{
		return curval;
	}

	public void setValue(int value)
	{
		this.curval = value;
		fixValue();
	}

	public int getRangeLo()
	{
		return loval;
	}

	public void setRangeLo(int loval)
	{
		this.loval = loval;
		fixValue();
	}

	public int getRangeHi()
	{
		return hival;
	}

	public void setRangeHi(int hival)
	{
		this.hival = hival;
		fixValue();
	}

	private void fixValue()
	{
		if (curval < loval)
			curval = loval;
		if (curval > hival)
			curval = hival;
	}

	public GLOShader getScrollBarShader()
	{
		return getShader("scrollbar");
	}

	public Dimension getMinimumSize()
	{
		if (vert)
			return new Dimension(getBarXSize(), getBarYSize()*2);
		else
			return new Dimension(getBarXSize()*2, getBarYSize());
	}

	public int getBarXSize()
	{
		return getScrollBarShader().xsize;
	}

	public int getBarYSize()
	{
		return getScrollBarShader().ysize;
	}

	protected void drawVerticalBar(GL gl, Point o, float tx1, float tx2, int yy1, int yy2)
	{
		int yys = getBarYSize();
		int xx1 = o.x + (getWidth() - getBarXSize())/2;
		int xx2 = xx1 + getBarXSize();

		gl.glBegin( GL.GL_QUAD_STRIP );

		gl.glTexCoord2f(tx1, 1f);
		gl.glVertex2i(xx1, yy1);
		gl.glTexCoord2f(tx2, 1f);
		gl.glVertex2i(xx2, yy1);

		gl.glTexCoord2f(tx1, 0.666f);
		gl.glVertex2i(xx1, yy1+yys);
		gl.glTexCoord2f(tx2, 0.666f);
		gl.glVertex2i(xx2, yy1+yys);

		gl.glTexCoord2f(tx1, 0.333f);
		gl.glVertex2i(xx1, yy2-yys);
		gl.glTexCoord2f(tx2, 0.333f);
		gl.glVertex2i(xx2, yy2-yys);

		gl.glTexCoord2f(tx1, 0f);
		gl.glVertex2i(xx1, yy2);
		gl.glTexCoord2f(tx2, 0f);
		gl.glVertex2i(xx2, yy2);

		gl.glEnd();
	}

	protected void drawHorizontalBar(GL gl, Point o, float tx1, float tx2, int xx1, int xx2)
	{
		int xxs = getBarXSize();
		int yy1 = o.y + (getHeight() - getBarYSize())/2;
		int yy2 = yy1 + getBarYSize();

		gl.glBegin( GL.GL_QUAD_STRIP );

		gl.glTexCoord2f(tx1, 1f);
		gl.glVertex2i(xx1, yy1);
		gl.glTexCoord2f(tx2, 1f);
		gl.glVertex2i(xx1, yy2);

		gl.glTexCoord2f(tx1, 0.666f);
		gl.glVertex2i(xx1+xxs, yy1);
		gl.glTexCoord2f(tx2, 0.666f);
		gl.glVertex2i(xx1+xxs, yy2);

		gl.glTexCoord2f(tx1, 0.333f);
		gl.glVertex2i(xx2-xxs, yy1);
		gl.glTexCoord2f(tx2, 0.333f);
		gl.glVertex2i(xx2-xxs, yy2);

		gl.glTexCoord2f(tx1, 0f);
		gl.glVertex2i(xx2, yy1);
		gl.glTexCoord2f(tx2, 0f);
		gl.glVertex2i(xx2, yy2);

		gl.glEnd();
	}

	public void render(GLOContext ctx)
	{
		GLOShader shader = getScrollBarShader();
		GL gl = ctx.getGL();
		Point o = getOrigin();
		shader.set(ctx);

		if (vert)
		{
			drawVerticalBar(gl, o, 0f, 0.5f, o.y, o.y+getHeight());
		} else {
			drawHorizontalBar(gl, o, 0f, 0.5f, o.x, o.x+getWidth());
		}

		if (hival <= loval)
			return;

		float pos1 = (getValue()-loval)*1f/(hival-loval);
		float pos2 = pos1;

		if (vert)
		{
			int h = getHeight()-getBarYSize()*2;
			drawVerticalBar(gl, o, 0.5f, 1f, (int)(o.y+h*pos1), (int)(o.y+h*pos2)+getBarYSize()*2);
		} else {
			int w = getWidth()-getBarXSize()*2;
			drawHorizontalBar(gl, o, 0.5f, 1f, (int)(o.x+w*pos1), (int)(o.x+w*pos2)+getBarXSize()*2);
		}
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOMouseButtonEvent)
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
				Point o = getOrigin();
				GLOMouseMovedEvent mme = (GLOMouseMovedEvent)event;
				float v;
				// todo: div by 0?
				if (!vert)
				{
					v = (mme.getX()-o.x)*1f/getWidth();
				} else {
					v = (mme.getY()-o.y)*1f/getHeight();
				}
				setValue( ((int)(v*(hival-loval)))+loval );
				notifyDataChanged();
				return true;
			}
		}

		return super.handleEvent(event);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOScrollBar.class);

	static {
		prophelp.registerGetSet("value", "Value", int.class);
		prophelp.registerGetSet("lo", "RangeLo", int.class);
		prophelp.registerGetSet("hi", "RangeHi", int.class);
		prophelp.registerGetSet("vert", "Vertical", boolean.class);
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
