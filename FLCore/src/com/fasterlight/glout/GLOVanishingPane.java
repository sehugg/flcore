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

import javax.media.opengl.GL;

/**
  * It shows itself whenever its hotzone is visited, and hides
  * when it is not.
  * It is meant to be used with GLOMenuTable, menus etc.
  */
public class GLOVanishingPane
extends GLOContainer
{
	private GLOSmoother smoother;

	static int HOTZONE_YPOS = 32;

	//

	public GLOVanishingPane()
	{
		super();
		getContext().beginEventCapture(this, GLOMouseMovedEvent.class);
		smoother = new GLOSmoother(1, 60f);
		setRaised(false);
	}

	public void renderChildren(GLOContext ctx)
	{
		float value = smoother.getValue();
		GL gl = ctx.getGL();
		gl.glPushMatrix();
		gl.glRotatef(-value*90, 0, 0, 1);
		super.renderChildren(ctx);
		gl.glPopMatrix();

		if (value > 0.5)
		{
			setRaised(false);
			return;
		}
		else if (!getRaised())
		{
			setRaised(true);
			return;
		}
	}

	public void setMenuShowing(boolean b)
	{
		smoother.setTarget( b ? 0 : 1 );
		if (b && !getRaised())
			setRaised(b);
	}

	private boolean isInChildComponent(int x, int y)
	{
		GLOComponent cmpt = getComponentAt(x, y);
		return (cmpt != null && cmpt != this);
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOMouseMovedEvent)
		{
			GLOMouseMovedEvent mme = (GLOMouseMovedEvent)event;
			boolean hotzone = (mme.getY()-origin.y < HOTZONE_YPOS);
			// don't hide if we are totally showing && we are in a child component
			if (!( !hotzone && smoother.getValue()!=1 && isInChildComponent(mme.getX(), mme.getY()) ))
			{
				setMenuShowing(hotzone);
			}
			return false;
		}

		return super.handleEvent(event);
	}

/**
	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOVanishingPane.class);

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
**/

}
