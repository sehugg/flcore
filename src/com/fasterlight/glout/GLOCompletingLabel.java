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

import java.util.SortedMap;

/**
  * A type of label that can complete text from a SortedMap
  * while the user is typing.
  */
public class GLOCompletingLabel
extends GLOLabel
{
	protected SortedMap mapping;

	public GLOCompletingLabel()
	{
		super();
		setEditable(true);
	}

	public GLOCompletingLabel(int minchars)
	{
		super(minchars);
		setEditable(true);
	}

	public GLOCompletingLabel(int minchars, String text)
	{
		super(minchars);
		setText(text);
		setEditable(true);
	}

	public void setMapping(SortedMap mapping)
	{
		this.mapping = mapping;
	}

	public boolean typeChar(char ch)
	{
		if (!super.typeChar(ch))
			return false;
		if (mapping != null)
		{
			String text = getText();
			// lookup the 1st part of the text
			if (text.length() > 0 && cursorpos == text.length())
			{
				String lo = text;
				String hi = ((char)(text.charAt(0)+1)) + text.substring(1);
				SortedMap submap = mapping.subMap(lo, hi);
				if (submap.size() > 0)
				{
					String firstkey = submap.firstKey().toString();
					if (firstkey.length() > text.length())
					{
						String newtext = text + firstkey.substring(text.length());
						setText( newtext );
						setSelection( text.length(), newtext.length());
					}
				}
			}
		}
		return true;
	}

}
