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
import java.util.StringTokenizer;

import com.fasterlight.io.IOUtil;
import com.fasterlight.vecmath.*;

/**
  * This 'sposed to read 3DS files.
  */
public class OBJReader
{
	Model3d model;
	String infilename;
	BufferedReader in;
	Model3d.Material curmat; // current material
	int surfidx = -1; // current material index

	//

	public OBJReader(String infilename)
	throws IOException
	{
		this.infilename = infilename;
		this.in = new BufferedReader(new FileReader(infilename));
		model = new Model3d();
		model.removeDups = false;
	}

	public Model3d getModel()
	{
		return model;
	}

	//

	void readMaterialLib(String filename)
	throws IOException
	{
		String dirname = IOUtil.getDirectoryOf(infilename);
		String newname = dirname + File.separator + filename;
		BufferedReader oldin = this.in;
		in = new BufferedReader(new FileReader(newname));
		readOBJ();
		this.in = oldin;
	}

	public void readOBJ()
	throws IOException
	{
		String line;

		while ((line = in.readLine()) != null)
		{
			line = line.trim();
			if (line.length() == 0 || line.charAt(0) == '#')
				continue;
			StringTokenizer st = new StringTokenizer(line, " \t\n\r");
			String tag = st.nextToken();
			if (tag.equals("mtllib"))
			{
				readMaterialLib(st.nextToken());
			}
			else if (tag.equals("v"))
			{
				Vector3f pt = readVector3f(st);
				model.addPoint(pt);
			}
			else if (tag.equals("vn"))
			{
				Vector3f pt = readVector3f(st);
				model.addNormal(pt);
			}
			else if (tag.equals("vt"))
			{
				Vector2f pt = readVector2f(st);
				model.addTexCoord(pt);
			}
			else if (tag.equals("f"))
			{
				int ninds = st.countTokens();
				short[] varr = new short[ninds];
				short[] narr = new short[ninds];
				short[] tarr = new short[ninds];
				for (int i=ninds-1; i>=0; i--)
//				for (int i=0; i<ninds; i++)
				{
					String word = st.nextToken();
					int pos = word.indexOf('/');
					varr[i] = (short)(Integer.parseInt(word.substring(0,pos))-1);
					int pos2 = word.indexOf('/', pos+1);
					String tistr = word.substring(pos+1,pos2);
					if (tistr.length() != 0)
						tarr[i] = (short)(Integer.parseInt(word.substring(pos+1,pos2))-1);
					else
						tarr[i] = -1;
					narr[i] = (short)(Integer.parseInt(word.substring(pos2+1))-1);
				}
				Model3d.Face f = model.newFace(varr, narr, tarr, surfidx);
				model.addFace(f);
			}
			else if (tag.equals("usemtl"))
			{
				String mname = st.nextToken();
				Model3d.Material m = model.getMaterial(mname);
				if (m != null)
				{
					curmat = m;
					surfidx = m.idx;
					System.out.println("usemtl: " + m);
				} else
					System.out.println("Material '" + mname + "'");
			}
			else if (tag.equals("newmtl"))
			{
				surfidx = model.materials.size();
				String mname = st.nextToken();
				curmat = model.newMaterial(mname);
//				curmat.flags |= Model3d.DOUBLE_SIDED;
				model.addMaterial(curmat);
			}
			else if (tag.equals("Kd"))
			{
				Vector3f colf = readVector3f(st);
				int r = (int)(colf.x*255.45f);
				int g = (int)(colf.y*255.45f);
				int b = (int)(colf.z*255.45f);
				curmat.color = (r&0xff) + ((g&0xff)<<8) + ((b&0xff)<<16);
				curmat.diffuse = 1.0f;
			}
			else if (tag.equals("map_Kd"))
			{
				curmat.texname = st.nextToken().toLowerCase();
				if (curmat.color == 0)
					curmat.color = 0xffffff;
			}
			else
				System.out.println("Line '" + line + "' not recognized");
		}
		in.close();

		model.prepare2();
	}

	Vector3f readVector3f(StringTokenizer st)
	{
		return new Vector3f(
			Float.parseFloat(st.nextToken()),
			Float.parseFloat(st.nextToken()),
			Float.parseFloat(st.nextToken())
		);
	}

	Vector2f readVector2f(StringTokenizer st)
	{
		return new Vector2f(
			Float.parseFloat(st.nextToken()),
			Float.parseFloat(st.nextToken())
		);
	}

	//

	public static void main(String[] args)
	throws Exception
	{
		// process args
		String outfilename = null;
		String infilename = null;
		boolean debug = false;

		for (int i=0; i<args.length; i++)
		{
			if (args[i].equals("-d"))
				debug = true;
			else if (args[i].equals("-o"))
				outfilename = args[++i];
			else
				infilename = args[i];
		}

		infilename = infilename.replace('_',' ');
		if (outfilename != null)
			outfilename = outfilename.replace('_',' ');
		OBJReader r = new OBJReader(infilename);
//		r.setDebug(debug);

		System.out.println("Reading `" + infilename + "'...");

		r.readOBJ();

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
			IOUtil.writeSerializedObject(outfilename, m);
		}

		System.out.println("Done.");
	}
}
