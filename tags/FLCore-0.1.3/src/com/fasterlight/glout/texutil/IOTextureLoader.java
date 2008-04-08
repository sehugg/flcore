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

import java.io.*;
import java.net.*;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * This abstract Class implements the file and url based methods "readTexture",
 * to call the specialised implementation of the stream based "readTexture"
 * method !
 *
 * @see TextureLoader
 */
public abstract class IOTextureLoader extends TextureLoader
{
	protected IOTextureLoader(GL gl, GLU glu)
	{
		super(gl, glu);
	}

	public boolean readTexture(String fname)
	{
		boolean result = false;
		InputStream is = null;
		try
		{
			if (new File(fname).exists())
				is = new java.io.FileInputStream(fname);
			else
				is = ClassLoader.getSystemResourceAsStream(fname);
			result = readTexture(is);
		} catch (Exception ex)
		{
			System.out.println("IOTextureLoader.readTexture <" + fname
					+ "> failed !\n" + ex);
		}
		try
		{
			if (is != null)
				is.close();
		} catch (Exception ex)
		{
		}
		return result;
	}

	public boolean readTexture(URL base, String uri)
	{
		boolean result = false;
		InputStream is = null;
		try
		{
			URL url = new URL(base, uri);
			URLConnection urlcon = url.openConnection();
			urlcon.setDoOutput(false);
			urlcon.setDoInput(true);
			is = urlcon.getInputStream();
			result = readTexture(is);
		} catch (Exception ex)
		{
			System.out.println("IOTextureLoader.readTexture <" + base + " / "
					+ uri + "> failed !\n" + ex);
		}
		try
		{
			if (is != null)
				is.close();
		} catch (Exception ex)
		{
		}
		return result;
	}

	protected abstract boolean readTexture(InputStream is);
}

