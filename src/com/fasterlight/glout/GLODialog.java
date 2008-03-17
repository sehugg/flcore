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


/**
  * A subclass of GLOWindow with dialog-specific features.
  */
public class GLODialog
extends GLOWindow
{
	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOActionEvent)
		{
			Object action = ((GLOActionEvent)event).getAction();
			if (action instanceof String)
			{
				String acts = (String)action;
				if (acts.startsWith("Cancel"))
				{
					dialogCancel(acts.substring(6));
					return true;
				}
				else if (acts.startsWith("Accept"))
				{
					dialogAccept(acts.substring(6));
					return true;
				}
				else if (acts.startsWith("Apply"))
				{
					dialogApply(acts.substring(5));
					return true;
				}
			}
		}

		return super.handleEvent(event);
	}

	public void dialogApply(String s)
	{
		// override me
	}

	public void dialogAccept(String s)
	{
		dialogApply(s);
		close();
	}

	public void dialogCancel(String s)
	{
		close();
	}

}
