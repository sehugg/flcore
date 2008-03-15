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
package com.fasterlight.sound;

import java.io.*;
import java.net.URL;
import java.util.*;

import javax.sound.sampled.*;

/**
 * An implementation of SoundServer that uses the JavaSound library. todo: do
 * something w/ exceptions
 */
public class JMFSoundServer implements SoundServer
{
	private static final double LOG_10 = Math.log(10);

	WeakHashMap clips = new WeakHashMap();
	Mixer mixer;
	Line openline;
	URL baseurl;
	String basepkg;
	int bufferSize = 1024;
	LinkedList queue = new LinkedList();
	boolean isQueueing;

	static final int mixerIndex = com.fasterlight.game.Settings.getInt("Sound", "DeviceNum", 0);

	//

	public JMFSoundServer(URL baseurl)
	{
		this.baseurl = baseurl;
		init();
	}

	public JMFSoundServer(String basepkg)
	{
		this.basepkg = basepkg;
		init();
	}

	private void init()
	{
		// use default Java Sound mixer (todo?)
		Mixer.Info ainfo[] = AudioSystem.getMixerInfo();
		mixer = AudioSystem.getMixer(ainfo[mixerIndex]);
		if (debug)
			System.out.println("Opened soundserver with mixer " + mixer);
	}

