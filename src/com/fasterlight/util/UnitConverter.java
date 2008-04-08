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
package com.fasterlight.util;

import java.util.*;

/**
  * A class for converting numeric values to string values,
  * and parsing strings to numerics.
  */
public class UnitConverter
{
	private HashMap unit_to_canon = new HashMap();
	private HashSet canonical_units = new HashSet();


	/**
	  * Parse to canonical units
	  */
	public double parseToCanonical(String s)
	throws NumberFormatException
	{
		double num = 1;
		double denom = 1;
		boolean usenum = true;
		boolean flushUnit = false;
		boolean flushNumber = false;
		boolean hadvalue = false;
		int state = 1;
		StringBuffer digits = new StringBuffer();
		StringBuffer unit = new StringBuffer();

		int l = s.length();
		for (int i=0; i<l; i++)
		{
			char ch = s.charAt(i);
			switch (ch)
			{
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case '-':
				case '+':
				case '.':
					if (state != 1)
					{
						flushUnit = true;
						state = 1;
					}
					digits.append(ch);
					break;
				case 'e':
				case 'E':
					if (state == 1) {
						digits.append(ch);
						break;
					}
				default:
					if (state == 1) {
						flushNumber = true;
						state = 0;
					}
					unit.append(Character.toLowerCase(ch));
					break;
				case ' ':
				case '\t':
				case '\n':
				case '\r':
				case '*':
					// skip it
					break;
				case '/':
					flushUnit = true;
					state = 2;
					break;
			}
			if (i==l-1)
			{
				flushUnit = flushNumber = true;
			}
			if (flushUnit && unit.length() > 0)
			{
				String unitstr = unit.toString();
				double conv = getCanonicalConversionFactor(unitstr);
				if (usenum)
					num *= conv;
				else
					denom *= conv;
				unit = new StringBuffer();
			}
			flushUnit = false;
			if (flushNumber && digits.length() > 0)
			{
				String numstr = digits.toString();
				num *= Util.parseDouble(numstr);
				digits = new StringBuffer();
				hadvalue = true;
			}
			flushNumber = false;
			if (state == 2)
			{
				state = 0;
				usenum = !usenum;
			}
		}
		if (!hadvalue)
			throw new NumberFormatException("Could not parse number \"" + s + "\"");
		return num/denom;
	}

	//

	public double getCanonicalConversionFactor(String unit)
	{
		if (canonical_units.contains(unit))
			return 1;
		UnitRec ur = (UnitRec)unit_to_canon.get(unit);
		if (ur == null)
			throw new NumberFormatException("Unit \"" + unit + "\" not recognized");
		return ur.scale;
	}

	//

	private void addUnit(String canonunit, String convunit, double scale)
	{
		UnitRec ur = new UnitRec();
		ur.canon = canonunit.toLowerCase();
		ur.conv = convunit.toLowerCase();
		ur.scale = scale;
		addUnitRec(ur);
	}

	private void addUnitRec(UnitRec ur)
	{
		canonical_units.add(ur.canon);
		unit_to_canon.put(ur.conv, ur);
	}

	class UnitRec
	{
		String canon,conv;
		double scale=1,bias=1;

	}

//

	private static UnitConverter defaultUnitConverter;

	public static UnitConverter getUnitConverter()
	{
		return defaultUnitConverter;
	}

	public static double parse(String s)
	{
		return getUnitConverter().parseToCanonical(s);
	}

	// taken from exo.orbit.Constants
	public static final double AU_TO_KM = 149597870.691;
	public static final double LIGHT_YEAR_KM = 9.46053e12; // km

	static {
		UnitConverter u = new UnitConverter();

		u.addUnit("km", "m", 0.001);
		u.addUnit("km", "mm", 1e-6);
		u.addUnit("km", "cm", 1e-5);
		u.addUnit("km", "AU", AU_TO_KM);
		u.addUnit("km", "ly", LIGHT_YEAR_KM);
		u.addUnit("km", "parsec", 1d/3.2407799001645346e-14);

		u.addUnit("kg", "g", 0.001);
		u.addUnit("kg", "Mkg", 1e-6);

		u.addUnit("km", "ft", 1d/3280.839895013123);
		u.addUnit("km", "in", 1d/39370.078740157485);
		u.addUnit("km", "mi", 1d/0.621371192237334);
		u.addUnit("km", "nmi", 1d/0.5399568034557235);
		u.addUnit("km", "furlong", 1d/4.970969537898672);

		u.addUnit("kg", "slug", 1d/0.06852176556196105);
		u.addUnit("kg", "lb", 1d/2.2046226218487757);

		u.addUnit("s", "msec", 1000);
		u.addUnit("s", "min", 60);
		u.addUnit("s", "hr", 3600);
		u.addUnit("s", "hrs", 3600);
		u.addUnit("s", "d", 86400);
		u.addUnit("s", "day", 86400);
		u.addUnit("s", "days", 86400);
		u.addUnit("s", "year", (86400*365.25));
		u.addUnit("s", "y", (86400*365.25));
		u.addUnit("s", "yr", (86400*365.25));
		u.addUnit("s", "yrs", (86400*365.25));
		u.addUnit("s", "fortnight", (86400*14));

		defaultUnitConverter = u;
	}

	//

}
