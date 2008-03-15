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
import java.util.Properties;

import javax.media.opengl.GL;

import com.fasterlight.spif.*;
import com.fasterlight.util.*;

/**
  * Shows a series of pictures, with transitions and zoom
  * effects controlled by a script.
  */
public class GLOSlideShow
extends GLOComponent
{
	protected INIFile ini;
	protected String inifile, prop_inifile;
	protected int framedelay=3000, transdelay=1000;

	protected int curframe=0, nextframe=-1;
	protected long starttime;
	protected SlideshowFrame[] frames;

	public GLOSlideShow()
	{
	}

	public GLOSlideShow(String filename)
	{
		this();
		setINIFilename(filename);
	}

	public boolean needsClipping()
	{
		return true;
	}

	public void setINIFilename(String filename)
	{
		if (filename != null && filename.equals(inifile))
			return;

		this.inifile = filename;
		if (filename == null)
		{
			this.ini = null;
			return;
		}
		this.ini = new CachedINIFile(filename);
		try {
			readINI();
		} catch (java.io.IOException ioe) {
			ioe.printStackTrace();
			this.ini = null;
		}
//		starttime = ctx.getFrameStartMillis();
	}

	public String getINIFilename()
	{
		return inifile;
	}

	public void setPropertyForINIFile(String prop_inifile)
	{
		this.prop_inifile = prop_inifile;
	}

	public String getPropertyForINIFile()
	{
		return prop_inifile;
	}

	SlideshowFrame getFrame(int i)
	{
		return (frames != null && i>=0 && i<frames.length) ? frames[i] : null;
	}

	public void setNumFrames(int n)
	{
		this.frames = new SlideshowFrame[n];
	}

	public int getNumFrames()
	{
		return (frames != null) ? frames.length : 0;
	}

	public int getFrameDelay()
	{
		return framedelay;
	}

	public void setFrameDelay(int framedelay)
	{
		this.framedelay = framedelay;
	}

	public int getTransitionDelay()
	{
		return transdelay;
	}

	public void setTransitionDelay(int transdelay)
	{
		this.transdelay = transdelay;
	}

	void readINI()
	throws java.io.IOException
	{
		Properties props = ini.getSection("Slideshow");
		if (props != null)
		{
			PropertyUtil.setFromProps(this, props);
		}
		for (int i=0; i<getNumFrames(); i++)
		{
			props = ini.getSection("frame"+i);
			frames[i] = new SlideshowFrame();
			if (props != null)
				frames[i].loadFromProps(props);
		}
	}

	public void render(GLOContext ctx)
	{
		if (prop_inifile != null)
		{
			Object o = getForPropertyKey(prop_inifile);
			if (o != null)
				setINIFilename(PropertyUtil.toString(o));
		}

		int numframes = getNumFrames();
		if (numframes <= 0)
			return;

		int totaldelay = framedelay+transdelay; // total time from frame to frame
		long curtime = ctx.getFrameStartMillis();
		long looptime = totaldelay*numframes; // time for 1 loop of all frames
		long seqtime = (curtime-starttime) % looptime; // time within current loop
		curframe = (int)(seqtime/totaldelay); // calculate current frame
		nextframe = (curframe+1) % numframes;
		long frametime = seqtime-curframe*totaldelay; // time within current frame
		float ratio = (frametime < framedelay) ? 1.0f : (totaldelay-frametime)*1.0f/transdelay; // fadeout ratio
//		System.out.println(seqtime + " " + ratio + " " + curframe);

		GL gl = ctx.getGL();
		gl.glDisable(GL.GL_BLEND);

		SlideshowFrame frame1 = getFrame(curframe);
		if (frame1 != null)
		{
			gl.glColor4f(ratio,ratio,ratio,1);
			frame1.render(ctx, frametime);
		}
		SlideshowFrame frame2 = getFrame(nextframe);
		if (frame2 != null)
		{
			if (ratio < 1.0f)
			{
				gl.glEnable(GL.GL_BLEND);
				gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE);
				gl.glColor4f(1-ratio,1-ratio,1-ratio,1);
				frame2.render(ctx, frametime-totaldelay);
				gl.glDisable(GL.GL_BLEND);
			} else
				frame2.setup(ctx);
		}
	}

	//

	class SlideshowFrame
	{
		String texname;
		int width, height;
		int x1,y1,s1,x2,y2,s2;

		void loadFromProps(Properties props)
		{
			texname = props.getProperty("pic");
			width = PropertyUtil.toInt(props.getProperty("width", "256"));
			height = PropertyUtil.toInt(props.getProperty("height", "256"));
			x1 = PropertyUtil.toInt(props.getProperty("x1", "0"));
			y1 = PropertyUtil.toInt(props.getProperty("y1", "0"));
			s1 = PropertyUtil.toInt(props.getProperty("size1", "256"));
			x2 = PropertyUtil.toInt(props.getProperty("x2", "0"));
			y2 = PropertyUtil.toInt(props.getProperty("y2", "0"));
			s2 = PropertyUtil.toInt(props.getProperty("size2", "256"));
		}
		int getTransDelay()
		{
			return transdelay;
		}
		int getFrameDelay()
		{
			return framedelay;
		}
		void setup(GLOContext ctx)
		{
			if (texname == null)
				return;
			ctx.getTextureCache().setTexture(texname); // , TextureCache.SCALE_NOSCALE);
		}
		void render(GLOContext ctx, long frametime)
		{
			if (texname == null)
				return;
			setup(ctx);
			Point o = getOrigin();

			float zoomr = (frametime+transdelay)*1f/(framedelay+transdelay*2);
			float sc = s1 + (s2-s1)*zoomr;
			float tx1 = (x1 + zoomr*(x2-x1))/width;
			float ty1 = 1 - (y1 + zoomr*(y2-y1))/height;
			float tx2 = tx1 + sc/width;
			float ty2 = ty1 - sc/height;

			GL gl = ctx.getGL();
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(tx1, ty1);
			gl.glVertex2i(o.x, o.y);
			gl.glTexCoord2f(tx2, ty1);
			gl.glVertex2i(o.x+getWidth(), o.y);
			gl.glTexCoord2f(tx2, ty2);
			gl.glVertex2i(o.x+getWidth(), o.y+getHeight());
			gl.glTexCoord2f(tx1, ty2);
			gl.glVertex2i(o.x, o.y+getHeight());
			gl.glEnd();
		}
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOSlideShow.class);

	static {
		prophelp.registerGetSet("inifile", "INIFilename", String.class);
		prophelp.registerGetSet("inifile_prop", "PropertyForINIFile", String.class);
		prophelp.registerGetSet("frames", "NumFrames", int.class);
		prophelp.registerGetSet("framedelay", "FrameDelay", int.class);
		prophelp.registerGetSet("transdelay", "TransitionDelay", int.class);
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
