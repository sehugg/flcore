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

import com.fasterlight.sound.TextToSpeechRenderer;

/**
  */
public class TTSRTest
extends TextToSpeechRenderer
{

	public void runTest()
	throws Exception
	{
		System.out.println(clean(" this is the best whitespace, like, ever ")+"*");
		System.out.println(clean(" ,,, ")+"*");

		debug = true;
		loadTransTable("sounds/transtbl.txt");
		test("great job, single engine press --- we have srb sep!");
		test("0");
		test("1.");
		test("10");
		test("11");
		test("12.");
		test("19 km");
		test("20 degrees");
		test("29%");
		test("99 km/s");
		test("100 percent");
		test("200.");
		test("101");
		test("110");
		test("119");
		test("125");
		test("185");
		test("-1200");
		test("123.456");
		Thread.sleep(10000);
	}

	public static void main(String[] args)
	throws Exception
	{
		new TTSRTest().runTest();
	}

}
