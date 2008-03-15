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
  * Shows a bitmap.
  */
public class GLOBitmap
extends GLOComponent
{
	protected String texname;
	protected float texx1,texy1;
	protected float texx2=1,texy2=1;

	public GLOBitmap()
	{
	}

	public GLOBitmap(String texname)
	{
		setTextureName(texname);
	}

	public String getTextureName()
	{
		return texname;
	}

	public void setTextureName(String texname)
	{
		this.texname = texname;
	}

	public GLOShader getBitmapShader()
	{
		return getShader("bitmap");
	}

	public void render(GLOContext ctx)
	{
		String tex = getTextureName();
		Point o = getOrigin();
		GL gl = ctx.getGL();
		setShader(getBitmapShader());
		gl.glEnable(GL.GL_TEXTURE_2D);
		if (tex != null)
			ctx.getTextureCache().setTexture(tex);

		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(texx1,texy2);
		gl.glVertex2f(o.x, o.y);
		gl.glTexCoord2f(texx2,texy2);
		gl.glVertex2f(o.x+w1, o.y);
		gl.glTexCoord2f(texx2,texy1);
		gl.glVertex2f(o.x+w1, o.y+h1);
		gl.glTexCoord2f(texx1,texy1);
		gl.glVertex2f(o.x, o.y+h1);
		gl.glEnd();
	}

	public float getTexX1()
	{
		return texx1;
	}

	public void setTexX1(float texx1)
	{
		this.texx1 = texx1;
	}

	public float getTexX2()
	{
		return texx2;
	}

	public void setTexX2(float texx2)
	{
		this.texx2 = texx2;
	}

	public float getTexY1()
	{
		return texy1;
	}

	public void setTexY1(float texy1)
	{
		this.texy1 = texy1;
	}

	public float getTexY2()
	{
		return texy2;
	}

	public void setTexY2(float texy2)
	{
		this.texy2 = texy2;
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOBitmap.class);

	static {
		prophelp.registerGetSet("texname", "TextureName", String.class);
		prophelp.registerGetSet("texx1", "TexX1", float.class);
		prophelp.registerGetSet("texy1", "TexY1", float.class);
		prophelp.registerGetSet("texx2", "TexX2", float.class);
		prophelp.registerGetSet("texy2", "TexY2", float.class);
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
