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

/**
  * A button that blinks for a given number of seconds
  * when it goes on
  */
public class GLOWarningButton
extends GLOButton
{
	protected int blink_interval = 333;
	protected int blink_duration = 5000;
	protected long last_state_chg = 0;

	public void setState(boolean b)
	{
		boolean oldb = getState();
		super.setState(b);
		if (oldb != b)
			last_state_chg = ctx.getFrameStartMillis();
	}

	public GLOShader getFrameShader()
	{
		boolean state;
		long t = ctx.getFrameStartMillis() - last_state_chg;
		if (t < blink_duration)
			state = ((Math.abs(ctx.getFrameStartMillis()) % blink_interval) < blink_interval/2);
		else
			state = getState();
		return getShader(state ? (depressed ? ondownshader : onupshader) : (depressed ? offdownshader : offupshader));
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOWarningButton.class);

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

}
