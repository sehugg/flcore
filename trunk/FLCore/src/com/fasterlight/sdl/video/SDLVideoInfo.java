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

import com.fasterlight.sdl.SDLException;

public class SDLVideoInfo
{
	int flags;
	int videomem;

	static final int HW_AVAILABLE = 0x00000001;
	static final int WM_AVAILABLE = 0x00000002;
	static final int BLIT_HW		= 0x00000400;
	static final int BLIT_HW_CC	= 0x00000800;
	static final int BLIT_HW_A		= 0x00001000;
	static final int BLIT_SW		= 0x00002000;
	static final int BLIT_SW_CC	= 0x00004000;
	static final int BLIT_SW_A		= 0x00008000;
	static final int BLIT_FILL		= 0x00010000;

	public boolean canCreateHWSurfaces()
	{
		return (flags & HW_AVAILABLE) != 0;
	}
	public boolean canUseWindowManager()
	{
		return (flags & WM_AVAILABLE) != 0;
	}
	public boolean canBlitHW()
	{
		return (flags & BLIT_HW) != 0;
	}
	public boolean canBlitHWColorKey()
	{
		return (flags & BLIT_HW_CC) != 0;
	}
	public boolean canBlitHWAlpha()
	{
		return (flags & BLIT_HW_A) != 0;
	}
	public boolean canBlitSW()
	{
		return (flags & BLIT_SW) != 0;
	}
	public boolean canBlitSWColorKey()
	{
		return (flags & BLIT_SW_CC) != 0;
	}
	public boolean canBlitSWAlpha()
	{
		return (flags & BLIT_SW_A) != 0;
	}
	public boolean canBlitFill()
	{
		return (flags & HW_AVAILABLE) != 0;
	}
	public int getVideoMemoryKbytes()
	{
		return videomem;
	}
	public SDLPixelFormat getPixelFormat()
	{
		throw new SDLException("getPixelFormat(): NYI");
	}
	public String toString()
	{
		return "SDLVideoInfo:flags=" + flags + ",videomem=" + videomem;
	}
}
