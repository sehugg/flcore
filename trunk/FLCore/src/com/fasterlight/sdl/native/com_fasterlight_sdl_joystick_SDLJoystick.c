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


#include "com_fasterlight_sdl_joystick_SDLJoystick.h"
#include "JSDL.h"

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_close
  (JNIEnv *env, jobject jjoy)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	SDL_JoystickClose(joy);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getAxis
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getAxis
  (JNIEnv *env, jobject jjoy, jint axis)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	return SDL_JoystickGetAxis(joy, axis);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getBall
 * Signature: (I)Ljava/awt/Point;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getBall
  (JNIEnv *env, jobject jjoy, jint ball)
{
	jobject jpt;
	int dx,dy,res;
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	res = SDL_JoystickGetBall(joy, ball, &dx, &dy);
	JSDL_ASSERT(res >= 0);
	JSDL_NewObject("java/awt/Point", &jpt);
	JSDL_SetIntField(jpt, "x", dx);
	JSDL_SetIntField(jpt, "y", dy);
	return jpt;
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getButton
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getButton
  (JNIEnv *env, jobject jjoy, jint button)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	return SDL_JoystickGetButton(joy, button);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getHat
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getHat
  (JNIEnv *env, jobject jjoy, jint hat)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	return SDL_JoystickGetHat(joy, hat);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getIndex
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getIndex
  (JNIEnv *env, jobject jjoy)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	return SDL_JoystickIndex(joy);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getJoystickName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getJoystickName
  (JNIEnv *env, jclass clas, jint index)
{
	const char *str;
	str = SDL_JoystickName(index);
	JSDL_ASSERT(str != NULL);
	return (*env)->NewStringUTF(env, str);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getNumAxes
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getNumAxes
  (JNIEnv *env, jobject jjoy)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	return SDL_JoystickNumAxes(joy);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getNumBalls
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getNumBalls
  (JNIEnv *env, jobject jjoy)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	return SDL_JoystickNumBalls(joy);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getNumButtons
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getNumButtons
  (JNIEnv *env, jobject jjoy)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	return SDL_JoystickNumButtons(joy);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getNumHats
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getNumHats
  (JNIEnv *env, jobject jjoy)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	return SDL_JoystickNumButtons(joy);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    getNumJoysticks
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_getNumJoysticks
  (JNIEnv *env, jclass clas)
{
	return SDL_NumJoysticks();
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    isOpen
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_isOpen
  (JNIEnv *env, jclass clas, jint index)
{
	int i = SDL_JoystickOpened(index);
	JSDL_ASSERT( (i==0 || i==1) );
	return i;
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    openJoystick
 * Signature: (I)Lcom/fasterlight/sdl/joystick/SDLJoystick;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_openJoystick
  (JNIEnv *env, jclass clas, jint index)
{
	jobject jjoy;
	SDL_Joystick *joy;
	
	joy = SDL_JoystickOpen(index);
	JSDL_ASSERT(joy != NULL);
	JSDL_NewObject(JSDLPKG "/joystick/SDLJoystick", &jjoy);
	(*env)->SetIntField(env, jjoy, jsdlfid_joydata_addr, (jint)joy);
	return jjoy;
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    setEventState
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_setEventState
  (JNIEnv *env, jobject jjoy, jint state)
{
	SDL_Joystick* joy;
	JSDL_GetJoystick(jjoy, &joy);
	JSDL_ASSERT(joy != NULL);

	return SDL_JoystickEventState(state);
}

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    update
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_update
  (JNIEnv *env, jclass clas)
{
	SDL_JoystickUpdate();
}

/**********/

/*
 * Class:     com_fasterlight_sdl_joystick_SDLJoystick
 * Method:    internalInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_joystick_SDLJoystick_internalInit
  (JNIEnv *env, jclass clas)
{
	fprintf(stderr, "Initializing SDL joystick\n");
	JSDL_ASSERT(SDL_Init(SDL_INIT_JOYSTICK) == 0);
	JSDL_Setup(env);
}


