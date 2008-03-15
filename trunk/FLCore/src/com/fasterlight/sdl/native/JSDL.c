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


#include "JSDL.h"
#include <stdio.h>

static char jsdl_is_setup = 0;


void JSDL_InternalError(JNIEnv* env, const char* fname, int lineno)
{
	jclass cls = (*env)->FindClass(env, JSDLPKG "/SDLException");
	if (cls != NULL) {
		char buf[128];
		sprintf(buf, "JSDL internal error (%s:%d)", fname, lineno);
		(*env)->ThrowNew(env, cls, buf);
	}
	(*env)->DeleteLocalRef(env, cls);
}

void JSDL_Setup(JNIEnv* env)
{
	jclass cls;
	
	if (jsdl_is_setup)
		return;
	jsdl_is_setup = 1;
	
	cls = (*env)->FindClass(env, JSDLPKG "/video/SDLRect");
	jsdlfid_rect_x = (*env)->GetFieldID(env, cls, "x", "S");
	JSDL_ASSERT(jsdlfid_rect_x != NULL);
	jsdlfid_rect_y = (*env)->GetFieldID(env, cls, "y", "S");
	JSDL_ASSERT(jsdlfid_rect_y != NULL);
	jsdlfid_rect_w = (*env)->GetFieldID(env, cls, "w", "S");
	JSDL_ASSERT(jsdlfid_rect_w != NULL);
	jsdlfid_rect_h = (*env)->GetFieldID(env, cls, "h", "S");
	JSDL_ASSERT(jsdlfid_rect_h != NULL);
	(*env)->DeleteLocalRef(env, cls);
	
	cls = (*env)->FindClass(env, JSDLPKG "/video/SDLSurface");
	jsdlfid_surf_addr = (*env)->GetFieldID(env, cls, "_sdlsurf", "I");
	JSDL_ASSERT(jsdlfid_surf_addr != NULL);
	(*env)->DeleteLocalRef(env, cls);

	cls = (*env)->FindClass(env, JSDLPKG "/joystick/SDLJoystick");
	jsdlfid_joydata_addr = (*env)->GetFieldID(env, cls, "_data", "I");
	JSDL_ASSERT(jsdlfid_joydata_addr != NULL);
	(*env)->DeleteLocalRef(env, cls);
}

jobject JSDL_NewSurface(JNIEnv* env, SDL_Surface* surf)
{
	jobject jsurf;
	JSDL_NewObject(JSDLPKG "/video/SDLSurface", &jsurf);
	(*env)->SetIntField(env, jsurf, jsdlfid_surf_addr, (jint)surf);
	JSDL_SetIntField(jsurf, "flags", surf->flags);
	JSDL_SetIntField(jsurf, "width", surf->w);
	JSDL_SetIntField(jsurf, "height", surf->h);
	JSDL_SetShortField(jsurf, "pitch", surf->pitch);
	return jsurf;
}

jfieldID jsdlfid_rect_x;
jfieldID jsdlfid_rect_y;
jfieldID jsdlfid_rect_w;
jfieldID jsdlfid_rect_h;
jfieldID jsdlfid_surf_addr;
jfieldID jsdlfid_joydata_addr;

