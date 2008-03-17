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
package com.fasterlight.io;

import java.io.*;
import java.util.Stack;

/**
 * Reader for an IFF file, with hats tipped off to the Lightwave (LWO) format.
 */
public class IFFReader
{
	protected DataInputStream in;
	protected Stack chunkstack;
	protected boolean debug;
	protected boolean little_endian;
	protected int curpos;

	//

	public IFFReader(DataInputStream in)
	{
		this.in = in;
		this.chunkstack = new Stack();
	}

	public String open()
	throws IOException
	{
		IFFChunk c = enterChunk();
		if (!c.getTypeStr().equals("FORM"))
			throw new IOException("Expected 'FORM', got " + c);
		int ftype = readInt();
		if (debug)
			System.out.println("open() = " + c + ", " + int2str4(ftype));
		return int2str4(ftype);
	}

	public void close()
	throws IOException
	{
		if (debug)
			System.out.println("close()");
		in.close();
	}

	public IFFChunk enterChunk()
	throws IOException
	{
		int type = readInt();
		int len = readInt();
		IFFChunk chunk = new IFFChunk(type, len, curpos);
		if (debug)
			System.out.println("enterChunk() = " + chunk);
		chunkstack.push(chunk);
		return chunk;
	}

	public IFFChunk enterSubChunk()
	throws IOException
	{
		int type = readInt();
		int len = readUshort();
		IFFChunk chunk = new IFFChunk(type, len, curpos);
		if (debug)
			System.out.println("getSubChunk() = " + chunk);
		chunkstack.push(chunk);
		return chunk;
	}

	public IFFChunk exitChunk()
	throws IOException
	{
		IFFChunk c = (IFFChunk)chunkstack.pop();
		int skipb = c.startpos+c.len-curpos;
		in.skip(skipb);
		curpos += skipb;
		if (debug)
			System.out.println("exitChunk() = " + c + ", skipped " + skipb);
		return c;
	}

	public IFFChunk peekChunk()
	{
		IFFChunk c = (IFFChunk)chunkstack.peek();
		return c;
	}

	public int bytesLeft()
	{
		IFFChunk c = (IFFChunk)chunkstack.peek();
		return (c.startpos+c.len-curpos);
	}

	protected void inc(int nbytes)
	throws IOException
	{
		if (!chunkstack.isEmpty())
		{
			IFFChunk c = peekChunk();
			if (curpos + nbytes > c.startpos + c.len)
				throw new IOException("Chunk " + c + " size overrun");
		}
		curpos += nbytes;
	}

	public int readInt()
	throws IOException
	{
		inc(4);
		int x = in.readInt();
		return little_endian ? flipint(x) : x;
	}

	public int readUbyte()
	throws IOException
	{
		inc(1);
		return in.readByte() & 0xff;
	}

	public int readShort()
	throws IOException
	{
		inc(2);
		int x = in.readShort();
		return little_endian ? flipshort(x) : x;
	}

	public int readUshort()
	throws IOException
	{
		return readShort() & 0xffff;
	}

	public float readFloat()
	throws IOException
	{
		inc(4);
		return in.readFloat();
	}

	public String readString0()
	throws IOException
	{
		StringBuffer st = new StringBuffer();
		do {
			char ch = (char)readUbyte();
			if (ch == 0)
				break;
			st.append(ch);
		} while (true);
		// if size of str is even, add 1 byte
		if ((st.length() & 1) == 0)
			readUbyte();
		return st.toString();
	}

	public static String int2str4(int x)
	{
		char c4 = (char)(x&0xff);
		char c3 = (char)((x>>8)&0xff);
		char c2 = (char)((x>>16)&0xff);
		char c1 = (char)((x>>24)&0xff);
		return new String("" + c1 + c2 + c3 + c4);
	}

	public static int str2int4(String s)
	{
		return
			(s.charAt(0)<<24) +
			(s.charAt(1)<<16) +
			(s.charAt(2)<<8) +
			s.charAt(3);
	}

	public static int flipint(int x)
	{
		return ((x&0xff)<<24) | ((x&0xff00)<<8) | ((x&0xff0000)>>>8) | ((x&0xff000000)>>>24);
	}

	public static int flipshort(int x)
	{
		return ((x&0xff)<<8) | ((x&0xff00)>>8);
	}

	public void setDebug(boolean b)
	{
		this.debug = b;
	}

	public static void main(String[] args)
	throws Exception
	{
		DataInputStream din = new DataInputStream(new FileInputStream("test.lwo"));
		IFFReader r = new IFFReader(din);
		IFFChunk c = r.enterChunk();
		System.out.println(c);
		din.close();
	}
}
