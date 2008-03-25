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

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Random;

import Acme.LruHashtable;

import com.fasterlight.io.IOUtil;
import com.sun.opengl.util.BufferUtil;

/**
  * A provider for procedural textures.
  * Textures may be loaded from .raw files, or may be
  * generated on-the-fly if no disk texture is available.
  */
public class ProcTexProvider
{
	private String prefix;
	// optz: replace with smarter cache
	private LruHashtable tqcache;
	private int pixsize = 1;
	private String suffix = ".raw";
	private byte[] palette;
	private int[] palints;
	private boolean hasPalette = true;

	public static final int MIN_LEVEL = 7;
	public static final int MAX_LEVEL = 29;

	private PixelConfabulator confab = new DefaultPixelConfabulator();

	private boolean cacheAll = true;

	private float minvalue, maxvalue; // for elevationmodel
	private int squashLevel = 0;

	private int texPower = 8;
	private int texSize = (1 << texPower);
	private int borderSize = 2;
	private int usableTexSize = texSize - borderSize * 2;

	private int arrmask;

	//

	public ProcTexProvider()
	{
		this(8, 2);
	}

	public ProcTexProvider(int texpower, int border)
	{
		this.texPower = texpower;
		this.texSize = (1 << texpower);
		this.borderSize = border;
		this.usableTexSize = texSize - borderSize * 2;
		this.arrmask = getMask(texPower);
		// base the cache size on available memory
		// (we assume only one cache is heavily active at a time)
		long maxMemory = Runtime.getRuntime().maxMemory();
		int lruSize = (int)(maxMemory / (texSize*texSize*4));
		if (lruSize <= 0) // maxMemory could be Long.MAX_VALUE
			lruSize = 256;
		this.tqcache = new LruHashtable(lruSize);
	}
	
	public void setSquashLevel(int squashLev)
	{
		this.squashLevel = squashLev;
	}

	public int getMask(int level)
	{
		return (level >= texPower)
			? ((1 << (texPower + texPower)) - 1)
			: (1 << (level + texPower)) - 1;
	}

	public void setPixelSize(int ps)
	{
		this.pixsize = ps;
	}

	public int getPixelSize()
	{
		return pixsize;
	}
	
	public void setHasPalette(boolean hasPal)
	{
		this.hasPalette = hasPal;
	}
	
	public boolean hasPalette()
	{
		return hasPalette;
	}
	
	void checkPalette()
	{
		if (palette == null)
			throw new RuntimeException("No palette for " + prefix);
	}

	public void setPixelConfabulator(PixelConfabulator confab)
	{
		if (confab == null)
			throw new IllegalArgumentException();
		this.confab = confab;
	}

	public PixelConfabulator getPixelConfabulator()
	{
		return confab;
	}

	public void setMinMax(float min, float max)
	{
		this.minvalue = min;
		this.maxvalue = max;
	}
	
	public byte[] getPaletteBytes()
	{
		checkPalette();
		return palette;
	}

	public int[] getPaletteInts()
	{
		checkPalette();
		return palints;
	}

	public void setCacheAll(boolean b)
	{
		this.cacheAll = b;
	}

	public boolean getCacheAll()
	{
		return cacheAll;
	}

	protected void loadPalette()
	{
		String path = prefix + ".col";
		byte[] pal = new byte[256 * 3];
		palette = pal;
		palints = new int[256];
		try
		{
			IOUtil.grabRawBytes(path, pal, 0, pal.length, 0, 3, 3);
		} catch (IOException ioe)
		{
			System.out.println("Couldn't load palette " + path + ": " + ioe);
			// assume greyscale
			for (int i = 0; i < 768; i++)
				pal[i] = (byte) (i / 3);
		}
		for (int i = 0; i < 256; i++)
			palints[i] = pal[i * 3] + (pal[i * 3 + 1] << 8) + (pal[i * 3 + 2] << 16);
	}

	public void setPathPrefix(String prefix)
	{
		this.prefix = prefix;
		if (hasPalette)
			loadPalette();
		if (debug)
			System.out.println("Prefix is " + prefix);
	}

	public String getPathPrefix()
	{
		return prefix;
	}

	public TexQuad getTexQuad(int x, int y, int level)
	{
		return getTexQuad(new TexKey(x, y, level));
	}

