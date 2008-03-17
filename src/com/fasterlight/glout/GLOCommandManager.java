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

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;

import com.fasterlight.spif.*;
import com.fasterlight.util.CachedINIFile;

/**
  * A class used by the menu classes and also to implement
  * keyboard shortcuts.
  */
public class GLOCommandManager
{
	public static final int SHIFT = 0x10000;
	public static final int CTRL = 0x20000;
	public static final int ALT = 0x80000;
	public static final int KEY_UP = 0x1000000;
	public static final int MODS_MASK = 0xffff0000;

	PropertyAware top;
	Map commands = new TreeMap();
	Map bindings = new HashMap();
	Map revbindings = new HashMap();

	//

	class Command
	{
		String[] propkeys;
		Object[] propvalues;

		public Command(int npairs)
		{
			propkeys = new String[npairs];
			propvalues = new Object[npairs];
		}

		public void setPair(int n, String key, String value)
		{
			propkeys[n] = key;
			propvalues[n] = PropertyUtil.parseValue(value);
		}

		public void execute()
		{
			for (int i = 0; i < propkeys.length; i++)
			{
				if ("<toggle>".equals(propvalues[i]))
				{
					boolean b =
						PropertyUtil.toBoolean(
							PropertyEvaluator.get(top, propkeys[i]));
					PropertyEvaluator.set(
						top,
						propkeys[i],
						b ? Boolean.FALSE : Boolean.TRUE);
				} else
				{
					PropertyEvaluator.set(top, propkeys[i], propvalues[i]);
				}
			}
		}
	}

	//

	public GLOCommandManager(PropertyAware top)
	{
		this.top = top;
	}

