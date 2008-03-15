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

#include "com_fasterlight_sdl_timer_SDLTimer.h"
#include "JSDL.h"

/*
 * Class:     com_fasterlight_sdl_timer_SDLTimer
 * Method:    getTicks
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_timer_SDLTimer_getTicks
  (JNIEnv *env, jclass clazz)
{
	return SDL_GetTicks();
}

/*
 * Class:     com_fasterlight_sdl_timer_SDLTimer
 * Method:    internalInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_timer_SDLTimer_internalInit
  (JNIEnv *env, jclass clazz)
{
	fprintf(stderr, "Initializing SDL timer\n");
	JSDL_ASSERT(SDL_Init(SDL_INIT_TIMER) == 0);
	JSDL_Setup(env);
}


