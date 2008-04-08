/********************************************************************
    Copyright (c) 2000-2008 Steven E. Hugg.

    This file is part of FLCore.

    FLCore is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FLCore is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FLCore.  If not, see <http://www.gnu.org/licenses/>.
*********************************************************************/
package com.fasterlight.proctex;


public class AveragingPixelConfabulator
extends PixelConfabulator
{
	private int generatePixel(int parpix, long surround, int p1, int p2, int p3)
	{
		int total=parpix*3;
		total += ((int)(surround>>>(p1*8))) & 0xff;
		total += ((int)(surround>>>(p2*8))) & 0xff;
		total += ((int)(surround>>>(p3*8))) & 0xff;
		int avg = total/6;

		// return random #
		int res;
		int dev = Math.abs(avg-parpix);
		int rnd = nextRand(parpix, surround, p1+p2+p3);
		res = avg + clampRand(rnd, -dev, dev+1);
		if (res<0) res=0;
		else if (res>255) res=255;
		return res;
	}

	public int newPixel(int parpix, long surround)
	{
		int pix1 = generatePixel(parpix, surround, 0, 1, 7);
		int pix2 = generatePixel(parpix, surround, 1, 2, 3);
		int pix4 = generatePixel(parpix, surround, 3, 4, 5);
		int pix3 = generatePixel(parpix, surround, 5, 6, 7);
		return pix1 + (pix2<<8) + (pix3<<16) + (pix4<<24);
	}
}
