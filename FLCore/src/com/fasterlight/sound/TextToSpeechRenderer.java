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
package com.fasterlight.sound;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
  * Performs table-based  string substitution to convert a
  * string of words and numbers into speech
  */
public class TextToSpeechRenderer
{
	String prefix;
	List entries = new ArrayList();
	boolean fast_talk = true;
	boolean do_queue = true;
	SoundServer sserver;

	//

	class Entry
	{
		String str,fn;
		Entry(String str, String fn)
		{
			this.str = str;
			this.fn = fn;
		}
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setQueueing(boolean b)
	{
		this.do_queue = b;
	}

	public boolean getQueueing()
	{
		return do_queue;
	}

	public void setSoundServer(SoundServer sserv)
	{
		this.sserver = sserv;
	}

	public SoundServer getSoundServer()
	{
		return sserver;
	}

	public static String clean(String s)
	{
		StringBuffer st = new StringBuffer();
		int whitespace=-1;
		for (int i=0; i<s.length(); i++)
		{
			char ch = s.charAt(i);
			if (Character.isLetterOrDigit(ch) || ch=='.' || ch=='-')
			{
				if (whitespace>0)
				{
					st.append(" ");
				}
				whitespace=0;
				st.append(Character.toLowerCase(ch));
			}
			else if (Character.isWhitespace(ch) && whitespace==0)
			{
				whitespace = 1;
			}
			else
			{
				// dump it
			}
		}
		return st.toString();
	}

	public void loadTransTable(String name)
	throws IOException
	{
		InputStream in = ClassLoader.getSystemResourceAsStream(name);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		loadTransTable(reader);
		in.close();
	}

	public void loadTransTable(BufferedReader in)
	throws IOException
	{
		do {
			String line = in.readLine();
			if (line == null)
				break;
			if (line.length() == 0 || line.charAt(0) == '#')
				continue;
			StringTokenizer st = new StringTokenizer(line, "\t");
			if (!st.hasMoreTokens())
				continue;
			try {
				String fn = st.nextToken();
				String str = st.nextToken();
				entries.add(new Entry(clean(str), fn));
			} catch (NoSuchElementException e) {
				System.out.println("Malformed line: " + line);
			}
		} while (true);
	}

	void queueFile(String fn)
	{
		if (prefix != null)
			fn = prefix + fn;
		if (debug)
			System.out.println("Queued " + fn);
		if (sserver != null)
		{
			SoundClip clip = sserver.getClip(fn, 0);
			if (clip != null)
				sserver.queue(clip);
			else
				System.out.println("Could not find sound \"" + fn + '"');
		}
	}

	public void sayNumber(String s, boolean finish)
	{
		if (s.charAt(0) == '-')
		{
			queueFile("numbers/minus.wav");
			s = s.substring(1);
		}

		// if a decimal, split into 2
		int p = s.indexOf('.');
		if (p >= 0)
		{
			if (p>0)
				sayNumber(s.substring(0,p), p>=s.length()-1);
			if (p<s.length()-1)
			{
				queueFile("numbers/point.wav");
				sayDigits(s.substring(p+1), finish);
			}
			return;
		}

		// now sound it out!
		while (s.length() > 1 && s.charAt(0) == '0')
			s = s.substring(1);
		if (s.equals("0"))
		{
			sayDigit('0', finish);
			return;
		}

		sayNumber2(s, finish);
	}

