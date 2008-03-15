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

package com.fasterlight.sdl.event;

public class SDLEventQueue
{
	/**
	 * Not instantiable
	 */
	private SDLEventQueue() { }

	public static native void pump();
	public static native boolean isEmpty();
	public static native SDLEvent poll();
	public static native SDLEvent waitForEvent();

	public static native int getAppState();
	public static native int setKeyboardRepeat(boolean on);
	public static native String getKeyName(int keysym);
	public static native byte[] getKeyStates();
	public static native int getModifiers();
	public static native SDLMouseEvent getMouseState();
	public static native boolean quitRequested();
}
