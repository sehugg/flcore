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
  * A control that uses a GLOListModel, and has up & down controls
  * on the left and right sides of the control.
  */
public class GLOSelectionBox
extends GLOFramedComponent
{
	GLOTableContainer table;
	GLOLabel label;
	GLOBitmapButton prevbtn, nextbtn;
	GLOListModel curmodel;
	protected String prop_model;

	static Object PREV_ACTION = new Object();
	static Object NEXT_ACTION = new Object();

	public GLOSelectionBox()
	{
		table = new GLOTableContainer(3,1);
		this.setContent(table);
		label = new GLOLabel();
		prevbtn = new GLOBitmapButton("sel-prev.png", PREV_ACTION);
		nextbtn = new GLOBitmapButton("sel-next.png", NEXT_ACTION);
		table.add(prevbtn);
		table.add(label);
		table.add(nextbtn);
	}

	public GLOSelectionBox(int minchars)
	{
		this();
		setMinChars(minchars);
	}

	public int getMinChars()
	{
		return label.getMinChars();
	}

	public void setMinChars(int minchars)
	{
		label.setMinChars(minchars);
	}

	public void layout()
	{
		int s = getShader("text").ysize;
		prevbtn.setSize(s,s);
		nextbtn.setSize(s,s);
		super.layout();
	}

	public void setModel(GLOListModel model)
	{
		this.curmodel = model;
	}

	public GLOListModel getModel()
	{
		if (prop_model != null)
		{
			Object o = getForPropertyKey(prop_model);
			if (o instanceof GLOListModel)
				setModel( (GLOListModel)o );
		}
		if (curmodel == null)
		{
			setModel(new GLOPropertyListModel(this));
		}
		return curmodel;
	}

	public String getPropertyForModel()
	{
		return prop_model;
	}

	public void setPropertyForModel(String prop_model)
	{
		this.prop_model = prop_model;
	}

	public GLOShader getFrameShader()
	{
		return getShader("selectionbox");
	}

	public void render(GLOContext ctx)
	{
		GLOListModel model = getModel();
		if (model != null)
		{
			Object item = model.getSelectedItem();
			if (item != null)
				label.setText(model.toString(item));
		}

		super.render(ctx);
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOActionEvent)
		{
			GLOActionEvent actev = (GLOActionEvent)event;
			GLOListModel model = getModel();
			if (model != null)
			{
				if (PREV_ACTION == actev.getAction())
				{
					model.previousItem();
					return true;
				}
				else if (NEXT_ACTION == actev.getAction())
				{
					model.nextItem();
					return true;
				}
			}
		}

		return super.handleEvent(event);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOSelectionBox.class);

	static {
		prophelp.registerGetSet("minchars", "MinChars", int.class);
		prophelp.registerGetSet("model", "Model", GLOListModel.class);
		prophelp.registerGetSet("model_prop", "PropertyForModel", String.class);
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
