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


#include "SDL.h"
#include "jni.h"

extern void JSDL_InternalError(JNIEnv* env, const char* fname, int lineno);
extern void JSDL_Setup(JNIEnv* env);
extern jobject JSDL_NewSurface(JNIEnv* env, SDL_Surface* surf);

#define JSDLPKG "com/fasterlight/sdl"

#define JSDL_ASSERT(COND) \
	if (!(COND)) { \
		JSDL_InternalError(env, __FILE__, __LINE__); \
		return 0; \
	}

#define JSDL_ASSERTBLIT(RES) do { \
	int _res = RES; \
	if (_res==-2) { \
		jclass cls = (*env)->FindClass(env, JSDLPKG "/MemoryLostException"); \
		(*env)->ThrowNew(env, cls, "memory lost during blit"); \
		(*env)->DeleteLocalRef(env, cls); \
		return 0; \
	} \
	JSDL_ASSERT(_res==0); \
} while (0)

#define JSDL_GetTYPEField(OBJ, FNAME, DEST, TYPE, SIG) do { \
	jfieldID fid; \
	jclass cls; \
	cls = (*env)->GetObjectClass(env, OBJ); \
	fid = (*env)->GetFieldID(env, cls, FNAME, SIG); \
	JSDL_ASSERT(fid != NULL); \
	*(DEST) = (*env)->Get##TYPE##Field(env, OBJ, fid); \
	(*env)->DeleteLocalRef(env, cls); \
} while (0)

#define JSDL_SetTYPEField(OBJ, FNAME, VALUE, TYPE, SIG) do { \
	jfieldID fid; \
	jclass cls; \
	cls = (*env)->GetObjectClass(env, OBJ); \
	fid = (*env)->GetFieldID(env, cls, FNAME, SIG); \
	JSDL_ASSERT(fid != NULL); \
	(*env)->Set##TYPE##Field(env, OBJ, fid, VALUE); \
	(*env)->DeleteLocalRef(env, cls); \
} while (0)

#define JSDL_GetIntField(OBJ, FNAME, DEST) \
	JSDL_GetTYPEField(OBJ, FNAME, DEST, Int, "I")

#define JSDL_SetIntField(OBJ, FNAME, VALUE) \
	JSDL_SetTYPEField(OBJ, FNAME, VALUE, Int, "I")

#define JSDL_GetShortField(OBJ, FNAME, DEST) \
	JSDL_GetTYPEField(OBJ, FNAME, DEST, Short, "S")

#define JSDL_SetShortField(OBJ, FNAME, VALUE) \
	JSDL_SetTYPEField(OBJ, FNAME, VALUE, Short, "S")

#define JSDL_GetByteField(OBJ, FNAME, DEST) \
	JSDL_GetTYPEField(OBJ, FNAME, DEST, Byte, "B")

#define JSDL_SetByteField(OBJ, FNAME, VALUE) \
	JSDL_SetTYPEField(OBJ, FNAME, VALUE, Byte, "B")

#define JSDL_GetBooleanField(OBJ, FNAME, DEST) \
	JSDL_GetTYPEField(OBJ, FNAME, DEST, Boolean, "Z")

#define JSDL_SetBooleanField(OBJ, FNAME, VALUE) \
	JSDL_SetTYPEField(OBJ, FNAME, VALUE, Boolean, "Z")

#define JSDL_GetCharField(OBJ, FNAME, DEST) \
	JSDL_GetTYPEField(OBJ, FNAME, DEST, Char, "C")

#define JSDL_SetCharField(OBJ, FNAME, VALUE) \
	JSDL_SetTYPEField(OBJ, FNAME, VALUE, Char, "C")

#define JSDL_GetFloatField(OBJ, FNAME, DEST) \
	JSDL_GetTYPEField(OBJ, FNAME, DEST, Float, "F")

#define JSDL_SetFloatField(OBJ, FNAME, VALUE) \
	JSDL_SetTYPEField(OBJ, FNAME, VALUE, Float, "F")

#define JSDL_NewObject(CNAME, DEST) do { \
	jclass cls; \
	jmethodID mid; \
	cls = (*env)->FindClass(env, CNAME); \
	JSDL_ASSERT(cls != NULL); \
	mid = (*env)->GetMethodID(env, cls, "<init>", "()V"); \
	JSDL_ASSERT(mid != NULL); \
	*(DEST) = (*env)->NewObject(env, cls, mid); \
	JSDL_ASSERT(*(DEST) != NULL); \
} while (0)

extern jfieldID jsdlfid_rect_x;
extern jfieldID jsdlfid_rect_y;
extern jfieldID jsdlfid_rect_w;
extern jfieldID jsdlfid_rect_h;
extern jfieldID jsdlfid_surf_addr;
extern jfieldID jsdlfid_joydata_addr;

#define JSDL_GetRect(OBJ, RECT) do { \
	(RECT)->x = (*env)->GetIntField(env, OBJ, jsdlfid_rect_x); \
	(RECT)->y = (*env)->GetIntField(env, OBJ, jsdlfid_rect_y); \
	(RECT)->w = (*env)->GetIntField(env, OBJ, jsdlfid_rect_w); \
	(RECT)->h = (*env)->GetIntField(env, OBJ, jsdlfid_rect_h); \
} while (0)

#define JSDL_GetSurface(OBJ, SURF) do { \
	*(SURF) = (SDL_Surface*)(*env)->GetIntField(env, OBJ, jsdlfid_surf_addr); \
} while (0)

#define JSDL_GetJoystick(OBJ, SURF) do { \
	*(SURF) = (SDL_Joystick*)(*env)->GetIntField(env, OBJ, jsdlfid_joydata_addr); \
} while (0)

