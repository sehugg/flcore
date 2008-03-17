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

import javax.media.opengl.GL;

import com.fasterlight.spif.*;

/**
  * An indicator that can rotate thru 360 degrees of movement,
  * using a single bitmap.
  */
public class GLORadialMeter
extends GLOComponent
{
	protected float curval=50;
	protected float loval=0, hival=100;
	protected float loang=-45, hiang=45;
	protected PropertyEvaluator prop_value;
	protected float slide_factor = 0.5f;

	//

	public float getRangeLo()
	{
		return loval;
	}

	public void setRangeLo(float loval)
	{
		this.loval = loval;
	}

	public float getRangeHi()
	{
		return hival;
	}

	public void setRangeHi(float hival)
	{
		this.hival = hival;
	}

	public float getAngleLo()
	{
		return loang;
	}

	public void setAngleLo(float loang)
	{
		this.loang = loang;
	}

	public float getAngleHi()
	{
		return hiang;
	}

	public void setAngleHi(float hiang)
	{
		this.hiang = hiang;
	}

	public String getPropertyForValue()
	{
		return getKey(prop_value);
	}

	public void setPropertyForValue(String s)
	{
		this.prop_value = new PropertyEvaluator(s);
	}

	public float getAngle()
	{
		float v = getValue();
		if (Double.isNaN(v) || Double.isInfinite(v))
			return loang;
		v = Math.min(hival, Math.max(loval, v));
		v = (v-loval)/(hival-loval);
		return loang+v*(hiang-loang);
	}

	public float toValue(Object o)
	{
		float x = PropertyUtil.toFloat(o);
		return x;
	}

	public float getValue()
	{
		if (prop_value != null)
		{
			Object o = getForPropertyKey(prop_value);
			if (o != null)
				curval = toValue(o);
				//curval += (toValue(o)-curval)*slide_factor;
		}
		return curval;
	}

	public void setValue(float value)
	{
		if (prop_value != null)
		{
			setForPropertyKey(prop_value, new Float(value));
		}
		this.curval = value;
	}

	public float getSlideFactor()
	{
		return slide_factor;
	}

	public void setSlideFactor(float x)
	{
		this.slide_factor = x;
	}



	//


	public void render(GLOContext ctx)
	{
		setShader("radialmeter");

		Point o = getOrigin();
		GL gl = ctx.getGL();

		float ww = w1*0.5f;
		float hh = h1*0.5f;

		gl.glPushMatrix();
		gl.glTranslatef(o.x+ww, o.y+hh, 0);

		float angle = getAngle();
		gl.glRotatef(angle, 0, 0, 1);

		drawTexturedBox(ctx, -ww, -hh, w1, h1);

		gl.glPopMatrix();
	}


	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLORadialMeter.class);

	static {
		prophelp.registerGetSet("value", "Value", float.class);
		prophelp.registerGetSet("value_prop", "PropertyForValue", String.class);
		prophelp.registerGetSet("lo", "RangeLo", float.class);
		prophelp.registerGetSet("hi", "RangeHi", float.class);
		prophelp.registerGetSet("loang", "AngleLo", float.class);
		prophelp.registerGetSet("hiang", "AngleHi", float.class);
		prophelp.registerGetSet("slide_factor", "SlideFactor", float.class);
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
