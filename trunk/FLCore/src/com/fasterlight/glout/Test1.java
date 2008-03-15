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
package com.fasterlight.glout;

import java.awt.*;
import java.util.*;

import javax.media.opengl.GLCanvas;

public class Test1 extends GLOAWTComponent
{
	boolean debug = false;
	GLODemonstrator demo;

	static String sampleText =
		"This is a piece of text intended to "
			+ "demonstrate the word-wrapping capability of yodabuf.  Enjoy it with a friend! "
			+ "Go stick your head in a pig.";

	//

	public Test1(int w, int h)
	{
		super(w, h);
	}

	void makeMenu()
	{
		GLOMenu menu = new GLOMenu();
		GLOMenuTable menutab = new GLOMenuTable();
		menutab.setHorizontal(true);
		menutab.setColumnPadding(8);

		try
		{
			menutab.loadMenu("panels/main.mnu");
			//   		GLOMenuPopup frame = new GLOMenuPopup();
			GLOFramedComponent frame = new GLOFramedComponent();
			frame.add(menutab);
			GLOVanishingPane pane = new GLOVanishingPane();
			pane.add(frame);
			pane.setSize(getWidth(), getHeight());
			ctx.add(pane);
			frame.layout();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void makeComponents()
	{
		super.makeComponents();

		ctx.debug = debug;

		TestSphere ts = new TestSphere();
		ts.setSize(ctx.getWidth(), ctx.getHeight());
		ctx.add(ts);
		int wn = 1;

		GLORadialMeter radmet = new GLORadialMeter();
		radmet.setSize(64, 64);
		radmet.setPropertyForValue("fps");
		radmet.setPosition(0, 128);
		ctx.add(radmet);

		Random rnd = new Random(0);
		int niters = 5;
		for (int i = 0; i < niters; i++)
		{
			GLOWindow c1 = new GLOWindow();
			c1.setPosition(rnd.nextInt() & 0x1ff, rnd.nextInt() & 0x1ff);
			c1.setSize(rnd.nextInt() & 0x1ff, rnd.nextInt() & 0x1ff);
			c1.setTitle("Window " + (wn++));

			int nrows = (rnd.nextInt() & 3) + 1;
			int ncols = (rnd.nextInt() & 3) + 1;
			GLOTableContainer c2 = new GLOTableContainer(ncols, nrows + 1);
			c2.setPadding(16, 16);
			for (int j = 0; j < nrows * ncols; j++)
			{
				GLOLabel lab = new GLOLabel();
				lab.setEditable(true);
				int x = j;
				lab.setText("(" + x + ")");
				c2.add(lab);
			}

			GLOButton btn1 = new GLOButton("(" + (ncols * nrows) + ")");
			c2.add(btn1);

			c1.setContent(c2);
			ctx.add(c1);
			c1.layout();
		}

		SortedMap mapping = new TreeMap();
		mapping.put("FOOBLITZ", "");
		mapping.put("FOOBAR", "");
		mapping.put("CHOWDER", "");
		mapping.put("C", "");
		mapping.put("CHILIDAWG", "");
		mapping.put("ZAPPA", "");

		for (int i = 0; i < niters; i++)
		{
			GLOWindow c1 = new GLOWindow();
			c1.setPosition(rnd.nextInt() & 0x1ff, rnd.nextInt() & 0x1ff);
			c1.setTitle("Window " + (wn++));

			GLOScrollBox c2 = new GLOScrollBox(true, true);
			c2.getBox().setSize(rnd.nextInt() & 0x1ff, rnd.nextInt() & 0x1ff);
			c2.getBox().setVirtualSize(
				c2.getBox().getWidth() * 2,
				c2.getBox().getHeight() * 2);

			int cnt = rnd.nextInt() & 15 + 4;
			for (int j = 0; j < cnt; j++)
			{
				GLOCompletingLabel lab = new GLOCompletingLabel(8);
				lab.setPosition(rnd.nextInt() & 0x1ff, rnd.nextInt() & 0x1ff);
				lab.setMapping(mapping);
				int x = j;
				lab.setText("(" + x + ")");
				c2.getBox().add(lab);
			}

			c1.setContent(c2);
			ctx.add(c1);
			c1.layout();
		}

		Object[] arr =
			{
				"First",
				"Second",
				"third",
				"fourth",
				"fifth",
				"sixth",
				"seventh",
				"and so on and so forth..." };
		GLOListModel model = new GLODefaultListModel(arr);

		for (int i = 0; i < niters; i++)
		{
			GLOWindow c1 = new GLOWindow();
			c1.setPosition(rnd.nextInt() & 0x1ff, rnd.nextInt() & 0x1ff);
			c1.setTitle("Window " + (wn++));

			GLOScrollBox c2 = new GLOScrollBox(false, true);
			c2.getBox().setSize(rnd.nextInt() & 0x1ff, rnd.nextInt() & 0x1ff);

			GLOStringList list = new GLOStringList();
			list.setModel(model);
			c2.getBox().add(list);

			c1.setContent(c2);
			c1.setFlags(GLOWindow.RESIZEABLE);
			c1.setResizeChild(c2.getBox());
			ctx.add(c1);
			c1.layout();
		}

		for (int i = 0; i < niters; i++)
		{
			GLOWindow c1 = new GLOWindow();
			c1.setPosition(rnd.nextInt() & 0x1ff, rnd.nextInt() & 0x1ff);
			c1.setTitle("Window " + (wn++));

			GLOComboBox combo = new GLOComboBox(20);
			combo.setModel(model);
			c1.setContent(combo);
			ctx.add(c1);
			c1.layout();
		}

		GLOMessageBox box =
			new GLOMessageBox(
				"This is a message, please enjoy reading it.",
				"Close");
		ctx.add(box);
		box.center();

		demo = new GLODemonstrator();
		demo.setSize(ctx.getWidth(), ctx.getHeight());
		demo.add(radmet);

		GLOSlideShow gloss = new GLOSlideShow("uitexs/illust/vehicles/A-4.txt");
		gloss.setSize(256, 256);
		gloss.setPosition(0, 256);
		ctx.add(gloss);

		for (int i = 0; i < 1; i++)
		{
			GLOWindow c1 = new GLOWindow();
			c1.setPosition(rnd.nextInt() & 0x1ff, rnd.nextInt() & 0x1ff);
			c1.setTitle("Console " + (wn++));

			GLOScrollBox c2 = new GLOScrollBox(false, true);
			//			c2.getBox().setSize( (rnd.nextInt()&0x1ff)+100, (rnd.nextInt()&0x1ff)+100 );
			c2.getBox().setSize(150, 150);

			GLOConsole cons =
				new GLOConsole(
					sampleText + "\n" + sampleText + "    " + sampleText);
			//			cons.setStandardErr(true);
			c2.getBox().add(cons);

			c1.setContent(c2);
			ctx.add(c1);
			c1.layout();
		}

		// must be on top
		makeMenu();

	}

	public GLOContext makeContext()
	{
		return new GLOContext()
		{
			public void renderScene()
			{
				super.renderScene();
				demo.renderHighlights(this);
				//				System.err.println(System.currentTimeMillis() + " - test");
			}
		};
	}

	public static void main(String[] args)
	{
		Test1 test = new Test1(600, 600);
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-d"))
				test.debug = true;
		}
		GLCanvas canvas = test.createGLCanvas();

		Frame f = new Frame();
		f.setLayout(new BorderLayout());
		f.add(canvas, BorderLayout.CENTER);
		f.pack();
		f.setSize(test.getSize());
		f.show();

		test.start(canvas, f);
	}

}
