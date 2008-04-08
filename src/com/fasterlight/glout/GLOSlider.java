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
  * For setting floating point properties
  */
public class GLOSlider
extends GLOScrollBar
{
	protected float lofval,hifval,fvalue;
	protected PropertyEvaluator prop_fvalue;

	//

	public GLOSlider()
	{
		setRange(0, 1000);
	}

	public GLOSlider(boolean isvert)
	{
		this();
		this.vert = isvert;
	}

	public GLOSlider(boolean isvert, float lofval, float hifval)
	{
		this(isvert);
		this.lofval = lofval;
		this.hifval = hifval;
	}

	protected int float2int(float x)
	{
		return (int)((x-lofval)*1000/(hifval-lofval));
	}

	protected float int2float(int x)
	{
		return (x/1000f)*(hifval-lofval)+lofval;
	}

	private void fixFValue()
	{
		fvalue = Math.max(lofval, Math.min(hifval, fvalue));
	}

	public float getFloatRangeLo()
	{
		return lofval;
	}

	public void setFloatRangeLo(float lofval)
	{
		this.lofval = lofval;
		fixFValue();
	}

	public float getFloatRangeHi()
	{
		return hifval;
	}

	public void setFloatRangeHi(float hifval)
	{
		this.hifval = hifval;
		fixFValue();
	}

	public void setValue(int value)
	{
		super.setValue(value);
		setFloatValue(int2float(value));
	}

	public float getFloatValue()
	{
		if (prop_fvalue != null)
		{
			Object o = getForPropertyKey(prop_fvalue);
			if (o != null)
				fvalue = PropertyUtil.toFloat(o);
		}
		return fvalue;
	}

	public void setFloatValue(float v)
	{
		this.fvalue = v;
		if (prop_fvalue != null)
			setForPropertyKey(prop_fvalue, new Float(v));
	}

	public void render(GLOContext ctx)
	{
		if (prop_fvalue != null)
			setValue(float2int(getFloatValue()));
		super.render(ctx);
	}

	public String getPropertyForFloatValue()
	{
		return getKey(prop_fvalue);
	}

	public void setPropertyForFloatValue(String prop_fvalue)
	{
		this.prop_fvalue = new PropertyEvaluator(prop_fvalue);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOSlider.class);

	static {
		prophelp.registerGetSet("fvalue", "FloatValue", float.class);
		prophelp.registerGetSet("fvalue_prop", "PropertyForFloatValue", String.class);
		prophelp.registerGetSet("flo", "FloatRangeLo", float.class);
		prophelp.registerGetSet("fhi", "FloatRangeHi", float.class);
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