	public TexQuad getTexQuad(TexKey key)
	{
		TexQuad tq = (TexQuad) tqcache.get(key);
		if (tq != null)
			return tq;
		tq = new TexQuad(key.x, key.y, key.level);
		prepareTexQuad(tq);
		return tq;
	}

	public TexKey fixTexKey(TexKey key)
	{
		int mask = ((1 << (key.level - texPower)) - 1);
		return new TexKey(key.x & mask, key.y & mask, key.level);
	}

	public IntBuffer getRGBData(TexQuad tq)
	{
		IntBuffer ints = BufferUtil.newIntBuffer(tq.getByteData().length);
		getRGBData(tq, ints);
		return ints;
	}

	public void getRGBData(TexQuad tq, IntBuffer ints)
	{
		checkPalette();
		byte[] bytes = tq.getByteData();
		int l = bytes.length;
		ints.position(0);
		for (int i = 0; i < l; i++)
		{
			ints.put(palints[bytes[i] & 0xff]);
		}
		ints.rewind();
	}

	protected boolean loadTexQuad(TexQuad tq)
	{
		try
		{
			String path =
				(tq.level <= MIN_LEVEL)
					? prefix + '-' + MIN_LEVEL + suffix
					: prefix + '-' + tq.level + '-' + tq.x + '-' + tq.y + suffix;
			if (debug)
				System.out.println("fetching " + path);
			int w = getWidth(tq);
			int h = getHeight(tq);
			byte[] cmap = new byte[w * h * pixsize];
			int skip = 0 * 786;
			int l = w * h * pixsize;
			IOUtil.grabRawBytes(path, cmap, 0, l, skip, pixsize, pixsize);
			tq.setByteData(cmap);
			tq.minvalue = minvalue;
			tq.maxvalue = maxvalue;
			return true;
		} catch (IOException ioe)
		{
			//			ioe.printStackTrace();
			return false;
		}
	}

	protected boolean prepareTexQuad(TexQuad key)
	{
		if (loadTexQuad(key))
		{
			if (cacheAll)
				tqcache.put(new TexKey(key), key);
			return true;
		}

		// we only put created quads in the cache
		createTexQuad(key);
		tqcache.put(new TexKey(key), key);
		return true;
	}

	// pixels around the current pix

	protected void createRandomQuad(TexQuad tq)
	{
		if (debug)
			System.out.println("creating random quad " + tq);

		byte[] tqdata = new byte[getWidth(tq) * getHeight(tq) * pixsize];
		tq.setByteData(tqdata);
		/*
for (int y=0; y<getHeight(tq); y+=4)
	for (int x=0; x<getWidth(tq); x+=4)
		setPixel(tq, x, y, 0xff);

if (tq != null)
	return;
*/
		Random rnd = new Random(tq.hashCode() ^ prefix.hashCode());
		for (int i = 0; i < tqdata.length; i += 4)
		{
			int x = rnd.nextInt();
			tqdata[i + 0] = (byte) (x & 0xff);
			tqdata[i + 1] = (byte) ((x >>> 8) & 0xff);
			tqdata[i + 2] = (byte) ((x >>> 16) & 0xff);
			tqdata[i + 3] = (byte) ((x >>> 24) & 0xff);
		}
	}

