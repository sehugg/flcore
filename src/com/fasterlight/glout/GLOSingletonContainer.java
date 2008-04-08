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

import com.fasterlight.spif.*;

/**
contains instance of 1 widget, identified by string!
whenever render()ed, it makes that widget's parent this container.
  */
public class GLOSingletonContainer
extends GLOContainer
{
	private String id;
	private boolean active;
	private boolean maximize;

	//

	public GLOSingletonContainer()
	{
	}

	/**
	  * be thee wary : group id's are stored in the GLOContext,
	  * and if you do not call reset() before reloading the components
	  * in the context, you may get old components in there!!!
	  */
	public void setGroupID(String id)
	{
		// remove from old group list
		if (id != null)
		{
			List l = getContext().getGroup(id);
			l.remove(this);
		}
		// set id
		this.id = id;
		// add to group
		if (id != null)
		{
			List l = getContext().getGroup(id);
			// add us to the list
			l.add(this);
		}
	}

	public String getGroupID()
	{
		return id;
	}

	private void moveContentsFrom(GLOContainer cont)
	{
		Iterator it = new ArrayList(cont.getChildList()).iterator();
		while (it.hasNext())
		{
			GLOComponent cmpt = (GLOComponent)it.next();
			cont.remove(cmpt);
			this.add(cmpt);
System.out.println("moved " + cmpt + " from " + cont + " to " + this);
			// maximize element if "maximized" is true
			if (maximize)
			{
				cmpt.setPosition(0,0);
				cmpt.setSize(this.getSize());
			}
		}
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean b)
	{
		if (b && !active)
		{
			List grouplist = getContext().getGroup(id);
			if (grouplist.size() > 0)
			{
				for (int i=0; i<grouplist.size(); i++)
				{
					GLOSingletonContainer gsc = (GLOSingletonContainer)grouplist.get(i);
					if (gsc.isActive() && gsc != this)
					{
						// copy contents into this window
						moveContentsFrom(gsc);
						gsc.setActive(false);
					}
				}
			}
		}
		this.active = b;
	}

	public boolean getMaximize()
	{
		return maximize;
	}

	public void setMaximize(boolean maximize)
	{
		this.maximize = maximize;
	}

	public void render(GLOContext ctx)
	{
		setActive(true);

		super.render(ctx);
	}


	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOSingletonContainer.class);

	static {
		prophelp.registerGetSet("group", "GroupID", String.class);
		prophelp.registerSet("active", "setActive", boolean.class);
		prophelp.registerGet("active", "isActive");
		prophelp.registerGetSet("maximize", "Maximize", boolean.class);
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
