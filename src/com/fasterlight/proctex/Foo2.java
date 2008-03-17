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
import java.util.*;

// 3% of earth texs (238115/8258048) are unique

public class Foo2
{
	ProcTexProvider ptp;
	TreeMap ents = new TreeMap();
	int numscanned;

	static final int AVG_DIV = 4;

	//


	class BlockEntry
	implements Comparable
	{
		int parpix; // parent pixel (0-255)
		int blk; // 4x4 pixel block
		int avg; // average value/4 (0-63)
		int dev; // LN2(standard dev) (0-7)
		int count; // # of refs

		public int hashCode()
		{
			return blk^(avg<<24)^(dev<<16)^count;
		}
		public int compareTo(Object o)
		{
			BlockEntry be = (BlockEntry)o;
			if (parpix!=be.parpix)
				return parpix-be.parpix;
			if (avg!=be.avg)
				return avg-be.avg;
			if (dev!=be.dev)
				return dev-be.dev;
			if (blk<be.blk)
				return -1;
			if (blk>be.blk)
				return 1;
//			if (count!=be.count)
//				return count-be.count;
			return 0;
		}
		public boolean equals(Object o)
		{
			return (o instanceof BlockEntry) && ((BlockEntry)o).compareTo(this)==0;
		}
		public String toString()
		{
			return "("+parpix+":"+avg+":"+dev+" "+Integer.toString(blk,16)+","+count+")";
		}
	}

	//

	public Foo2(ProcTexProvider ptp)
	{
		this.ptp = ptp;
	}

	public void analyzeQuad(int xx, int yy, int level)
	{
		TexQuad tq = ptp.getTexQuad(xx, yy, level);
		TexQuad pq = ptp.getQuadParent(tq);

		System.out.println("analyzing " + tq);

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
				int parx = x1+(x>>1);
				int pary = y1+(y>>1);
				int parpix = ptp.getPixel(pq, parx, pary);
				long surr = getSurrounding(pq, parx, pary);
				int avg = getAverage(surr);
				int dev = getDeviation(surr, avg);
				avg /= AVG_DIV;

				int blk = (tarr[adr]&0xff) + ((tarr[adr+1]&0xff)<<8) +
					((tarr[adr+w+1]&0xff)<<16) + ((tarr[adr+w]&0xff)<<24);
				int rotamt = getRotAmt(blk);

				BlockEntry be = new BlockEntry();
				be.parpix = parpix;
				be.blk = bestblk;
				be.avg = avg;
				be.dev = dev;
				addBlockEntry(ents, be);

				adr += 2;
				numscanned++;
			}
		}
		System.out.println("# of blocks scanned: " + numscanned);
		System.out.println("# of unique blocks: " + ents.size());
	}

	int getAverage(long x)
	{
		int t=0;
		for (int i=0; i<8; i++)
		{
			t += (x&0xff);
			x >>>= 8;
		}
		return t/8;
	}

	int getDeviation(long x, int avg)
	{
		int t=0;
		int last = 127;
		for (int i=0; i<8; i++)
		{
			int pix = (int)(x&0xff);
			t += Math.abs(pix-last);
			last = pix;
			x >>>= 8;
		}
		return t>>8;
	}

	long getSurrounding(TexQuad pq, int x, int y)
	{
		return (
			(long)(ptp.getPixel(pq,x-1,y-1)<<0) +
			(long)(ptp.getPixel(pq,x  ,y-1)<<8) +
			(ptp.getPixel(pq,x+1,y-1)<<16) +
			(ptp.getPixel(pq,x+1,y  )<<24) +
			(ptp.getPixel(pq,x+1,y+1)<<32) +
			(ptp.getPixel(pq,x  ,y+1)<<40) +
			(ptp.getPixel(pq,x-1,y+1)<<48) +
			(ptp.getPixel(pq,x-1,y  )<<56)
		);
	}

	void addBlockEntry(TreeMap tmap, BlockEntry newbe)
	{
		BlockEntry be = (BlockEntry)tmap.get(newbe);
		if (be == null)
		{
			newbe.count++;
			tmap.put(newbe, newbe);
		} else {
			be.count++;
		}
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

	BlockEntry getClosestEntry(int parpix, int avg, int dev)
	{
		BlockEntry key1 = new BlockEntry();
		key1.parpix = parpix;
		key1.avg = avg;
		key1.dev = dev;

		SortedMap submap = ents.tailMap(key1);

		BlockEntry bestent = null;
		Iterator it = submap.values().iterator();
		while (it.hasNext())
		{
			BlockEntry ent = (BlockEntry)it.next();
			if (bestent != null && ent.dev != key1.dev)
				break;
			if (bestent == null || ent.count > bestent.count)
				bestent = ent;
		}
		return (bestent != null) ? bestent : key1;
	}

	void writeBinary(OutputStream out)
	throws IOException
	{
		DataOutputStream dout = new DataOutputStream(
			new BufferedOutputStream(out));

		for (int par=0; par<256; par++)
		{
			for (int avg=0; avg<256/AVG_DIV; avg++)
			{
				for (int dev=0; dev<8; dev++)
				{
					BlockEntry be = getClosestEntry(par,avg,dev);
//					System.out.println(be);
					dout.writeInt(be.blk);
				}
			}
		}

		dout.writeInt(0xcafebeef);

		dout.close();
	}

	//

	public static void main(String[] args)
	throws Exception
	{
		ProcTexProvider ptp = new ProcTexProvider();
		ptp.setPathPrefix("texs/Earth/Earth");

		int level=12;
		Foo2 foo = new Foo2(ptp);
		for (int y=0; y<(1<<level)>>8; y++)
			for (int x=0; x<(2<<level)>>8; x++)
				foo.analyzeQuad(x,y,level);

		System.out.println("Writing...");
		foo.writeBinary(new FileOutputStream("pixtab2.out"));
	}

	//

	static boolean debug = true;

}

