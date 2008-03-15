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
package com.fasterlight.sound.test;

import java.net.URL;
import java.util.Random;

import com.fasterlight.sound.*;

public class JMFSoundTest
{
	public static void main(String args[]) throws Exception
	{
		JMFSoundServer ss = new JMFSoundServer(new URL("file:./data/sounds/"));
		ss.setDebug(true);
		ss.open();
		System.out.println(ss.isOpen());
		SoundClip clip = ss.getClip("test2.wav", 0);
		for (int i = 0; i < 20; i++)
		{
			SoundChannel chan = ss.getChannel(clip, 0);
			float rate = rnd.nextFloat() * 44100;
			chan.setSampleRate(rate);
			System.out.println(chan + " " + rate);
			chan.play(clip);
			Thread.sleep(rnd.nextInt() & 511);
		}
		ss.close();
		System.out.println(ss.isOpen());
	}

	static Random rnd = new Random();

}
