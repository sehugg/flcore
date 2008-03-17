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

/**
  * This class smoothly interpolates between different float
  * values using a logarithmic function.
  * The "half life" of the function is settable.
  */
public class GLOSmoother
{
	protected GLOContext ctx;

	protected float halflife;
	protected long t0; // initial time
	protected long endt; // final time
	protected float v0;
	protected float v1; // target value
	protected int maxDeltaTime;

	static final double MAX_FACTOR = 0.999;
	static final double LN_2 = Math.log(2);
	static final float DEFAULT_HALFLIFE = 200f; // 1/2 of its value per 1/5 second

	//

	public GLOSmoother(GLOContext ctx)
	{
		this.ctx = ctx;
		setHalflife(DEFAULT_HALFLIFE);
	}

	public GLOSmoother(GLOContext ctx, float initialValue, float halflife)
	{
		this(ctx);
		setValue(initialValue);
		setHalflife(halflife);
	}

	public GLOSmoother(float initialValue)
	{
		this(GLOContext.getCurrent(), initialValue, DEFAULT_HALFLIFE);
	}

	public GLOSmoother(float initialValue, float halflife)
	{
		this(GLOContext.getCurrent(), initialValue, halflife);
	}

	public void setHalflife(float f)
	{
		this.halflife = f;
		this.maxDeltaTime = (int)(-halflife*Math.log(1-MAX_FACTOR)/LN_2);
	}

	public float getHalflife()
	{
		return halflife;
	}

	public float getTarget()
	{
		return v1;
	}

	public void setTarget(float v)
	{
		this.v0 = getValue();
		this.v1 = v;
		this.t0 = ctx.getFrameStartMillis();
		this.endt = t0 + maxDeltaTime;
	}

	public float getValue()
	{
		long t1 = ctx.getFrameStartMillis();
		if (t1 > endt)
			return v1;
		if (t1 == t0)
			return v0;
		double vt = v1 - (v1-v0)*Math.pow(0.5, (t1-t0)/halflife);
		v0 = (float)vt;
		t0 = t1;
		return v0;
	}

	public void setValue(float v)
	{
		this.endt = 0;
		this.v1 = v;
	}

	//

	public static void main(String[] args)
	{
		GLOContext ctx = new GLOContext();
		GLOSmoother timer = new GLOSmoother(ctx);
		timer.setTarget(1);
		System.out.println(timer.getValue());
		for (int i=0; i<20000; i+=500)
		{
			ctx.frame_start_msec = i;
			System.out.println(i + " : " + timer.getValue());
			if (i==5000)
				timer.setTarget(0);
		}
	}
}
