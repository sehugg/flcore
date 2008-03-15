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


public class SDLColor
{
	public byte r,g,b;
	private byte unused;

	public SDLColor(byte r, byte g, byte b)
	{
		this.r=r;
		this.g=g;
		this.b=b;
	}

	public SDLColor(int value)
	{
		this.r=(byte)(value);
		this.g=(byte)(value>>8);
		this.b=(byte)(value>>16);
	}
}
