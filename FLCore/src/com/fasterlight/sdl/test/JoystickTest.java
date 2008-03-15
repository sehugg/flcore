/*
    JSDL - Java(TM) interface to SDL
    Copyright (C) 2001  Steven Hugg

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Library General Public
    License as published by the Free Software Foundation; either
    version 2 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Library General Public License for more details.

    You should have received a copy of the GNU Library General Public
    License along with this library; if not, write to the Free
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Steven Hugg
    hugg@pobox.com
*/

package com.fasterlight.sdl.test;

import com.fasterlight.sdl.joystick.SDLJoystick;

public class JoystickTest
{
	public static void main(String[] args)
	throws Exception
	{
		int repeat = -1;
		if (args.length > 0)
			repeat = Integer.parseInt(args[0]);

		int nj = SDLJoystick.getNumJoysticks();
		System.out.println("# of joysticks: " + nj);

		int j1 = 0;
		int j2 = nj;
		if (repeat >= 0)
		{
			j1 = repeat;
			j2 = repeat+1;
		}

	while (true)
	{
		for (int i=j1; i<j2; i++)
		{
			System.out.println("\nJOYSTICK " + i + "\n");
			System.out.println("Name: " + SDLJoystick.getJoystickName(i));
			SDLJoystick joy = SDLJoystick.openJoystick(i);
			System.out.println("Joystick opened = " + SDLJoystick.isOpen(i));

			System.out.println("Index = " + joy.getIndex());
			joy.update();
			System.out.println("Updated");

			System.out.println("NumAxes = " + joy.getNumAxes());
			for (int j=0; j<joy.getNumAxes(); j++)
				System.out.println("  Axis " + j + " = " + joy.getAxis(j));

			System.out.println("NumBalls = " + joy.getNumBalls());
			for (int j=0; j<joy.getNumBalls(); j++)
				System.out.println("  Ball " + j + " = " + joy.getBall(j));

			System.out.println("NumHats = " + joy.getNumHats());
			for (int j=0; j<joy.getNumHats(); j++)
				System.out.println("  Hat " + j + " = " + joy.getHat(j));

			System.out.println("NumButtons = " + joy.getNumButtons());
			for (int j=0; j<joy.getNumButtons(); j++)
				System.out.println("  Buttons " + j + " = " + joy.getButton(j));

			joy.close();
			System.out.println("Joystick closed = " + !SDLJoystick.isOpen(i));
		}
		if (repeat < 0)
			break;
	}

	}
}
