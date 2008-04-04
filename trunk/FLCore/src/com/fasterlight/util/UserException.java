package com.fasterlight.util;

import com.fasterlight.glout.GLOMessageBox;

/**
 * 
 * An exception that has a printable message.
 *
 */
public class UserException extends RuntimeException
{
	public UserException(String arg0)
	{
		super(arg0);
	}

	public static String getMessage(Throwable t)
	{
		while (t != null)
		{
			// show dialog for UserException's
			if (t instanceof UserException)
			{
				return t.getMessage();
			}
			t = t.getCause();
		} while (t != null);
		return null;
	}
}
