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
import java.util.*;

import com.fasterlight.io.IOUtil;
import com.fasterlight.util.FloatList;
import com.fasterlight.vecmath.*;

public class Model3d
implements Serializable
{
	protected FloatList points = new FloatList(); // contains Vector3d objects
	protected FloatList normals = new FloatList(); // normal of vertex to all connected faces
	protected FloatList texcoords = new FloatList(); // U,V texture coordinates

	protected List materials = new ArrayList();
	protected Map materialmap = new HashMap();
	protected List faces = new ArrayList();

	public static final int MAX_ARRAY_SIZE = 0x7ffe;
	public transient boolean removeDups = true;

	// lists the indices of faces for each point
	protected transient short[][] faces_of_pts;

	protected Vector3f minpt = new Vector3f();
	protected Vector3f maxpt = new Vector3f();

	public static final int LUMINOUS = 1;
	public static final int OUTLINE = 2;
	public static final int SMOOTHING = 4;
	public static final int COLOR_HIGHLIGHTS = 8;
	public static final int COLOR_FILTER = 16;
	public static final int OPAQUE_EDGE = 32;
	public static final int TRANSP_EDGE = 64;
	public static final int SHARP_TERM = 128;
	public static final int DOUBLE_SIDED = 256;
	public static final int ADDITIVE = 512;
	public static final int SHADOW_ALPHA = 1024;

	public static final int TEX_X_AXIS = 1;
	public static final int TEX_Y_AXIS = 2;
	public static final int TEX_Z_AXIS = 4;
	public static final int TEX_WORLD = 8;
	public static final int TEX_NEGATIVE = 16;
	public static final int TEX_BLENDING = 32;
	public static final int TEX_ANTIALIAS = 64;

	public static final int CYLINDRICAL_MAP = 0;
	public static final int PLANAR_MAP = 1;

	static final long serialVersionUID = 3308676753023728436L;

	//

	public Model3d()
	{
		// add default material
		Material m = newMaterial("Default");
		addMaterial(m);
	}

	public void addPoint(Vector3f p)
	{
		points.add(p);
		if (points.size()/3 > MAX_ARRAY_SIZE)
			throw new RuntimeException("too many elements in array");
		minpt.x = Math.min(p.x, minpt.x);
		minpt.y = Math.min(p.y, minpt.y);
		minpt.z = Math.min(p.z, minpt.z);
		maxpt.x = Math.max(p.x, maxpt.x);
		maxpt.y = Math.max(p.y, maxpt.y);
		maxpt.z = Math.max(p.z, maxpt.z);
	}

	public void addMaterial(Material m)
	{
		materials.add(m);
		materialmap.put(m.name, m);
	}

	public List getMaterialList()
	{
		return Collections.unmodifiableList(materials);
	}

	public Material getMaterial(String name)
	{
		return (Material)materialmap.get(name);
	}

	public int getNumPoints()
	{
		return points.size()/3;
	}

	public Vector3f getPoint(int i)
	{
		return points.getVector3f(i);
	}

	public Vector3f getNormal(int i)
	{
		return normals.getVector3f(i);
	}

	public int addNormal(Vector3f nml)
	{
		int i = removeDups ? normals.indexOf(nml) : -1;
		if (i < 0)
		{
			normals.add(nml);
			if (normals.size()/3 > MAX_ARRAY_SIZE)
				throw new RuntimeException("too many elements in array");
			return normals.size()/3-1;
		} else {
			return i;
		}
	}

	public int addTexCoord(Vector2f texcoord)
	{
		int i = removeDups ? texcoords.indexOf(texcoord) : -1;
		if (i < 0)
		{
			texcoords.add(texcoord);
			if (texcoords.size()/2 > MAX_ARRAY_SIZE)
				throw new RuntimeException("too many elements in array");
			return texcoords.size()/2-1;
		} else {
			return i;
		}
	}

	public Vector2f getTextureCoordinate(int i)
	{
		return texcoords.getVector2f(i);
	}

	public void addFace(Face f)
	{
		faces.add(f);
	}

	public int getNumFaces()
	{
		return faces.size();
	}

	public Face getFace(int i)
	{
		return (Face)faces.get(i);
	}

	public Vector3f getMinimumPt()
	{
		return minpt;
	}

	public Vector3f getMaximumPt()
	{
		return maxpt;
	}

	//

	public class Face
	implements Serializable
	{
		public short[] vertinds;
		public short[] nmlinds;
		public short[] texinds;
		public short surfidx;
		public Vector3f normal = new Vector3f();

		static final long serialVersionUID = -5133853004606143244L;

		//

		public Face(short[] vertarr, int surfidx)
		{
			this.vertinds = vertarr;
			this.surfidx = (short)surfidx;
			this.nmlinds = new short[vertinds.length];
			this.texinds = new short[vertinds.length];
			for (int i=0; i<vertinds.length; i++)
			{
				nmlinds[i] = -1;
				texinds[i] = -1;
			}
			computeNormal();
		}
		public Face(short[] vertarr, short[] nmlarr, short[] texarr, int surfidx)
		{
			this.vertinds = vertarr;
			this.surfidx = (short)surfidx;
			this.nmlinds = nmlarr;
			this.texinds = texarr;
			computeNormal();
		}
		void computeNormal()
		{
			if (getNumVertices() >= 3)
			{
				Vector3f a = new Vector3f(getVertex(1));
				a.sub(getVertex(0));
				Vector3f b = new Vector3f(getVertex(2));
				b.sub(getVertex(0));
				normal.cross(b,a);
				if (normal.length() > 1e-10)
					normal.normalize();
				else
					normal.set(0,0,1);
			} else {
				normal.set(0,0,1);
			}
		}
		public int getNumVertices()
		{
			return vertinds.length;
		}
		public Vector3f getVertex(int vi)
		{
			return getPoint(vertinds[vi]);
		}
		public Vector2f getTexCoord(int vi)
		{
			int ti = texinds[vi];
			if (ti != -1)
				return getTextureCoordinate(ti);
			else
				return null;
		}
		public int getVertexIndex(int vi)
		{
			return vertinds[vi];
		}
		public Vector3f getVertexNml(int vi)
		{
//			System.out.println(vi + " " + nmlinds[vi] + " " + normal);
			if (nmlinds[vi] < 0)
				return normal;
			else
				return getNormal(nmlinds[vi]);
		}
		boolean containsVertIdx(int vi)
		{
			for (int i=0; i<vertinds.length; i++)
			{
				if (vertinds[i] == vi)
					return true;
			}
			return false;
		}
		public Vector3f getFaceNormal()
		{
			return normal;
		}
		public Material getMaterial()
		{
			return (Material)materials.get(surfidx & 0xffff);
		}
		public String toString()
		{
			StringBuffer st = new StringBuffer();
			st.append("[Face:{");
			for (int i=0; i<vertinds.length; i++)
			{
				if (i>0)
					st.append(',');
				st.append(vertinds[i]);
			}
			st.append("}," + surfidx + "]");
			return st.toString();
		}
		public int getMinVertex()
		{
			// look for the minimum vertex
			int minvi = 99999;
			for (int i=0; i<vertinds.length; i++)
			{
				minvi = Math.min(minvi, vertinds[i]);
			}
			return minvi;
		}
	}

	public class Material
	implements Serializable
	{
		public String name;
		public int idx;
		public int color = 0xffffff;
		public int flags = 0;
		public float maxsmoothang = 0;
		public float diffuse = 1;
		public float luminent, specular, specexp;
		public Vector3f texcen, texsize;
		public int texflags, xwrapmode, ywrapmode;
		public String texname;
		public int projtype = PLANAR_MAP;
		public float fp0 = 1;

		static final long serialVersionUID = -1403869573595276570L;

		//

		public Material(String name, int idx)
		{
			this.name = name;
			this.idx = idx;
		}
		public String toString()
		{
			return "[Material:" + name + "(" + idx + "),color=" + Integer.toString(color,16) +
				",flags=" + flags + ",smooth=" + maxsmoothang + "]";
		}
	}

	public Face newFace(short[] viarr, int surfidx)
	{
		return new Face(viarr, surfidx);
	}

	public Face newFace(short[] viarr, short[] niarr, short[] tiarr, int surfidx)
	{
		return new Face(viarr, niarr, tiarr, surfidx);
	}

	public Material newMaterial(String name)
	{
		return new Material(name, materials.size());
	}

	///

	void prepare()
	{
		triangluateFaces();
		computeFacesOfPoints();
		computeSmoothedNormals();
		computeTextureCoordinates();
		sortFacesByMaterial();
	}

	void prepare2()
	{
		triangluateFaces();
		sortFacesByMaterial();
	}

	void computeTextureCoordinates()
	{
		int nf = getNumFaces();
		for (int fi=0; fi<nf; fi++)
		{
			Face face = getFace(fi);
			Material m = face.getMaterial();
			int nv = face.getNumVertices();
			lasthemi = 99;
			for (int vi=nv-1; vi>=0; vi--)
			{
				Vector3f p = face.getVertex(vi);
				Vector2f tc = getTextureCoord(m, face, p);
				if (tc != null)
					face.texinds[vi] = (short)addTexCoord(tc);
			}
		}
	}

	// fill the faces_of_pts array
	// for each face, iterate over the points
	// add the face index to faces_of_pts[pointidx] array
	void computeFacesOfPoints()
	{
		faces_of_pts = new short[getNumPoints()][];
		for (int j=0; j<faces.size(); j++)
		{
			Face face = getFace(j);
			for (int i=0; i<face.getNumVertices(); i++)
			{
				int vi = face.getVertexIndex(i);
				short[] oldarr = faces_of_pts[vi];
				// add face index (j) to faces_of_pts[vi]
				if (oldarr == null)
				{
					faces_of_pts[vi] = new short[] { (short)j };
				} else {
					short[] newarr = new short[oldarr.length+1];
					System.arraycopy(oldarr, 0, newarr, 0, oldarr.length);
					newarr[oldarr.length] = (short)j;
					faces_of_pts[vi] = newarr;
				}
			}
		}
	}

	void computeSmoothedNormals()
	{
		// iterate thru all faces
		// for each vertex in face, add the face's normal
		// to the vertex's normal
		for (int j=0; j<faces.size(); j++)
		{
			Face face = getFace(j);
			Vector3f facenml = face.getFaceNormal();
			Material mat = face.getMaterial();
			if ((mat.flags & SMOOTHING) != 0)
			{
				// iterate over all points of the face,
				// for each one decide if we should smooth it
				for (int i=0; i<face.getNumVertices(); i++)
				{
					Vector3f newnml = null;
					short[] adjfaceidx = faces_of_pts[face.getVertexIndex(i)];

					for (int k=0; k<adjfaceidx.length; k++)
					{
						// don't iterate over the face in the outer loop
						if (adjfaceidx[k] != j)
						{
							Face adjface = getFace(adjfaceidx[k]);
							Vector3f adjnml = adjface.getFaceNormal();
							double ang = Math.acos(facenml.dot(adjnml));
							// if angle is less than max. smooth angle,
							// add this face's normal to the new normal
							if (ang <= mat.maxsmoothang)
							{
								if (newnml == null)
									newnml = new Vector3f(facenml);
								newnml.add(adjnml);
							}
//							System.out.println(j + " " + i + " " + k + " " + adjfaceidx[k] + ": " + Math.toDegrees(ang));
						}
					}

					// if we did any smoothing, add this normal
					if (newnml != null)
					{
						newnml.normalize();
						face.nmlinds[i] = (short)addNormal(newnml);
//						System.out.println(face.nmlinds[i] + "\tfacenml=" + facenml + "\tnewnml=" + newnml);
					}
				}
			}
		}
	}

	int lasthemi = 99;

	protected Vector2f getTextureCoord(Material mat, Face face, Vector3f v0)
	{
		if (mat.texname == null)
			return null;
		float xx,yy,zz;
		Vector3f v = new Vector3f(v0);
		if (mat.texcen != null)
			v.sub(mat.texcen);
		if (mat.texsize != null)
		{
			v.x /= mat.texsize.x;
			v.y /= mat.texsize.y;
			v.z /= mat.texsize.z;
		}
		switch (mat.texflags & 7)
		{
			case 1: xx = v.y; yy = v.z; zz = v.x; break;
			case 2: xx = v.x; yy = v.z; zz = v.y; break;
			case 4: xx = v.x; yy = v.y; zz = v.z; break;
			default:
				return null;
		}
		switch (mat.projtype)
		{
			case Model3d.CYLINDRICAL_MAP:
				float tx = 0.5f+(float)(Math.atan2(xx,yy)/(Math.PI*2));
				float ty = zz+0.5f;
				tx *= mat.fp0;
				int h = (int)(tx*4);
				if (h == 4)
					h = 3;
				if (h == lasthemi+3) { // going from quad 0 to quad 3?
					tx -= 1;
					h = 0;
				} else if (h+3 == lasthemi) { // going from quad 3 to quad 0?
					tx += 1;
					h = 3;
				}
				lasthemi = h;
				return new Vector2f(tx,ty);
			case Model3d.PLANAR_MAP:
			default:
				//System.out.println(xx + " " + yy + " " + zz);
				return new Vector2f(xx+0.5f,yy-0.5f);
		}
	}

	void triangluateFaces()
	{
		List newfaces = new ArrayList();
		for (int j=0; j<faces.size(); j++)
		{
			Face face = getFace(j);
			int nv = face.getNumVertices();
			if (nv > 3)
			{
				for (int i=0; i<nv-2; i++)
				{
					short[] arr = new short[3];
					arr[0] = face.vertinds[0];
					arr[1] = face.vertinds[i+1];
					arr[2] = face.vertinds[i+2];
					Face newface = newFace(arr, face.surfidx);
					newfaces.add(newface);
				}
			} else
				newfaces.add(face);
		}

		this.faces = newfaces;
	}

	void sortFacesByMaterial()
	{
		Collections.sort(faces, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				Face f1 = (Face)o1;
				Face f2 = (Face)o2;
				int i1 = f1.getMaterial().idx;
				int i2 = f2.getMaterial().idx;
				if (i1 != i2)
					return (i1 - i2);
				else
					return f1.getMinVertex() - f2.getMinVertex();
			}
		});
	}

	public static Model3d loadModel(String fn, boolean debug) throws IOException
	{
		Model3d model;
		if (fn.endsWith(".lwo"))
		{
			DataInputStream din = new DataInputStream(IOUtil.getBinaryResource(fn));
			LWOReader r = new LWOReader(din);
			r.setDebug(debug);
			r.readLWO();
			model = r.getModel();
		} else
		{
			model = (Model3d)IOUtil.readSerializedObject(fn, true);
		}
		return model;
	}
}