	protected void createTexQuad(TexQuad tq)
	{
		if (tq.level < this.getTexPower())
			throw new IllegalArgumentException("Level " + tq.level + " not supported");

		if (tq.x < 0
			|| tq.x > (2 << tq.level) >> texPower
			|| tq.y < 0
			|| tq.y > (1 << tq.level) >> texPower)
			throw new IllegalArgumentException("Quad " + tq + " out of range");

		if (tq.level == this.getTexPower())
		{
			createRandomQuad(tq);
			tq.minvalue = 0;
			tq.maxvalue = 255;
			return;
		}

		// get the parent quad to use as a reference
		TexQuad parent = getQuadParent(tq);
		if (parent == null)
		{
			throw new RuntimeException("Couldn't load parent " + tq + "!");
		}

		if (debug)
			System.out.println("creating quad " + tq);

		byte[] tqdata = new byte[getWidth(tq) * getHeight(tq) * pixsize];
		tq.setByteData(tqdata);

		byte[] pqdata = parent.getByteData();
		int pqmask = getMask(parent.level);

		int lopix = 255;
		int hipix = 0;
		// we can squash a level if we can cut all of its levels
		// exactly in 1/2
		boolean squash = (!parent.squashed) && (squashLevel > 0) && (parent.level >= squashLevel); 
		if (squash)
		{
			for (int i = 0; i <= pqmask; i++)
			{
				int pix = pqdata[i] & 0xff;
				if (pix < lopix)
				{
					lopix = pix;
					if (hipix-lopix >= 128)
					{
						squash = false;
						break;
					}
				}
				if (pix > hipix)
				{
					hipix = pix;
					if (hipix-lopix >= 128)
					{
						squash = false;
						break;
					}
				}
			}
		}

		if (squash && hipix > lopix)
		{
			int cen = ((hipix+lopix+1)>>1);
			if (cen < 64)
				cen = 64;
			if (cen > 192)
				cen = 192;
			lopix = cen-64;
			hipix = cen+64;
			assert(lopix>=0&&hipix<=256);
			float minvalue = parent.minvalue + ((parent.maxvalue - parent.minvalue) * lopix) / 256;
			float maxvalue = parent.minvalue + ((parent.maxvalue - parent.minvalue) * hipix) / 256;
			System.out.println("Squashed " + parent.minvalue + ", " + parent.maxvalue + " to " + minvalue + ", " + maxvalue + " range " + (maxvalue-minvalue));
			byte[] src = pqdata;
			pqdata = new byte[pqdata.length];
			for (int i=0; i<=pqmask; i++)
			{
				pqdata[i] = (byte)((src[i]-lopix)*256/(hipix-lopix));
			}
			// TODO: not threadsafe
			parent.setData(minvalue, maxvalue, pqdata);
		}

		parent.squashed = true;
		tq.minvalue = parent.minvalue;
		tq.maxvalue = parent.maxvalue;

		// get the upper-left position
		// if border is 2, (1,1) on our parent quad goes to
		// (0,0)-(1,1) block on the dest. quad
		int x1 = ((tq.x & 1) == 0) ? 1 : (getWidth(parent) >> 1) - 1;
		int y1 = ((tq.y & 1) == 0) ? 1 : (getHeight(parent) >> 1) - 1;
		int s = getTexSize();

		// draw around the borders

		if (tq.level > 8)
		{
			for (int x = 0; x < s; x++)
			{
				int a = getPixel(parent, x1 + (x >> 1) - 1, y1);
				int b = getPixel(parent, x1 + (x >> 1) - 1, y1 + (s >> 1) - 1);
				setPixel(tq, x, 0, a);
				setPixel(tq, x, s - 1, b);
			}
			for (int y = 1; y < s - 1; y++)
			{
				int a = getPixel(parent, x1, y1 + (y >> 1) - 1);
				int b = getPixel(parent, x1 + (s >> 1) - 1, y1 + (y >> 1) - 1);
				setPixel(tq, 0, y, a);
				setPixel(tq, s - 1, y, b);
			}
		} else
		{
			for (int x = 0; x < s; x++)
			{
				int a = getPixel(parent, x1 + (x >> 1) - 1, y1);
				int b = getPixel(parent, x1 + (x >> 1) - 1, y1);
				setPixel(tq, x, 0, a);
				setPixel(tq, x, s - 1, b);
			}
			for (int y = 1; y < s - 1; y++)
			{
				int a = getPixel(parent, x1, y1 + (y >> 1) - 1);
				int b = getPixel(parent, x1, y1 + (y >> 1) - 1);
				setPixel(tq, 0, y, a);
				setPixel(tq, s - 1, y, b);
			}
		}

		// now fill in the inside
		// we are trying to map 126x126 pixels onto 252x252 pixels

		// seed random
		confab.init(tq);

		long surround;

		for (int y = 0; y < s; y += 2)
		{
			// fill up box pixels
			int xx = x1;
			int yy = y1 + (y >> 1);
			// todo: crap at seams
			int surr1 =
				(getPixel(parent, xx - 1, yy - 1) << 0)
					| (getPixel(parent, xx, yy - 1) << 8)
					| (getPixel(parent, xx + 1, yy - 1) << 16)
					| (getPixel(parent, xx + 1, yy) << 24);
			int surr2 =
				(getPixel(parent, xx + 1, yy + 1) << 0)
					| (getPixel(parent, xx, yy + 1) << 8)
					| (getPixel(parent, xx - 1, yy + 1) << 16)
					| (getPixel(parent, xx - 1, yy) << 24);
			int parpix = getPixel(parent, xx, yy);
			surround = ((surr1) & 0xffffffffL) | (((surr2) & 0xffffffffL) << 32);

			for (int x = 0; x < s; x += 2)
			{
				/*
				// naive implementation
				surr1 =
					(getPixel(parent, xx - 1, yy - 1) << 0)
						| (getPixel(parent, xx, yy - 1) << 8)
						| (getPixel(parent, xx + 1, yy - 1) << 16)
						| (getPixel(parent, xx + 1, yy) << 24);
				surr2 =
					(getPixel(parent, xx + 1, yy + 1) << 0)
						| (getPixel(parent, xx, yy + 1) << 8)
						| (getPixel(parent, xx - 1, yy + 1) << 16)
						| (getPixel(parent, xx - 1, yy) << 24);
				parpix = getPixel(parent, xx, yy);
				surround = ((surr1) & 0xffffffffL) | (((surr2) & 0xffffffffL) << 32);
				*/
				int newpix = confab.newPixel(parpix, surround);
				tqdata[y * s + x] = (byte) (newpix);
				tqdata[y * s + x + 1] = (byte) (newpix >> 8);
				tqdata[(y + 1) * s + x + 1] = (byte) (newpix >> 16);
				tqdata[(y + 1) * s + x] = (byte) (newpix >> 24);

				// move surround pixels
				// 0 1 2 3 4 5 6 7 8
				// 1 2 a b c 4 5 8 3
				//				System.out.println("\nold: " + Long.toString(surround,16));
				int oldparpix = parpix;
				parpix = (int) (surround >>> (8 * 3)) & 0xff; // from cell 3
					surround = ((surround >>> 8) & 0x0000000000ffffL) | // (1,2) -> (0,1)
		 ((surround << 8) & 0xffff0000000000L) | // (4,5) -> (5,6)
		 (((long) oldparpix) << (8 * 7)) | // cen -> (7)
		 (((long) pqdata[(yy - 1) * s + xx + 2] & 0xff) << (8 * 2)) | // new -> (2)
		 (((long) pqdata[((yy) * s + xx + 2) & pqmask] & 0xff) << (8 * 3)) | // new -> (3)
		 (((long) pqdata[((yy + 1) * s + xx + 2) & pqmask] & 0xff) << (8 * 4)) // new -> (4)
	;
				//				System.out.println("new: " + Long.toString(surround,16));

				xx++;
			}
		}
	}

