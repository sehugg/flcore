/*
    JSDL - Java(TM) interface to SDL
    Copyright (C) 2001  Steven Hugg

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Library General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Library General Public License for more details.

    You should have received a copy of the GNU Library General Public
    License along with this library; if not, write to the Free
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Steven Hugg
    hugg@pobox.com
*/

package com.fasterlight.sdl.event;


public class SDLEvent
{
	byte type;

	public int getEventType()
	{
		return type;
	}

	public String toString()
	{
		String name = this.getClass().getName();
		name = name.substring(name.lastIndexOf('.')+1);
		return name + "(" + type + ")";
	}

	//

/* General keyboard/mouse state definitions */
	public static final int PRESSED = 0x01;
	public static final int RELEASED = 0x00;

	// keyboard stuff

/* This is the mask which refers to all hotkey bindings */
	public static final int ALL_HOTKEYS = 0xFFFFFFFF;

/* The available application states */
	public static final int APPMOUSEFOCUS = 0x01;		/* The app has mouse coverage */
	public static final int APPINPUTFOCUS = 0x02;		/* The app has input focus */
	public static final int APPACTIVE = 0x04;		/* The application is active */

/*
 * Enable/Disable keyboard repeat.  Keyboard repeat defaults to off.
 * 'delay' is the initial delay in ms between the time when a key is
 * pressed, and keyboard repeat begins.
 * 'interval' is the time in ms between keyboard repeat events.
 */
	public static final int DEFAULT_REPEAT_DELAY = 500;
	public static final int DEFAULT_REPEAT_INTERVAL = 30;

/* Event enumerations */

	public static final int NOEVENT = 0;			/* Unused (do not remove) */
   public static final int ACTIVEEVENT = 1;			/* Application loses/gains visibility */
   public static final int KEYDOWN = 2;			/* Keys pressed */
   public static final int KEYUP = 3;			/* Keys released */
   public static final int MOUSEMOTION = 4;			/* Mouse moved */
   public static final int MOUSEBUTTONDOWN = 5;		/* Mouse button pressed */
   public static final int MOUSEBUTTONUP = 6;		/* Mouse button released */
   public static final int JOYAXISMOTION = 7;		/* Joystick axis motion */
   public static final int JOYBALLMOTION = 8;		/* Joystick trackball motion */
   public static final int JOYHATMOTION = 9;		/* Joystick hat position change */
	public static final int JOYBUTTONDOWN = 10;		/* Joystick button pressed */
	public static final int JOYBUTTONUP = 11;			/* Joystick button released */
	public static final int QUIT = 12;			/* User-requested quit */
	public static final int SYSWMEVENT = 13;			/* System specific event */
	public static final int EVENT_RESERVEDA = 14;		/* Reserved for future use.. */
	public static final int EVENT_RESERVEDB = 15;		/* Reserved for future use.. */
	public static final int VIDEORESIZE = 16;			/* User resized video mode */
	public static final int EVENT_RESERVED1 = 17;		/* Reserved for future use.. */
	public static final int EVENT_RESERVED2 = 18;		/* Reserved for future use.. */
	public static final int EVENT_RESERVED3 = 19;		/* Reserved for future use.. */
	public static final int EVENT_RESERVED4 = 20;		/* Reserved for future use.. */
	public static final int EVENT_RESERVED5 = 21;		/* Reserved for future use.. */
	public static final int EVENT_RESERVED6 = 22;		/* Reserved for future use.. */
	public static final int EVENT_RESERVED7 = 23;		/* Reserved for future use.. */
       /* Events SDL_USEREVENT through SDL_MAXEVENTS-1 are for your use */
	public static final int USEREVENT = 24;
       /* This last event is only for bounding internal arrays
	  It is the number of bits in the event mask datatype -- Uint32
        */
	public static final int NUMEVENTS = 32;

/* Predefined event masks */
	public static final int ACTIVEEVENTMASK = (1<<ACTIVEEVENT);
	public static final int KEYDOWNMASK = (1<<KEYDOWN);
	public static final int KEYUPMASK = (1<<KEYUP);
	public static final int MOUSEMOTIONMASK = (1<<MOUSEMOTION);
	public static final int MOUSEBUTTONDOWNMASK = (1<<MOUSEBUTTONDOWN);
	public static final int MOUSEBUTTONUPMASK = (1<<MOUSEBUTTONUP);
	public static final int MOUSEEVENTMASK	= (1<<MOUSEMOTION)|
	                          (1<<MOUSEBUTTONDOWN)|
	                          (1<<MOUSEBUTTONUP);
	public static final int JOYAXISMOTIONMASK = (1<<JOYAXISMOTION);
	public static final int JOYBALLMOTIONMASK = (1<<JOYBALLMOTION);
	public static final int JOYHATMOTIONMASK = (1<<JOYHATMOTION);
	public static final int JOYBUTTONDOWNMASK = (1<<JOYBUTTONDOWN);
	public static final int JOYBUTTONUPMASK = (1<<JOYBUTTONUP);
	public static final int JOYEVENTMASK	= (1<<JOYAXISMOTION)|
	                          (1<<JOYBALLMOTION)|
	                          (1<<JOYHATMOTION)|
	                          (1<<JOYBUTTONDOWN)|
	                          (1<<JOYBUTTONUP);
	public static final int VIDEORESIZEMASK = (1<<VIDEORESIZE);
	public static final int QUITMASK = (1<<QUIT);
	public static final int SYSWMEVENTMASK	= (1<<SYSWMEVENT);

