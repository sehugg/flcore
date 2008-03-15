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

package com.fasterlight.sdl.audio;

import java.io.IOException;

import com.fasterlight.sdl.SDLException;

public class SDLWave
{
	SDLAudioSpec format;
	int len;
	int _data;

	public SDLWave(String filename)
	throws SDLException, IOException
	{
		loadInternal(filename);
	}

	private native void loadInternal(String filename);

	public native void free();

	protected void finalize()
	{
		free();
	}
}
