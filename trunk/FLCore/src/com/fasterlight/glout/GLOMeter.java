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
import java.text.*;

import javax.media.opengl.GL;

import com.fasterlight.spif.*;

/**
  * A horizontal or vertical value indicator.
  * This has several style options (scrolling/non scrolling/bar),
  * left/center/right alignment, etc.
  * todo: horizontal.
  * todo: fix case when low scale and high value (precision err)
  * todo: make slide_factor depend on time since last frame
  */
public class GLOMeter
extends GLOComponent
{
	protected float loval, lomark, himark;
	protected float hival=100, curval=50;
	protected float lodisp=0, hidisp=100, pagedisp=100;
	protected float tickscale = 10; // units per major tick
	protected float value_scale = 1;
	protected float value_bias = 0;

	protected GLOSmoother smoother;

	public static final String metershader = "meter";
	public static final String textshader = "metertext";
	public static final String wmarkshader = "meterwmark";
	public static final String pointershader = "meterpointer";

	protected boolean vert = true;
	protected boolean drawTicks = true;
	protected boolean drawTickLabels = true;
	protected boolean drawHighlight = false;

	protected byte style = STYLE_POINTER;
	protected byte pointerAlign = RIGHT;
	protected byte labelAlign = LEFT;
	protected byte tickLabelXOffset = 0;

	protected PropertyEvaluator prop_value, prop_lomark, prop_himark;

	public static final int STYLE_NONE = 0;
	public static final int STYLE_POINTER = 1;
	public static final int STYLE_WATERMARK = 2;
	public static final int STYLE_SCROLLING = 3;

	public static final int LEFT   = 0;
	public static final int CENTER = 1;
	public static final int RIGHT  = 2;

	public static final int MAX_SANE_TICKS = 250;

	//

	public boolean needsClipping()
	{
		// todo: is it ever safe not to have it?
		return (style != STYLE_SCROLLING) || drawTickLabels || drawHighlight;
	}

	public void setRange(float lo, float hi)
	{
		this.loval = lo;
		this.hival = hi;
		fixValue();
	}

	public void setDisplayRange(float lo, float hi)
	{
		this.lodisp = lo;
		this.hidisp = hi;
		fixValue();
	}

	public float toValue(Object o)
	{
		float x = PropertyUtil.toFloat(o) * value_scale + value_bias;
		return x;
	}

	public float getValue()
	{
		if (prop_value != null)
		{
			Object o = getForPropertyKey(prop_value);
			if (o != null)
				setValue(toValue(o));
		}
		return (smoother != null) ? smoother.getValue() : curval;
	}

	public void setValue(float value)
	{
		/*
		if (prop_value != null)
		{
			setForPropertyKey(prop_value, new Float(value));
		}
		*/
		if (this.curval != value)
		{
			this.curval = value;
			fixValue();
			if (smoother != null)
				smoother.setTarget(curval);
		}
	}

	public float getSlideFactor()
	{
		return (smoother != null) ? smoother.getHalflife() : 0;
	}

	public void setSlideFactor(float x)
	{
		if (x > 0)
			smoother = new GLOSmoother(getContext(), getValue(), x);
		else
			smoother = null;
	}

	public String getPropertyForValue()
	{
		return getKey(prop_value);
	}

	public void setPropertyForValue(String s)
	{
		this.prop_value = new PropertyEvaluator(s);
	}

	public String getPropertyForLoMark()
	{
		return getKey(prop_lomark);
	}

	public void setPropertyForLoMark(String s)
	{
		this.prop_lomark = new PropertyEvaluator(s);
	}

	public String getPropertyForHiMark()
	{
		return getKey(prop_himark);
	}

	public void setPropertyForHiMark(String s)
	{
		this.prop_himark = new PropertyEvaluator(s);
	}

	public float getValueScale()
	{
		return value_scale;
	}

	public void setValueScale(float value_scale)
	{
		this.value_scale = value_scale;
	}

	public float getValueBias()
	{
		return value_bias;
	}

	public void setValueBias(float value_bias)
	{
		this.value_bias = value_bias;
	}

	protected float fix(float x)
	{
		return Math.min(hival, Math.max(loval, x));
	}

	private void fixValue()
	{
		curval = fix(curval);
	}

	public float getRangeLo()
	{
		return loval;
	}

	public void setRangeLo(float loval)
	{
		this.loval = loval;
		this.lodisp = loval;
	}

	public float getRangeHi()
	{
		return hival;
	}

	public void setRangeHi(float hival)
	{
		this.hival = hival;
		this.hidisp = hival;
	}

	public float getDisplayRangeLo()
	{
		return lodisp;
	}

	public void setDisplayRangeLo(float lodisp)
	{
		this.lodisp = lodisp;
	}

	public float getDisplayRangeHi()
	{
		return hidisp;
	}

	public void setDisplayRangeHi(float hidisp)
	{
		this.hidisp = hidisp;
	}

	public float getMarkRangeLo()
	{
		return lodisp;
	}

	public void setMarkRangeLo(float lodisp)
	{
		this.lodisp = lodisp;
	}

	public float getMarkRangeHi()
	{
		return hidisp;
	}

	public void setMarkRangeHi(float hidisp)
	{
		this.hidisp = hidisp;
	}

	public float getPageRange()
	{
		return pagedisp;
	}

	public void setPageRange(float pagedisp)
	{
		this.pagedisp = pagedisp;
	}

	public float getTickScale()
	{
		return tickscale;
	}

	public void setTickScale(float tickscale)
	{
		this.tickscale = tickscale;
	}

	public int getStyle()
	{
		return style;
	}

	public void setStyle(int style)
	{
		this.style = (byte)style;
	}

	public boolean getDrawTickLabels()
	{
		return drawTickLabels;
	}

	public void setDrawTickLabels(boolean drawTickLabels)
	{
		this.drawTickLabels = drawTickLabels;
	}

	public boolean getDrawTicks()
	{
		return drawTicks;
	}

	public void setDrawTicks(boolean drawTicks)
	{
		this.drawTicks = drawTicks;
	}

	public boolean getDrawHighlight()
	{
		return drawHighlight;
	}

	public void setDrawHighlight(boolean drawHighlight)
	{
		this.drawHighlight = drawHighlight;
	}

	public int getPointerAlign()
	{
		return pointerAlign;
	}

	public void setPointerAlign(int pointerAlign)
	{
		this.pointerAlign = (byte)pointerAlign;
	}

	public int getLabelAlign()
	{
		return labelAlign;
	}

	public void setLabelAlign(int labelAlign)
	{
		this.labelAlign = (byte)labelAlign;
	}

	public GLOShader getMeterShader()
	{
		return getShader(metershader);
	}

	public GLOShader getTickFontShader()
	{
		return getShader(textshader);
	}

	private static final NumberFormat flt_fmt = new DecimalFormat("0.#");

	public String valueToStr(float v)
	{
		/*
		if (v == (int)v)
			return Integer.toString((int)v);
		else
		*/
			return flt_fmt.format(v);
	}

	public void render(GLOContext ctx)
	{
		GLOShader shader = getMeterShader();
		GL gl = ctx.getGL();
		Point o = getOrigin();
		shader.set(ctx);

		float v = getValue();
		float lopage, hipage;
		if (style == STYLE_SCROLLING)
		{
			lopage = v-pagedisp/2;
			hipage = v+pagedisp/2;
		} else {
			lopage = lodisp;
			hipage = hidisp;
		}

		int w = getWidth();
		int h = getHeight();
		float lotex = lopage/tickscale;
		float hitex = hipage/tickscale;
		float floor = (float)Math.floor(lotex);
		lotex -= floor;
		hitex -= floor;

		if (drawTicks)
		{
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0, lotex);
			gl.glVertex2i(o.x, o.y+h);
			gl.glTexCoord2f(1, lotex);
			gl.glVertex2i(o.x+w, o.y+h);
			gl.glTexCoord2f(1, hitex);
			gl.glVertex2i(o.x+w, o.y);
			gl.glTexCoord2f(0, hitex);
			gl.glVertex2i(o.x, o.y);
			gl.glEnd();
		}

		if (drawTickLabels)
		{
			if (prop_lomark != null)
			{
				Object o1 = getForPropertyKey(prop_lomark);
				if (o1 != null)
					lomark = fix(toValue(o1));
			}
			if (prop_himark != null)
			{
				Object o1 = getForPropertyKey(prop_himark);
				if (o1 != null)
					himark = fix(toValue(o1));
			}
			GLFontServer fs = ctx.getFontServer();
			getTickFontShader().set(ctx);
			int i1 = (int)(lopage/tickscale);
			int i2 = (int)(hipage/tickscale);
			int ticks=0;
			for (int i=i1; i<=i2; i++)
			{
				float val = tickscale*i;
				if (val >= loval && val <= hival)
				{
					float y = o.y+h - (val-lopage)*h/(hipage-lopage) + fs.getTextHeight()*1f/2;
					String str = valueToStr(val);
					float x;
					switch (labelAlign) {
						case LEFT:
							x = o.x+tickLabelXOffset;
							break;
						case CENTER:
							x = o.x+(getWidth()-fs.getStringWidth(str))/2;
							break;
						case RIGHT:
						default:
							x = o.x+getWidth()-tickLabelXOffset-fs.getStringWidth(str);
							break;
					}
					fs.drawText(str, x, y);
				}
				// if something was wrong with the numbers, just give up
				if (++ticks > MAX_SANE_TICKS)
				{
					System.out.println(this + " had " + ticks + " ticks!");
					return;
				}
			}
		}

		// if highlight, we draw it
		if (drawHighlight)
		{
			float y1 = o.y+h - (lomark-lopage)*h/(hipage-lopage);
			float y2 = o.y+h - (himark-lopage)*h/(hipage-lopage);
			setShader(wmarkshader); //todo
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0, y2/tickscale);
			gl.glVertex2f(o.x, y2);
			gl.glTexCoord2f(1, y2/tickscale);
			gl.glVertex2f(o.x+w, y2);
			gl.glTexCoord2f(1, y1/tickscale);
			gl.glVertex2f(o.x+w, y1);
			gl.glTexCoord2f(0, y1/tickscale);
			gl.glVertex2f(o.x, y1);
			gl.glEnd();
		}

		// now indicate the value
		if (style != STYLE_NONE)
		{
			if (v < loval)
				v = loval;
			if (v > hival)
				v = hival;
			if (v < lopage)
				v = lopage;
			if (v > hipage)
				v = hipage;
			float y;
			if (style == STYLE_SCROLLING)
				y = o.y+h/2f;
			else
				y = o.y+h - (v-lopage)*h/(hipage-lopage);
			switch (style)
			{
				case STYLE_WATERMARK:
					if (y < o.y+h)
					{
						setShader(wmarkshader); //todo
						gl.glBegin(GL.GL_QUADS);
						gl.glTexCoord2f(0, lotex);
						gl.glVertex2f(o.x, o.y+h);
						gl.glTexCoord2f(1, lotex);
						gl.glVertex2f(o.x+w, o.y+h);
						gl.glTexCoord2f(1, y/tickscale);
						gl.glVertex2f(o.x+w, y);
						gl.glTexCoord2f(0, y/tickscale);
						gl.glVertex2f(o.x, y);
						gl.glEnd();
					}
					break;
				case STYLE_SCROLLING:
				case STYLE_POINTER:
					GLOShader ptrshad = setShader(pointershader);
					int ww = ptrshad.xsize;
					int hh = ptrshad.ysize;
					if (pointerAlign == RIGHT)
						drawTexturedBox(ctx, o.x+w-ww, y-hh/2, ww, hh);
					else
						drawTexturedBox(ctx, o.x+ww, y-hh/2, -ww, hh);
					break;
			}

		}

	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOMeter.class);

	static {
		prophelp.registerGetSet("value", "Value", float.class);
		prophelp.registerGetSet("value_scale", "ValueScale", float.class);
		prophelp.registerGetSet("value_bias", "ValueBias", float.class);
		prophelp.registerGetSet("slide_factor", "SlideFactor", float.class);
		prophelp.registerGetSet("lo", "RangeLo", float.class);
		prophelp.registerGetSet("hi", "RangeHi", float.class);
		prophelp.registerGetSet("lodisp", "DisplayRangeLo", float.class);
		prophelp.registerGetSet("hidisp", "DisplayRangeHi", float.class);
		prophelp.registerGetSet("lomark", "MarkRangeLo", float.class);
		prophelp.registerGetSet("himark", "MarkRangeHi", float.class);
		prophelp.registerGetSet("page", "PageRange", float.class);
		prophelp.registerGetSet("scale", "TickScale", float.class);
		prophelp.registerGetSet("style", "Style", int.class);
		prophelp.registerGetSet("ticklabels", "DrawTickLabels", boolean.class);
		prophelp.registerGetSet("drawticks", "DrawTicks", boolean.class);
		prophelp.registerGetSet("highlight", "DrawHighlight", boolean.class);
		prophelp.registerGetSet("pointeralign", "PointerAlign", int.class);
		prophelp.registerGetSet("labelalign", "LabelAlign", int.class);
		prophelp.registerGetSet("value_prop", "PropertyForValue", String.class);
		prophelp.registerGetSet("lomark_prop", "PropertyForLoMark", String.class);
		prophelp.registerGetSet("himark_prop", "PropertyForHiMark", String.class);
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
