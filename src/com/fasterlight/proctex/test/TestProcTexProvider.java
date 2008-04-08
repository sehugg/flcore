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
package com.fasterlight.proctex.test;

import junit.framework.TestCase;

import com.fasterlight.proctex.*;

public class TestProcTexProvider extends TestCase
{

	public TestProcTexProvider(String arg0)
	{
		super(arg0);
	}

	public void doPTPTest(int level, int border)
	{
		ProcTexProvider ptp = new ProcTexProvider(level, border);
		ptp.setPathPrefix("foobar");
		ptp.setCacheAll(true);
		assertEquals(level, ptp.getTexPower());
		assertEquals(1 << level, ptp.getTexSize());
		for (int i = level; i <= ptp.MAX_LEVEL; i++)
		{
			int s = (i>level) ? 2 : 1;
			for (int y = 0; y < s; y++)
			{
				for (int x = 0; x < s; x++)
				{
					TexQuad tq = ptp.getTexQuad(x, y, i);
					byte[] barr = tq.getByteData();
					assertEquals(ptp.getWidth(tq) * ptp.getHeight(tq), barr.length);
					if (i>level)
					{
						verifyBordersAgree(ptp, tq, -1, -1);
					}
				}
			}
		}
	}

	private void verifyBordersAgree(ProcTexProvider ptp, TexQuad tq, int dx, int dy)
	{
		TexQuad tq2 = ptp.getTexQuad(ptp.fixTexKey(new TexKey(tq.x+dx, tq.y+dy, tq.level)));
		int b = ptp.getBorder();
		int uts = ptp.getUsableTexSize();
		int pix1 = ptp.getPixel(tq, b+dx, b+dy);
		int pix2 = ptp.getPixel(tq2, b+uts-dx, b+uts-dy);
		assertEquals("pixels for " + tq + " and " + tq2 + " did not agree", pix1, pix2);
	}


	public void testPTP2_2()
	{
		doPTPTest(2, 2);
	}
	public void testPTP4_2()
	{
		doPTPTest(4, 2);
	}
	public void testPTP6_2()
	{
		doPTPTest(6, 2);
	}
	public void testPTP8_2()
	{
		doPTPTest(8, 2);
	}
	public void testPTP8_3()
	{
		doPTPTest(8, 3);
	}
}
