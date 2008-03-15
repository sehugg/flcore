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
package com.fasterlight.util;

import java.util.Iterator;

/**
  * An iterator that concatenates two Iterator objects.
  * Does not support remove().
  */
public class JoinedIterator
implements Iterator
{
	Iterator a,b;

	//

	public JoinedIterator(Iterator a, Iterator b)
	{
		this.a = a;
		this.b = b;
	}

	public boolean hasNext()
	{
		if (a != null)
		{
			boolean ahn = a.hasNext();
			if (!ahn)
				a = null;
			else
				return true;
		}
		return b.hasNext();
	}

	public Object next()
	{
		if (a != null && a.hasNext())
			return a.next();
		else
			return b.next();
	}

	public void remove()
	{
		throw new UnsupportedOperationException("Does not support remove()");
	}
}
