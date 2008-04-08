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

import com.fasterlight.sound.JOALSoundServer;
import com.fasterlight.sound.SoundChannel;
import com.fasterlight.sound.SoundClip;

public class JOALSoundTest
{
	public static void main(String args[]) throws Exception
	{
		JOALSoundServer ss = new JOALSoundServer(new URL("file:../Exoflight/data/sounds/"));
		ss.open();
		System.out.println(ss.isOpen());
		SoundClip clip = ss.getClip("test2.wav", 0);
		System.out.println(clip + " " + clip.getSampleRate());
		SoundClip clip2 = ss.getClip("engine2.wav", 0);
		SoundChannel chan2 = ss.getChannel(clip2, 0);
		chan2.loop(-1);
		SoundClip clip3 = ss.getClip("shuttle/great_job.wav", 0);
		ss.queue(clip3);
		ss.queue(clip3);
		System.out.println(ss.hasQueued());
		Thread.sleep(3000);
		System.out.println(ss.hasQueued());
		ss.queue(clip3);
		for (int i = 0; i < 100; i++)
		{
			chan2.setPitch(1+i/100.0f);
			Thread.sleep(10);
		}
		for (int i = 0; i < 25; i++)
		{
			SoundChannel chan = ss.getChannel(clip, 0);
			float rate = rnd.nextFloat();
			chan.setPitch(rate*4);
			System.out.println(chan + " " + rate);
			chan.play();
			Thread.sleep(rnd.nextInt() & 511);
			chan2.setPitch(1+i*0.1f);
		}
		ss.close();
		System.out.println(ss.isOpen());
	}

	static Random rnd = new Random();

}
