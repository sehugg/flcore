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

/**
  * An OutputStream that multiplexes into two separate OutputStream
  * objects.
  */
public class SplitOutputStream
extends OutputStream
{
	OutputStream o1,o2;

	public SplitOutputStream(OutputStream o1, OutputStream o2)
	{
		this.o1 = o1;
		this.o2 = o2;
	}

	public void close()
	throws IOException
	{
		o1.close();
		o2.close();
	}

	public void flush()
	throws IOException
	{
		o1.flush();
		o2.flush();
	}

	public void write(int i)
	throws IOException
	{
		o1.write(i);
		o2.write(i);
	}

	public void write(byte[] arr)
	throws IOException
	{
		o1.write(arr);
		o2.write(arr);
	}

	public void write(byte[] arr, int ofs, int len)
	throws IOException
	{
		o1.write(arr,ofs,len);
		o2.write(arr,ofs,len);
	}
}
