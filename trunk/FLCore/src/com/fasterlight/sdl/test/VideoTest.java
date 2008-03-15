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

package com.fasterlight.sdl.test;

import com.fasterlight.sdl.video.*;

public class VideoTest
{
	public static void main(String[] args)
	throws Exception
	{
		SDLVideoInfo vi = SDLVideo.getVideoInfo();
		System.out.println(vi.canUseWindowManager());
		System.out.println(vi.getVideoMemoryKbytes());
		SDLVideoMode mode = new SDLVideoMode(320, 240, 16, 0);
		SDLSurface surf = SDLVideo.setVideoMode(mode);
		System.out.println(surf);
		SDLRect rect = new SDLRect(0, 0, 160, 120);
		surf.fillRect(rect, 0xffffff);
		surf.update();

		Thread.sleep(1000);

		SDLSurface bmp = SDLSurface.loadBMP("xlogo.bmp");
		System.out.println(bmp);
		System.out.println("blit: " + surf.blit(rect, bmp, rect));
		surf.update();

		Thread.sleep(2000);
	}
}