	public static final int ALLEVENTS = 0xFFFFFFFF;

/*
  This function allows you to set the state of processing certain events.
  If 'state' is set to SDL_IGNORE, that event will be automatically dropped
  from the event queue and will not event be filtered.
  If 'state' is set to SDL_ENABLE, that event will be processed normally.
  If 'state' is set to SDL_QUERY, SDL_EventState() will return the
  current processing state of the specified event.
*/
	public static final int QUERY = -1;
	public static final int IGNORE = 0;
	public static final int ENABLE = 1;

/* The maximum number of CD-ROM tracks on a disk */
	public static final int MAX_TRACKS = 99;

/* The types of CD-ROM track possible */
	public static final int AUDIO_TRACK = 0x00;
	public static final int DATA_TRACK = 0x04;

/* The possible states which a CD-ROM drive can be in. */
	public static final int CD_TRAYEMPTY = 0;
	public static final int CD_STOPPED = 1;
	public static final int CD_PLAYING = 2;
	public static final int CD_PAUSED = 3;
	public static final int CD_ERROR = -1;

/* As of version 0.5, SDL is loaded dynamically into the application */

/* These are the flags which may be passed to SDL_Init() -- you should
   specify the subsystems which you will be using in your application.
*/

	public static final int INIT_TIMER	= 0x00000001;
	public static final int INIT_AUDIO = 0x00000010;
	public static final int INIT_VIDEO = 0x00000020;
	public static final int INIT_CDROM = 0x00000100;
	public static final int INIT_JOYSTICK = 0x00000200;
	public static final int INIT_NOPARACHUTE = 0x00100000;	/* Don't catch fatal signals */
	public static final int INIT_EVENTTHREAD = 0x01000000;	/* Not supported on all OS's */
	public static final int INIT_EVERYTHING = 0x0000FFFF;

/* This is the OS scheduler timeslice, in milliseconds */
	public static final int TIMESLICE = 10;

/* This is the maximum resolution of the SDL timer on all platforms */
	public static final int TIMER_RESOLUTION = 10;	/* Experimentally determined */

/* Used as a mask when testing buttons in buttonstate
   Button 1:	Left mouse button
   Button 2:	Middle mouse button
   Button 3:	Right mouse button
 */
	public static final int BUTTON_LEFT = 1;
	public static final int BUTTON_MIDDLE = 2;
	public static final int BUTTON_RIGHT = 3;
	public static final int BUTTON_LMASK = PRESSED<<(BUTTON_LEFT-1);
	public static final int BUTTON_MMASK = PRESSED<<(BUTTON_MIDDLE-1);
	public static final int BUTTON_RMASK = PRESSED<<(BUTTON_RIGHT-1);

