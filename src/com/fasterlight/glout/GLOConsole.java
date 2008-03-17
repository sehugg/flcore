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
package com.fasterlight.glout;

import java.io.*;
import java.util.List;

import com.fasterlight.spif.*;

/**
  * A component that prints a text buffer in multiple lines
  * with word-wrap.
  *
  * todo: respect maxLines
  * todo: get rid of CRLF crap
  * todo: auto-scroll
  * todo: trim text buffer when full
  * todo: first line gets chopped
  */
public class GLOConsole
extends GLOAbstractList
{
	StringBuffer text = new StringBuffer();
	int maxLines = 1000;
	int[] lines = new int[maxLines*2];
	int rowspacing = 2;
	int head, tail;
	boolean autoScroll = true;

	// we can use a List
	Object last_list_obj;
	PropertyEvaluator prop_list;

	static final int MAX_TEXTSIZE = 1000*100;

	//

	public GLOConsole()
	{
	}

	public GLOConsole(String s)
	{
		append(s);
	}

	public boolean isSelected(int row)
	{
		return false;
	}

	public void selectRow(int row)
	{
	}

	public int getRowHeight()
	{
		return getShader("text").ysize + rowspacing;
	}

	public int getRowWidth()
	{
		return 1000; //todo?
	}

	public int getRowCount()
	{
		return (tail >= head) ? tail-head : maxLines-head+tail;
	}

	public void drawRow(GLOContext ctx, int row, int xpos, int ypos, boolean selected)
	{
		GLFontServer fs = ctx.getFontServer();
		fs.setPosition(xpos, ypos);
		int i = (head+row) % maxLines;
		int s = lines[i*2];
		int e = lines[i*2+1];
		fs.drawTextFrag(text, s, e-s);
//System.out.println(row + " " + i + " " + text.substring(s,e));
	}

	//

	private void reset()
	{
		head = tail = 0;
	}

	public void append(String s)
	{
		for (int i=0; i<s.length(); i++)
			append(s.charAt(i));
	}

	public void append(StringBuffer s)
	{
		for (int i=0; i<s.length(); i++)
			append(s.charAt(i));
	}

	public void append(char ch)
	{
		if (text.length() > MAX_TEXTSIZE)
			computeLines();
		text.append(ch);
		reset();
	}

	GLOVirtualBox getVirtualBox()
	{
		GLOComponent parent = getParent();
		return (parent instanceof GLOVirtualBox) ? (GLOVirtualBox)parent : null;
	}

	boolean isDelimiter(char ch)
	{
		return (ch == ' ');
	}

	void computeLines()
	{
		GLOVirtualBox box = getVirtualBox();
		if (box == null)
			return;
		int fontWidth = getShader("text").xsize;
		int fontHeight = getShader("text").ysize;
		int charsPerLine = box.getWidth()/fontWidth-1;
		int pos = 0;
		int start = 0;
		int xpos = 0;
		int textlen = text.length();
		boolean chomp = false; // if we should just chomp whitespace
		boolean flush = false;
		char lastCR = 0;

		while (pos <= textlen)
		{
			if (pos == textlen)
			{
				flush = true;
			} else {
   			char ch = text.charAt(pos);
   			switch (ch)
   			{
   				case '\r':
   				case '\n':
   					if (lastCR==0 || ch==lastCR) {
	   					flush = true;
	   					lastCR = ch;
	   				} else
	   					start=pos+1;
   					break;
   				default:
   					// if 'escape' char, skip it
   					if (ch > 255)
   					{
   						break;
   					}
   					if (chomp) {
   						chomp = false;
   						start = pos;
   						xpos = 0;
   					}
   					else if (xpos >= charsPerLine)
   					{
   						int oldpos = pos;
   						while (--pos > start)
   						{
   							char ch2 = text.charAt(pos);
   							if (isDelimiter(ch2))
   								break;
   						}
   						if (pos <= start)
   							pos = oldpos;
   						flush = true;
   						chomp = true;
   					}
   					else
   						xpos++;
   					break;
   			}
   		}
			// should we output the next line?
			if (flush)
			{
//System.err.println("line " + tail + ": " + start + " " + pos);
				lines[tail*2] = start;
				lines[tail*2+1] = pos;
				tail++;
				if (tail >= maxLines)
					tail = 0;
				if (tail == head)
					head = tail;
				flush = false;
				xpos = 0;
			}
			pos++;
		}

		// chomp text if head of 1st line > 0 offset
		if (lines[head*2] > 0)
		{
			int s = lines[head*2];
			text.delete(0, s);
			for (int i=0; i<lines.length; i++)
			{
				lines[i] -= s;
			}
		}

		// set the width & height of the parent virtual box
		// and this component
		setScrollBoxSize();
		if (autoScroll)
		{
			box.setYOffset(box.getYRange());
		}
	}

	public void render(GLOContext ctx)
	{
		try {
			updateWithListData();

			if (tail == head)
				computeLines();

			setShader("text");
			super.render(ctx);
		} catch (Exception exc) {
			setStandardOut(false);
			setStandardErr(false);
			exc.printStackTrace();
		}
	}

	public OutputStream getOutputStream()
	{
		return new OutputStream() {
			public void write(int ch) {
				append((char)ch);
			}
		};
	}

	PrintStream oldout, olderr;

	public void setStandardOut(boolean b)
	{
		if (b && oldout==null)
		{
			oldout = System.out;
			System.setOut(new PrintStream(getOutputStream()));
		}
		else if (oldout != null)
		{
			System.setOut(oldout);
			oldout = null;
		}
	}

	public void setStandardErr(boolean b)
	{
		if (b && olderr==null)
		{
			olderr = System.err;
			System.setErr(new PrintStream(getOutputStream()));
		}
		else if (olderr != null)
		{
			System.setOut(olderr);
			olderr = null;
		}
	}

	// list property stuff

	public String getPropertyForList()
	{
		return getKey(prop_list);
	}

	public void setPropertyForList(String s)
	{
		this.prop_list = new PropertyEvaluator(s);
	}

	public List getList()
	{
		if (prop_list != null)
		{
			return (List)getForPropertyKey(prop_list);
		} else
			return null;
	}

	public void appendMessage(String s)
	{
		append(s);
		append("\r\n");
	}

	public void updateWithListData()
	{
		if (prop_list != null)
		{
			List list = getList();
			if (list != null)
			{
				int l = list.size();
				if (l > 0)
				{
					// start at end of list and work backwards
					int i = l-1;
					while (i >= 0)
					{
						Object o = list.get(i);
						if (o == last_list_obj)
							break;
						i--;
					}
					// now start from that point and go to end of list, adding
					// each element to the console
					while (++i < l)
					{
						Object o = list.get(i);
						if (o != null)
						{
							appendMessage(o.toString());
							// save this for later so we know where to start from next time
							last_list_obj = o;
						}
					}
				}
			}
		}
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOConsole.class);

	static {
		prophelp.registerSet("append", "append", String.class);
		prophelp.registerSet("stdout", "setStandardOut", boolean.class);
		prophelp.registerSet("stderr", "setStandardErr", boolean.class);
		prophelp.registerGetSet("list_prop", "PropertyForList", String.class);
	}

	public Object getProp(String key)
	{
		Object o = prophelp.getProp(this, key);
		if (o == null)
			o = super.getProp(key);
		return o;
	}

	public void setProp(String key, Object value)
	{
		try {
			prophelp.setProp(this, key, value);
		} catch (PropertyRejectedException e) {
			super.setProp(key, value);
		}
	}

}
