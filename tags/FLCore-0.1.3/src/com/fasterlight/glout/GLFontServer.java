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

import java.util.Random;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.fasterlight.vecmath.Vector2f;

public class GLFontServer
{
	TextureCache texcache;
	GL gl;
	GLU glu;
	String fontpath;

	private float x,y;
	private float w=8;
	private float h=16;

	Random rnd = new Random();

	int dlist;

	// last colors used, if any
	public byte col_r,col_g,col_b,col_a;

	public static final char ESCAPE_COLOR = 0xff01;

	//

	public GLFontServer(GL gl, GLU glu, TextureCache texcache)
	{
		this.gl = gl;
		this.glu = glu;
		this.texcache = texcache;
	}

	public void init()
	{
		// prepare a display list for each char.
		dlist = gl.glGenLists(256);
		for (int ch=0; ch<256; ch++)
		{
			float tx = (ch&15)*(txd);
			float ty = 1f-(ch/16)*(tyd);
			gl.glNewList(dlist+ch, GL.GL_COMPILE);
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(tx+txi, ty-tyd+tyi);
			gl.glVertex2f(0, 0);
			gl.glTexCoord2f(tx+txd-txi, ty-tyd+tyi);
			gl.glVertex2f(1, 0);
			gl.glTexCoord2f(tx+txd-txi, ty-tyi-ty1);
			gl.glVertex2f(1, 1);
			gl.glTexCoord2f(tx+txi, ty-tyi-ty1);
			gl.glVertex2f(0, 1);
			gl.glEnd();
			gl.glTranslatef(1,0,0);
			gl.glEndList();
		}
	}

	// todo: hotspot
	public void setFont(String name)
	{
		this.fontpath = name + ".png";
		prepare();
	}

	public void prepare()
	{
		texcache.setTexture(fontpath);
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	float txd = 1f/16;
	float tyd = 1f/16;
	float txi = 3f/256;
	float tyi = 0f/256;
	float ty1 = 0.5f/256;

	float rndscale = 0.001f;

	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public void drawText(String text, int x, int y)
	{
		this.x = x;
		this.y = y;
		drawText(text);
	}

	public void drawText(String text, float x, float y)
	{
		this.x = x;
		this.y = y;
		drawText(text);
	}

	public void drawText(String text)
	{
		drawTextFrag(text, 0, text.length());
	}

	public void drawText(StringBuffer text)
	{
		drawTextFrag(text, 0, text.length());
	}

	// i hate cut & paste but we need 2 separate methods here

	public void drawTextFrag(String text, int ofs, int len)
	{
		if (len <= 0)
			return;

		gl.glPushMatrix();
		gl.glTranslatef(x,y,0);
		gl.glScalef(w,h,1);
		float startx = x;

		int l = ofs+len;
		for (int i=ofs; i<l; i++)
		{
			int ch = text.charAt(i);
			switch (ch)
			{
				case '\n':
					gl.glPopMatrix();
					x = startx;
					y -= (h*5/4);
					gl.glPushMatrix();
					gl.glTranslatef(x,y,0);
					gl.glScalef(w,h,1);
					break;
				case ESCAPE_COLOR:
					if (i+4<l)
					{
						col_r = (byte)text.charAt(i+1);
						col_g = (byte)text.charAt(i+2);
						col_b = (byte)text.charAt(i+3);
						col_a = (byte)text.charAt(i+4);
						gl.glColor4ub(col_r,col_g,col_b,col_a);
						i+=4;
					}
					break;
				default:
					if (ch >= 0 && ch <= 255)
					{
						gl.glCallList(dlist + ch);
						x += w;
					}
					break;
			}
		}

		gl.glPopMatrix();
	}

	public void drawTextFrag(StringBuffer text, int ofs, int len)
	{
		if (len <= 0)
			return;

		gl.glPushMatrix();
		gl.glTranslatef(x,y,0);
		gl.glScalef(w,h,1);

		int l = ofs+len;
		for (int i=ofs; i<l; i++)
		{
			int ch = text.charAt(i);
			switch (ch)
			{
				case ESCAPE_COLOR:
					if (i+4<l)
					{
						col_r = (byte)text.charAt(i+1);
						col_g = (byte)text.charAt(i+2);
						col_b = (byte)text.charAt(i+3);
						col_a = (byte)text.charAt(i+4);
						gl.glColor4ub(col_r,col_g,col_b,col_a);
						i+=4;
					}
					break;
				default:
					if (ch >= 0 && ch <= 255)
					{
						gl.glCallList(dlist + ch);
						x += w;
					}
					break;
			}
		}

		gl.glPopMatrix();
	}

	public float getTextHeight()
	{
		return (h<0)?-h:h;
	}

	public float getTextWidth()
	{
		return w;
	}

	public void setTextSize(float w, float h)
	{
		this.w = w;
		this.h = h;
	}

	public float getStringWidth(String s)
	{
		return s.length()*w;
	}

	public float getStringHeight(String s)
	{
		return getTextHeight();
	}

	// in our silly little world, characters that are bounded by
	// two 0xffff's are not printable -- they are "escapes!"
	// but not every component can print these -- only
	public static int countPrintable(String s)
	{
		int count = 0;
		int l = s.length();
		for (int i=0; i<l; i++)
		{
			char ch = s.charAt(i);
			if (ch >= 0 && ch < 256)
				count++;
		}
		return count;
	}

	public static String escapeColor(int rgba)
	{
		return "" + ESCAPE_COLOR +
			(char)(((rgba>>>0) & 0xff) | 0xfe00) +
			(char)(((rgba>>>8) & 0xff) | 0xfe00) +
			(char)(((rgba>>>16) & 0xff) | 0xfe00) +
			(char)(((rgba>>>24) & 0xff) | 0xfe00);
	}

	public float getXPos()
	{
		return x;
	}

	public float getYPos()
	{
		return y;
	}

	public Vector2f getStringSize(String title)
	{
		return new Vector2f(getStringWidth(title), getStringHeight(title));
	}

	public Vector2f getTextSize()
	{
		return new Vector2f(w,h);
	}
}