	/* The keyboard syms have been cleverly chosen to map to ASCII */
	public static final int K_UNKNOWN = 0;
	public static final int K_FIRST = 0;
	public static final int K_BACKSPACE = 8;
	public static final int K_TAB = 9;
	public static final int K_CLEAR = 12;
	public static final int K_RETURN = 13;
	public static final int K_PAUSE = 19;
	public static final int K_ESCAPE = 27;
	public static final int K_SPACE = 32;
	public static final int K_EXCLAIM = 33;
	public static final int K_QUOTEDBL = 34;
	public static final int K_HASH = 35;
	public static final int K_DOLLAR = 36;
	public static final int K_AMPERSAND = 38;
	public static final int K_QUOTE = 39;
	public static final int K_LEFTPAREN = 40;
	public static final int K_RIGHTPAREN = 41;
	public static final int K_ASTERISK = 42;
	public static final int K_PLUS = 43;
	public static final int K_COMMA = 44;
	public static final int K_MINUS = 45;
	public static final int K_PERIOD = 46;
	public static final int K_SLASH = 47;
	public static final int K_0 = 48;
	public static final int K_1 = 49;
	public static final int K_2 = 50;
	public static final int K_3 = 51;
	public static final int K_4 = 52;
	public static final int K_5 = 53;
	public static final int K_6 = 54;
	public static final int K_7 = 55;
	public static final int K_8 = 56;
	public static final int K_9 = 57;
	public static final int K_COLON = 58;
	public static final int K_SEMICOLON = 59;
	public static final int K_LESS = 60;
	public static final int K_EQUALS = 61;
	public static final int K_GREATER = 62;
	public static final int K_QUESTION = 63;
	public static final int K_AT = 64;
	/*
	   Skip uppercase letters
	 */
	public static final int K_LEFTBRACKET = 91;
	public static final int K_BACKSLASH = 92;
	public static final int K_RIGHTBRACKET = 93;
	public static final int K_CARET = 94;
	public static final int K_UNDERSCORE = 95;
	public static final int K_BACKQUOTE = 96;
	public static final int K_a = 97;
	public static final int K_b = 98;
	public static final int K_c = 99;
	public static final int K_d = 100;
	public static final int K_e = 101;
	public static final int K_f = 102;
	public static final int K_g = 103;
	public static final int K_h = 104;
	public static final int K_i = 105;
	public static final int K_j = 106;
	public static final int K_k = 107;
	public static final int K_l = 108;
	public static final int K_m = 109;
	public static final int K_n = 110;
	public static final int K_o = 111;
	public static final int K_p = 112;
	public static final int K_q = 113;
	public static final int K_r = 114;
	public static final int K_s = 115;
	public static final int K_t = 116;
	public static final int K_u = 117;
	public static final int K_v = 118;
	public static final int K_w = 119;
	public static final int K_x = 120;
	public static final int K_y = 121;
	public static final int K_z = 122;
	public static final int K_DELETE = 127;
	/* End of ASCII mapped keysyms */

