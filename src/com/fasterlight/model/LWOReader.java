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
package com.fasterlight.model;

import java.io.*;

import com.fasterlight.io.*;
import com.fasterlight.vecmath.Vector3f;

/**
  * This 'sposed to read Lightwave LWO objects.
  */
public class LWOReader extends IFFReader
{
	// Luminous, Outline, Smoothing, Color Highlights, Color Filter,
	// Opaque Edge, Transparent Edge, Sharp Terminator, Double Sided, Additive, and Shadow Alpha

	Model3d model;

	//

	public LWOReader(DataInputStream in)
	{
		super(in);
		model = new Model3d();
	}

	public Model3d getModel()
	{
		return model;
	}

	static final int SRFS = str2int4("SRFS");
	static final int PNTS = str2int4("PNTS");
	static final int POLS = str2int4("POLS");
	static final int SURF = str2int4("SURF");

	public void readLWO() throws IOException
	{
		String formtype = open();
		if (!formtype.equals("LWOB"))
			throw new IOException("Expected LWOB, got " + formtype);

		// read chunks until we recognize one
		while (bytesLeft() > 0)
		{
			IFFChunk chunk = enterChunk();
			if (debug)
				System.out.println(chunk);
			int type = chunk.getTypeInt();
			if (type == SRFS)
			{
				while (bytesLeft() > 0)
				{
					// todo: addpoint method
					Model3d.Material m = readMaterial();
					if (debug)
						System.out.println(m);
					model.addMaterial(m);
				}
			} else if (type == PNTS)
			{
				while (bytesLeft() > 0)
				{
					// todo: addpoint method
					Vector3f point = readPoint();
					point.x *= -1; // this is b/c lightwave is annoying
					model.addPoint(point);
				}
			} else if (type == POLS)
			{
				while (bytesLeft() > 0)
				{
					Model3d.Face face = readFace();
					model.faces.add(face);
				}
			} else if (type == SURF)
			{
				Model3d.Material m = readMaterialDef();
				if (debug)
					System.out.println(m);
			}
			exitChunk();
		}

		close();
		model.prepare();
	}

	Vector3f readPoint() throws IOException
	{
		float x = readFloat();
		float y = readFloat();
		float z = readFloat();
		return new Vector3f(x, y, z);
	}

	Model3d.Face readFace() throws IOException
	{
		int np = readUshort();
		short[] arr = new short[np];
		for (int i = 0; i < np; i++)
		{
			int vi = readUshort();
			arr[i] = (short) vi;
		}
		int surfidx = readUshort();
		Model3d.Face f = model.newFace(arr, surfidx);
		return f;
	}

	Model3d.Material readMaterial() throws IOException
	{
		String name = readString0();
		Model3d.Material m = model.newMaterial(name);
		return m;
	}

	Vector3f readVector3f() throws IOException
	{
		return new Vector3f(readFloat(), readFloat(), readFloat());
	}

	static final int COLR = str2int4("COLR");
	static final int FLAG = str2int4("FLAG");
	static final int SMAN = str2int4("SMAN");
	static final int VLUM = str2int4("VLUM");
	static final int VDIF = str2int4("VDIF");
	static final int VSPC = str2int4("VSPC");
	static final int VRFL = str2int4("VRFL");
	static final int VTRN = str2int4("VTRN");
	static final int GLOS = str2int4("GLOS");

	static final int TIMG = str2int4("TIMG");
	static final int TWRP = str2int4("TWRP");
	static final int TFLG = str2int4("TFLG");
	static final int TSIZ = str2int4("TSIZ");
	static final int TCTR = str2int4("TCTR");
	static final int TAAS = str2int4("TAAS");
	static final int TCLR = str2int4("TCLR");
	static final int TFP0 = str2int4("TFP0");

	static final int CTEX = str2int4("CTEX");
	static final int DTEX = str2int4("DTEX");

