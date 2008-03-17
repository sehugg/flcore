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
import java.util.*;

import com.fasterlight.spif.PropertyUtil;
import com.fasterlight.util.*;

/**
  * Handles loading GLO components from an INI file.
  */
public class GLOLoader
{
	GLOComponent top;

	public GLOLoader()
	{
	}

	public GLOLoader(GLOComponent top)
	{
		setTopComponent(top);
	}

	public GLOComponent getTopComponent()
	{
		return top;
	}

	public void setTopComponent(GLOComponent top)
	{
		this.top = top;
	}

	private static Map types = new HashMap();

	// todo: not static?
	static {
		registerType("component", GLOComponent.class);
		registerType("frame", GLOFramedComponent.class);
		registerType("container", GLOContainer.class);
		registerType("meter", GLOMeter.class);
		registerType("label", GLOLabel.class);
		registerType("editbox", GLOEditBox.class);
		registerType("combobox", GLOComboBox.class);
		registerType("window", GLOWindow.class);
		registerType("table", GLOTableContainer.class);
		registerType("stringlist", GLOStringList.class);
		registerType("scrollbox", GLOScrollBox.class);
		registerType("scrollbar", GLOScrollBar.class);
		registerType("wraptext", GLOWrapText.class);
		registerType("slideshow", GLOSlideShow.class);
		registerType("button", GLOButton.class);
		registerType("pagestack", GLOPageStack.class);
		registerType("bitmap", GLOBitmap.class);
		registerType("bitmapbtn", GLOBitmapButton.class);
		registerType("selectbox", GLOSelectionBox.class);
		registerType("switch", GLOSwitch.class);
		registerType("boolswitch", GLOBooleanSwitch.class);
		registerType("boollight", GLOBooleanLight.class);
		registerType("menutable", GLOMenuTable.class);
		registerType("menulabel", GLOMenuLabel.class);
		registerType("menupopup", GLOMenuPopup.class);
		registerType("radialmeter", GLORadialMeter.class);
		registerType("warnbutton", GLOWarningButton.class);
		registerType("console", GLOConsole.class);
		registerType("hintlabel", GLOHintLabel.class);
		registerType("singletoncontainer", GLOSingletonContainer.class);
		registerType("vanishingpane", GLOVanishingPane.class);
		registerType("checkbox", GLOCheckBox.class);
		registerType("empty", GLOEmpty.class);
		registerType("slider", GLOSlider.class);
	}

	public static void registerType(String typename, Class clazz)
	{
		types.put(typename, clazz);
	}

	public Object makeObject(String type)
	{
		Class clazz = (Class)types.get(type);
		if (clazz == null)
		{
			try {
				clazz = Class.forName(type);
			} catch (Exception exc) {
			}
		}
		if (clazz == null)
			throw new RuntimeException("Not recognized: type `" + type + "'");
		Object o;
		try {
			o = clazz.newInstance();
		} catch (Exception exc2) {
			exc2.printStackTrace();
			throw new RuntimeException("Couldnt make obj type " + type + ": " + exc2.toString());
		}
		return o;
	}

	public GLOComponent load(String filename)
	throws IOException
	{
		InputStream in = ClassLoader.getSystemResourceAsStream(filename);
		if (in != null)
		{
			CachedINIFile ini = new CachedINIFile(in);
			GLOComponent cmpt = load(ini);
			return cmpt;
		} else {
			throw new IOException("Could not find " + filename);
		}
	}

// todo: check errors
	public GLOComponent load(INIFile ini)
	throws IOException
	{
		List v = ini.getSectionNames();
		Iterator it = v.iterator();
		List layoutlist = new ArrayList();
		GLOComponent topcmpt = null;
		Map loaded = new HashMap();

		// iterate thru all sections
		while (it.hasNext())
		{
			String name = (String)it.next();
			Properties props = ini.getSection(name);

			GLOComponent cmpt;

			// now set parent
			String parname = props.getProperty("parent");
			GLOComponent parent;
			if (topcmpt != null && parname != null) {
				parent = (GLOComponent)loaded.get(parname);
//				parent = topcmpt.getComponentByName(parname);
				if (parent == null)
					System.out.println(name + ": Can't find parent '" + parname + "'");
			} else {
				parent = top;
				// todo: having more than one cmpt with 'top' parent in a file does not work
			}

			// find out if we should recurse into another .ini file
			String linkfile = props.getProperty("load");
			if (linkfile != null)
			{
				GLOComponent oldtop = getTopComponent();
				setTopComponent(parent);
				if (debug) {
					System.out.println("setting top to " + parent + ", loading " + linkfile);
				}
				cmpt = this.load(linkfile);
				setTopComponent(oldtop);
			} else {
				// find out type of component
				String type = props.getProperty("type");
				if (type == null)
					throw new RuntimeException("No type field for `" + name + "'");

				// create object
				cmpt = (GLOComponent)makeObject(type);
			}
			cmpt.setName(name);

			if (linkfile == null)
			{
				if (parent instanceof GLOScrollBox) {
					((GLOScrollBox)parent).getBox().add(cmpt);
				}
				else if (parent instanceof GLOContainer) {
					((GLOContainer)parent).add(cmpt);
				}
				else if (parent != null)
					System.out.println("Warning: Can't add '" + name + "' to parent '" + parname + "'");
			}

			// load from ini file properties
			try {
				cmpt.load(props);
			} catch (Exception exc) {
				exc.printStackTrace(); //todo?
			}

			// add to hash table
 			loaded.put(name, cmpt);

			// add to layout list
			if (PropertyUtil.toBoolean(props.getProperty("layout")))
				layoutlist.add(cmpt);

			if (debug)
			{
				System.out.println(name + " " + cmpt + " " + cmpt.getParent());
				System.out.println(cmpt.getOrigin() + " " + cmpt.getSize());
			}

			if (topcmpt == null)
				topcmpt = cmpt;
		}

		// now layout cmpts
		it = layoutlist.iterator();
		while (it.hasNext())
		{
			GLOComponent cmpt = (GLOComponent)it.next();
			cmpt.layout();
		}

		return topcmpt;
	}

	boolean debug = false;
}
