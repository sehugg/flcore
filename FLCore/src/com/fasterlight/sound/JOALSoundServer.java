package com.fasterlight.sound;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import net.java.games.joal.*;
import net.java.games.joal.util.ALut;

public class JOALSoundServer implements SoundServer {

	AL al;
	ALC alc;

	public class Channel implements SoundChannel {

		private Clip clip;
		private int flags;
		private int[] sources = new int[1];
		private int state[] = new int[1];
		private ArrayList queuedClips;

		public Channel(Clip clip, int flags) {
			this.clip = clip;
			this.flags = flags;
			al.alGenSources(1, sources, 0);
			al.alSourcei(sources[0], AL.AL_BUFFER, clip.buffers[0]);
			checkError();
		}

		public Channel(int flags) {
			this.flags = flags;
			al.alGenSources(1, sources, 0);
			queuedClips = new ArrayList();
		}

		public void finalize() {
			close();
		}

		public void close() {
			//al.alDeleteSources(1, sources, 0);
		}

		public boolean isPlaying() {
			al.alGetSourcei(sources[0], AL.AL_SOURCE_STATE, state, 0);
			return (state[0] == AL.AL_PLAYING);
		}

		public void loop(int nloops) {
			al.alSourcei(sources[0], AL.AL_LOOPING, AL.AL_TRUE);
			al.alSourcePlay(sources[0]);
		}

		public void play() {
			al.alSourcePlay(sources[0]);
		}
		
		void queue(Clip qclip) {
			al.alSourceQueueBuffers(sources[0], 1, qclip.buffers, 0);
			if (!isPlaying())
			{
				// unqueue all previously-queued clips
				for (int i=0; i<queuedClips.size(); i++)
				{
					Clip c = (Clip)queuedClips.get(i);
					al.alSourceUnqueueBuffers(sources[0], 1, c.buffers, 0);
				}
				queuedClips.clear();
				play();
			}
			queuedClips.add(qclip);
		}

		public void setPan(float value) {
			// TODO
		}

		public void setSampleRate(float value) {
			setPitch(value / clip.getSampleRate());
		}

		public void setVolume(float value) {
			al.alSourcef(sources[0], AL.AL_GAIN, value);
		}

		public void stop() {
			al.alSourceStop(sources[0]);
		}

		public void setPitch(float rate) {
			al.alSourcef(sources[0], AL.AL_PITCH, rate);
		}

	}

	public class Clip implements SoundClip {

		private int fmt;
		private int size;
		private int freq;
		private int loop;
		private ByteBuffer data;
		private int[] buffers = new int[1];

		public Clip(InputStream in) {
			int[] fmta = new int[1];
			int[] sizea = new int[1];
			int[] freqa = new int[1];
			int[] loopa = new int[1];
			ByteBuffer[] dataa = new ByteBuffer[1];
			ALut.alutLoadWAVFile(in, fmta, dataa, sizea, freqa, loopa);
			this.fmt = fmta[0];
			this.size = sizea[0];
			this.freq = freqa[0];
			this.loop = loopa[0];
			this.data = dataa[0];
			al.alGenBuffers(1, buffers, 0);
			al.alBufferData(buffers[0], fmt, data, size, freq);
			checkError();
		}

		public void finalize() {
			//al.alDeleteBuffers(1, buffers, 0);
		}

		public int getSampleRate() {
			return freq;
		}

	}

	private String basepkg;
	private boolean isopen;
	private URL baseurl;

	public JOALSoundServer(String basepkg) {
		this.basepkg = basepkg;
	}

	public JOALSoundServer(URL url) {
		this.baseurl = url;
	}

	public void finalize() {
		close();
	}

	public void close() {
		if (isopen) {
			isopen = false;
			ALut.alutExit();
			al = null;
			alc = null;
		}
	}

	public void checkError() {
		int err = al.alGetError();
		checkError(err);
	}
	
	void checkError(int err)
	{
		if (err != al.AL_NO_ERROR) {
			try {
				throw new Exception("OpenAL error " + err);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public SoundChannel getChannel(SoundClip clip, int flags) {
		Channel ch = new Channel((Clip) clip, flags);
		checkError();
		return ch;
	}

	public SoundClip getClip(String name, int flags) {
		try {
			InputStream in;
			if (baseurl != null) {
				URL url = new URL(baseurl, name);
				in = url.openStream();
			} else
				in = ClassLoader.getSystemResourceAsStream(basepkg + name);
			SoundClip clip = new Clip(in);
			in.close();
			checkError();
			return clip;
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
	}

	public boolean isOpen() {
		return isopen;
	}

	public void open() {
		if (!isopen) {
			ALut.alutInit();
			al = ALFactory.getAL();
			alc = ALFactory.getALC();
			ALCcontext alctx = alc.alcGetCurrentContext();
			ALCdevice alcdev = alc.alcGetContextsDevice(alctx);
			try {
				int[] ver = new int[2];
				alc.alcGetIntegerv(alcdev, ALC.ALC_MAJOR_VERSION, 1, ver, 0);
				alc.alcGetIntegerv(alcdev, ALC.ALC_MINOR_VERSION, 1, ver, 1);
				System.out.println("OpenAL version " + ver[0] + "." + ver[1]);
				System.out.println("OpenAL device = " + alc.alcGetString(alcdev, ALC.ALC_DEVICE_SPECIFIER));
			} catch (Exception e) {
				System.out.println("Could not get OpenAL version: " + e);
			}
			checkError(alc.alcGetError(alcdev));
			queueChannel = new Channel(0);
			checkError();
			isopen = true;
		}
	}

	Channel queueChannel;

	public void play(SoundClip clip, int flags) {
		if (!isOpen() || clip == null)
			return;

		SoundChannel channel = getChannel(clip, flags);
		if (channel == null)
			return;
		channel.play();
		checkError();
	}

	public void queue(SoundClip clip) {
		if (!isOpen() || clip == null)
			return;

		queueChannel.queue((Clip) clip);
		checkError();
	}

	public boolean hasQueued() {
		return queueChannel.isPlaying();
	}

}
