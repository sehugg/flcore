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

import javax.media.opengl.GL;

/**
  * A class that helps to minimize redundant state changes.
  */
public class GLState

{
	public static final int TEXTURE_1D     = (1<<0);
	public static final int TEXTURE_2D     = (1<<1);
	public static final int LIGHTING       = (1<<2);
	public static final int BLEND          = (1<<3);
	public static final int DEPTH_TEST     = (1<<4);
	public static final int CULL_FACE      = (1<<5);
	public static final int FOG            = (1<<6);
	public static final int NORMALIZE      = (1<<7);
	public static final int SCISSOR_TEST   = (1<<8);
	public static final int STENCIL_TEST   = (1<<9);
	public static final int ALPHA_TEST     = (1<<10);
	public static final int CULL_BACK      = (1<<11);

	public static final int DEFAULT_ENABLE_FLAGS = 0;

	public static final int DEFAULT_BLEND_FUNC =
		(GL.GL_ONE) | (GL.GL_ZERO<<16);

	public static final int DEFAULT_DEPTH_FUNC =
		GL.GL_LESS;

	public static final int FLAG_STACK_SIZE = 16;

	//

	private GL gl;
	private int enable_flags;
	private int blendfunc;
	private int depthfunc;
	private int active_texunit;
	private int[] texints = new int[8];

	private int[] flagstack = new int[FLAG_STACK_SIZE];
	private int flagstackpos = 0;

	//

	public GLState(GL gl)
	{
		this.gl = gl;
		enable_flags = DEFAULT_ENABLE_FLAGS;
		blendfunc = DEFAULT_BLEND_FUNC;
		depthfunc = DEFAULT_DEPTH_FUNC;
	}

	public void enableTexture1D()
	{
		if ((enable_flags & TEXTURE_1D) == 0) {
			gl.glEnable(GL.GL_TEXTURE_1D);
			enable_flags |= TEXTURE_1D;
		}
	}

	public void disableTexture1D()
	{
		if ((enable_flags & TEXTURE_1D) != 0) {
			gl.glDisable(GL.GL_TEXTURE_1D);
			enable_flags &= ~TEXTURE_1D;
		}
	}

	public void enableTexture2D()
	{
		if ((enable_flags & TEXTURE_2D) == 0) {
			gl.glEnable(GL.GL_TEXTURE_2D);
			enable_flags |= TEXTURE_2D;
		}
	}

	public void disableTexture2D()
	{
		if ((enable_flags & TEXTURE_2D) != 0) {
			gl.glDisable(GL.GL_TEXTURE_2D);
			enable_flags &= ~TEXTURE_2D;
		}
	}

	public void enableLighting()
	{
		if ((enable_flags & LIGHTING) == 0) {
			gl.glEnable(GL.GL_LIGHTING);
			enable_flags |= LIGHTING;
		}
	}

	public void disableLighting()
	{
		if ((enable_flags & LIGHTING) != 0) {
			gl.glDisable(GL.GL_LIGHTING);
			enable_flags &= ~LIGHTING;
		}
	}

	public void enableBlend()
	{
		if ((enable_flags & BLEND) == 0) {
			gl.glEnable(GL.GL_BLEND);
			enable_flags |= BLEND;
		}
	}

	public void disableBlend()
	{
		if ((enable_flags & BLEND) != 0) {
			gl.glDisable(GL.GL_BLEND);
			enable_flags &= ~BLEND;
		}
	}

	public void enableDepthTest()
	{
		if ((enable_flags & DEPTH_TEST) == 0) {
			gl.glEnable(GL.GL_DEPTH_TEST);
			enable_flags |= DEPTH_TEST;
		}
	}

	public void disableDepthTest()
	{
		if ((enable_flags & DEPTH_TEST) != 0) {
			gl.glDisable(GL.GL_DEPTH_TEST);
			enable_flags &= ~DEPTH_TEST;
		}
	}

	public void enableCullFace()
	{
		if ((enable_flags & CULL_FACE) == 0) {
			gl.glEnable(GL.GL_CULL_FACE);
			enable_flags |= CULL_FACE;
		}
	}

