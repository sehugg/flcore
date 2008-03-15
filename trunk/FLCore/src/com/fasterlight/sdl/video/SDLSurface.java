/*
    JSDL - Java(TM) interface to SDL
    Copyright (C) 2001  Steven Hugg

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Library General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Library General Public License for more details.

    You should have received a copy of the GNU Library General Public
    License along with this library; if not, write to the Free
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Steven Hugg
    hugg@pobox.com
*/

package com.fasterlight.sdl.video;


public class SDLSurface
{
	private int _sdlsurf;
	private int flags, width, height;
	private short pitch;

	public final int getWidth() { return width; }
	public final int getHeight() { return height; }
	public final short getPitch() { return pitch; }

	public native void update();
	public native void update(SDLRect rect);
	public native void update(SDLRect[] rect);
	public native void flip();
	public native boolean setColors(SDLColor[] colors, int start, int len);

	public static native SDLSurface createRGBSurface(int flags, int width, int height,
		int depth, int rmask, int gmask, int bmask, int amask);
	public static native SDLSurface createRGBSurfaceFrom(int[] pixels, int width, int height,
		int depth, int pitch, int rmask, int gmask, int bmask, int amask);
	public native void free();

	public native void lock();
	public native void unlock();

	public static native SDLSurface loadBMP(String file);
	public native void saveBMP(String file);

	public native void setColorKey(int flag, int key);
	public native void setAlpha(int flag, byte alpha);
	public final void setClipping(SDLRect rect)
	{
		setClipping(rect.x, rect.y, rect.x+rect.w, rect.y+rect.h);
	}
	public native void setClipping(int x1, int y1, int x2, int y2);
	public native SDLRect getClipping();
	public SDLRect getBounds()
	{
		return new SDLRect(0, 0, width, height);
	}

	public native SDLSurface convert(SDLPixelFormat fmt, int flags);
	public native int blit(SDLRect destrect, SDLSurface src, SDLRect srcrect);
	public int blit(SDLRect destrect, SDLSurface src)
	{
		return blit(destrect, src, null);
	}
	public int blit(SDLSurface src)
	{
		return blit(null, src, null);
	}
	public native void fillRect(SDLRect rect, int color);

	public native SDLSurface convertForDisplay();

	protected void finalize()
	{
		System.err.println("Freeing surface " + this);
		free();
	}
}
