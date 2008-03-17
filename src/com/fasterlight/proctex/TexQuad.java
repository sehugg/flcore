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
package com.fasterlight.proctex;

public class TexQuad
extends TexKey
{
	public byte[] data;
	public float minvalue, maxvalue;

	//

	public TexQuad(int x, int y, int level)
	{
		super(x, y, level);
	}

	public byte[] getByteData()
	{
		return data;
	}

	public void setByteData(byte[] data)
	{
		this.data = data;
	}

	public float byteToValue(int b)
	{
		return b*(maxvalue-minvalue)/255.0f + minvalue;
	}

	public float byteToValue(float b)
	{
		return b*(maxvalue-minvalue)/255.0f + minvalue;
	}

	public float byteToValue(byte b)
	{
		return byteToValue(b & 0xff);
	}
}