	public void disableCullFace()
	{
		if ((enable_flags & CULL_FACE) != 0) {
			gl.glDisable(GL.GL_CULL_FACE);
			enable_flags &= ~CULL_FACE;
		}
	}

	public void enableFog()
	{
		if ((enable_flags & FOG) == 0) {
			gl.glEnable(GL.GL_FOG);
			enable_flags |= FOG;
		}
	}

	public void disableFog()
	{
		if ((enable_flags & FOG) != 0) {
			gl.glDisable(GL.GL_FOG);
			enable_flags &= ~FOG;
		}
	}

	public void enableNormalize()
	{
		if ((enable_flags & NORMALIZE) == 0) {
			gl.glEnable(GL.GL_NORMALIZE);
			enable_flags |= NORMALIZE;
		}
	}

	public void disableNormalize()
	{
		if ((enable_flags & NORMALIZE) != 0) {
			gl.glDisable(GL.GL_NORMALIZE);
			enable_flags &= ~NORMALIZE;
		}
	}

	public void enableScissorTest()
	{
		if ((enable_flags & SCISSOR_TEST) == 0) {
			gl.glEnable(GL.GL_SCISSOR_TEST);
			enable_flags |= SCISSOR_TEST;
		}
	}

	public void disableScissorTest()
	{
		if ((enable_flags & SCISSOR_TEST) != 0) {
			gl.glDisable(GL.GL_SCISSOR_TEST);
			enable_flags &= ~SCISSOR_TEST;
		}
	}

	public void enableStencilTest()
	{
		if ((enable_flags & STENCIL_TEST) == 0) {
			gl.glEnable(GL.GL_STENCIL_TEST);
			enable_flags |= STENCIL_TEST;
		}
	}

	public void disableStencilTest()
	{
		if ((enable_flags & STENCIL_TEST) != 0) {
			gl.glDisable(GL.GL_STENCIL_TEST);
			enable_flags &= ~STENCIL_TEST;
		}
	}

	public void enableAlphaTest()
	{
		if ((enable_flags & ALPHA_TEST) == 0) {
			gl.glEnable(GL.GL_ALPHA_TEST);
			enable_flags |= ALPHA_TEST;
		}
	}

	public void disableAlphaTest()
	{
		if ((enable_flags & ALPHA_TEST) != 0) {
			gl.glDisable(GL.GL_ALPHA_TEST);
			enable_flags &= ~ALPHA_TEST;
		}
	}

	public void enableCullBack()
	{
		if ((enable_flags & CULL_BACK) == 0) {
			gl.glFrontFace(GL.GL_CW);
			enable_flags |= CULL_BACK;
		}
	}

	public void disableCullBack()
	{
		if ((enable_flags & CULL_BACK) != 0) {
			gl.glFrontFace(GL.GL_CCW);
			enable_flags &= ~CULL_BACK;
		}
	}

	public void setBlendFunc(int src, int dest)
	{
		int x = src | (dest<<16);
		if (x != blendfunc)
		{
			gl.glBlendFunc(src, dest);
			this.blendfunc = x;
		}
	}

	public void setDepthFunc(int x)
	{
		if (x != depthfunc)
		{
			gl.glDepthFunc(x);
			this.depthfunc = x;
		}
	}

	public void setTexUnit(int texunit)
	{
		if (active_texunit != texunit)
		{
			gl.glActiveTexture(GL.GL_TEXTURE0 + texunit);
			active_texunit = texunit;
		}
	}

	public void bindTexture2d(int texstage, int texint)
	{
		setTexUnit(texstage);
		if (texints[texstage] != texint)
		{
			gl.glBindTexture(GL.GL_TEXTURE_2D, texint);
			texints[texstage] = texint;
		}
	}

	public void pushAttrib(int bits)
	{
		bits |= GL.GL_ENABLE_BIT;
		gl.glPushAttrib(bits);
		flagstack[flagstackpos++] = enable_flags;
	}

	public void popAttrib()
	{
		gl.glPopAttrib();
		enable_flags = flagstack[--flagstackpos];
	}

	public void reset()
	{
		flagstackpos = 0;
	}

}
