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
package com.fasterlight.io.test;

import java.io.IOException;
import java.util.List;

import junit.framework.*;

import com.fasterlight.io.IOUtil;
import com.fasterlight.testing.*;

public class IOTests
extends FLTestCase
{
	RandomnessProvider rnd = new RandomnessProvider();

	//

	public IOTests(String name)
	{
		super(name);
	}

	//

	public void testIO()
	throws IOException
	{
		String str = IOUtil.readString("com/fasterlight/io/test/test.txt");
		java.util.zip.CRC32 crc = new java.util.zip.CRC32();
		for (int i=0; i<str.length(); i++)
			if (str.charAt(i) != '\r')
				crc.update(str.charAt(i));
		assertTrue("testIO(): test string was wrong ("+crc.getValue()+")", crc.getValue()==3051116955l);
	}

	public void testCP1()
	throws IOException
	{
/*
		System.out.println(ClassPath.getDefault());
		System.out.println(System.getProperty("java.class.path"));
		System.out.println(System.getProperty("env.class.path"));
*/
		List list = IOUtil.getFilesInClassPath("com/fasterlight/io/test", "IOTests.class");
		assertTrue(list.size() == 1);
		assertEquals("IOTests.class", list.get(0));
	}

	public void testCP2()
   throws IOException
   {
		List list = IOUtil.getFilesInClassPath("com/fasterlight/io", ".class");
//System.out.println(list);
		assertTrue(list.size() > 2);
   }

	public void testCP2b()
   throws IOException
   {
		List list = IOUtil.getFilesInClassPath("modules", ".ini");
		System.out.println(list);
   }

	//

	public static Test suite()
	{
		TestSuite suite = new TestSuite(IOTests.class);
		return suite;
	}

}
