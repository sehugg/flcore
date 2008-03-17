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


/**
  */
public class GLOKeyEvent
extends GLOEvent
{
	int flags, keycode;
	boolean pressed;
	char keychar;

public static final int MOD_SHIFT = 1;
public static final int MOD_CTRL  = 2;
public static final int MOD_ALT   = 8;
public static final int MOD_ALL   = MOD_SHIFT|MOD_CTRL|MOD_ALT;
//todo

public static final int VK_0 = '0';
public static final int VK_1 = '1';
public static final int VK_2 = '2';
public static final int VK_3 = '3';
public static final int VK_4 = '4';
public static final int VK_5 = '5';
public static final int VK_6 = '6';
public static final int VK_7 = '7';
public static final int VK_8 = '8';
public static final int VK_9 = '9';

public static final int VK_A = 'A';
public static final int VK_B = 'B';
public static final int VK_C = 'C';
public static final int VK_D = 'D';
public static final int VK_E = 'E';
public static final int VK_F = 'F';
public static final int VK_G = 'G';
public static final int VK_H = 'H';
public static final int VK_I = 'I';
public static final int VK_J = 'J';
public static final int VK_K = 'K';
public static final int VK_L = 'L';
public static final int VK_M = 'M';
public static final int VK_N = 'N';
public static final int VK_O = 'O';
public static final int VK_P = 'P';
public static final int VK_Q = 'Q';
public static final int VK_R = 'R';
public static final int VK_S = 'S';
public static final int VK_T = 'T';
public static final int VK_U = 'U';
public static final int VK_V = 'V';
public static final int VK_W = 'W';
public static final int VK_X = 'X';
public static final int VK_Y = 'Y';
public static final int VK_Z = 'Z';

public static final int VK_AMPERSAND = '&';
public static final int VK_ASTERISK = '*';
public static final int VK_AT = '@';
public static final int VK_BACK_QUOTE = '`';
public static final int VK_BACK_SLASH = '\\';
public static final int VK_BACK_SPACE = '\b';
public static final int VK_BRACELEFT = '{';
public static final int VK_BRACERIGHT = '}';
public static final int VK_CIRCUMFLEX = '^';
public static final int VK_OPEN_BRACKET = '[';
public static final int VK_CLOSE_BRACKET = ']';
public static final int VK_COLON = ':';
public static final int VK_COMMA = ',';
public static final int VK_DOLLAR = '$';
public static final int VK_ENTER = '\n';
public static final int VK_EQUALS = '=';
public static final int VK_EXCLAMATION_POINT = '!';
public static final int VK_GREATER = '>';
public static final int VK_LESS = '<';
public static final int VK_LEFT_PARENTHESIS = '(';
public static final int VK_RIGHT_PARENTHESIS = ')';
public static final int VK_MINUS = '-';
public static final int VK_NUMBER_SIGN = '#';
public static final int VK_PERIOD = '.';
public static final int VK_PLUS = '+';
public static final int VK_QUOTE = '"';
public static final int VK_SEMICOLON = ';';
public static final int VK_SLASH = '/';
public static final int VK_SPACE = ' ';
public static final int VK_TAB = '\t';
public static final int VK_UNDERSCORE = '_';

public static final int VK_F1 = 0x70;
public static final int VK_F2 = 0x71;
public static final int VK_F3 = 0x72;
public static final int VK_F4 = 0x73;
public static final int VK_F5 = 0x74;
public static final int VK_F6 = 0x75;
public static final int VK_F7 = 0x76;
public static final int VK_F8 = 0x77;
public static final int VK_F9 = 0x78;
public static final int VK_F10 = 0x79;
public static final int VK_F11 = 0x7A;
public static final int VK_F12 = 0x7B;

public static final int VK_NUMPAD0 = 0x60;
public static final int VK_NUMPAD1 = 0x61;
public static final int VK_NUMPAD2 = 0x62;
public static final int VK_NUMPAD3 = 0x63;
public static final int VK_NUMPAD4 = 0x64;
public static final int VK_NUMPAD5 = 0x65;
public static final int VK_NUMPAD6 = 0x66;
public static final int VK_NUMPAD7 = 0x67;
public static final int VK_NUMPAD8 = 0x68;
public static final int VK_NUMPAD9 = 0x69;

public static final int VK_ADD = 0x6B;
public static final int VK_SUBTRACT = 0x6D;
public static final int VK_MULTIPLY = 0x6A;
public static final int VK_DIVIDE = 0x6F;
public static final int VK_DECIMAL = 0x6E;
public static final int VK_SEPARATER = 0x6C;

public static final int VK_LEFT = 0x25;
public static final int VK_RIGHT = 0x27;
public static final int VK_UP = 0x26;
public static final int VK_DOWN = 0x28;

public static final int VK_HOME = 0x24;
public static final int VK_END = 0x23;
public static final int VK_PAGE_UP = 0x21;
public static final int VK_PAGE_DOWN = 0x22;

public static final int VK_ALT = 0x12;
public static final int VK_CANCEL = 0x03;
public static final int VK_CAPS_LOCK = 0x14;
public static final int VK_CLEAR = 0x0C;
public static final int VK_CONTROL = 0x11;
public static final int VK_DELETE = 0x7F;
public static final int VK_ESCAPE = 0x1B;
public static final int VK_HELP = 0x9C;
public static final int VK_INSERT = 0x9B;
public static final int VK_META = 0x9D;
public static final int VK_NUM_LOCK = 0x90;
public static final int VK_PAUSE = 0x13;
public static final int VK_PRINTSCREEN = 0x9A;
public static final int VK_SCROLL_LOCK = 0x91;
public static final int VK_SHIFT = 0x10;

public static final int VK_CONVERT = 0x1C;
public static final int VK_NONCONVERT = 0x1D;
public static final int VK_ACCEPT = 0x1E;
public static final int VK_FINAL = 0x18;
public static final int VK_MODECHANGE = 0x1F;

public static final int VK_KANA = 0x15;
public static final int VK_KANJI = 0x19;

public static final int VK_UNDEFINED = 0;
public static final char CHAR_UNDEFINED = 0;


	public GLOKeyEvent(GLOContext ctx, int flags, int keycode, char keychar,
		boolean pressed)
	{
		super(ctx);
		this.flags = flags & MOD_ALL;
		this.keycode = keycode;
		this.pressed = pressed;
		this.keychar = keychar;
	}

	public int getFlags()
	{
		return flags;
	}

	public int getKeyCode()
	{
		return keycode;
	}

	/**
	  * @deprecated
	  */
	public boolean getPressed()
	{
		return pressed;
	}

	public boolean isPressed()
	{
		return pressed;
	}

	public char getChar()
	{
		return keychar;
	}

	public String toString()
	{
		return getClass().getName() + ":key=" + keycode + ",flags=" + flags + "," + pressed;
	}
}