	public int getPixel(TexQuad tq, int x, int y)
	{
		int i = (((y << texPower) + x) & arrmask) * pixsize;
		switch (pixsize)
		{
			case 1 :
				return (tq.data[i] & 0xff);
			case 2 :
				return (tq.data[i] & 0xff) + ((tq.data[i + 1] & 0xff) << 8);
			case 3 :
				return (tq.data[i] & 0xff)
					+ ((tq.data[i + 1] & 0xff) << 8)
					+ ((tq.data[i + 2] & 0xff) << 16);
			case 4 :
				return (tq.data[i] & 0xff)
					+ ((tq.data[i + 1] & 0xff) << 8)
					+ ((tq.data[i + 2] & 0xff) << 16)
					+ ((tq.data[i + 3] & 0xff) << 24);
			default :
				return -1;
		}
	}

	public void setPixel(TexQuad tq, int x, int y, int pix)
	{
		int i = ((y * getTexSize()) + x) * pixsize;
		for (int j = 0; j < pixsize; j++)
		{
			tq.data[i++] = (byte) pix;
			pix >>>= 8;
		}
	}

	public TexQuad getQuadParent(TexQuad tq)
	{
		return getTexQuad(tq.x >> 1, tq.y >> 1, tq.level - 1);
	}

	//

	public static void main(String[] args) throws Exception
	{
		ProcTexProvider ptp = new ProcTexProvider();
		ptp.setPathPrefix("texs/Earth/Earth");
		TexQuad tq = new TexQuad(0, 0, 13);
		ptp.loadTexQuad(tq);
	}

	//

	static boolean debug = true;

	public int getTexPower()
	{
		return texPower;
	}

	public int getTexSize()
	{
		return texSize;
	}

	public int getBorder()
	{
		return borderSize;
	}

	public int getUsableTexSize()
	{
		return usableTexSize;
	}

	public int getWidth(TexKey key)
	{
		return (key.level >= getTexPower()) ? (1 << getTexPower()) : 2 << key.level;
	}

	public int getHeight(TexKey key)
	{
		return (key.level >= getTexPower()) ? (1 << getTexPower()) : 1 << key.level;
	}

}
