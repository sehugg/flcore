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


public class SDLVideo
{
	public static native SDLSurface getVideoSurface();
	public static native SDLVideoInfo getVideoInfo();
	public static native SDLVideoMode[] getVideoModes(SDLPixelFormat fmt, int flags);
	public static native int isVideoModeOK(SDLVideoMode videomode);
	public static native SDLSurface setVideoMode(SDLVideoMode videomode);

	/* These are the currently supported flags for the SDL_surface */
	/* Available for SDL_CreateRGBSurface() or SDL_SetVideoMode() */
	public static final int  SWSURFACE  = 0x00000000;	/* Surface is in system memory */
	public static final int  HWSURFACE	 = 0x00000001;	/* Surface is in video memory */
	public static final int  ASYNCBLIT	 = 0x00000004;	/* Use asynchronous blits if possible */

/* Available for SDL_SetVideoMode() */
	public static final int  ANYFORMAT	 = 0x10000000;	/* Allow any video depth/pixel-format */
	public static final int  HWPALETTE	 = 0x20000000;	/* Surface has exclusive palette */
	public static final int  DOUBLEBUF	 = 0x40000000;	/* Set up double-buffered video mode */
	public static final int  FULLSCREEN = 0x80000000;	/* Surface is a full screen display */
	public static final int  OPENGL     = 0x00000002;      /* Create an OpenGL rendering context */
	public static final int  OPENGLBLIT =	0x0000000A;	/* Create an OpenGL rendering context and use it for blitting */
	public static final int  RESIZABLE  = 0x00000010;	/* This video mode may be resized */
	public static final int  NOFRAME    = 0x00000020;	/* This video mode may be resized */

/* Used internally (read-only) */
	public static final int  HWACCEL    = 0x00000100;	/* Blit uses hardware acceleration */
	public static final int  SRCCOLORKEY= 0x00001000;	/* Blit uses a source color key */
	public static final int  RLEACCELOK = 0x00002000;	/* Private flag */
	public static final int  RLEACCEL   = 0x00004000;	/* Colorkey blit is RLE accelerated */
	public static final int  SRCALPHA   = 0x00010000;	/* Blit uses source alpha blending */
	public static final int  PREALLOC   = 0x01000000;	/* Surface uses preallocated memory */

/* The most common video overlay formats.
   For an explanation of these pixel formats, see:
	http://www.webartz.com/fourcc/indexyuv.htm

   For information on the relationship between color spaces, see:
   http://www.neuro.sfc.keio.ac.jp/~aly/polygon/info/color-space-faq.html
 */
	public static final int YV12_OVERLAY = 0x32315659;	/* Planar mode: Y + V + U  (3 planes) */
	public static final int IYUV_OVERLAY = 0x56555949;	/* Planar mode: Y + U + V  (3 planes) */
	public static final int YUY2_OVERLAY = 0x32595559;	/* Packed mode: Y0+U0+Y1+V0 (1 plane) */
	public static final int UYVY_OVERLAY = 0x59565955;	/* Packed mode: U0+Y0+V0+Y1 (1 plane) */
	public static final int YVYU_OVERLAY = 0x55595659;	/* Packed mode: Y0+V0+Y1+U0 (1 plane) */

/* Public enumeration for setting the OpenGL window attributes. */
   public static final int GL_RED_SIZE = 0;
   public static final int GL_GREEN_SIZE = 1;
   public static final int GL_BLUE_SIZE = 2;
   public static final int GL_ALPHA_SIZE = 3;
   public static final int GL_BUFFER_SIZE = 4;
   public static final int GL_DOUBLEBUFFER = 5;
   public static final int GL_DEPTH_SIZE = 6;
   public static final int GL_STENCIL_SIZE = 7;
   public static final int GL_ACCUM_RED_SIZE = 8;
   public static final int GL_ACCUM_GREEN_SIZE = 9;
   public static final int GL_ACCUM_BLUE_SIZE = 10;
   public static final int GL_ACCUM_ALPHA_SIZE = 11;

/* flags for SDL_SetPalette() */
	public static final int LOGPAL = 0x01;
	public static final int PHYSPAL = 0x02;

	public static final int GRAB_QUERY = -1;
	public static final int GRAB_OFF = 0;
	public static final int GRAB_ON = 1;
	public static final int GRAB_FULLSCREEN = 2; /* Used internally */


	static native void internalInit();

	// todo: what if other classes are invoked before this one?
	static {
		System.loadLibrary("jsdl");
		internalInit();
	}
}
