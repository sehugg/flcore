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

import java.awt.Rectangle;
import java.util.*;

import com.fasterlight.spif.*;

/**
  * A generic container class that makes no attempt to
  * lay out its children.
  */
public class GLODemonstrator
extends GLOComponent
{
	protected List cmptlist = new ArrayList(0);
	protected int blink_interval = 500;

	//

	public void add(GLOComponent cmpt)
	{
		cmptlist.add(cmpt);
	}

	public void remove(GLOComponent cmpt)
	{
		cmptlist.remove(cmpt);
	}

	public boolean contains(GLOComponent cmpt)
	{
		return cmptlist.contains(cmpt);
	}

	public Iterator getWatched()
	{
		return cmptlist.iterator();
	}

	public void removeAllWatched()
	{
		cmptlist.clear();
	}

	public void clear(boolean b)
	{
		if (b)
			removeAllWatched();
	}

	public float getAlpha()
	{
		long t1 = Math.abs(ctx.frame_start_msec);
		int t = (int)(t1 % blink_interval)*2 - blink_interval;
		return Math.abs(t*1.0f/blink_interval);
	}

	public void renderHighlight(GLOContext ctx, GLOComponent cmpt)
	{
		Rectangle r = cmpt.getBounds();
		drawTexturedBox(ctx, r);
	}

	public void renderHighlights(GLOContext ctx)
	{
		if (cmptlist.size() == 0)
			return;

		GLOShader shader = getShader("demo-hilite");
		ctx.setShader(shader);
		shader.setColor(ctx.getGL(), getAlpha());

		for (int i=0; i<cmptlist.size(); i++)
		{
			GLOComponent cmpt = (GLOComponent)cmptlist.get(i);
			if (cmpt.isShowing())
				renderHighlight(ctx, cmpt);
		}
	}

	/**
	  * Just renders all cmptlist
	  */
	public void render(GLOContext ctx)
	{
		renderHighlights(ctx);
	}


	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLODemonstrator.class);

	static {
		prophelp.registerSet("add", "add", GLOComponent.class);
		prophelp.registerSet("remove", "remove", GLOComponent.class);
		prophelp.registerSet("clear", "clear", boolean.class);
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
