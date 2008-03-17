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
package com.fasterlight.util.test;

import junit.framework.*;

import com.fasterlight.testing.*;
import com.fasterlight.util.UnitConverter;

public class UnitConverterTests
extends NumericTestCase
{
	RandomnessProvider rnd = new RandomnessProvider();

	//

	public UnitConverterTests(String s)
	{
		super(s);
		THRESHOLD = 1e-8;
	}

	public void test_units()
	{
		try {
			UnitConverter.parse("");
			fail("Should have thrown an exception");
		} catch (NumberFormatException nfe) {
		}

		try {
			UnitConverter.parse("km/s");
			fail("Should have thrown an exception");
		} catch (NumberFormatException nfe) {
		}

		compareAssert(0d, UnitConverter.parse("0"));
		compareAssert(0d, UnitConverter.parse("-0.000"));
		compareAssert(0.001d, UnitConverter.parse("+0.001"));
		compareAssert(1e-6, UnitConverter.parse("1e-6"));
		compareAssert(0.12345, UnitConverter.parse("0.12345"));

		compareAssert(0d, UnitConverter.parse("0 km"));
		compareAssert(1d, UnitConverter.parse("1 km"));
		compareAssert(1d, UnitConverter.parse("1 km/s"));
		compareAssert(1d, UnitConverter.parse("1 KM/S"));
		compareAssert(0.001d, UnitConverter.parse("1 m"));
		compareAssert(0.001d*0.001d, UnitConverter.parse("1 mm"));
		compareAssert(10/(1000*3600d), UnitConverter.parse("10 m/hr"));

		compareAssert(60d, UnitConverter.parse("1 min"));
		compareAssert(3600d, UnitConverter.parse("1 hr"));
		compareAssert(86400d*365.25, UnitConverter.parse("1 yr"));
	}

   public static Test suite()
   {
      TestSuite suite = new TestSuite(UnitConverterTests.class);
      return suite;
   }

}

