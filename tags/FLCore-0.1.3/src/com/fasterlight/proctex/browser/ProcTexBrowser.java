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
package com.fasterlight.proctex.browser;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;

import Acme.LruHashtable;

import com.fasterlight.proctex.*;

/**
 * GUI browser to view a procedural texture system, supporting
 * zoom in/out, pan, etc.
 */
public class ProcTexBrowser extends JFrame implements KeyListener
{
	ProcTexProvider ptp;
	IndexColorModel colormodel;
	LruHashtable imgmap = new LruHashtable(128);
	int zoomlevel;

	JScrollPane pane;
	WorldBrowser wbrowser;

	//

	public ProcTexBrowser(ProcTexProvider ptp)
	{
		this.ptp = ptp;
		byte[] pal = ptp.getPaletteBytes();
		this.colormodel = new IndexColorModel(8, 256, pal, 0, false);
		System.out.println(colormodel);

		//		for (int i=0; i<pal.length; i++)
		//			System.out.print((pal[i]&0xff) + " ");

		getContentPane().setLayout(new BorderLayout());
		wbrowser = new WorldBrowser();
		wbrowser.setAutoscrolls(true);
		pane = new JScrollPane(wbrowser);
		getContentPane().add(pane);

		setZoomLevel(0);

		Component p = wbrowser;
		p.addKeyListener(this);
		//		p.requestFocus();
	}

	public void setZoomLevel(int z)
	{
		int oldz = zoomlevel;
		if (z < 8)
			z = 8;
		this.zoomlevel = z;

		Point p = pane.getViewport().getViewPosition();
		if (z > oldz)
		{
			pane.getViewport().setViewPosition(new Point(p.x << (z - oldz), p.y << (z - oldz)));
		} else
		{
			pane.getViewport().setViewPosition(new Point(p.x >> (oldz - z), p.y >> (oldz - z)));
		}
		wbrowser.revalidate();
		wbrowser.repaint();
	}

	MemoryImageSource getImageSourceForQuad(TexQuad tq)
	{
		return new MemoryImageSource(
			ptp.getWidth(tq),
			ptp.getHeight(tq),
			colormodel,
			tq.getByteData(),
			0,
			ptp.getWidth(tq));
	}

	public Image getImageForQuad(TexQuad key)
	{
		Image img = (Image) imgmap.get(key);
		if (img == null)
		{
			TexQuad tq = ptp.getTexQuad(key.x, key.y, key.level);
			img = createImage(getImageSourceForQuad(tq));
			imgmap.put(key, img); // make new tq?
		}
		return img;
	}

	public void keyTyped(KeyEvent e)
	{
	}

	public void keyPressed(KeyEvent e)
	{
		//		System.out.println(e);
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_Z :
				setZoomLevel(zoomlevel + 1);
				break;
			case KeyEvent.VK_X :
				setZoomLevel(zoomlevel - 1);
				break;
		}
	}

	public void keyReleased(KeyEvent e)
	{
	}

	//

	class WorldBrowser extends JPanel
	{
		public boolean isFocusable()
		{
			return true;
		}

		public Dimension getPreferredSize()
		{
			int z = zoomlevel;
			return new Dimension(2 << z + 256, 1 << z + 256);
		}

		public void paint(Graphics g)
		{
			int xmax = (1 << zoomlevel) >> 7;
			int ymax = (1 << zoomlevel) >> 8;
			Rectangle clip = g.getClipBounds();
			g.setColor(Color.red);
			g.fillRect(clip.x, clip.y, clip.width, clip.height);
			int b = ptp.getBorder();
			int sc = ptp.getTexSize() - b * 2;
			int pz = sc;
			g.setColor(Color.blue);
			for (int y = -1; y <= ymax; y++)
			{
				for (int x = -1; x <= xmax; x++)
				{
					int qx = (x & (xmax - 1));
					int qy = (y & (ymax - 1));
					int xx = x * pz + pz/2;
					int yy = y * pz + pz/2;
					Rectangle rect = new Rectangle(xx, yy, pz, pz);
					if (rect.intersects(clip))
					{
						TexQuad tq = new TexQuad(qx, qy, zoomlevel);
						Image img = getImageForQuad(tq);
						g.drawImage(
							img,
							rect.x,
							rect.y,
							rect.x + rect.width,
							rect.y + rect.height,
							b,
							b,
							b + sc,
							b + sc,
							this);
						int cx = rect.x+rect.width/2;
						int cy = rect.y+rect.height/2;
						g.drawLine(cx,cy-3,cx,cy+3);
						g.drawLine(cx-3,cy,cx+3,cy);
						g.drawString(zoomlevel+"-"+qx+"-"+qy, cx, cy-10);
					}
				}
			}
			this.requestFocus();
		}

	}

	//

	public static void main(String[] args) throws Exception
	{

		String planet = "Luna";
		String prefix = planet;
		if (args.length > 0)
		{
			planet = prefix = args[0];
		}
		if (args.length > 1)
			prefix = args[1];

		ProcTexProvider ptp = getEarthPlanet(planet, prefix);
//		ProcTexProvider ptp = getRandomPlanet();

		ProcTexBrowser brow = new ProcTexBrowser(ptp);
		brow.pack();
		brow.setSize(512, 512);
		brow.setVisible(true);
		brow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	static ProcTexProvider getEarthPlanet(String planet, String prefix)
	{
		ProcTexProvider ptp = new ProcTexProvider();
		//ptp.setPathPrefix("texs/" + planet + "/" + prefix);
		ptp.setPathPrefix("elevtexs/" + planet + "/" + prefix + "-elev");
		ptp.setPixelConfabulator(new RandomNeighborPixelConfabulator());
		return ptp;
	}

	static ProcTexProvider getRandomPlanet()
	{
		ProcTexProvider ptp = new ProcTexProvider(7,2);
		//ptp.setPixelConfabulator(new CompositePixelConfabulator());
		ptp.setPixelConfabulator(new AveragingPixelConfabulator());
		ptp.setPathPrefix("foobar");
		return ptp;
	}
}
