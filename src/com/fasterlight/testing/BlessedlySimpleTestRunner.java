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
package com.fasterlight.testing;

import java.lang.reflect.Method;
import java.util.*;

import junit.framework.*;

public class BlessedlySimpleTestRunner
{

	public static void printEnum(java.io.PrintStream w, Enumeration e)
	{
		while (e.hasMoreElements())
		{
			TestFailure fail = (TestFailure)e.nextElement();
			w.println(fail);
		}
	}

	public static void printResults(java.io.PrintStream w, TestResult result)
	{
		if (result.wasSuccessful())
		{
			w.println("...ok, " + result.runCount() + " tests");
			return;
		}
		w.println("!!! failed: " +
			result.runCount() + " tests, " +
			result.errorCount() + " errors, " +
			result.failureCount() + " failures");
		w.println("!!! ----------FAILURES-----------");
		printEnum(w, result.failures());
		w.println("!!! ----------ERRORS-----------");
		printEnum(w, result.errors());
	}

	public static void main(String[] args)
	throws Exception
	{
		List files = new ArrayList();
		for (int i=0; i<args.length; i++)
		{
			files.add(args[i]);
		}

		long t1 = System.currentTimeMillis();
		Iterator it = files.iterator();
		while (it.hasNext())
		{
			String filename = (String)it.next();
			try {
				System.err.println("...Loading " + filename);
				Class c = Class.forName(filename);
				Method getsuite = c.getDeclaredMethod("suite", new Class[0]);
				if (getsuite != null)
				{
					TestResult result = new TestResult();
					TestSuite suite = (TestSuite)getsuite.invoke(null, new Object[0]);
					suite.run(result);
					printResults(System.err, result);
				} else {
					System.err.println("!!! no suite() method in " + c);
				}
			} catch (Exception e) {
				System.err.println("!!! exception in " + filename);
				e.printStackTrace(System.err);
			}
		}
		long t2 = System.currentTimeMillis();
		System.err.println("Total test execution time was " + (t2-t1) + " ms");
	}
}