	/* International keyboard syms */
	public static final int K_WORLD_0 = 160;		/* 0xA0 */
	public static final int K_WORLD_1 = 161;
	public static final int K_WORLD_2 = 162;
	public static final int K_WORLD_3 = 163;
	public static final int K_WORLD_4 = 164;
	public static final int K_WORLD_5 = 165;
	public static final int K_WORLD_6 = 166;
	public static final int K_WORLD_7 = 167;
	public static final int K_WORLD_8 = 168;
	public static final int K_WORLD_9 = 169;
	public static final int K_WORLD_10 = 170;
	public static final int K_WORLD_11 = 171;
	public static final int K_WORLD_12 = 172;
	public static final int K_WORLD_13 = 173;
	public static final int K_WORLD_14 = 174;
	public static final int K_WORLD_15 = 175;
	public static final int K_WORLD_16 = 176;
	public static final int K_WORLD_17 = 177;
	public static final int K_WORLD_18 = 178;
	public static final int K_WORLD_19 = 179;
	public static final int K_WORLD_20 = 180;
	public static final int K_WORLD_21 = 181;
	public static final int K_WORLD_22 = 182;
	public static final int K_WORLD_23 = 183;
	public static final int K_WORLD_24 = 184;
	public static final int K_WORLD_25 = 185;
	public static final int K_WORLD_26 = 186;
	public static final int K_WORLD_27 = 187;
	public static final int K_WORLD_28 = 188;
	public static final int K_WORLD_29 = 189;
	public static final int K_WORLD_30 = 190;
	public static final int K_WORLD_31 = 191;
	public static final int K_WORLD_32 = 192;
	public static final int K_WORLD_33 = 193;
	public static final int K_WORLD_34 = 194;
	public static final int K_WORLD_35 = 195;
	public static final int K_WORLD_36 = 196;
	public static final int K_WORLD_37 = 197;
	public static final int K_WORLD_38 = 198;
	public static final int K_WORLD_39 = 199;
	public static final int K_WORLD_40 = 200;
	public static final int K_WORLD_41 = 201;
	public static final int K_WORLD_42 = 202;
	public static final int K_WORLD_43 = 203;
	public static final int K_WORLD_44 = 204;
	public static final int K_WORLD_45 = 205;
	public static final int K_WORLD_46 = 206;
	public static final int K_WORLD_47 = 207;
	public static final int K_WORLD_48 = 208;
	public static final int K_WORLD_49 = 209;
	public static final int K_WORLD_50 = 210;
	public static final int K_WORLD_51 = 211;
	public static final int K_WORLD_52 = 212;
	public static final int K_WORLD_53 = 213;
	public static final int K_WORLD_54 = 214;
	public static final int K_WORLD_55 = 215;
	public static final int K_WORLD_56 = 216;
	public static final int K_WORLD_57 = 217;
	public static final int K_WORLD_58 = 218;
	public static final int K_WORLD_59 = 219;
	public static final int K_WORLD_60 = 220;
	public static final int K_WORLD_61 = 221;
	public static final int K_WORLD_62 = 222;
	public static final int K_WORLD_63 = 223;
	public static final int K_WORLD_64 = 224;
	public static final int K_WORLD_65 = 225;
	public static final int K_WORLD_66 = 226;
	public static final int K_WORLD_67 = 227;
	public static final int K_WORLD_68 = 228;
	public static final int K_WORLD_69 = 229;
	public static final int K_WORLD_70 = 230;
	public static final int K_WORLD_71 = 231;
	public static final int K_WORLD_72 = 232;
	public static final int K_WORLD_73 = 233;
	public static final int K_WORLD_74 = 234;
	public static final int K_WORLD_75 = 235;
	public static final int K_WORLD_76 = 236;
	public static final int K_WORLD_77 = 237;
	public static final int K_WORLD_78 = 238;
	public static final int K_WORLD_79 = 239;
	public static final int K_WORLD_80 = 240;
	public static final int K_WORLD_81 = 241;
	public static final int K_WORLD_82 = 242;
	public static final int K_WORLD_83 = 243;
	public static final int K_WORLD_84 = 244;
	public static final int K_WORLD_85 = 245;
	public static final int K_WORLD_86 = 246;
	public static final int K_WORLD_87 = 247;
	public static final int K_WORLD_88 = 248;
	public static final int K_WORLD_89 = 249;
	public static final int K_WORLD_90 = 250;
	public static final int K_WORLD_91 = 251;
	public static final int K_WORLD_92 = 252;
	public static final int K_WORLD_93 = 253;
	public static final int K_WORLD_94 = 254;
	public static final int K_WORLD_95 = 255;		/* 0xFF */

