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
package com.fasterlight.util.test;

import junit.framework.*;

import com.fasterlight.testing.RandomnessProvider;
import com.fasterlight.util.StringUtil;

public class StringUtilTests extends TestCase
{
	RandomnessProvider rnd = new RandomnessProvider();

	//

	public StringUtilTests(String s)
	{
		super(s);
	}

	private String rndstring(int minlen, int maxlen)
	{
		if (maxlen == 0)
			return "";
		int l = rnd.rnd(0, maxlen);
		StringBuffer st = new StringBuffer();
		while (l-- > 0)
		{
			switch (rnd.rnd(8))
			{
				case 0 :
					st.append(' ');
					break;
				default :
					st.append((char) rnd.rnd(32, 128));
					break;
					//				case 1: st.append((char)rnd.rnd(0,0x10000)); break;
			}
		}
		return st.toString();
	}

	public void test_subst()
	{
		assertEquals("", StringUtil.subst("", "foo", "bar"));
		assertEquals("", StringUtil.subst("foo", "foo", ""));
		assertEquals("", StringUtil.subst("fff", "f", ""));
		assertEquals("bar", StringUtil.subst("foo", "foo", "bar"));
		assertEquals("barbar", StringUtil.subst("foofoo", "foo", "bar"));
		assertEquals("123231", StringUtil.subst("124241", "4", "3"));
	}

	public void test_remove_chars()
	{
		assertEquals("", StringUtil.removeChars("", "", true));
		assertEquals("", StringUtil.removeChars("", "", false));
		assertEquals("1111", StringUtil.removeChars("1212121", "2", false));
		assertEquals("", StringUtil.removeChars("121212211", "12", false));
		assertEquals("222", StringUtil.removeChars("1212121", "2", true));
		assertEquals("121212211", StringUtil.removeChars("121212211", "12", true));
	}

	public void test_truncate_nicely()
	{
		for (int i = 0; i < 10000; i++)
		{
			String s1 = rndstring(0, 100);
			int tl = rnd.rnd(0, 100);
			String suff = rndstring(0, 100);
			String s2 = StringUtil.truncateNicely(s1, tl, suff);
			if (false)
			{
				System.out.println("\n" + s1);
				System.out.println(suff);
				System.out.println(s1.length() + " " + tl + " " + suff.length());
				System.out.println(s2);
			}
			assertTrue(s2.length() <= tl);
			if (s1.length() > tl && suff.length() <= tl)
				assertTrue(s2.endsWith(suff));
		}
	}

	public void test_escape_unescape()
	{
		for (int i = 0; i < 10000; i++)
		{
			String s1 = rndstring(0, 100);
			String s2 = StringUtil.escape(s1);
			String s3 = StringUtil.unescape(s2);
			assertEquals(s1, s3);
		}
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(StringUtilTests.class);
		return suite;
	}

}
