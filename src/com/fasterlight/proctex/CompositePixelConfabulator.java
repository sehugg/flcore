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

import java.util.*;

public class CompositePixelConfabulator
extends PixelConfabulator
{
	private List delegates = new ArrayList();

	public CompositePixelConfabulator()
	{
		addConfabulator(new DefaultPixelConfabulator());
		addConfabulator(new RandomNeighborPixelConfabulator());
		addConfabulator(new AveragingPixelConfabulator());
		addConfabulator(new FunkyPixelConfabulator());
	}

	public void clear()
	{
		delegates.clear();
	}

	public void addConfabulator(PixelConfabulator pixConfab)
	{
		delegates.add(pixConfab);
	}

	public int newPixel(int parpix, long surround)
	{
		//int rnd = nextRand(parpix, surround, 0);
		int rnd = parpix ^ (int)surround;
		int index = (rnd & 0xffff) % delegates.size();
		PixelConfabulator pixConfab = (PixelConfabulator)delegates.get(index);
		return pixConfab.newPixel(parpix, surround);
	}

}
