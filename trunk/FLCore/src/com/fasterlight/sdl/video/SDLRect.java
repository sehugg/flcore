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


public class SDLRect
{
	public short x,y;
	public short w,h;

	public SDLRect(short x, short y, short w, short h)
	{
		this.x=x;
		this.y=y;
		this.w=w;
		this.h=h;
	}
	public SDLRect(int x, int y, int w, int h)
	{
		this.x=(short)x;
		this.y=(short)y;
		this.w=(short)w;
		this.h=(short)h;
	}
	public void move(int ox, int oy)
	{
		x += ox;
		y += oy;
	}

	public String toString()
	{
		return "SDLRect:" + x + ',' + y + ',' + w + ',' + h;
	}
}
