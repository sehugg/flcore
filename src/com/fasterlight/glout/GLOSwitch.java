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
  * This is a control used to implement a switch or knob.
  * It has a mapping of property values to bitmap names,
  * and shows the bitmap for each mapping of a single property's
  * value.
  */
public class GLOSwitch
extends GLOBitmap
{
	private PropertyEvaluator propname;
	private List settings = new ArrayList();
	private int selectmode;
	private boolean editable=true;
/*
	private int clickx,clicky;
	private int travelamt = 8;
*/
	public static final int SEL_VERT  = 0;
	public static final int SEL_HORIZ = 1;

	//

	public class Setting
	implements PropertyAware
	{
		public String texname;
		public Object val;

		public Setting()
		{
		}

		public Setting(String texname, Object val)
		{
			this.texname = texname;
			this.val = val;
		}

		public Object getValue()
		{
			if (val instanceof String)
			{
				String s = (String)val;
				if (s.length() > 2 && s.charAt(0) == '\'' && s.charAt(s.length()-1) == '\'')
				{
					s = s.substring(1, s.length()-1);
					return getForPropertyKey(s);
				}
			}
			return val;
		}

		public Object getProp(String key)
		{
			if ("texname".equals(key))
				return texname;
			else if ("value".equals(key))
				return val;
			else
				return null;
		}

		public void setProp(String key, Object value)
		{
			if ("texname".equals(key))
				texname = PropertyUtil.toString(value);
			else if ("value".equals(key))
				val = PropertyUtil.parseValue(value);
			else
				throw new PropertyRejectedException(this, key, value);
		}
	}

	//

	public GLOSwitch()
	{
	}

	public boolean getEditable()
	{
		return editable;
	}

	public boolean isEditable()
	{
		return getEditable();
	}

	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}

	public void addSetting(Setting set)
	{
		settings.add(set);
	}

	public Setting getSetting(int i)
	{
		if (i < 0 || i >= settings.size())
			return null;
		else
			return (Setting)settings.get(i);
	}

	public String getTextureName()
	{
		int i = getIndex();
		Setting s = getSetting(i);
		return (s != null) ? s.texname : null;
	}

	public String getPropertyName()
	{
		return getKey(propname);
	}

	public void setPropertyName(String prop)
	{
		this.propname = new PropertyEvaluator(prop);
	}

	public int getIndex()
	{
		// todo: don't check every time!
		Object val = getForPropertyKey(propname);
		for (int i=0; i<settings.size(); i++)
		{
			Setting set = (Setting)settings.get(i);
			Object val2 = set.getValue();
//			System.out.println(val + " " + val2);
			if (PropertyUtil.equals(val, val2))
			{
				return i;
			}
		}
		return 0;
	}

	public void setIndex(int i)
	{
		if (propname == null)
			return;
		if (i<0)
			i = 0;
		if (i>=settings.size())
			i = settings.size()-1;
		Setting set = getSetting(i);
		if (set == null)
			return;
//		System.out.println("setIndex() value=" + set.getValue());
		setForPropertyKey(propname, set.getValue());
	}

	public int getSelectMode()
	{
		return selectmode;
	}

	public void setSelectMode(int selectmode)
	{
		this.selectmode = selectmode;
	}

	public GLOShader getBitmapShader()
	{
		return isDragging() ? getShader("switching-bitmap") : super.getBitmapShader();
	}

	//

	public boolean handleEvent(GLOEvent event)
	{
		if (isEditable() && event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			if (mbe.isPressed(1))
			{
				// if clicked lower or right 1/2, set to TRUE
				boolean b=false;
				switch (selectmode)
				{
					case SEL_VERT:
						b = (mbe.getY() < getOrigin().y+getHeight()/2);
						break;
					case SEL_HORIZ:
						b = (mbe.getX() > getOrigin().x+getWidth()/2);
						break;
				}

				int oldi = getIndex();
				int newi = (b ? getIndex()+1 : getIndex()-1);
				setIndex(newi);
				if (getIndex() != oldi)
					beginDrag(event);

				return true;
			}
			else if (isDragging() && mbe.isReleased(1))
			{
				endDrag(event);
				return true;
			}
		}
		return super.handleEvent(event);
	}

/*** new-fangled click-n-drag style
	public boolean handleEvent(GLOEvent event)
	{
		if (isEditable() && event instanceof GLOMouseButtonEvent)
		{
			GLOMouseButtonEvent mbe = (GLOMouseButtonEvent)event;
			if (mbe.isPressed(1))
			{
				beginDrag(event);
				clickx = mbe.getX();
				clicky = mbe.getY();
				return true;
			}
			else if (isDragging() && mbe.isReleased(1))
			{
				endDrag(event);
				return true;
			}
		}
		else if (isDragging() && event instanceof GLOMouseMovedEvent)
		{
			GLOMouseMovedEvent mme = (GLOMouseMovedEvent)event;
			int oldpos = (selectmode == SEL_VERT) ? clicky : clickx;
			int newpos = (selectmode == SEL_VERT) ? mme.getY() : mme.getX();
			int posdelta = (newpos-oldpos);
			if (selectmode == SEL_VERT)
				posdelta = -posdelta;

			if (Math.abs(posdelta) >= travelamt)
			{
				setIndex( getIndex() + ((posdelta>0)?1:-1) );
				endDrag(event);
			}

			return true;
		}
		return super.handleEvent(event);
	}
***/

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOSwitch.class);

	static {
		prophelp.registerGetSet("propname", "PropertyName", String.class);
		prophelp.registerGetSet("selmode", "SelectMode", int.class);
		prophelp.registerGetSet("int", "Index", int.class);
		prophelp.registerGetSet("editable", "Editable", boolean.class);
	}

	public Object getProp(String key)
	{
		// return setting #x
		if (key.startsWith("setting#"))
		{
			// expand settings list to size x
			int i = Integer.parseInt(key.substring(8));
			while (i >= settings.size())
				addSetting(new Setting());
			return getSetting(i);
		}
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
