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

import java.io.IOException;
import java.util.*;

import com.fasterlight.spif.*;
import com.fasterlight.util.INIFile;

/**
  * This class can render a variety of sounds based on a
  * sound properties file.  The sounds are dependent upon
  * named properties of a base object.
  *
  * TERMS:
  *
  * An "attr" is a floating point value linked to a property,
  * modified by a scale, bias, and exponent.
  *
  * A "def" is a definition of a single sound, modified by
  * volume, pitch, pan, and possibly other attrs.
  *
  * A "group" is a grouping of defs.
  *
  */
public class PropertySoundRenderer
{
	INIFile ini;
	SoundServer sserver;
	PropertyAware top;
	HashMap groupmap = new HashMap();
	List defs = new ArrayList();

	public void setINI(INIFile ini)
	{
		this.ini = ini;
	}

	public List loadGroup(String name)
	throws IOException
	{
		List list = new ArrayList();
		Properties props = ini.getSection(name);
		Enumeration e = props.propertyNames();
		while (e.hasMoreElements())
		{
			String key = (String)e.nextElement();
			String value = props.getProperty(key);
			SoundDef def;
			try {
				def = new SoundDef(value);
   			if (debug)
   				System.out.println("Loaded def " + def);
   			list.add(def);
   			defs.add(def);
			} catch (Exception ex) {
				System.out.println("Error loading sound [" + name + "] " + key + ": " + ex);
			}
		}
		groupmap.put(name, list);
		return list;
	}

	public List getGroup(String name)
	{
		try {
			Object group = groupmap.get(name);
			if (group == Boolean.FALSE)
				return null;
			if (group == null)
				group = loadGroup(name);
			return (List)group;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

	public void setPropertyTop(PropertyAware obj)
	{
		this.top = obj;
	}

	public void setSoundServer(SoundServer sserver)
	{
		this.sserver = sserver;
	}

	public void open()
	{
	}

	public void close()
	{
		Iterator it = defs.iterator();
		while (it.hasNext())
		{
			Object o = it.next();
			if (o instanceof SoundDef)
			{
				SoundDef def = (SoundDef)o;
				def.close();
			}
		}
	}

	public void updateGroup(String defname)
	{
		List group = getGroup(defname);
		if (group != null)
		{
			Iterator it = group.iterator();
			while (it.hasNext())
			{
				SoundDef def = (SoundDef)it.next();
				def.update();
			}
		}
	}

	//

	class SoundAttr
	{
		String propkey;
		float bias=0, scale=1, power=1;

		public String toString()
		{
			return '\'' + propkey + "'^" + power + '*' + scale + '+' + bias;
		}

		SoundAttr(String spec)
		{
			if (spec.length() == 0)
				return;
			// parse the prop key, if it is there
			if (spec.charAt(0) == '\'')
			{
				int p = spec.indexOf('\'', 1);
				if (p > 0)
				{
					propkey = spec.substring(1, p);
					spec = spec.substring(p+1);
				}
			}
			// now start parsing the other fields
			int mode = 0; // mode 0=bias, 1=scale, 2=power
			for (int i=0; i<spec.length(); i++)
			{
				char ch = spec.charAt(i);
				switch (ch)
				{
					case '+' : mode=0; break;
					case '*' : mode=1; break;
					case '^' : mode=2; break;
					case '-' :
					default:
						if (Character.isDigit(ch) || ch == '-')
						{
							String numstr = ""+ch;
							while (++i<spec.length())
							{
								char c = spec.charAt(i);
								if (!Character.isDigit(c) && c!='.')
									break;
								numstr += spec.charAt(i);
							}
							float num = Float.parseFloat(numstr);
							switch (mode)
							{
								case 0 : bias = num; break;
								case 1 : scale = num; break;
								case 2 : power = num; break;
								default : //??
							}
						}
						break;
				}
			}
		}

		float getValue()
		{
			if (propkey == null)
				return bias;
			try {
				Object obj = PropertyEvaluator.get(top, propkey);
				if (debug)
					System.out.println(propkey + "\t" + obj);
				if (obj == null)
					return bias;
				float val = PropertyUtil.toFloat(obj);
				if (Float.isNaN(val))
					return bias;

				if (power == 1)
					val = val*scale+bias;
				else
					val = (float)(Math.pow(val,power)*scale+bias);
				return val;
			} catch (PropertyException pe) {
				return bias;
			}
		}
	}

	///

	class SoundDef
	{
		String filename;
		SoundAttr volume, pan, pitch;

		SoundClip clip;
		SoundChannel channel;
		boolean playing;

		SoundDef(String spec)
		{
			StringTokenizer st = new StringTokenizer(spec, ";");
			while (st.hasMoreTokens())
			{
				String s = st.nextToken();
				if (s.startsWith("volume="))
					volume = new SoundAttr(s.substring(7));
				else if (s.startsWith("pan="))
					pan = new SoundAttr(s.substring(4));
				else if (s.startsWith("pitch="))
					pitch = new SoundAttr(s.substring(6));
				else
					filename = s;
			}
		}

		public String toString()
		{
			return '"' + filename + "\" volume=" + volume + " pan=" + pan + " pitch=" + pitch + ']';
		}

		void open()
		{
			if (channel != null)
				return;
			clip = sserver.getClip(filename, 0);
			if (clip == null)
				return;
			channel = sserver.getChannel(clip, 0);
		}

		void close()
		{
			if (channel != null) {
				channel.close();
				channel = null;
			}
		}

		void update()
		{
			open();
			if (channel == null)
				return;

			float v;
			if (pan != null)
			{
				v = pan.getValue();
				channel.setPan(v);
			}
			if (pitch != null)
			{
				v = pitch.getValue();
				channel.setSampleRate(v*clip.getSampleRate());
			}
			v=0;
			if (volume != null)
			{
				v = volume.getValue();
				channel.setVolume(v);
			}

			// start or stop playing
			if (v <= 0.005f)
			{
				if (playing) {
					channel.stop();
					playing = false;
				}
			} else {
				if (!playing) {
					channel.loop(clip, -1);
					playing = true;
				}
			}

		}
	}

	//

	boolean debug = false;

}
