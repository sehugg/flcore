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

public class GLOStringList
extends GLOAbstractList
{
	GLOListModel model;
	int padding = 4;
	int height;
	protected PropertyEvaluator prop_model;

	public GLOListModel getModel()
	{
		if (prop_model != null)
		{
			Object o = getForPropertyKey(prop_model);
			if (o instanceof GLOListModel)
				setModel( (GLOListModel)o );
		}
		return model;
	}

	public void setModel(GLOListModel model)
	{
		this.model = model;
	}

	public int getRowHeight()
	{
		return height;
	}

	public int getRowWidth()
	{
		return 1024; // arbitrary
	}

	public int getRowCount()
	{
		GLOListModel model = getModel();
		return (model==null) ? 0 : model.size();
	}

	public void layout()
	{
		super.layout();
		if (getContext() != null)
			height = (int)getContext().getFontServer().getTextHeight() + padding;
	}

	public boolean isSelected(int row)
	{
		GLOListModel model = getModel();
		Object o = model.getSelectedItem();
		if (o == null)
			return false;
		if (row < 0 || row >= model.size())
			return false;
		return o.equals(model.get(row));
	}

	public void selectRow(int row)
	{
		GLOListModel model = getModel();
		if (row < 0 || row >= model.size())
			return;
		model.setSelectedItem(model.get(row));
	}

	public String getPropertyForModel()
	{
		return getKey(prop_model);
	}

	public void setPropertyForModel(String prop_model)
	{
		this.prop_model = new PropertyEvaluator(prop_model);
	}

	public void drawRow(GLOContext ctx, int row, int xpos, int ypos, boolean selected)
	{
		if (row >= getRowCount())
			return;

		GLOListModel model = getModel();
		Object o = model.get(row);
		String s = model.toString(o);

		GLFontServer fs = ctx.getFontServer();
		fs.prepare();

		GLOShader shader;
		if (selected)
			shader = getShader("seltext");
		else
			shader = getShader("text");
		shader.set(ctx);

		fs.drawText(s, xpos, ypos);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOStringList.class);

	static {
		prophelp.registerGetSet("model", "Model", GLOListModel.class);
		prophelp.registerGet("rowheight", "getRowHeight");
		prophelp.registerGet("rowwidth", "getRowWidth");
		prophelp.registerGet("rowcount", "getRowCount");
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
