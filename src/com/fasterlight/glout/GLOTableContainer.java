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

import java.awt.Dimension;
import java.util.Iterator;

import com.fasterlight.spif.*;

/**
  * A container class that lays out its children
  * in a table of rows and columns.
  */
public class GLOTableContainer
extends GLOContainer
{
	protected int cols, rows;
	protected int rowpad, colpad;
	protected byte[] rowflags = new byte[32];
	protected byte[] colflags = new byte[16];

	public static final int HALIGN_CENTER = 0;
	public static final int HALIGN_LEFT   = 1;
	public static final int HALIGN_RIGHT  = 2;
	public static final int VALIGN_CENTER = 0;
	public static final int VALIGN_TOP    = 1;
	public static final int VALIGN_BOTTOM = 2;

	public GLOTableContainer()
	{
		this(1,1);
	}

	public GLOTableContainer(int cols, int rows)
	{
		setNumColumns(cols);
		setNumRows(rows);
	}

	public int getNumColumns()
	{
		return cols;
	}

	public int getNumRows()
	{
		return rows;
	}

	public void setNumColumns(int cols)
	{
		if (cols < 1 || cols > 16)
			throw new IllegalArgumentException("Invalid # of columns: " + cols);
		this.cols = cols;
	}

	public void setNumRows(int rows)
	{
		if (rows < 1 || rows > 32)
			throw new IllegalArgumentException("Invalid # of rows: " + rows);
		this.rows = rows;
	}


	public int getColumnPadding()
	{
		return colpad;
	}

	public void setColumnPadding(int colpad)
	{
		this.colpad = colpad;
	}

	public int getRowPadding()
	{
		return rowpad;
	}

	public void setRowPadding(int rowpad)
	{
		this.rowpad = rowpad;
	}

	public void setPadding(int colpad, int rowpad)
	{
		setColumnPadding(colpad);
		setRowPadding(rowpad);
	}

	public void setColumnFlags(int col, int flags)
	{
		colflags[col] = (byte)flags;
	}

	public void setRowFlags(int row, int flags)
	{
		rowflags[row] = (byte)flags;
	}

	public int getColumnFlags(int col)
	{
		return colflags[col];
	}

	public int getRowFlags(int row)
	{
		return rowflags[row];
	}

	public void layout()
	{
		// first calc min size of rows, cols
		int[] rowsizes = new int[rows];
		int[] colsizes = new int[cols];
		int row,col;
		row=0;
		col=0;
		Iterator it = getChildren();
		while (it.hasNext())
		{
			GLOComponent cmpt = (GLOComponent)it.next();
			cmpt.layout();
			Dimension size = cmpt.getSize();
			if (size.width > colsizes[col])
				colsizes[col] = size.width;
			if (size.height > rowsizes[row])
				rowsizes[row] = size.height;
			col++;
			if (col >= cols) {
				col = 0;
				row++;
				if (row >= rows)
					break;
			}
		}
		// now size this container
		int[] colofs = new int[cols+1];
		int[] rowofs = new int[rows+1];
		for (int i=1; i<=cols; i++)
			colofs[i] = colofs[i-1]+colsizes[i-1];
		for (int i=1; i<=rows; i++)
			rowofs[i] = rowofs[i-1]+rowsizes[i-1];
		int w = colofs[cols] + (cols-1)*colpad;
      //int rowsused = Math.min((this.getChildCount()+cols-1)/cols, rows)
		int h = rowofs[rows] + (rows-1)*rowpad;
		this.setSize(w,h);
		// and now position the components
		row=0;
		col=0;
		it = getChildren();
		while (it.hasNext())
		{
			GLOComponent cmpt = (GLOComponent)it.next();
			int x = colofs[col] + col*colpad;
			int y = rowofs[row] + row*rowpad;
			Dimension size = cmpt.getSize();
			switch (colflags[col]&3) {
				case HALIGN_CENTER: x += (colsizes[col]-size.width)/2; break;
				case HALIGN_RIGHT: x += colsizes[col]-size.width; break;
			}
			switch (rowflags[row]&3) {
				case VALIGN_CENTER: y += (rowsizes[row]-size.height)/2; break;
				case VALIGN_BOTTOM: y += rowsizes[row]-size.height; break;
			}
			cmpt.setPosition(x,y);
			col++;
			if (col >= cols) {
				col = 0;
				row++;
				if (row >= rows)
					break;
			}
		}
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOTableContainer.class);

	static {
		prophelp.registerGetSet("cols", "NumColumns", int.class);
		prophelp.registerGetSet("rows", "NumRows", int.class);
		prophelp.registerGetSet("colpadding", "ColumnPadding", int.class);
		prophelp.registerGetSet("rowpadding", "RowPadding", int.class);
		// todo: fix flags
	}

	public Object getProp(String key)
	{
		if (key.startsWith("rowflags#"))
			return new Integer( getRowFlags(Integer.parseInt(key.substring(9))) );
		else if (key.startsWith("colflags#"))
			return new Integer( getColumnFlags(Integer.parseInt(key.substring(9))) );
		else {
			Object o = prophelp.getProp(this, key);
			if (o == null)
				o = super.getProp(key);
			return o;
		}
	}

	public void setProp(String key, Object value)
	{
		try {
			if (key.startsWith("rowflags#"))
				setRowFlags(Integer.parseInt(key.substring(9)), PropertyUtil.toInt(value) );
			else if (key.startsWith("colflags#"))
				setColumnFlags(Integer.parseInt(key.substring(9)), PropertyUtil.toInt(value) );
			else
				prophelp.setProp(this, key, value);
		} catch (PropertyRejectedException e) {
			super.setProp(key, value);
		}
	}


}
