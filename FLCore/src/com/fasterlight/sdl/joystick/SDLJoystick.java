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

package com.fasterlight.sdl.joystick;


public class SDLJoystick
{
	private int _data;

/******************* STATIC METHODS *****************/

/*
 * Count the number of joysticks attached to the system
 */
	public static native int getNumJoysticks();

/*
 * Get the implementation dependent name of a joystick.
 * This can be called before any joysticks are opened.
 * If no name can be found, this function returns NULL.
 */
	public static native String getJoystickName(int index);

/*
 * Open a joystick for use - the index passed as an argument refers to
 * the N'th joystick on the system.  This index is the value which will
 * identify this joystick in future joystick events.
 *
 * This function returns a joystick identifier, or NULL if an error occurred.
 */
	public static native SDLJoystick openJoystick(int index);

/*
 * Returns 1 if the joystick has been opened, or 0 if it has not.
 */
 	public static native boolean isOpen(int index);

/*
 * Update the current state of the open joysticks.
 * This is called automatically by the event loop if any joystick
 * events are enabled.
 */
 	public static native void update();

/******************* INSTANCE METHODS *****************/

/*
 * Get the device index of an opened joystick.
 */
 	public native boolean getIndex();

/*
 * Get the number of general axis controls on a joystick
 */
 	public native int getNumAxes();

/*
 * Get the number of trackballs on a joystick
 * Joystick trackballs have only relative motion events associated
 * with them and their state cannot be polled.
 */
 	public native int getNumBalls();

/*
 * Get the number of POV hats on a joystick
 */
 	public native int getNumHats();

/*
 * Get the number of buttons on a joystick
 */
 	public native int getNumButtons();

/*
 * Enable/disable joystick event polling.
 * If joystick events are disabled, you must call SDL_JoystickUpdate()
 * yourself and check the state of the joystick when you want joystick
 * information.
 * The state can be one of SDL_QUERY, SDL_ENABLE or SDL_IGNORE.
 */
 	public native int setEventState(int state);

/*
 * Get the current state of an axis control on a joystick
 * The state is a value ranging from -32768 to 32767.
 * The axis indices start at index 0.
 */
 	public native int getAxis(int axis);

/*
 * The hat indices start at index 0.
 */
 	public native int getHat(int hat);

/*
 * Get the ball axis change since the last poll
 * This returns 0, or -1 if you passed it invalid parameters.
 * The ball indices start at index 0.
 */
 	public native java.awt.Point getBall(int ball);

/*
 * Get the current state of a button on a joystick
 * The button indices start at index 0.
 */
 	public native int getButton(int button);

/*
 * Close a joystick previously opened with SDL_JoystickOpen()
 */
 	public native void close();

	// joystick constants

	public static final int HAT_CENTERED	= 0x00;
	public static final int HAT_UP			= 0x01;
	public static final int HAT_RIGHT		= 0x02;
	public static final int HAT_DOWN		= 0x04;
	public static final int HAT_LEFT		= 0x08;
	public static final int HAT_RIGHTUP	= (HAT_RIGHT|HAT_UP);
	public static final int HAT_RIGHTDOWN	= (HAT_RIGHT|HAT_DOWN);
	public static final int HAT_LEFTUP		= (HAT_LEFT|HAT_UP);
	public static final int HAT_LEFTDOWN	= (HAT_LEFT|HAT_DOWN);


/********************** INIT STUFF **************/

	static native void internalInit();

	// todo: what if other classes are invoked before this one?
	static {
		System.loadLibrary("jsdl");
		internalInit();
	}
}
