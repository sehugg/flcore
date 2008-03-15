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
package com.fasterlight.glout.texutil;

import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

/**
 * This abstract Class defines the interface for ALL texture Grabbers ! The TextureGrabber's
 * implementations are used to save the pixels of the GL Context to a file !
 *
 * @see TextureTool
 * @see GLImageCanvas
 */
public abstract class TextureGrabber
{
	protected ByteBuffer pixels;
	protected int xpos;
	protected int ypos;
	protected int width;
	protected int height;
	protected GL gl;

	public TextureGrabber(GL gl)
	{
		this.gl = gl;
	}

	/**
	 * Grab the pixels outta the OpenGL Frambuffer
	 *
	 * @param source
	 *            the frambuffer source (like glReadBuffer), can be: GL.GL_FRONT, GL.GL_BACK, ....
	 * @param x
	 *            the xpos
	 * @param y
	 *            the ypos
	 * @param w
	 *            the width
	 * @param h
	 *            the height
	 *
	 * @see GL#glReadBuffer
	 */
	public void grabPixels(int source, int x, int y, int w, int h)
	{
		int swapbytes[] = { 0 }, lsbfirst[] = { 0 }, rowlength[] = { 0 };
		int skiprows[] = { 0 }, skippixels[] = { 0 }, alignment[] = { 0 };

		xpos = x;
		ypos = y;
		width = w;
		height = h;
		pixels = BufferUtil.newByteBuffer(w * h * 3);

		/* Save current modes. */
		gl.glGetIntegerv(GL.GL_PACK_SWAP_BYTES, swapbytes, 0);
		gl.glGetIntegerv(GL.GL_PACK_LSB_FIRST, lsbfirst, 0);
		gl.glGetIntegerv(GL.GL_PACK_ROW_LENGTH, rowlength, 0);
		gl.glGetIntegerv(GL.GL_PACK_SKIP_ROWS, skiprows, 0);
		gl.glGetIntegerv(GL.GL_PACK_SKIP_PIXELS, skippixels, 0);
		gl.glGetIntegerv(GL.GL_PACK_ALIGNMENT, alignment, 0);

		/*
		 * Little endian machines (DEC Alpha, Intel 80x86, ... for example) could benefit from
		 * setting GL.GL_PACK_LSB_FIRST to GL.GL_TRUE instead of GL.GL_FALSE, but this would
		 * require changing the
		 */
		gl.glPixelStorei(GL.GL_PACK_SWAP_BYTES, 0);
		gl.glPixelStorei(GL.GL_PACK_LSB_FIRST, 1);
		gl.glPixelStorei(GL.GL_PACK_ROW_LENGTH, w);
		gl.glPixelStorei(GL.GL_PACK_SKIP_ROWS, 0);
		gl.glPixelStorei(GL.GL_PACK_SKIP_PIXELS, 0);
		gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);

		//get viewport data
		gl.glReadBuffer(source);
		gl.glReadPixels(x, y, w, h, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);

		gl.glPixelStorei(GL.GL_PACK_SWAP_BYTES, swapbytes[0]);
		gl.glPixelStorei(GL.GL_PACK_LSB_FIRST, lsbfirst[0]);
		gl.glPixelStorei(GL.GL_PACK_ROW_LENGTH, rowlength[0]);
		gl.glPixelStorei(GL.GL_PACK_SKIP_ROWS, skiprows[0]);
		gl.glPixelStorei(GL.GL_PACK_SKIP_PIXELS, skippixels[0]);
		gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, alignment[0]);

	}

	public boolean write2File(String fname)
	{
		try
		{
			OutputStream os = new java.io.FileOutputStream(fname);
			return write2File(os);
		} catch (Exception ex)
		{
			System.out.println("TGATextureGrabber.write2File <" + fname + "> failed !\n" + ex);
		}
		return false;
	}

	public boolean write2File(URL base, String uri)
	{
		try
		{
			URL url = new URL(base, uri);
			URLConnection urlcon = url.openConnection();
			urlcon.setDoOutput(true);
			OutputStream os = urlcon.getOutputStream();
			return write2File(os);
		} catch (Exception ex)
		{
			System.out.println(
				"TGATextureGrabber.write2File <" + base + " / " + uri + "> failed !\n" + ex);
		}
		return false;
	}

	public abstract boolean write2File(OutputStream os);
}
