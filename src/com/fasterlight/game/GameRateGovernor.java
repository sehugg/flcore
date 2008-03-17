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
package com.fasterlight.game;

import com.fasterlight.spif.*;

/**
  * Used to keep a Game going at a given realtime rate,
  * and manage time overruns and time scale changes.
  * @see Game
 **/
public class GameRateGovernor implements java.io.Serializable, PropertyAware
{
	private Game game;

	private int timescaleidx = 0;
	private float TIME_SCALE = 1;
	private int curoverrun;
	private long t1, t2, tdur;
	private boolean paused;

	public static final int IS_PAUSED = 99;

	private int maxupdate = Settings.getInt("Sim", "MaxUpdate", 200); // msec
	private int maxoverruns = Settings.getInt("Sim", "MaxOverruns", 1);

	// default time scales, in seconds
	private float timescales[] =
		new float[] {
			1,
			2,
			4,
			8,
			15,
			30,
			60,
			120,
			300,
			600,
			1800,
			3600,
			7200,
			21600,
			43200,
			86400,
			259200,
			604800,
			1209600,
			2592000,
			7776000,
			31557600 };

	//

	public GameRateGovernor(Game game)
	{
		this.game = game;
	}

	/**
	 * Sets the list of preset time scales, in seconds.
	 */
	public void setPresetTimeScales(float[] timescales)
	{
		this.timescales = timescales;
	}

	public boolean addTimeScaleIndex(int didx)
	{
		return setTimeScaleIndex(getTimeScaleIndex() + didx);
	}

	public boolean setTimeScaleIndex(int idx)
	{
		if (idx < 0)
			idx = 0;
		if (idx >= timescales.length)
			idx = timescales.length - 1;
		TIME_SCALE = timescales[idx];
		if (timescaleidx != idx)
		{
			timescaleidx = idx;
			//			System.out.println("Time scale is now 1 sec = " + AstroUtil.toDuration(TIME_SCALE));
			return true;
		}
		else
			return false;
	}

	public int getTimeScaleIndex()
	{
		return timescaleidx;
	}

	/**
	 * Updates the game for dur*TIME_SCALE ticks,
	 * where 'dur' is the time in msec since the last update
	 * @return IS_PAUSED if the game is paused
	 * 			OUT_OF_TIME if the game exceeded getMaxUpdateTime() more
	 * 						than getMaxOverruns() times
	 */
	public int update()
	{
		t2 = t1;
		t1 = currentTime();
		long dur = (t1 - t2);
		if (dur > maxupdate)
			dur = maxupdate;
		tdur = (long) (dur * TIME_SCALE);
		if (tdur == 0)
			tdur = 1;
		int reason = IS_PAUSED;
		if (!paused)
		{
			reason = game.update(tdur, maxupdate);
			switch (reason)
			{
				case Game.OUT_OF_TIME :
					if (++curoverrun > maxoverruns)
					{
						System.out.println("time overrun");
						setTimeScaleIndex(getTimeScaleIndex() - 1);
						curoverrun = 0;
					}
					break;
				default :
					curoverrun = 0;
					break;
			}
		}
		return reason;
	}

	protected long currentTime()
	{
		return System.currentTimeMillis();
	}

	public float getTimeScale()
	{
		return TIME_SCALE;
	}

	public boolean setTimeScale(float sc)
	{
		for (int i = 0; i < timescales.length; i++)
		{
			if (sc == timescales[i])
			{
				return setTimeScaleIndex(i);
			}
		}
		boolean b = setTimeScaleIndex(0);
		TIME_SCALE = sc;
		return b;
	}

	public boolean decreaseTimeScaleTo(float sc)
	{
		if (sc < TIME_SCALE)
			return setTimeScale(sc);
		else
			return false;
	}

	public boolean getPaused()
	{
		return paused;
	}

	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}

	public long getLastUpdateTime()
	{
		return tdur;
	}

	public int getMaxUpdateTime()
	{
		return maxupdate;
	}

	public void setMaxUpdateTime(int maxupdate)
	{
		this.maxupdate = maxupdate;
	}

	public int getMaxOverruns()
	{
		return maxoverruns;
	}

	public void setMaxOverruns(int maxoverruns)
	{
		this.maxoverruns = maxoverruns;
	}

	public String getTimeScaleStr()
	{
		if (TIME_SCALE < 60)
			return TIME_SCALE + " sec";
		else if (TIME_SCALE < 3600)
			return (TIME_SCALE / 60) + " min";
		else if (TIME_SCALE < 86400)
			return (TIME_SCALE / 3600) + " hr";
		else
			return (TIME_SCALE / 86400) + " days";
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GameRateGovernor.class);

	static {
		prophelp.registerGetSet("paused", "Paused", boolean.class);
		prophelp.registerGetSet("maxupdatetime", "MaxUpdateTime", int.class);
		prophelp.registerGetSet("maxoverruns", "MaxOverruns", int.class);
		prophelp.registerGetSet("timescaleidx", "TimeScaleIndex", int.class);
		prophelp.registerSet("addtimescaleidx", "addTimeScaleIndex", int.class);
		prophelp.registerGetSet("timescale", "TimeScale", float.class);
		prophelp.registerGet("timescalestr", "getTimeScaleStr");
	}

	public Object getProp(String key)
	{
		return prophelp.getProp(this, key);
	}

	public void setProp(String key, Object value)
	{
		prophelp.setProp(this, key, value);
	}

}
