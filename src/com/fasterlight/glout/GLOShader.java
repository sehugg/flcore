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

import java.util.*;

import javax.media.opengl.GL;

/**
  * A shader describes the display properties of a component --
  * things like color, blend mode, background color, etc.
  */
public class GLOShader
implements Cloneable
{
	public int color=0xffffffff;
	public int srcblend=GL.GL_ONE;
	public int destblend=GL.GL_ZERO;
	public String texname, fontname;
	public int xsize,ysize; // size for borders & such
	public float frameratio;

	public GLOShader()
	{
	}

	public GLOShader(int color)
	{
		this.color = color;
	}

	public GLOShader(int color, int srcblend, int destblend)
	{
		this.color = color;
		this.srcblend = srcblend;
		this.destblend = destblend;
	}

	public GLOShader(String texname)
	{
		this.texname = texname;
	}

	public GLOShader(String texname, int srcblend, int destblend)
	{
		this.texname = texname;
		this.srcblend = srcblend;
      this.destblend = destblend;
	}

	public GLOShader(String texname, int srcblend, int destblend, int xsize, int ysize)
	{
		this.texname = texname;
		this.srcblend = srcblend;
      this.destblend = destblend;
      this.xsize = xsize;
      this.ysize = ysize;
	}

	public GLOShader(Properties props)
	{
		this.color = (int)Long.parseLong(props.getProperty("color", "ffffffff"), 16);
		this.srcblend = resolveGLConst(props.getProperty("srcblend", "ONE"));
		this.destblend = resolveGLConst(props.getProperty("destblend", "ZERO"));
		this.xsize = Integer.parseInt(props.getProperty("width", "16"));
		this.ysize = Integer.parseInt(props.getProperty("height", "16"));
		this.frameratio = Float.parseFloat(props.getProperty("ratio", "0.333"));
		this.texname = props.getProperty("texname");
		this.fontname = props.getProperty("fontname");
	}

	public GLOShader getClone()
	{
		try {
			GLOShader newshader = (GLOShader)this.clone();
			return newshader;
		} catch (CloneNotSupportedException cnse) {
			throw new RuntimeException(cnse.toString());
		}
	}

	/**
	  * Issues the appropriate GL commands to set up
	  * for this particular shader.
	  * todo: hotspot
	  */
	public void set(GLOContext ctx)
	{
		GL gl = ctx.getGL();

		if (srcblend != GL.GL_ONE || destblend != GL.GL_ZERO)
		{
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(srcblend, destblend);
		} else {
			gl.glDisable(GL.GL_BLEND);
		}
		setColor(gl);
		if (texname != null)
		{
			ctx.getTextureCache().setTexture(texname);
			gl.glEnable(GL.GL_TEXTURE_2D);
		}
		else if (fontname != null)
		{
			GLFontServer fs = ctx.getFontServer();
			fs.setFont(fontname);
			fs.setTextSize(xsize, -ysize);
		}
		else
			gl.glDisable(GL.GL_TEXTURE_2D);
	}


	public void setColor(GL gl, float alpha)
	{
		if (alpha < 1)
			gl.glColor4ub((byte)color, (byte)(color>>8), (byte)(color>>16),
				(byte)(((color>>24)&0xff)*alpha));
		else
			setColor(gl);
	}

	public void setColor(GL gl)
	{
		gl.glColor4ub((byte)color, (byte)(color>>8), (byte)(color>>16),
			(byte)(color>>24));
	}

	//

	static int resolveGLConst(String s)
	{
		Object o = blendnames.get(s.toUpperCase());
		if (o != null)
			s = o.toString();
		return Integer.parseInt(s);
	}

	static Map blendnames;

	static {
		blendnames = new HashMap();
		blendnames.put("ZERO", new Integer(GL.GL_ZERO));
		blendnames.put("ONE", new Integer(GL.GL_ONE));
		blendnames.put("DST_COLOR", new Integer(GL.GL_DST_COLOR));
		blendnames.put("SRC_COLOR", new Integer(GL.GL_SRC_COLOR));
		blendnames.put("ONE_MINUS_DST_COLOR", new Integer(GL.GL_ONE_MINUS_DST_COLOR));
		blendnames.put("ONE_MINUS_SRC_COLOR", new Integer(GL.GL_ONE_MINUS_SRC_COLOR));
		blendnames.put("SRC_ALPHA", new Integer(GL.GL_SRC_ALPHA));
		blendnames.put("ONE_MINUS_SRC_ALPHA", new Integer(GL.GL_ONE_MINUS_SRC_ALPHA));
		blendnames.put("DST_ALPHA", new Integer(GL.GL_DST_ALPHA));
		blendnames.put("ONE_MINUS_DST_ALPHA", new Integer(GL.GL_ONE_MINUS_DST_ALPHA));
		blendnames.put("SRC_ALPHA_SATURATE", new Integer(GL.GL_SRC_ALPHA_SATURATE));
	}
}
