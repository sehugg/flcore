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

import java.awt.Point;

import com.fasterlight.spif.*;

public class GLOComboBox
extends GLOTableContainer
{
	GLOEditBox editbox;
	GLOButton dropbtn;
	GLOListModel model;
	GLOFramedComponent dropframe;
	GLOScrollBox dropbox;
	GLOStringList droplist;
	protected String prop_model;

	static Object COMBO_BOX_DROP = new Object();

	public GLOComboBox()
	{
		this(25);
	}

	public GLOComboBox(int minchars)
	{
		super(2,1);
		editbox = new GLOEditBox(minchars) {
			protected GLOLabel makeEditLabel(int minch) {
				GLOLabel input = new GLOCompletingLabel(minch);
				return input;
			}
		};
		add(editbox);
		dropbtn = new GLOButton("^", COMBO_BOX_DROP);
		add(dropbtn);

		droplist = new GLOStringList() {
			public void selectRow(int row) {
				super.selectRow(row);
				hideComboList();
			}
			public boolean handleEvent(GLOEvent event) {
				if (event instanceof GLOFocusEvent) {
					GLOFocusEvent focusev = (GLOFocusEvent)event;
					if (!focusev.isGained())
					{
						hideComboList();
						return true;
					}
				}
				return super.handleEvent(event);
			}
		};
		dropbox = new GLOScrollBox(false, true);
		dropbox.getBox().add(droplist);
		dropframe = new GLOFramedComponent(dropbox);
	}

	public void layout()
	{
		super.layout();
	}

	public void setModel(GLOListModel model)
	{
		this.model = model;
	}

	public GLOListModel getModel()
	{
		if (prop_model != null)
		{
			Object o = getForPropertyKey(prop_model);
			if (o instanceof GLOListModel)
				setModel( (GLOListModel)o );
		}
		if (model == null)
		{
			setModel(new GLOPropertyListModel(this));
		}
		return model;
	}

	public boolean comboListShowing()
	{
		return (dropframe.getParent() != null);
	}

	public void showComboList()
	{
		if (!comboListShowing())
		{
			droplist.setModel(getModel());
			GLOContext ctx = getContext();
			Point o = getOrigin();
			int boxheight = getHeight()*4;
			dropbox.getBox().setSize(getWidth(), boxheight);
			// position above, or below?
			if (o.y+boxheight > ctx.getHeight())
			{
				dropframe.setPosition(o.x, o.y-boxheight-getHeight());
			} else {
				dropframe.setPosition(o.x, o.y+getHeight());
			}
			ctx.add(dropframe);
			dropframe.layout();
			ctx.requestFocus(droplist);
		}
	}

	public void hideComboList()
	{
		if (comboListShowing())
		{
			GLOContext ctx = dropframe.getContext();
			ctx.remove(dropframe);
		}
	}

	public void render(GLOContext ctx)
	{
		getModel();
		if (model != null && ctx.getFocused() != editbox.getInputLabel())
		{
			Object item = model.getSelectedItem();
			if (item != null)
				editbox.setText(model.toString(item));
		}

		super.render(ctx);
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOActionEvent)
		{
			GLOActionEvent actev = (GLOActionEvent)event;
			if (COMBO_BOX_DROP == actev.getAction())
			{
				if (comboListShowing())
					hideComboList();
				else
					showComboList();
				return true;
			}
		}
		else if (event instanceof GLOFocusEvent)
		{
			GLOFocusEvent focusev = (GLOFocusEvent)event;
			if (focusev.getComponent() == droplist && !focusev.isGained())
			{
				hideComboList();
				return true;
			}
		}

		return super.handleEvent(event);
	}

	public String getPropertyForModel()
	{
		return prop_model;
	}

	public void setPropertyForModel(String prop_model)
	{
		this.prop_model = prop_model;
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOComboBox.class);

	static {
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
