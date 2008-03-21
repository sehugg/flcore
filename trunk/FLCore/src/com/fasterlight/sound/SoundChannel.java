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

import com.fasterlight.sound.JOALSoundServer.Clip;

public interface SoundChannel
{
	public void play();
	public void loop(int nloops);
	public void stop();
	public void close();
	public void setVolume(float value);
	public void setPan(float value);
	public void setSampleRate(float value);
	public void setPitch(float rate);
	public boolean isPlaying();
}
