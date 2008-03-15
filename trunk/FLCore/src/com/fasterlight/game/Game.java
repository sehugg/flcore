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

import java.io.*;
import java.util.*;

import com.fasterlight.spif.*;
import com.fasterlight.util.*;

/**
 * A game is essentially a priority queue which keeps track of time
 * and events.
 */
public class Game implements java.io.Serializable, PropertyAware
{
	private long curtime;
	private PriorityQueueVector eventq;
	private boolean debug;
	private Random rnd = new Random();

	public static final long INVALID_TICK = Long.MIN_VALUE;

	public static final int OK = 0;
	public static final int STOP_REQUESTED = 1;
	public static final int TICK_CHANGED = 2;
	public static final int OUT_OF_TIME = 3;

	//

	/**
	 * Constructs a Game at a start tick = 0
	 */
	public Game()
	{
		eventq = new PriorityQueueVector();
	}

	/**
	 * Constructs a Game at a specific start tick
	 * @param t0 the starting tick
	 */
	public Game(long t0)
	{
		this();
		this.curtime = t0;
	}

	/**
	 * @return the current tick
	 */
	public final long time()
	{
		return curtime;
	}

	/**
	  * Set the current time.
	  * NOTE: this can result in bad behavior, use update() where possible
	  */
	public void setTime(long t)
	{
		curtime = t;
	}

	/**
	 * Posts a new GameEvent to the queue.
	 */
	public void postEvent(GameEvent e)
	{
		eventq.add(e);
	}

	/**
	 * Cancels a GameEvent. The event will be removed when it gets popped off
	 * of the queue.
	 */
	public void cancelEvent(GameEvent e)
	{
		e.cancelled = true;
	}

	/**
	 * Updates the game state by a given # of ticks.
	 * @param timedelta the # of ticks to update
	 */
	public void update(long timedelta)
	{
		update(timedelta, -1);
	}

	/**
	 * Updates the game state by a given # of ticks.
	 * @param timedelta the # of ticks to update
	 * @param msec the maximum # of msec to update, or -1 to go forever
	 */
	public int update(long timedelta, long msec)
	{
		GameEvent e;
		long t1 = 0;
		if (msec >= 0)
			t1 = System.currentTimeMillis() + msec;
		long endtime = time() + timedelta;
		while (!eventq.isEmpty())
		{
			e = (GameEvent) eventq.peek();
			if (e.eventtime < curtime)
			{
				System.err.println("***Event time " + e.eventtime + " < " + curtime + " : " + e);
				eventq.remove();
			}
			else if (e.eventtime <= endtime)
			{
				long et = e.eventtime;
				curtime = et;
				eventq.remove();
				if (!e.cancelled)
				{
					if (dispatchEvent(e))
						return STOP_REQUESTED;
					// if the event caused the time to change,
					// we have to get out of this loop because
					// things will get weird
					if (curtime != et)
						return TICK_CHANGED;
				}
			}
			else
				break;
			if (msec >= 0 && System.currentTimeMillis() >= t1)
				return OUT_OF_TIME;
		}
		curtime = endtime;
		return OK;
	}

	/**
	  * Dispatch an event, return true to cause the event
	  * loop to stop processing events
	  */
	protected boolean dispatchEvent(GameEvent event)
	{
		if (debug)
			System.out.println(Long.toString(time() & 0xffffffff, 16) + " " + event);
		event.handleEvent(this);
		return false;
	}

	/**
	 * Returns the current queue size
	 */
	public int getQueueSize()
	{
		return eventq.size();
	}

	static Map inifiles = new HashMap();

	/**
	 * Helper class to load .ini file resources.
	 */
	public static String getResource(String restype, String section, String key, String defaultval)
	{
		try
		{
			INIFile ini = (INIFile) inifiles.get(restype);
			if (ini == null)
			{
				InputStream in = ClassLoader.getSystemResourceAsStream(restype);
				ini = new INIFile(in);
				inifiles.put(restype, ini);
			}
			return ini.getString(section, key, defaultval);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			throw new RuntimeException("Resource '" + restype + "' not found");
		}
	}

	public void setDebug(boolean b)
	{
		this.debug = b;
	}

	public boolean getDebug()
	{
		return debug;
	}

	// PROPERTY AWARE

	public Object getProp(String key)
	{
		if ("tick".equals(key))
			return new Long(time());
		else if ("qsize".equals(key))
			return new Integer(eventq.size());
		// todo: why is this needed?
		else if ("settings".equals(key))
			return new Settings();
		else
			return null;
	}

	public void setProp(String key, Object value)
	{
		if ("debug".equals(key))
		{
			setDebug(PropertyUtil.toBoolean(value));
			return;
		}
		throw new PropertyNotFoundException(key);
	}

	// UTIL

	/**
	  * Returns the nearest value to 't' that is a multiple
	  * of 'interval' and not less than 't'.
	  */
	public static long quantize(long t, long interval)
	{
		if (t > 0)
			return ((t + interval - 1) / interval) * interval;
		else
			return (t / interval) * interval;
	}

	/**
	  * Returns the random object used by this Game.
	  * You can set up your own seed to run repeatable simulations.
	  */
	public Random getRandom()
	{
		return rnd;
	}
}
