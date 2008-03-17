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

import java.net.URL;
import java.util.HashMap;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.fasterlight.glout.texutil.*;

public class TextureCache
{
	String pkgbase;
	URL urlbase;
	HashMap texs = new HashMap();
	HashMap preptexs = new HashMap();
	GL gl;
	GLU glu;
	Object awtcmpt;

	int MAX_TEXS = 1024;
	int[] texints = new int[MAX_TEXS];
	int ntexs;

	public static final int SCALE_BEST = 0;
	public static final int SCALE_NOSCALE = 1;
	public static final int CLAMP_S = 8;
	public static final int CLAMP_T = 16;
	public static final int CLAMP_ST = CLAMP_S | CLAMP_T;

	public int DEFAULT_FLAGS = 0;

	//

	public TextureCache(String urlbase, GL gl, GLU glu, Object awtcmpt)
	{
		this.awtcmpt = awtcmpt;
		try
		{
			if (urlbase != null)
			{
				if (urlbase.startsWith("file:") || urlbase.startsWith("http:"))
					this.urlbase = new URL(urlbase);
				else
					this.pkgbase = urlbase;
			}
		} catch (Exception ioe)
		{
			System.out.println(ioe);
		}
		this.gl = gl;
		this.glu = glu;
	}

	public void setMaxNumTexs(int maxtex)
	{
		this.MAX_TEXS = maxtex;
	}

	public void init()
	{
		gl.glGenTextures(MAX_TEXS, texints, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texints[0]);
	}

	void setupTexture(TextureLoader tl, int flags)
	{
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
				((flags & CLAMP_S) == 0) ? GL.GL_REPEAT : GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
				((flags & CLAMP_T) == 0) ? GL.GL_REPEAT : GL.GL_CLAMP);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		//      gl.glHint( GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST );

		if (tl.getGLFormat() != GL.GL_ALPHA)
		{
			glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, tl.getComponents(), tl.getTextureWidth(), tl
					.getTextureHeight(), tl.getGLFormat(), tl.getGLComponentFormat(), tl
					.getTexture());
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
					GL.GL_LINEAR_MIPMAP_NEAREST);
		} else
		{
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		}
	}

	public TextureLoader getTextureLoader(String name)
	{
		TextureLoader tl;
		if (name.endsWith("-ALPHA.png"))
		{
			tl = new PngTextureLoader(gl, glu);
			((PngTextureLoader) tl).setGrayType(GL.GL_ALPHA);
		} else if (name.endsWith("-INTENS.png"))
		{
			tl = new PngTextureLoader(gl, glu);
			((PngTextureLoader) tl).setGrayType(GL.GL_INTENSITY);
		} else if (name.endsWith(".png"))
		{
			tl = new PngTextureLoader(gl, glu);
		} else if (name.endsWith(".tga"))
		{
			tl = new TGATextureLoader(gl, glu);
		} else
			tl = new AWTTextureLoader(null, gl, glu);
		return tl;
	}

	public int setTexture(String name)
	{
		return setTexture(name, DEFAULT_FLAGS);
	}

	public void setTexture(int texid)
	{
		gl.glBindTexture(GL.GL_TEXTURE_2D, texints[texid]);
	}

	public int bindTextureSlot(String name)
	{
		if (name == null)
			throw new IllegalArgumentException();
		Integer i = (Integer) texs.get(name);
		if (i != null)
		{
			if (i.intValue() >= 0)
				gl.glBindTexture(GL.GL_TEXTURE_2D, texints[i.intValue()]);
			return i.intValue();
		} else
		{
			// get next tex slot
			ntexs++;
			gl.glBindTexture(GL.GL_TEXTURE_2D, texints[ntexs]);
			texs.put(name, new Integer(ntexs));
			return ntexs;
		}
	}

	public int setTexture(String name, int flags)
	{
		if (name == null)
			throw new IllegalArgumentException();
		Integer i = (Integer) texs.get(name);
		if (i != null)
		{
			if (i.intValue() >= 0)
				gl.glBindTexture(GL.GL_TEXTURE_2D, texints[i.intValue()]);
		} else
		{
			// get next tex slot
			ntexs++;
			gl.glBindTexture(GL.GL_TEXTURE_2D, texints[ntexs]);
			i = new Integer(ntexs);

			// load it
			TextureLoader tl = getTextureLoader(name);
			try
			{
				if (debug)
					System.out.println("Reading texture " + name + " (" + texints[ntexs] + ")");
				boolean readSucc;
				if (urlbase != null)
					readSucc = tl.readTexture(urlbase, name);
				else if (pkgbase != null)
					readSucc = tl.readTexture(pkgbase + name);
				else
					readSucc = tl.readTexture(name);
				if (!readSucc)
				{
					i = new Integer(-1);
					if (debug)
						System.out.println("Failed, " + i);
				}
			} catch (Exception ioe)
			{
				ioe.printStackTrace();
				return -1;
			}

			if (tl.isOk() && i.intValue() >= 0)
			{
				if (debug)
					System.out.println("Texture ok: " + tl);
				switch (flags & 7)
				{
					case SCALE_NOSCALE:
						tl.texImage2DNonScaled(true);
						break;
					case SCALE_BEST:
					default:
						tl.texImage2DScaled4BestSize();
						break;
				}
				setupTexture(tl, flags);
				//   	    	preptexs.put(name, tl);
			}

			texs.put(name, i);
		}
		return i.intValue();
	}

	//

	static boolean debug = false;
}