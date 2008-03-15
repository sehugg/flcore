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
#include <unistd.h>

int main()
{
	SDL_Surface *surf;
	SDL_Surface *bmp;
	SDL_Rect r;
	int res;

	SDL_Init(SDL_INIT_VIDEO);
	surf = SDL_SetVideoMode(320, 240, 16, 0);
	bmp = SDL_LoadBMP("xlogo.bmp");
/*	bmp = SDL_DisplayFormat(bmp);*/
	r.x = r.y = 40;
	r.w = bmp->w;
	r.h = bmp->h;
	SDL_FillRect(surf, &r, 43);
	res = SDL_BlitSurface(bmp, NULL, surf, &r);
	printf("rect: %d,%d,%d,%d\n", r.x,r.y,r.w,r.h);
	printf("blit: %d\n", res);
	SDL_UpdateRect(surf,0,0,0,0);
	sleep(2);
	SDL_Quit();

	return 0;
}
