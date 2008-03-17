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

import java.util.Random;

public abstract class PixelConfabulator
{
	public static int B0 = (1<<0);
	public static int B1 = (1<<1);
	public static int B2 = (1<<2);
	public static int B3 = (1<<3);
	public static int B4 = (1<<4);
	public static int B5 = (1<<5);
	public static int B6 = (1<<6);
	public static int B7 = (1<<7);

	public abstract int newPixel(int parpix, long surround);

	//

	private Random rnd = new Random();
	private int level;

	//

	private int getHashed(long seed)
	{
		rnd.setSeed(seed);
		return rnd.nextInt();
	}

	protected int nextRand(int parpix, long surround, int nmask)
	{
		long seed = (parpix<<16) ^ surround ^ nmask ^ (level<<8);
		return getHashed(seed);
	}

	public void init(TexQuad tq)
	{
		level = tq.level;
	}

	public static int clampRand(int x, int a, int b)
	{
		return ((x&0x7fffffff)%(b-a)) + a;
	}

}
