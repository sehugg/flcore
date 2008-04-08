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
  * A label that fades out after a certain interval of time.
  */
public class GLOHintLabel
extends GLOLabel
{
	private int sustain_time = 2500;
	private int fade_time = 1000;
	private long last_change_time;

	private int brightness;

	public GLOHintLabel()
	{
		super();
	}

	public GLOHintLabel(String name)
	{
		super(name);
	}

	public GLOHintLabel(String name, int alignment)
	{
		super(name, alignment);
	}

	public GLOHintLabel(int minchars)
	{
		super(minchars);
	}

	//

	public void setChanged()
	{
		last_change_time = ctx.getFrameStartMillis();
	}

	// we null this out so that super.render() calls it
	// :-p
	protected String computeText()
	{
		return this.text;
	}

	public void setText(String text)
	{
		super.setText(text);
		setChanged();
	}

	public int getSustainTime()
	{
		return sustain_time;
	}

	public void setSustainTime(int sustaintime)
	{
		this.sustain_time = sustaintime;
	}

	public int getFadeTime()
	{
		return fade_time;
	}

	public void setFadeTime(int fadetime)
	{
		this.fade_time = fadetime;
	}

	protected GLOShader getTextShader()
	{
		GLOShader s = super.getTextShader();
		if (brightness == 255) {
			return s;
		} else {
			s = s.getClone();
			s.color = (s.color & 0xffffff) | (brightness << 24);
			return s;
		}
	}

	public void render(GLOContext ctx)
	{
		String str = super.computeText();
		if (str != null && !str.equals(this.text))
		{
			setChanged();
			setText(str);
		}

		long msec = ctx.getFrameStartMillis() - last_change_time;
		if (msec < sustain_time)
			brightness = 255;
		else if (msec < sustain_time + fade_time)
			brightness = 255-(int)((msec - sustain_time)*255/fade_time);
		else
			brightness = 0;

		if (brightness > 0)
		{
			super.render(ctx);
		}
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOHintLabel.class);

	static {
		prophelp.registerGetSet("sustaintime", "SustainTime", int.class);
		prophelp.registerGetSet("fadetime", "FadeTime", int.class);
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
