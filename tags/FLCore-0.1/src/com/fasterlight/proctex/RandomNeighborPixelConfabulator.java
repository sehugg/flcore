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


public class RandomNeighborPixelConfabulator
extends PixelConfabulator
{
	int mask1 = B0|B1|B7;
	int mask2 = B1|B2|B3;
	int mask3 = B3|B4|B5;
	int mask4 = B5|B6|B7;

	private int generatePixel(int parpix, long surround, int nmask)
	{
		int rnd = nextRand(parpix, surround, nmask);
		int n = rnd&7;
		if ((nmask & (1<<n)) != 0)
		{
			long s = surround>>>(n*8);
			int pix = ((int)s) & 0xff;
			if (pix > parpix)
				pix = clampRand(rnd, parpix, pix+1);
			else if (pix < parpix)
				pix = clampRand(rnd, pix, parpix+1);
			return pix;
		} else
			return parpix;
	}

	public int newPixel(int parpix, long surround)
	{
		int pix1 = generatePixel(parpix, surround, mask1);
		int pix2 = generatePixel(parpix, surround, mask2);
		int pix3 = generatePixel(parpix, surround, mask3);
		int pix4 = generatePixel(parpix, surround, mask4);
		return pix1 + (pix2<<8) + (pix3<<16) + (pix4<<24);
	}
}
