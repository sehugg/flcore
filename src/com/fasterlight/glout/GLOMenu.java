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

import java.io.IOException;
import java.util.*;

import nanoxml.XMLElement;

import com.fasterlight.io.IOUtil;

/**
  * Data definition of a menu.
  */
public class GLOMenu
{
	List items = new ArrayList();

	//

	public GLOMenu()
	{
	}

	public GLOMenu(GLOMenuItem[] items)
	{
		setItems(items);
	}

	public GLOMenu(String path)
	{
		try
		{
			load(path);
		} catch (IOException ioe)
		{
			throw new RuntimeException(ioe.toString());
		}
	}

	public void load(String path) throws IOException
	{
		String xml = IOUtil.readString(IOUtil.getTextResource(path));
		XMLElement elem = new XMLElement();
		elem.parseString(xml, 0);
		if (!elem.getName().equalsIgnoreCase("MENU"))
			throw new RuntimeException("Top tag must be 'menu'");

		parseElement(elem);
	}

	private void parseElement(XMLElement elem)
	{
		Enumeration e = elem.enumerateChildren();
		while (e.hasMoreElements())
		{
			XMLElement elem2 = (XMLElement) e.nextElement();
			if (elem2.getName().equalsIgnoreCase("ITEM"))
			{
				GLOMenuItem item = new GLOMenuItem();
				item.setText(elem2.getProperty("NAME"));
				item.setAction(elem2.getProperty("ACTION"));
				items.add(item);

				if (elem2.countChildren() > 0)
				{
					GLOMenu submenu = new GLOMenu();
					submenu.parseElement(elem2);
					item.setSubMenu(submenu);
				}
			} else if (elem2.getName().equalsIgnoreCase("SEPARATOR"))
			{
				GLOMenuItem item = new GLOMenuItem();
				item.setText("---"); //todo
				items.add(item);
			} else
				throw new RuntimeException(
					"Tag type '" + elem2.getName() + "' not recognized");
		}
	}

	//

	public void setItems(GLOMenuItem[] arr)
	{
		items = new ArrayList();
		for (int i = 0; i < arr.length; i++)
			items.add(arr[i]);
	}

	public List getItems()
	{
		return items;
	}

	//

	public static void main(String[] args) throws Exception
	{
		GLOMenu menu = new GLOMenu();
		GLOMenuTable mt = new GLOMenuTable();
		mt.loadMenu("panels/main.mnu");
	}
}