	public void loadCommands(String path) throws IOException
	{
		InputStream in = ClassLoader.getSystemResourceAsStream(path);
		if (in == null)
			throw new IOException(
				"Could not load control bindings file " + path);
		CachedINIFile ini = new CachedINIFile(in);

		List snames = ini.getSectionNames();
		Iterator si = snames.iterator();
		while (si.hasNext())
		{
			String cmdname = (String) si.next();
			Properties props = ini.getSection(cmdname);

			int l = props.size();
			Command cmd = new Command(l);

			Enumeration e = props.propertyNames();
			int i = 0;
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				String value = props.getProperty(key);
				if (i < l)
				{
					cmd.setPair(i, key, value);
					i++;
				} else
				{
					System.out.println(
						"ERROR in command "
							+ cmdname
							+ ", "
							+ key
							+ "="
							+ value);
				}
			}

			commands.put(cmdname, cmd);
		}
		in.close();
	}

	public void loadControlBindings(String path) throws IOException
	{
		InputStream in = ClassLoader.getSystemResourceAsStream(path);
		if (in == null)
			throw new IOException(
				"Could not load control bindings file " + path);
		CachedINIFile ini = new CachedINIFile(in);
		Properties props = ini.getSection("Keys");
		if (props == null)
			throw new IOException("No section 'Keys'");

		Enumeration e = props.propertyNames();
		while (e.hasMoreElements())
		{
			String key = (String) e.nextElement();
			String cmd = props.getProperty(key).trim();
			Integer intkey = new Integer(decodeKey(key));
			bindings.put(intkey, cmd);
			revbindings.put(cmd, intkey);
		}
	}

	public int decodeKey(String s)
	{
		s = s.toUpperCase().trim();

		int n = 0;
		do
		{
			if (s.startsWith("UP+"))
			{
				n |= KEY_UP;
				s = s.substring(3);
			} else if (s.startsWith("SHIFT+"))
			{
				n |= SHIFT;
				s = s.substring(6);
			} else if (s.startsWith("CTRL+"))
			{
				n |= CTRL;
				s = s.substring(5);
			} else if (s.startsWith("^"))
			{
				n |= CTRL;
				s = s.substring(1);
			} else if (s.startsWith("ALT+"))
			{
				n |= ALT;
				s = s.substring(4);
			} else if (s.startsWith("MENU+"))
			{
				n |= getPlatformKeyMask();
				s = s.substring(5);
			} else
				break;
		} while (true);

		try
		{
			if (s.startsWith("0X"))
				n |= Integer.parseInt(s.substring(2), 16);
			else if (s.startsWith("\\0"))
				n |= Integer.parseInt(s.substring(2), 8);
			else
				n |= text2code(s.trim());
			return n;
		} catch (Exception e)
		{
			return -1;
		}
	}

	public static int getPlatformKeyMask()
	{
		return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() << 16;
	}

	public Command getCommand(String cmdname)
	{
		return (Command) commands.get(cmdname);
	}

	public Set getCommandNames()
	{
		return commands.keySet();
	}

	public Set getBindingKeys()
	{
		return bindings.keySet();
	}

	public String getCommandForControl(int keycode)
	{
		return (String) bindings.get(new Integer(keycode));
	}

	public int getKeyCodeForCommand(String cmd)
	{
		Integer i = (Integer) revbindings.get(cmd);
		return (i != null) ? i.intValue() : -1;
	}

	public boolean execute(String cmdname)
	{
		Command cmd = getCommand(cmdname);
		if (cmd == null)
			return false;

		cmd.execute();
		return true;
	}

	public boolean executeControl(int keycode)
	{
		String cmd = getCommandForControl(keycode);
		if (cmd == null)
			return false;
		else
			return execute(cmd);
	}

	public boolean executeControl(GLOKeyEvent ke)
	{
		int code = ke.getKeyCode();
		int flags = ke.getFlags();
		if ((flags & ke.MOD_SHIFT) != 0)
			code |= SHIFT;
		if ((flags & ke.MOD_CTRL) != 0)
			code |= CTRL;
		if ((flags & ke.MOD_ALT) != 0)
			code |= ALT;
		if (!ke.isPressed())
			code |= KEY_UP;
		return executeControl(code);
	}

	public static String keyCodeToString(int keycode)
	{
		if (keycode < 0)
			return "n/a";
		StringBuffer st = new StringBuffer();
		if ((keycode & KEY_UP) != 0)
			st.append("Up+");
		if ((keycode & SHIFT) != 0)
			st.append("Shift+");
		if ((keycode & CTRL) != 0)
			st.append("Ctrl+");
		if ((keycode & ALT) != 0)
			st.append("Alt+");
		st.append(KeyEvent.getKeyText(keycode & ~MODS_MASK));
		//todo: not complete
		return st.toString();
	}

	//

	private static Map text2codes = new HashMap();

	static {
		addCodeTrans("ENTER", GLOKeyEvent.VK_ENTER);
		addCodeTrans("BACK_SPACE", GLOKeyEvent.VK_BACK_SPACE);
		addCodeTrans("TAB", GLOKeyEvent.VK_TAB);
		addCodeTrans("CANCEL", GLOKeyEvent.VK_CANCEL);
		addCodeTrans("CLEAR", GLOKeyEvent.VK_CLEAR);
		addCodeTrans("SHIFT", GLOKeyEvent.VK_SHIFT);
		addCodeTrans("CONTROL", GLOKeyEvent.VK_CONTROL);
		addCodeTrans("ALT", GLOKeyEvent.VK_ALT);
		addCodeTrans("PAUSE", GLOKeyEvent.VK_PAUSE);
		addCodeTrans("CAPS_LOCK", GLOKeyEvent.VK_CAPS_LOCK);
		addCodeTrans("ESCAPE", GLOKeyEvent.VK_ESCAPE);
		addCodeTrans("SPACE", GLOKeyEvent.VK_SPACE);
		addCodeTrans("PAGE_UP", GLOKeyEvent.VK_PAGE_UP);
		addCodeTrans("PAGE_DOWN", GLOKeyEvent.VK_PAGE_DOWN);
		addCodeTrans("END", GLOKeyEvent.VK_END);
		addCodeTrans("HOME", GLOKeyEvent.VK_HOME);
		addCodeTrans("LEFT", GLOKeyEvent.VK_LEFT);
		addCodeTrans("UP", GLOKeyEvent.VK_UP);
		addCodeTrans("RIGHT", GLOKeyEvent.VK_RIGHT);
		addCodeTrans("DOWN", GLOKeyEvent.VK_DOWN);
		addCodeTrans("COMMA", GLOKeyEvent.VK_COMMA);
		addCodeTrans("MINUS", GLOKeyEvent.VK_MINUS);
		addCodeTrans("PERIOD", GLOKeyEvent.VK_PERIOD);
		addCodeTrans("SLASH", GLOKeyEvent.VK_SLASH);
		addCodeTrans("SEMICOLON", GLOKeyEvent.VK_SEMICOLON);
		addCodeTrans("EQUALS", GLOKeyEvent.VK_EQUALS);
		addCodeTrans("OPEN_BRACKET", GLOKeyEvent.VK_OPEN_BRACKET);
		addCodeTrans("BACK_SLASH", GLOKeyEvent.VK_BACK_SLASH);
		addCodeTrans("CLOSE_BRACKET", GLOKeyEvent.VK_CLOSE_BRACKET);
		addCodeTrans("NUMPAD0", GLOKeyEvent.VK_NUMPAD0);
		addCodeTrans("NUMPAD1", GLOKeyEvent.VK_NUMPAD1);
		addCodeTrans("NUMPAD2", GLOKeyEvent.VK_NUMPAD2);
		addCodeTrans("NUMPAD3", GLOKeyEvent.VK_NUMPAD3);
		addCodeTrans("NUMPAD4", GLOKeyEvent.VK_NUMPAD4);
		addCodeTrans("NUMPAD5", GLOKeyEvent.VK_NUMPAD5);
		addCodeTrans("NUMPAD6", GLOKeyEvent.VK_NUMPAD6);
		addCodeTrans("NUMPAD7", GLOKeyEvent.VK_NUMPAD7);
		addCodeTrans("NUMPAD8", GLOKeyEvent.VK_NUMPAD8);
		addCodeTrans("NUMPAD9", GLOKeyEvent.VK_NUMPAD9);
		addCodeTrans("MULTIPLY", GLOKeyEvent.VK_MULTIPLY);
		addCodeTrans("ADD", GLOKeyEvent.VK_ADD);
		addCodeTrans("SEPARATER", GLOKeyEvent.VK_SEPARATER);
		addCodeTrans("SUBTRACT", GLOKeyEvent.VK_SUBTRACT);
		addCodeTrans("DECIMAL", GLOKeyEvent.VK_DECIMAL);
		addCodeTrans("DIVIDE", GLOKeyEvent.VK_DIVIDE);
		addCodeTrans("DELETE", GLOKeyEvent.VK_DELETE);
		addCodeTrans("NUM_LOCK", GLOKeyEvent.VK_NUM_LOCK);
		addCodeTrans("SCROLL_LOCK", GLOKeyEvent.VK_SCROLL_LOCK);
		addCodeTrans("F1", GLOKeyEvent.VK_F1);
		addCodeTrans("F2", GLOKeyEvent.VK_F2);
		addCodeTrans("F3", GLOKeyEvent.VK_F3);
		addCodeTrans("F4", GLOKeyEvent.VK_F4);
		addCodeTrans("F5", GLOKeyEvent.VK_F5);
		addCodeTrans("F6", GLOKeyEvent.VK_F6);
		addCodeTrans("F7", GLOKeyEvent.VK_F7);
		addCodeTrans("F8", GLOKeyEvent.VK_F8);
		addCodeTrans("F9", GLOKeyEvent.VK_F9);
		addCodeTrans("F10", GLOKeyEvent.VK_F10);
		addCodeTrans("F11", GLOKeyEvent.VK_F11);
		addCodeTrans("F12", GLOKeyEvent.VK_F12);
		addCodeTrans("PRINTSCREEN", GLOKeyEvent.VK_PRINTSCREEN);
		addCodeTrans("INSERT", GLOKeyEvent.VK_INSERT);
		addCodeTrans("HELP", GLOKeyEvent.VK_HELP);
		addCodeTrans("META", GLOKeyEvent.VK_META);
		addCodeTrans("BACK_QUOTE", GLOKeyEvent.VK_BACK_QUOTE);
		addCodeTrans("QUOTE", GLOKeyEvent.VK_QUOTE);
		addCodeTrans("AMPERSAND", GLOKeyEvent.VK_AMPERSAND);
		addCodeTrans("ASTERISK", GLOKeyEvent.VK_ASTERISK);
		addCodeTrans("LESS", GLOKeyEvent.VK_LESS);
		addCodeTrans("GREATER", GLOKeyEvent.VK_GREATER);
		addCodeTrans("BRACELEFT", GLOKeyEvent.VK_BRACELEFT);
		addCodeTrans("BRACERIGHT", GLOKeyEvent.VK_BRACERIGHT);
		addCodeTrans("AT", GLOKeyEvent.VK_AT);
		addCodeTrans("COLON", GLOKeyEvent.VK_COLON);
		addCodeTrans("CIRCUMFLEX", GLOKeyEvent.VK_CIRCUMFLEX);
		addCodeTrans("DOLLAR", GLOKeyEvent.VK_DOLLAR);
		addCodeTrans("EXCLAMATION_MARK", GLOKeyEvent.VK_EXCLAMATION_POINT);
		addCodeTrans("LEFT_PARENTHESIS", GLOKeyEvent.VK_LEFT_PARENTHESIS);
		addCodeTrans("NUMBER_SIGN", GLOKeyEvent.VK_NUMBER_SIGN);
		addCodeTrans("PLUS", GLOKeyEvent.VK_PLUS);
		addCodeTrans("RIGHT_PARENTHESIS", GLOKeyEvent.VK_RIGHT_PARENTHESIS);
		addCodeTrans("UNDERSCORE", GLOKeyEvent.VK_UNDERSCORE);
	}

	private static void addCodeTrans(String s, int i)
	{
		text2codes.put(s, new Integer(i));
	}

	/**
	  * Converts a key name into its key code representation.
	  */
	public static int text2code(String keyname)
	{
		Object o = text2codes.get(keyname);
		if (o != null)
			return ((Integer) o).intValue();
		else
			return (int) keyname.charAt(0);
	}
}
