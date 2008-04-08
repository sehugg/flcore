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
import com.fasterlight.vecmath.Vector3f;

/**
 * Like GLODefault3DCanvas, but allows Z and X keys.
 */
public class GLOZoomable3DCanvas extends GLODefault3DCanvas
{
	protected float translateAmount = 1.25f;
	protected float zoomAmount = 1.25f;
	protected float moveViewAmount = 0;
	private Vector3f vp = new Vector3f();

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOKeyEvent)
		{
			GLOKeyEvent ke = (GLOKeyEvent) event;
			if ((ke.getFlags() & GLOKeyEvent.MOD_SHIFT) == 0)
			{
				switch (ke.getKeyCode())
				{
					case GLOKeyEvent.VK_Z:
						zoom(zoomAmount);
						return true;
					case GLOKeyEvent.VK_X:
						zoom(1 / zoomAmount);
						return false;
				}
			} else
			{
				switch (ke.getKeyCode())
				{
					case GLOKeyEvent.VK_Z:
						closer(translateAmount);
						return true;
					case GLOKeyEvent.VK_X:
						closer(1 / translateAmount);
						return false;
				}
			}
		}

		return super.handleEvent(event);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOZoomable3DCanvas.class);

	static
	{
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
		try
		{
			prophelp.setProp(this, key, value);
		} catch (PropertyRejectedException e)
		{
			super.setProp(key, value);
		}
	}

}