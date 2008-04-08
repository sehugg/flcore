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
import java.util.*;
import java.util.zip.*;

/**
  * Utilities for IO related thingies.
  */
public class IOUtil
{
	/**
	  * Reads the contents of a Reader into a String.
	  * Also closes the input stream.
	  */
	public static String readString(Reader in) throws IOException
	{
		StringBuffer st = new StringBuffer();
		int n;
		char[] arr = new char[512];
		do
		{
			n = in.read(arr, 0, arr.length);
			if (n < 0)
				break;
			st.append(arr, 0, n);
		} while (true);
		in.close();
		return st.toString();
	}

	/**
	  * Reads the entire contents of an InputStream into a byte array.
	  * Also closes the input stream.
	  */
	public static byte[] readBytes(InputStream in) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int n;
		byte[] arr = new byte[512];
		do
		{
			n = in.read(arr, 0, arr.length);
			if (n < 0)
				break;
			baos.write(arr, 0, n);
		} while (true);
		in.close();
		return baos.toByteArray();
	}

	public static String readString(String path) throws IOException
	{
		return readString(getTextResource(path));
	}

	public static BufferedReader getTextResource(String path)
		throws IOException
	{
		return new BufferedReader(
			new InputStreamReader(getBinaryResource(path)));
	}

	public static InputStream getBinaryResource(String path) throws IOException
	{
		InputStream in = ClassLoader.getSystemResourceAsStream(path);
		if (in == null)
			throw new IOException("Resource \"" + path + "\" not found");
		return new BufferedInputStream(in);
	}

	public static Object readSerializedObject(String path) throws IOException
	{
		return readSerializedObject(getBinaryResource(path));
	}

	public static Object readSerializedObject(String path, boolean compressed) throws IOException
	{
		if (compressed)
			return readSerializedObject(new GZIPInputStream(getBinaryResource(path)));
		else
			return readSerializedObject(getBinaryResource(path));
	}

	public static Object readSerializedObject(InputStream in)
		throws IOException
	{
		try
		{
			ObjectInputStream oin = new ObjectInputStream(in);
			Object top = oin.readObject();
			oin.close();
			return top;
		} catch (IOException ioe)
		{
			throw ioe;
		} catch (Exception exc)
		{
			throw new IOException(exc.toString());
		}
	}

	public static void writeSerializedObject(String path, Object obj)
		throws IOException
	{
		writeSerializedObject(
			new BufferedOutputStream(new FileOutputStream(path)),
			obj);
	}

	public static void writeSerializedObject(OutputStream out, Object obj)
		throws IOException
	{
		try
		{
			ObjectOutputStream oout = new ObjectOutputStream(out);
			oout.writeObject(obj);
			oout.close();
		} catch (IOException ioe)
		{
			throw ioe;
		} catch (Exception exc)
		{
			throw new IOException(exc.toString());
		}
	}

	public static void writeSerializedObject(
		String path,
		Object obj,
		boolean compress) throws IOException
	{
		if (compress)
		{
			writeSerializedObject(
				new BufferedOutputStream(
					new GZIPOutputStream(new FileOutputStream(path))),
				obj);
		} else
			writeSerializedObject(path, obj);
	}

	public static List getFilesInClassPath(String path, String endsWith)
		throws IOException
	{
		Set list = new TreeSet();
		StringTokenizer st =
			new StringTokenizer(
				System.getProperty("java.class.path"),
				File.pathSeparator);
		while (st.hasMoreTokens())
		{
			String pathElement = st.nextToken();
			File file = new File(pathElement, path);
			if (file.isDirectory())
			{
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					String filename = files[i].getName();
					if (filename.endsWith(endsWith))
						list.add(filename);
				}
			}
		}
		ArrayList reallist = new ArrayList();
		reallist.addAll(list);
		return reallist;
	}

	public static String getDirectoryOf(String filename)
	{
		int pos = filename.lastIndexOf('/');
		if (pos < 0)
			pos = filename.lastIndexOf('\\');
		return (pos < 0) ? "." : filename.substring(0, pos);
	}

	// from GLOUtil

	public static void grabRawBytes(
		String path,
		byte[] arr,
		int offset,
		int len,
		int skip,
		int bpp,
		int stride)
		throws IOException
	{
		InputStream in = getBinaryResource(path);
		grabRawBytes(in, arr, offset, len, skip, bpp, stride);
		in.close();
	}

	public static void grabRawBytes(
		InputStream in,
		byte[] arr,
		int offset,
		int len,
		int skip,
		int bpp,
		int stride)
		throws IOException
	{
		DataInputStream din = new DataInputStream(in);
		if (skip > 0)
			din.skip(skip);
		if (bpp >= stride)
			din.readFully(arr, offset, len);
		else
		{
			int end = offset + len;
			while (offset + stride <= end)
			{
				for (int i = 0; i < bpp; i++)
					arr[offset++] = (byte) din.read();
				for (int j = 0; j < (stride - bpp); j++)
					din.read();
			}
		}
	}

	public static String findBaseURLForResource(String resource)
		throws NoSuchElementException
	{
		java.net.URL sampleUrl = ClassLoader.getSystemResource(resource);
		if (sampleUrl == null)
			throw new NoSuchElementException("Could not find texs directory");
		String urlbase = sampleUrl.toString();
		int pos = urlbase.lastIndexOf('/');
		if (pos > 0)
			urlbase = urlbase.substring(0, pos + 1);
		System.out.println(urlbase);
		return urlbase;
	}


}
