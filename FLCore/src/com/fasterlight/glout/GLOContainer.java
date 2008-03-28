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

/**
  * A generic container class that makes no attempt to
  * lay out its children.
  */
public class GLOContainer
extends GLOComponent
{
	/**
	  * Top children at end of list
	  */
	protected List children = new ArrayList();
	protected boolean cullchildren = true;

	public void add(GLOComponent cmpt)
	{
		addChild(cmpt);
	}

	public void addFirst(GLOComponent cmpt)
	{
		addChildFirst(cmpt);
	}

	public void remove(GLOComponent cmpt)
	{
		removeChild(cmpt);
	}

	public boolean contains(GLOComponent cmpt)
	{
		return (children.contains(cmpt));
	}

	public boolean shows(GLOComponent cmpt)
	{
		// todo: contains() should be there?
		return (!cullchildren || ctx.intersects(cmpt));
	}

	private void addChild(GLOComponent cmpt)
	{
		if (cmpt.getParent() != null)
			throw new RuntimeException("Cannot add, " + cmpt + " already is child of " + cmpt.getParent());
		children.add(cmpt);
		cmpt.setParent(this);
	}

	private void addChildFirst(GLOComponent cmpt)
	{
		children.add(0, cmpt);
		cmpt.setParent(this);
	}

	private void removeChild(GLOComponent cmpt)
	{
		if (!children.remove(cmpt))
			throw new RuntimeException("Cannot remove, " + cmpt + " is not in " + this);
		cmpt.setParent(null);
	}

	public void raiseChild(GLOComponent cmpt)
	{
		removeChild(cmpt);
		addChild(cmpt);
	}

	public void lowerChild(GLOComponent cmpt)
	{
		removeChild(cmpt);
		addChildFirst(cmpt);
	}

	public Iterator getChildren()
	{
		return children.iterator();
	}

	public List getChildList()
	{
		return Collections.unmodifiableList(children);
	}

	public GLOComponent getChild(int i)
	{
		return (GLOComponent)children.get(i);
	}

	public int getChildCount()
	{
		return children.size();
	}

	public void removeAllChildren()
	{
		while (getChildCount() > 0)
			removeChild(getChild(0));
	}

	public GLOComponent getComponentAt(int x, int y)
	{
		if (!(this.containsPoint(x,y)))
			return null;
		for (int i=getChildCount()-1; i>=0; i--)
		{
			GLOComponent cmpt = getChild(i);
			if (cmpt.isReceiving())
			{
				GLOComponent c2 = cmpt.getComponentAt(x,y);
//				System.out.println("cmpt=" + cmpt + "\tc2=" + c2);
				if (c2 != null)
				{
					return c2;
				}
			}
		}
		return this;
	}

	public GLOComponent getChildNamed(String name)
	{
		Iterator it = getChildren();
		while (it.hasNext())
		{
			GLOComponent cmpt = (GLOComponent)it.next();
			if (name.equals(cmpt.getName()))
				return cmpt;
		}
		return null;
	}

	public void layout()
	{
		// layout children
		Iterator it = getChildren();
		while (it.hasNext())
		{
			GLOComponent cmpt = (GLOComponent)it.next();
			cmpt.layout();
		}
	}

	public void align()
	{
		super.align();
		// layout children
		Iterator it = getChildren();
		while (it.hasNext())
		{
			GLOComponent cmpt = (GLOComponent)it.next();
			cmpt.align();
		}
	}

	/**
	  * Renders all children of the container
	  */
	public void renderChildren(GLOContext ctx)
	{
		for (int i=0; i<children.size(); i++)
		{
			GLOComponent cmpt = (GLOComponent)children.get(i);
			if (cmpt.isShowing())
				ctx.renderComponent(cmpt);
		}
	}

	/**
	  * Just renders all children
	  */
	public void render(GLOContext ctx)
	{
		renderChildren(ctx);
	}

	// PROPERTIES

	public Object getProp(String key)
	{
		if (key.startsWith("$"))
			return getChildNamed(key.substring(1));
		else
			return super.getProp(key);
	}

}