	protected void sayNumber2(String s, boolean finish)
	{
		while (s.length() > 0 && s.charAt(0) == '0')
			s = s.substring(1);
		if (s.length() == 0)
			return;

		char ch0 = s.charAt(0);
		switch (s.length())
		{
			case 4:
				sayNumber2(s.substring(0,2), false);
				queueFile("numbers/00a.wav");
				sayNumber2(s.substring(2,4), finish);
				break;
			case 3:
				sayDigit(ch0, false);
				String s2 = s.substring(1);
				// shortcuts for fast talkers
				// if y0x, say "y zero x"
				// if yxx, say "y xx"
				if (fast_talk)
				{
					if (s2.charAt(0) == '0')
					{
						sayDigits(s2, false);
						break;
					}
					if (s2.charAt(1) != '0')
					{
						sayNumber2(s2, false);
						break;
					}
					// todo
				}
				queueFile("numbers/00a.wav");
				sayNumber2(s2, finish);
				break;
			case 2:
				if (ch0 == '1')
				{
					queueFile("numbers/1" + s.charAt(1) + (finish?'b':'a') + ".wav");
				} else {
					if (s.charAt(1) != '0')
					{
						queueFile("numbers/" + ch0 + "0a.wav");
						sayDigit(s.charAt(1), finish);
					} else {
						queueFile("numbers/" + ch0 + '0' + (finish?'b':'a') + ".wav");
					}
				}
				break;
			case 1:
				queueFile("numbers/" + ch0 + (finish?'b':'a') + ".wav");
				break;
		}
	}

	public void sayDigits(String s, boolean finish)
	{
		for (int i=0; i<s.length(); i++)
		{
			sayDigit(s.charAt(i), finish&&(i==s.length()-1));
		}
	}

	public void sayDigit(char ch, boolean finish)
	{
		if (Character.isDigit(ch))
		{
			queueFile("numbers/" + ch + (finish?'b':'a') + ".wav");
		}
	}

	public void say(String s)
	{
		s = clean(s);
		if (debug)
			System.out.println("say \"" + s + '"');
		if (s.length() == 0)
			return;

		// try to find a match in the entries table
		Iterator it = entries.iterator();
		while (it.hasNext())
		{
			Entry e = (Entry)it.next();
			if (s.startsWith(e.str))
			{
				int l = e.str.length();
				if (l < s.length()) // TODO: ==?
				{
					char ch = s.charAt(l);
					if (Character.isWhitespace(ch) || ch=='.')
					{
						if (debug)
							System.out.println("matched with \"" + e.str + '"');
						queueFile(e.fn);
						s = clean(s.substring(e.str.length()));
						say(s);
						return;
					}
				}
			}
		}

		// no match, get the next word
		int p = s.indexOf(' ');
		String word;
		if (p>0) {
			word = s.substring(0,p);
			s = s.substring(p);
		} else {
			word = s;
			s = null;
		}

		if (debug)
			System.out.println("next word: \"" + word + '"');

		boolean finish = (s == null);
		if (word.endsWith("."))
			finish = true;

		// see if it is a number
		char ch = word.charAt(0);
		if (Character.isDigit(ch) || ch=='-')
		{
			try {
				float f = Float.parseFloat(word);
				sayNumber(word, finish);
			} catch (NumberFormatException nfe) {
			}
		}

		// if there is more of the string, say the rest
		if (s != null)
			say(s);
	}

	public void test(String s)
	{
		say(s);
	}

	public static boolean debug = false;

	//

	public static void main(String[] args)
	throws Exception
	{
		System.out.println(clean(" this is the best whitespace, like, ever ")+"*");
		System.out.println(clean(" ,,, ")+"*");

		TextToSpeechRenderer ttsr = new TextToSpeechRenderer();
		ttsr.debug = true;
		ttsr.loadTransTable("sounds/transtbl.txt");
		ttsr.test("great job, single engine press --- we have srb sep!");
		ttsr.test("0");
		ttsr.test("1.");
		ttsr.test("10");
		ttsr.test("11");
		ttsr.test("12.");
		ttsr.test("19 km");
		ttsr.test("20 degrees");
		ttsr.test("29%");
		ttsr.test("99 km/s");
		ttsr.test("100 percent");
		ttsr.test("200.");
		ttsr.test("101");
		ttsr.test("110");
		ttsr.test("119");
		ttsr.test("125");
		ttsr.test("185");
		ttsr.test("-1200");
		ttsr.test("123.456");
	}
}
