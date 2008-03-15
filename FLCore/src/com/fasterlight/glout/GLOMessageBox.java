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

import java.util.StringTokenizer;

/**
  * A message box that displays a message, and one or more buttons.
  */
public class GLOMessageBox
extends GLOWindow
{
   private GLOComponent receiver;

   public static int DEFAULT_XCHARS = 60; // todo: alter dep. on width of scrn?

   public static final String OK = "Ok";
   public static final String OKCANCEL = "Ok|Cancel";
   public static final String YESNOCANCEL = "Yes|No|Cancel";
   public static final String CLOSE = "Close";

   public GLOMessageBox(String message, String buttons)
   {
      this(message, buttons, DEFAULT_XCHARS);
   }

   public GLOMessageBox(String message, String buttons, int xchars)
   {
      GLOTableContainer table1 = new GLOTableContainer(1,3);

      // add text message
System.out.println("xchars=" + xchars);
      table1.add(new GLOWrapText(message, xchars));

      // add space
      table1.add(new GLOLabel(""));

		// add buttons
      StringTokenizer st = new StringTokenizer(buttons, "|");
      GLOTableContainer table2 = new GLOTableContainer(st.countTokens(),1);
      while (st.hasMoreTokens())
      {
         String btnname = st.nextToken();
         GLOButton btn = new GLOButton(btnname, btnname);
         table2.add(btn);
      }
      table1.add(table2);

      setContent(table1);
      layout();
   }

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOActionEvent)
		{
         Object act = ((GLOActionEvent)event).getAction();
         close();
         return false; // let our parent worry about it
		}

		return super.handleEvent(event);
	}

   //

   public static GLOMessageBox show(GLOContainer container,
      String message, String buttons)
   {
      GLOMessageBox box = new GLOMessageBox(message, buttons);
      container.add(box);
      box.center();
      return box;
   }

   public static GLOMessageBox showOk(GLOContainer container,
      String message)
   {
      return show(container, message, "Ok");
   }

   public static GLOMessageBox showOk(String message)
   {
   	if (GLOContext.getCurrent() != null)
	      return show(GLOContext.getCurrent(), message, "Ok");
	   else
	   	return null;
   }
}
