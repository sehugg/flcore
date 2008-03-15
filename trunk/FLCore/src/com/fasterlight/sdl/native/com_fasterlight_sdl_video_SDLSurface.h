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

/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_fasterlight_sdl_video_SDLSurface */

#ifndef _Included_com_fasterlight_sdl_video_SDLSurface
#define _Included_com_fasterlight_sdl_video_SDLSurface
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    blit
 * Signature: (Lcom/fasterlight/sdl/video/SDLRect;Lcom/fasterlight/sdl/video/SDLSurface;Lcom/fasterlight/sdl/video/SDLRect;)I
 */
JNIEXPORT jint JNICALL Java_com_fasterlight_sdl_video_SDLSurface_blit
  (JNIEnv *, jobject, jobject, jobject, jobject);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    convert
 * Signature: (Lcom/fasterlight/sdl/video/SDLPixelFormat;I)Lcom/fasterlight/sdl/video/SDLSurface;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_video_SDLSurface_convert
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    convertForDisplay
 * Signature: ()Lcom/fasterlight/sdl/video/SDLSurface;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_video_SDLSurface_convertForDisplay
  (JNIEnv *, jobject);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    createRGBSurface
 * Signature: (IIIIIIII)Lcom/fasterlight/sdl/video/SDLSurface;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_video_SDLSurface_createRGBSurface
  (JNIEnv *, jclass, jint, jint, jint, jint, jint, jint, jint, jint);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    createRGBSurfaceFrom
 * Signature: ([IIIIIIIII)Lcom/fasterlight/sdl/video/SDLSurface;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_video_SDLSurface_createRGBSurfaceFrom
  (JNIEnv *, jclass, jintArray, jint, jint, jint, jint, jint, jint, jint, jint);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    fillRect
 * Signature: (Lcom/fasterlight/sdl/video/SDLRect;I)V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_fillRect
  (JNIEnv *, jobject, jobject, jint);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    flip
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_flip
  (JNIEnv *, jobject);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    free
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_free
  (JNIEnv *, jobject);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    loadBMP
 * Signature: (Ljava/lang/String;)Lcom/fasterlight/sdl/video/SDLSurface;
 */
JNIEXPORT jobject JNICALL Java_com_fasterlight_sdl_video_SDLSurface_loadBMP
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    lock
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_lock
  (JNIEnv *, jobject);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    saveBMP
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_saveBMP
  (JNIEnv *, jobject, jstring);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    setAlpha
 * Signature: (IB)V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_setAlpha
  (JNIEnv *, jobject, jint, jbyte);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    setClipping
 * Signature: (IIII)V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_setClipping
  (JNIEnv *, jobject, jint, jint, jint, jint);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    setColorKey
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_setColorKey
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    setColors
 * Signature: ([Lcom/fasterlight/sdl/video/SDLColor;II)Z
 */
JNIEXPORT jboolean JNICALL Java_com_fasterlight_sdl_video_SDLSurface_setColors
  (JNIEnv *, jobject, jobjectArray, jint, jint);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    unlock
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_unlock
  (JNIEnv *, jobject);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    update
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_update__
  (JNIEnv *, jobject);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    update
 * Signature: (Lcom/fasterlight/sdl/video/SDLRect;)V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_update__Lcom_fasterlight_sdl_video_SDLRect_2
  (JNIEnv *, jobject, jobject);

/*
 * Class:     com_fasterlight_sdl_video_SDLSurface
 * Method:    update
 * Signature: ([Lcom/fasterlight/sdl/video/SDLRect;)V
 */
JNIEXPORT void JNICALL Java_com_fasterlight_sdl_video_SDLSurface_update___3Lcom_fasterlight_sdl_video_SDLRect_2
  (JNIEnv *, jobject, jobjectArray);

#ifdef __cplusplus
}
#endif
#endif
