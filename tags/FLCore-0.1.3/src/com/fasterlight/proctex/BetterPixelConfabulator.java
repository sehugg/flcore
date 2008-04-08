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

import java.io.*;
import java.util.Random;

import com.fasterlight.io.IOUtil;

public class BetterPixelConfabulator
extends PixelConfabulator
{
	int[] data;
	Random rnd = new Random();

	public BetterPixelConfabulator(int[] data)
	{
		this.data = data;
	}

	public BetterPixelConfabulator(String path)
	{
		try {
			DataInputStream din = new DataInputStream(IOUtil.getBinaryResource(path));
			int len = 256*64*8;
			data = new int[len];
			System.out.println("reading " + (len*4) + " bytes");
			for (int i=0; i<len; i++)
				data[i] = din.readInt();
			din.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void init(TexQuad tq)
	{
		rnd.setSeed(tq.hashCode());
	}

	private int rand(int lo, int hi)
	{
		return lo + ((rnd.nextInt()&0x7fffffff)%(hi-lo));
	}

	public int newPixel(int parpix, long surround)
	{
		int total = 0;
		int dev = 0;
		int last = 127;
		for (int i=0; i<8; i++)
		{
			int pix = (int)(surround & 0xff);
			total += pix;
			dev += Math.abs(last-pix);
			last = pix;
			surround >>>= 8;
		}

//		int pix = data[((parpix&0xff)<<8)+(total>>3)];

		// 1st 3 bits : deviation
		// next 6 bits : average
		// next 8 bits : parpix

//	System.out.println(parpix + " " + total + " " + dev);
		int pix = data[((parpix&0xff)<<9)+((total>>5)<<3)+(dev>>8)];

		switch (rnd.nextInt()&3)
		{
			case 0: return pix;
			case 1: return (pix>>>8) + (pix<<24);
			case 2: return (pix>>>16) + (pix<<16);
			case 3: return (pix>>>24) + (pix<<8);
			default: return pix;
		}
	}
}