	Model3d.Material readMaterialDef() throws IOException
	{
		String name = readString0();
		Model3d.Material m = model.getMaterial(name);

		while (bytesLeft() > 0)
		{
			IFFChunk chunk = enterSubChunk();
			int type = chunk.getTypeInt();
			if (type == COLR)
			{
				int i = readInt();
				m.color = flipint(i);
			} else if (type == FLAG)
			{
				m.flags = readUshort();
			} else if (type == SMAN)
			{
				m.maxsmoothang = readFloat();
			} else if (type == VDIF)
			{
				m.diffuse = readFloat();
			} else if (type == VLUM)
			{
				m.luminent = readFloat();
			} else if (type == VSPC)
			{
				m.specular = readFloat();
			} else if (type == GLOS)
			{
				m.specexp = readShort() / 8f;
			} else if (type == TIMG)
			{
				String s = readString0();
				int p = s.lastIndexOf('/');
				if (p < 0)
					p = s.lastIndexOf('\\');
				if (p >= 0)
					s = s.substring(p + 1);
				m.texname = s;
			} else if (type == TWRP)
			{
				m.xwrapmode = readUshort();
				m.ywrapmode = readUshort();
			} else if (type == TFLG)
			{
				m.texflags = readUshort();
			} else if (type == TSIZ)
			{
				m.texsize = readVector3f();
				m.texsize.x *= -1;
			} else if (type == TCTR)
			{
				m.texcen = readVector3f();
				m.texcen.x *= -1;
			} else if (type == CTEX || type == DTEX)
			{
				String s = readString0();
				if (s.indexOf("Cylind") >= 0)
					m.projtype = Model3d.CYLINDRICAL_MAP;
				else if (s.indexOf("Planar") >= 0)
					m.projtype = Model3d.PLANAR_MAP;
			} else if (type == TFP0)
			{
				m.fp0 = readFloat();
			}
			exitChunk();
		}

		return m;
	}

	public static void main(String[] args) throws Exception
	{
		// process args
		String outfilename = null;
		String infilename = null;
		boolean debug = false;
		String[] infilelist = null;

		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-d"))
				debug = true;
			else if (args[i].equals("-o"))
				outfilename = args[++i];
			else if (args[i].equals("-m"))
			{
				// multiple files
				infilelist = new String[args.length-i-1];
				System.arraycopy(args, i+1, infilelist, 0, infilelist.length);
			} else
				infilename = args[i];
		}

		//		infilename = infilename.replace('_',' ');
		//		if (outfilename != null)
		//			outfilename = outfilename.replace('_',' ');

		if (infilelist != null)
		{
			for (int i=0; i<infilelist.length; i++)
			{
				String infile = infilelist[i];
				String outfile = infile.substring(0, infile.length()-4) + ".esm";
				convertLWOtoESM(outfile, infile, debug);
			}
		} else
			convertLWOtoESM(outfilename, infilename, debug);
	}

	private static void convertLWOtoESM(
		String outfilename,
		String infilename,
		boolean debug)
		throws FileNotFoundException, IOException
	{
		DataInputStream din =
			new DataInputStream(new FileInputStream(infilename));
		LWOReader r = new LWOReader(din);
		r.setDebug(debug);

		System.out.println("Reading `" + infilename + "'...");

		r.readLWO();

		Model3d m = r.getModel();
		Vector3f minpt = m.getMinimumPt();
		Vector3f maxpt = m.getMaximumPt();
		System.out.println("min=" + minpt);
		System.out.println("max=" + maxpt);
		Vector3f v = new Vector3f(maxpt);
		v.sub(minpt);
		System.out.println("dim=" + v);

		System.out.println("Preparing...");

		m.prepare();
		/*
				for (int i=0; i<m.faces.size(); i++)
				{
					Model3d.Face f = (Model3d.Face)m.faces.get(i);
					System.out.println(i + "\t" + f);
				}
		*/
		if (outfilename != null)
		{
			System.out.println("Writing to `" + outfilename + "'...");
			IOUtil.writeSerializedObject(outfilename, m, true);
			long lastmod = new File(infilename).lastModified();
			new File(outfilename).setLastModified(lastmod);
		}

		System.out.println("Done.");
	}
}
