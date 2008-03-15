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

#include "com_fasterlight_sdl_video_SDLVideo.h"
#include "JSDL.h"

/*
 * Class:     com_fasterlight_sdl_video_SDLVideo
 * Method:    getVideoInfo
 * Signature: ()Lcom/fasterlight/sdl/SDLVideoInfo;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_video_SDLVideo_getVideoInfo
  (JNIEnv *env, jclass clazz)
{
	jobject viobj;
	const SDL_VideoInfo* sdl_vinfo;

	JSDL_NewObject(JSDLPKG "/video/SDLVideoInfo", &viobj);

	sdl_vinfo = SDL_GetVideoInfo();
	JSDL_ASSERT(sdl_vinfo != NULL);
		
	/* this is dangerous */
	JSDL_SetIntField(viobj, "flags", *((int*)sdl_vinfo) );
	JSDL_SetIntField(viobj, "videomem", sdl_vinfo->video_mem);

	/* todo: rest of fields */
	return viobj;
}

/*
 * Class:     com_fasterlight_sdl_video_SDLVideo
 * Method:    getVideoModes
 * Signature: ()[Lcom/fasterlight/sdl/SDLVideoMode;
 */
JNIEXPORT jobjectArray JNICALL Java_com_fasterlight_sdl_video_SDLVideo_getVideoModes
  (JNIEnv *, jclass, jobject, jint);

/*
 * Class:     com_fasterlight_sdl_video_SDLVideo
 * Method:    getVideoSurface
 * Signature: ()Lcom/fasterlight/sdl/SDLSurface;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_video_SDLVideo_getVideoSurface
  (JNIEnv *env, jclass clazz)
{
	SDL_Surface* surf;
	surf = SDL_GetVideoSurface();
	if (surf == NULL)
		return NULL;
	else
		return JSDL_NewSurface(env, surf);
}

/*
 * Class:     com_fasterlight_sdl_video_SDLVideo
 * Method:    isVideoModeOK
 * Signature: (Lcom/fasterlight/sdl/SDLVideoMode;)I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_video_SDLVideo_isVideoModeOK
  (JNIEnv *env, jclass clazz, jobject vmode)
{
	int width, height, bpp, flags;

	JSDL_GetIntField(vmode, "width", &width);
	JSDL_GetIntField(vmode, "height", &height);
	JSDL_GetIntField(vmode, "bpp", &bpp);
	JSDL_GetIntField(vmode, "flags", &flags);
	return SDL_VideoModeOK(width, height, bpp, flags);
}

/*
 * Class:     com_fasterlight_sdl_video_SDLVideo
 * Method:    setVideoMode
 * Signature: (Lcom/fasterlight/sdl/SDLVideoMode;)Lcom/fasterlight/sdl/SDLSurface;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_video_SDLVideo_setVideoMode
  (JNIEnv *env, jclass clazz, jobject vmode)
{
	int width, height, bpp, flags;
	SDL_Surface* surf;

	JSDL_GetIntField(vmode, "width", &width);
	JSDL_GetIntField(vmode, "height", &height);
	JSDL_GetIntField(vmode, "bpp", &bpp);
	JSDL_GetIntField(vmode, "flags", &flags);
	surf = SDL_SetVideoMode(width, height, bpp, flags);
	JSDL_ASSERT(surf != NULL);
	return JSDL_NewSurface(env, surf);
}

/*
 * Class:     com_fasterlight_sdl_video_SDLVideo
 * Method:    internalInit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLVideo_internalInit
  (JNIEnv *env, jclass clazz)
{
	fprintf(stderr, "Initializing SDL video\n");
	JSDL_ASSERT(SDL_Init(SDL_INIT_VIDEO) == 0);
	JSDL_Setup(env);
}


