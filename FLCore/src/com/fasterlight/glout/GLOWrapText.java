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

import java.util.*;

import com.fasterlight.spif.*;

/**
  * A component that lays out multi-line text, automatically
  * wrapping long lines at whitespace.  It is really a table
  * container in disguise.
  */
public class GLOWrapText
extends GLOTableContainer
{
	protected String text, prop_text;
	protected int minxchars=10, minrows=1;

	public GLOWrapText()
	{
		setRowPadding(4);
	}

	public GLOWrapText(String text)
	{
		this();
		setText(text);
	}

	public GLOWrapText(String text, int minxchars)
	{
      this();
      setMinXChars(minxchars);
      setText(text);
	}

   static List splitText(String s, int linelen)
	{
		List list = new ArrayList();
		StringTokenizer tok = new StringTokenizer(s, " \t\n\r");
		StringBuffer curline = new StringBuffer();
		while (tok.hasMoreTokens())
		{
			String word = tok.nextToken();
			if ( (curline.length() + word.length()) > linelen)
			{
				list.add(curline.toString());
				curline.setLength(0);
			}
			if (curline.length() > 0)
				curline.append(' ');
			curline.append(word);
		}
		if (curline.length() > 0)
			list.add(curline.toString());
		return list;
   }

	public void layout()
	{
		removeAllChildren();

		String s = getText();
		int row=0;
		if (s != null)
		{
			List lines = splitText(s, minxchars);
			for (int i=0; i<lines.size(); i++)
			{
				String line = (String)lines.get(i);
				GLOLabel lab = new GLOLabel(minxchars+1);
				lab.setAlignment(GLOLabel.ALIGN_CENTER);
				lab.setText(line);
				this.add(lab);
				row++;
			}
		}
		while (row<minrows)
		{
			GLOLabel label = new GLOLabel(minxchars+1);
			label.setAlignment(GLOLabel.ALIGN_CENTER);
			add(label);
			row++;
		}
		if (row > 0)
			setNumRows(row);

		super.layout();
	}

	public void setText(String text)
	{
		if (prop_text != null)
		{
			setForPropertyKey(prop_text, text);
		}
		this.text = text;
		layout();
	}

	public String getText()
	{
		if (prop_text != null)
		{
			Object o = getForPropertyKey(prop_text);
			if (o != null)
				text = convertToString(o);
		}
		return text;
	}

	/**
	  * Sets the minimum # of characters displayed horizontally.
	  */
	public void setMinXChars(int n)
	{
		this.minxchars = n;
	}

	public int getMinXChars()
	{
		return minxchars;
	}

	/**
	  * Sets the minimum # of rows
	  */
	public void setMinRows(int n)
	{
		this.minrows = n;
	}

	public int getMinRows()
	{
		return minrows;
	}

	protected String convertToString(Object o)
	{
		return PropertyUtil.toString(o);
	}

	public String getPropertyForText()
	{
		return prop_text;
	}

	public void setPropertyForText(String s)
	{
		this.prop_text = s;
	}

	public void render(GLOContext ctx)
	{
		String s1 = text;
		String s2 = getText();
		if (s2 != null && !s2.equals(s1))
			layout();
		super.render(ctx);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOWrapText.class);

	static {
		prophelp.registerGetSet("text", "Text", String.class);
		prophelp.registerGetSet("text_prop", "PropertyForText", String.class);
		prophelp.registerGetSet("minxchars", "MinXChars", int.class);
		prophelp.registerGetSet("minrows", "MinRows", int.class);
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
