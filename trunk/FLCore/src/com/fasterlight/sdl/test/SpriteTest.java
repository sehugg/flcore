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

import java.util.*;

import com.fasterlight.sdl.event.*;
import com.fasterlight.sdl.video.*;

public class SpriteTest
{
	SDLSurface icon, screen;
	Random rnd = new Random();
	Vector sprites = new Vector();

	class Sprite
	{
		SDLRect bounds;
		Sprite()
		{
			int x = Math.abs(rnd.nextInt()) % screen.getWidth();
			int y = Math.abs(rnd.nextInt()) % screen.getHeight();
			bounds = new SDLRect(x, y, icon.getWidth(), icon.getHeight());
		}
		void show()
		{
			screen.blit(bounds, icon, null);
		}
		void move()
		{
			bounds.x += rnd.nextInt() % 4;
			bounds.y += rnd.nextInt() % 4;
		}
	}

	void show()
	{
		Enumeration e = sprites.elements();
		while (e.hasMoreElements())
		{
			Sprite sp = (Sprite)e.nextElement();
			sp.show();
		}
	}

	void move()
	{
		Enumeration e = sprites.elements();
		while (e.hasMoreElements())
		{
			Sprite sp = (Sprite)e.nextElement();
			sp.move();
		}
	}

	void sploo()
	{
		SDLRect r1 = screen.getBounds();
		SDLRect r2 = screen.getBounds();
		r1.move(1, 0);
		r1.w--;
		r2.w--;
		screen.blit(r2, screen, r1);
	}

	void setup()
	{
		SDLVideoInfo vi = SDLVideo.getVideoInfo();
		SDLVideoMode mode = new SDLVideoMode(640, 480, 16,
			SDLVideo.HWSURFACE|SDLVideo.FULLSCREEN);
		int bpp = SDLVideo.isVideoModeOK(mode);
		System.out.println("Using " + bpp + " bpp mode");
		mode.bpp = bpp;
		screen = SDLVideo.setVideoMode(mode);
		icon = SDLSurface.loadBMP("icon.bmp");
		icon.setColorKey(SDLVideo.SRCCOLORKEY | SDLVideo.RLEACCEL, 0x0);
		icon = icon.convertForDisplay();
		for (int i=0; i<50; i++)
		{
			sprites.addElement(new Sprite());
		}
		SDLEvent event;
		boolean done = false;
		long t1 = System.currentTimeMillis();
		int nframes = 1;
		do
		{
			while ( (event=SDLEventQueue.poll()) != null)
			{
				System.out.println(event);
				if (event instanceof SDLQuitEvent)
					done = true;
			}
			show();
			move();
			sploo();
			screen.update();
			nframes++;
		} while (!done);
		long t2 = System.currentTimeMillis();
		System.out.println((nframes*1000.0f)/(t2-t1) + " FPS");
	}

	public static void main(String[] args)
	throws Exception
	{
		// this is dangerous???
		System.runFinalizersOnExit(true);
		SpriteTest test = new SpriteTest();
		test.setup();
	}
}
