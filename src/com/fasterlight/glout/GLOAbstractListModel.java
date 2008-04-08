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

import com.fasterlight.spif.*;

/**
  * An abstract GLOListModel implementation, is the base
  * class for GLODefaultListModel.
  */
public abstract class GLOAbstractListModel
implements GLOListModel, PropertyAware
{
	protected Object selitem;
	protected boolean always_select = true;

	public String toString(Object o)
	{
		if (o == null)
			return "...";
		else
			return o.toString();
	}
	public Object getSelectedItem()
	{
		if (selitem == null)
			return selectDefaultItem();
		if (indexOf(selitem) < 0)
			selitem = null;
		return selitem;
	}
	protected Object selectDefaultItem()
	{
		if (size() > 0) {
			Object o = get(0);
			setSelectedItem(o);
			return o;
		} else
			return null;
	}
	public void setSelectedItem(Object o)
	{
		selitem = o;
	}
	public void setAlwaysSelect(boolean b)
	{
		this.always_select = b;
	}

	// PROPERTIES

	public Object getProp(String key)
	{
		if ("size".equals(key))
			return new Integer(size());
		else if ("selected".equals(key))
			return getSelectedItem();
		else
			return null;
	}

	public void setProp(String key, Object value)
	{
		if ("selected".equals(key))
			setSelectedItem(value);
		else
			throw new PropertyRejectedException(this, key, value);
	}

	public void previousItem()
	{
		int selindex = indexOf(selitem);
		if (selindex > 0)
			setSelectedItem(get(selindex-1));
	}

	public void nextItem()
	{
		int selindex = indexOf(selitem);
		if (selindex < size()-1)
			setSelectedItem(get(selindex+1));
	}
}
