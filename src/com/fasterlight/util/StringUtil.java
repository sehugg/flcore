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
package com.fasterlight.util;

import java.util.*;

public class StringUtil
{
	/**
	  * Unescape \n, \r, \t, and \0xxx unicode escapes
	  */
	public static String unescape(String s)
	{
		int l = s.length();
		StringBuffer dest = null;
		int i = 0;
		while (i < l-1)
		{
			char c = s.charAt(i);
			if (c == '\\')
			{
				if (dest == null)
				{
					dest = new StringBuffer(l);
					dest.append(s.substring(0,i));
				}
				i++;
				c = s.charAt(i);
				switch (c)
				{
					case 'n' :
						dest.append('\n');
						break;
					case 'r' :
						dest.append('\r');
						break;
					case 't' :
						dest.append('\t');
						break;
					case '*' :
						break;
					case '0' :
						i++;
						int starti = i;
						while ( i < l && Character.isDigit(s.charAt(i)) )
							i++;
						int octint = Integer.parseInt(s.substring(starti,i));
						dest.append((char)octint);
						i--;
						break;
					default :
						dest.append(c);
						break;
				}
			} else {
				if (dest != null)
					dest.append(c);
			}
			i++;
		}
		if (dest != null)
			while (i < l)
				dest.append(s.charAt(i++));
		if (dest != null)
			return dest.toString();
		else
			return s;
	}

	/**
	  * Unescape \n, \r, \t, and \0xxx unicode escapes
	  */
	public static String escape(String s)
	{
		int l = s.length();
		StringBuffer dest = new StringBuffer(l);
		for (int i=0; i<l; i++)
		{
			char c = s.charAt(i);
			switch (c)
			{
				case '\n' :
					dest.append("\\n"); break;
				case '\r' :
					dest.append("\\r"); break;
				case '\t' :
					dest.append("\\t"); break;
				case '\\' :
					dest.append("\\\\"); break;
				default :
					if (c < 32 || c > 255)
					{
						dest.append("\\0");
						dest.append(Integer.toString(c));
						dest.append("\\*");
						break;
					} else {
						dest.append(c);
						break;
					}
			}
		}
		return dest.toString();
	}

	/**
	  * Like strstr for C...
	  */
	public static String subst(String s, String find, String replace)
	{
		int l = s.length();
		int end = s.indexOf(find);
		if (end < 0)
			return s;
		int start = 0;
		int length = find.length();
		StringBuffer res = new StringBuffer();
		while (end >= 0)
		{
			res.append(s.substring(start,end));
			res.append(replace);
			start = end+length;
			end = s.indexOf(find,start);
		}
		if (start < l)
			res.append(s.substring(start));
		return res.toString();
	}

	/**
	  * Remove a certain set of characters in a string
	  */
	public static String removeChars(String s, String chars, boolean allbut)
	{
		StringBuffer st = new StringBuffer();
		for (int i=0; i<s.length(); i++)
		{
			char ch = s.charAt(i);
			if ( (chars.indexOf(ch) < 0) ^ allbut )
				st.append(ch);
		}
		return st.toString();
	}

	private static Map internMap;

	/**
	  * Like String.intern(), but unlimited # of strings.
	  */
	public static synchronized String intern(String s)
	{
		if (internMap == null)
			internMap = new HashMap(7);
		String s2 = (String)internMap.get(s);
		if (s2 == null)
		{
			internMap.put(s,s);
			s2 = s;
		}
		return s2;
	}

  /**
   * Nicely truncate a string.
   * Truncates s to n chars breaking s on whitespace,
   * and adding a 'suffix' to the end.
   */
	public static String truncateNicely(String s, int n, String suffix)
	{
		int l = s.length();
		if (s.length() <= n)
			return s;

		int sufflen = suffix.length();
		for (int i=n-sufflen; i>0; i--)
		{
			char ch = s.charAt(i);
			if (Character.isWhitespace(ch))
			{
				return s.substring(0,i) + suffix;
			}
		}
		int p = n-suffix.length();
		if (p > 0)
			return s.substring(0,p) + suffix;
		else
			return suffix.substring(0,n);
	}

	/**
	  * Return the string after a given substring
	  */
	public static String substringAfter(String s, String substr, boolean fromend)
	{
		int p = fromend ? s.lastIndexOf(substr) : s.indexOf(substr);
		return (p >= 0) ? s.substring(p+1) : s;
	}

	/**
	  * Return the string before a given substring
	  */
	public static String substringBefore(String s, String substr, boolean fromend)
	{
		int p = fromend ? s.lastIndexOf(substr) : s.indexOf(substr);
		return (p >= 0) ? s.substring(0,p) : s;
	}

	//

	public static void main(String[] args)
	{
		System.out.println(unescape("CR\nfoo\tbar\n\041\042foo\043m^2\045"));
	}


}

