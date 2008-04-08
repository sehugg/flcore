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
import java.util.Enumeration;

import Acme.IntHashtable;

// 3% of earth texs (238115/8258048) are unique

public class Foo1
{
	ProcTexProvider ptp;
	IntHashtable ents = new IntHashtable();
	IntHashtable entarr[];
	int numscanned;

	//


	class BlockEntry
	{
		int blk;
		int count;
		public int hashCode()
		{
			return blk;
		}
		public boolean equals(Object o)
		{
			return (o instanceof BlockEntry) && ((BlockEntry)o).blk==blk;
		}
	}

	//

	public Foo1(ProcTexProvider ptp)
	{
		this.ptp = ptp;
		entarr = new IntHashtable[256];
		for (int i=0; i<256; i++)
			entarr[i] = new IntHashtable();
	}

	public void analyzeQuad(int xx, int yy, int level)
	{
		TexQuad tq = ptp.getTexQuad(xx, yy, level);
		TexQuad pq = ptp.getQuadParent(tq);

		byte[] tarr = tq.getByteData();
		int w = ptp.getWidth(tq);
		int h = ptp.getHeight(tq);

		int x1 = ((tq.x&1)==0) ? 1 : ptp.getWidth(tq)>>1;
		int y1 = ((tq.y&1)==0) ? 1 : ptp.getHeight(tq)>>1;

		for (int y=1; y<h-1; y+=2)
		{
			int adr = 1+(y*w);
			for (int x=1; x<w-1; x+=2)
			{
				int parpix = ptp.getPixel(pq, x1+(x>>1), y1+(y>>1));

				int blk = (tarr[adr]&0xff) + ((tarr[adr+1]&0xff)<<8) +
					((tarr[adr+w+1]&0xff)<<16) + ((tarr[adr+w]&0xff)<<24);
				int rotamt = getRotAmt(blk);
//				System.out.println(Integer.toString(bestblk, 16));

				addBlockEntry(entarr[parpix]);
				addBlockEntry(ents);

				adr += 2;
				numscanned++;
			}
		}
		System.out.println("# of blocks scanned: " + numscanned);
		System.out.println("# of unique blocks: " + ents.size());
	}

	void addBlockEntry(IntHashtable hash)
	{
				BlockEntry be = (BlockEntry)hash.get(bestblk);
				if (be == null)
				{
					be = new BlockEntry();
					be.blk = bestblk;
					hash.put(bestblk, be);
				}
				be.count++;
	}

	void pruneEntries(int x, int lim)
	{
		IntHashtable newset = new IntHashtable();
		Enumeration e = entarr[x].elements();
		int total=0;
		while (e.hasMoreElements())
		{
			BlockEntry be = (BlockEntry)e.nextElement();
			if (be.count >= lim)
				newset.put(be.blk, be);
		}
		entarr[x] = newset;
	}

	int countEntries(int count)
	{
		Enumeration e = ents.elements();
		int total=0;
		while (e.hasMoreElements())
		{
			BlockEntry be = (BlockEntry)e.nextElement();
			if (be.count == count)
				total++;
		}
		return total;
	}

	int bestblk;

	int getRotAmt(int x)
	{
		int best = x;
		int bestrot = 0;
		for (int i=1; i<4; i++)
		{
			x = (x<<8) + ((x>>24)&0xff);
			if (x > best)
			{
				best = x;
				bestrot = i;
			}
		}
		this.bestblk = best;
		return bestrot;
	}

	void writeBinary(OutputStream out)
	throws IOException
	{
		DataOutputStream dout = new DataOutputStream(
			new BufferedOutputStream(out));

		int len = 257;
		for (int i=0; i<256; i++)
		{
			len += entarr[i].size()+1;
		}
		dout.writeInt(len);

		int ofs = 257;
		for (int i=0; i<256; i++)
		{
			dout.writeInt(ofs);
			ofs += entarr[i].size()+1;
		}
		dout.writeInt(ofs);

		for (int i=0; i<256; i++)
		{
			Enumeration e = entarr[i].elements();
			while (e.hasMoreElements())
			{
				BlockEntry be = (BlockEntry)e.nextElement();
				dout.writeInt(be.blk);
			}
			dout.writeInt(0);
		}

		dout.writeInt(0xcafedead);

		dout.close();
	}

	//

	public static void main(String[] args)
	throws Exception
	{
		ProcTexProvider ptp = new ProcTexProvider();
		ptp.setPathPrefix("texs/Earth/Earth");

		Foo1 foo = new Foo1(ptp);
		for (int y=0; y<16; y++)
			for (int x=0; x<32; x++)
				foo.analyzeQuad(x,y,12);

		for (int i=1; i<10; i++)
		{
			System.out.println("freq " + i + ": " + foo.countEntries(i));
		}

		for (int i=0; i<256; i++)
		{
			foo.pruneEntries(i, 3);
			System.out.println("pixel " + i + ": " + foo.entarr[i].size());
		}

		System.out.println("Writing...");
		foo.writeBinary(new FileOutputStream("pixtab.out"));
	}

	//

	static boolean debug = true;

}

