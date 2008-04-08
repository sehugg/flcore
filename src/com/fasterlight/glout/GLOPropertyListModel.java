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

import java.util.List;

import com.fasterlight.spif.*;

/**
  * Property GLOListModel implementation, uses java.util.List
  * as a backing store.
  */
public class GLOPropertyListModel
extends GLOAbstractListModel
implements GLOListModel, PropertyAware
{
	protected GLOComponent cmpt;
	protected PropertyEvaluator prop_list, prop_sel;
	protected List list;

	//

	public GLOPropertyListModel(GLOComponent cmpt)
	{
		this.cmpt = cmpt;
	}

	public String getPropertyForList()
	{
		return cmpt.getKey(prop_list);
	}

	public void setPropertyForList(String prop_list)
	{
		this.prop_list = new PropertyEvaluator(prop_list);
	}

	public String getPropertyForSelected()
	{
		return cmpt.getKey(prop_sel);
	}

	public void setPropertyForSelected(String prop_sel)
	{
		this.prop_sel = new PropertyEvaluator(prop_sel);
	}

	//

	public Object getSelectedItem()
	{
		if (prop_sel != null)
		{
			selitem = cmpt.getForPropertyKey(prop_sel);
		}
		return super.getSelectedItem();
	}

	public void setSelectedItem(Object o)
	{
		if (prop_sel != null)
		{
			cmpt.setForPropertyKey(prop_sel, o);
		} else {
			super.setSelectedItem(o);
		}
	}

	public List getList()
	{
		if (prop_list != null)
			list = (List)cmpt.getForPropertyKey(prop_list);
		return list;
	}
	public int size()
	{
		getList();
		return (list != null) ? list.size() : 0;
	}
	public Object get(int i)
	{
		getList();
		return (list != null) ? list.get(i) : null;
	}
	public int indexOf(Object o)
	{
		getList();
		return (list != null) ? list.indexOf(o) : -1;
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOPropertyListModel.class);

	static {
		prophelp.registerGetSet("list_prop", "PropertyForList", String.class);
		prophelp.registerGetSet("sel_prop", "PropertyForSelected", String.class);
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