	public void open()
	{
		if (openline != null)
			return;
		try
		{
			Line.Info lineinfo = mixer.getSourceLineInfo()[0];
			Line line = mixer.getLine(lineinfo);
			if (line == null)
				return;
			line.open();
			if (debug)
				System.out.println("open, # lines = " + mixer.getSourceLines().length);
			this.openline = line;
		} catch (LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		// close all open lines
		Line[] openlines = mixer.getSourceLines();
		for (int i = 0; i < openlines.length; i++)
			openlines[i].close();
		// clear the "queue"
		queue.clear();
		isQueueing = false;
		if (openline != null)
		{
			openline.close();
			openline = null;
		}
	}

	public boolean isOpen()
	{
		return (openline != null) && openline.isOpen();
	}

	protected AudioInputStream loadClip(String name, int flags) throws IOException,
			UnsupportedAudioFileException, LineUnavailableException
	{
		if (baseurl != null)
		{
			URL url = new URL(baseurl, name);
			return AudioSystem.getAudioInputStream(url);
		} else
		{
			InputStream in = ClassLoader.getSystemResourceAsStream(basepkg + name);
			return AudioSystem.getAudioInputStream(in);
		}
	}

	public SoundClip getClip(String name, int flags)
	{
		if (!isOpen())
			return null;

		JMFSoundClip clip = (JMFSoundClip) clips.get(name);
		if (clip == null)
		{
			clip = new JMFSoundClip();
			try
			{
				if (debug)
					System.out.println("loading sound " + name);
				AudioInputStream ais = loadClip(name, flags);
				clip.fmt = ais.getFormat();
				clip.setData(ais);
				clips.put(name, clip);
			} catch (IOException ioe)
			{//todo
				ioe.printStackTrace();
				return null;
			} catch (LineUnavailableException ioe)
			{//todo
				ioe.printStackTrace();
				return null;
			} catch (UnsupportedAudioFileException ioe)
			{//todo
				ioe.printStackTrace();
				return null;
			}
		}
		return clip;
	}

	public SoundChannel getChannel(SoundClip clip, int flags)
	{
		if (!isOpen())
			return null;

		try
		{
			JMFSoundClip jclip = (JMFSoundClip) clip;
			DataLine.Info info = new DataLine.Info(Clip.class, jclip.fmt, 512);
			Clip line = (Clip) mixer.getLine(info);
			line.open(jclip.fmt, jclip.data, 0, jclip.data.length);
			if (debug)
				System.out.println("open, # lines = " + mixer.getSourceLines().length);
			return new JMFSoundChannel(line);
		} catch (LineUnavailableException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void play(SoundClip clip, int flags)
	{
		if (!isOpen() || clip == null)
			return;

		boolean doQueue = (flags & QUEUE) != 0;
		if (doQueue && isQueueing)
		{
			queue.add(clip);
			if (debug)
				System.out.println("pushed, queue size=" + queue.size());
		} else
		{
			SoundChannel channel = getChannel(clip, flags);
			if (channel == null)
			{
				return;
			}
			if (doQueue)
			{
				isQueueing = true;
			}
			channel.play(clip);
		}
	}

	public void playNextQueued(int flags)
	{
		if (!queue.isEmpty())
		{
			SoundClip clip = (SoundClip) queue.removeFirst();
			play(clip, flags);
			if (debug)
				System.out.println("play next, queue size=" + queue.size());
		} else
			isQueueing = false;
	}

	public boolean hasQueued()
	{
		return isQueueing;
	}

	//

	class JMFSoundClip implements SoundClip
	{
		byte[] data;
		AudioFormat fmt;

		void setData(AudioInputStream ais) throws IOException
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] arr = new byte[4096];
			int l = 0;
			do
			{
				l = ais.read(arr);
				if (l < 0)
					break;
				baos.write(arr, 0, l);
			} while (true);
			data = baos.toByteArray();
		}

		public int getSampleRate()
		{
			return (int) fmt.getSampleRate();
		}
	}

	//

	class JMFSoundChannel implements SoundChannel, LineListener
	{
		Clip line;

		JMFSoundChannel(Clip line)
		{
			this.line = line;
		}

		void setNotify()
		{
			line.addLineListener(this);
		}

		public void update(LineEvent event)
		{
			if (event.getType() == LineEvent.Type.STOP)
			{
				line.removeLineListener(this);
				close();
				playNextQueued(0);
			}
		}

		public boolean isPlaying()
		{
			return line.isActive();
		}

		public void play(SoundClip clip)
		{
			setNotify();
			line.start();
		}

		public void loop(SoundClip clip, int ntimes)
		{
			line.setFramePosition(1);
			line.loop(ntimes);
		}

		public void close()
		{
			stop();
			line.close();
			if (debug)
				System.out.println("close, # lines = " + mixer.getSourceLines().length);
		}

		public void stop()
		{
			line.stop();
		}

		void setControl(Control.Type type, float value)
		{
			FloatControl ctrl = (FloatControl) line.getControl(type);
			float minvalue = ctrl.getMinimum();
			float maxvalue = ctrl.getMaximum();
			float v = minvalue + value * (maxvalue - minvalue);
			ctrl.setValue(v);
			if (debug)
				System.out.println("set " + type + " -> " + value + ", prec=" + ctrl.getPrecision()
						+ " " + ctrl.getUnits());
		}

		void setControlAbs(Control.Type type, float value)
		{
			try {
			FloatControl ctrl = (FloatControl) line.getControl(type);
			ctrl.setValue(value);
			} catch (IllegalArgumentException iae) { // todo
			}
		}

		float getControlAbs(Control.Type type)
		{
			return ((FloatControl) line.getControl(type)).getValue();
		}

		public void setVolume(float value)
		{
			// TODO: use log table
			setControlAbs(FloatControl.Type.MASTER_GAIN, (float) (20 * Math.log(value) / LOG_10));
		}

		public void setPan(float value)
		{
			setControlAbs(FloatControl.Type.PAN, value);
		}

		public void setSampleRate(float value)
		{
			// TODO why does Sample Rate not work?
			setControlAbs(FloatControl.Type.SAMPLE_RATE, value);
		}

		public float getVolume()// todo
		{
			return getControlAbs(FloatControl.Type.MASTER_GAIN);
		}

		public float getPan() //todo
		{
			return getControlAbs(FloatControl.Type.PAN);
		}

		public float getSampleRate()
		{
			return getControlAbs(FloatControl.Type.SAMPLE_RATE);
		}
	}

	///

	public void setDebug(boolean b)
	{
		this.debug = b;
	}

	public boolean debug = !true;

}