	/* Numeric keypad */
	public static final int K_KP0 = 256;
	public static final int K_KP1 = 257;
	public static final int K_KP2 = 258;
	public static final int K_KP3 = 259;
	public static final int K_KP4 = 260;
	public static final int K_KP5 = 261;
	public static final int K_KP6 = 262;
	public static final int K_KP7 = 263;
	public static final int K_KP8 = 264;
	public static final int K_KP9 = 265;
	public static final int K_KP_PERIOD = 266;
	public static final int K_KP_DIVIDE = 267;
	public static final int K_KP_MULTIPLY = 268;
	public static final int K_KP_MINUS = 269;
	public static final int K_KP_PLUS = 270;
	public static final int K_KP_ENTER = 271;
	public static final int K_KP_EQUALS = 272;

	/* Arrows + Home/End pad */
	public static final int K_UP = 273;
	public static final int K_DOWN = 274;
	public static final int K_RIGHT = 275;
	public static final int K_LEFT = 276;
	public static final int K_INSERT = 277;
	public static final int K_HOME = 278;
	public static final int K_END = 279;
	public static final int K_PAGEUP = 280;
	public static final int K_PAGEDOWN = 281;

	/* Function keys */
	public static final int K_F1 = 282;
	public static final int K_F2 = 283;
	public static final int K_F3 = 284;
	public static final int K_F4 = 285;
	public static final int K_F5 = 286;
	public static final int K_F6 = 287;
	public static final int K_F7 = 288;
	public static final int K_F8 = 289;
	public static final int K_F9 = 290;
	public static final int K_F10 = 291;
	public static final int K_F11 = 292;
	public static final int K_F12 = 293;
	public static final int K_F13 = 294;
	public static final int K_F14 = 295;
	public static final int K_F15 = 296;

	/* Key state modifier keys */
	public static final int K_NUMLOCK = 300;
	public static final int K_CAPSLOCK = 301;
	public static final int K_SCROLLOCK = 302;
	public static final int K_RSHIFT = 303;
	public static final int K_LSHIFT = 304;
	public static final int K_RCTRL = 305;
	public static final int K_LCTRL = 306;
	public static final int K_RALT = 307;
	public static final int K_LALT = 308;
	public static final int K_RMETA = 309;
	public static final int K_LMETA = 310;
	public static final int K_LSUPER = 311;		/* Left "Windows" key */
	public static final int K_RSUPER = 312;		/* Right "Windows" key */
	public static final int K_MODE = 313;		/* "Alt Gr" key */
	public static final int K_COMPOSE = 314;		/* Multi-key compose key */

	/* Miscellaneous function keys */
	public static final int K_HELP = 315;
	public static final int K_PRINT = 316;
	public static final int K_SYSREQ = 317;
	public static final int K_BREAK = 318;
	public static final int K_MENU = 319;
	public static final int K_POWER = 320;		/* Power Macintosh power key */
	public static final int K_EURO = 321;		/* Some european keyboards */

	/* Add any other keys here */

	 public static final int K_LAST = 322;

/* Enumeration of valid key mods (possibly OR'd together) */
	public static final int KMOD_NONE = 0x0000;
	public static final int KMOD_LSHIFT = 0x0001;
	public static final int KMOD_RSHIFT = 0x0002;
	public static final int KMOD_LCTRL = 0x0040;
	public static final int KMOD_RCTRL = 0x0080;
	public static final int KMOD_LALT = 0x0100;
	public static final int KMOD_RALT = 0x0200;
	public static final int KMOD_LMETA = 0x0400;
	public static final int KMOD_RMETA = 0x0800;
	public static final int KMOD_NUM = 0x1000;
	public static final int KMOD_CAPS = 0x2000;
	public static final int KMOD_MODE = 0x4000;
	public static final int KMOD_RESERVED = 0x8000;

	public static final int KMOD_CTRL = (KMOD_LCTRL|KMOD_RCTRL);
	public static final int KMOD_SHIFT = (KMOD_LSHIFT|KMOD_RSHIFT);
	public static final int KMOD_ALT = (KMOD_LALT|KMOD_RALT);
	public static final int KMOD_META = (KMOD_LMETA|KMOD_RMETA);


}
