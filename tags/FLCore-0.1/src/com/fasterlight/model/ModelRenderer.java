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

import java.util.*;

import javax.media.opengl.GL;

import com.fasterlight.glout.TextureCache;
import com.fasterlight.vecmath.*;

public class ModelRenderer
{
	Model3d model;
	GL gl;
	int flags;
	TextureCache texcache;

	int dlist = -1;
	boolean texenable;
	int curprim = -1; // current primitive (param to glBegin)
	int vi1, vi2, vi3; // last 3 tri indices

	public static final int WIREFRAME = 1;
	public static final int NO_NORMALS = 2;
	public static final int NO_MATERIALS = 4;
	public static final int NO_TEXTURES = 8;
	public static final int COLORS = 16;
	public static final int BEAUTIFUL = 32;
	public static final int NO_LOAD_TEXTURES = 64;

	//

	public ModelRenderer(Model3d model, GL gl)
	{
		this.model = model;
		this.gl = gl;
	}

	public ModelRenderer(Model3d model, GL gl, int flags)
	{
		this(model, gl);
		setOptions(flags);
	}

	public ModelRenderer(Model3d model, GL gl, TextureCache texcache, int flags)
	{
		this(model, gl);
		setTextureCache(texcache);
		setOptions(flags);
	}

	public void setTextureCache(TextureCache texcache)
	{
		this.texcache = texcache;
	}

	public void setOptions(int flags)
	{
		this.flags = flags;
	}

	public void render()
	{
		if (dlist < 0)
		{
			prepareMaterials();
			dlist = gl.glGenLists(1);
			gl.glNewList(dlist, GL.GL_COMPILE);
			renderGL();
			gl.glEndList();
		}
		gl.glCallList(dlist);
	}

	// todo: much optimization
	// todo: textures

	protected float[] getFloatArr(int c, float s)
	{
		float[] col = new float[4];
		col[0] = (c&0xff)*s/255f;
		col[1] = ((c&0xff00)>>8)*s/255f;
		col[2] = ((c&0xff0000)>>16)*s/255f;
		col[3] = 1f;
		return col;
	}

	protected void setMaterial(int surfaces, int type, int c, float s)
	{
		float[] col = getFloatArr(c, s);
		gl.glMaterialfv(surfaces, type, col, 0);
	}

	protected void setupMaterial(Model3d.Material m)
	{
		if ((flags & COLORS) != 0)
		{
			gl.glColor4fv(getFloatArr(m.color, 1), 0);
		} else {
			setMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE, m.color, m.diffuse);
			setMaterial(GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, m.color, m.luminent);
			gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, m.specexp);
			if (m.specexp > 0)
				setMaterial(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, -1, m.specular);
		}
		if ((m.flags & Model3d.DOUBLE_SIDED) != 0)
			gl.glDisable(GL.GL_CULL_FACE);
		else
			gl.glEnable(GL.GL_CULL_FACE);
		if ((flags & NO_TEXTURES) == 0)
		{
			if (m.texname == null || texcache == null)
			{
				if (texenable)
				{
					gl.glDisable(GL.GL_TEXTURE_2D);
					texenable = false;
				}
			} else {
				if ((flags & NO_LOAD_TEXTURES) == 0)
				{
					int res = texcache.setTexture(m.texname);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, m.xwrapmode<2?GL.GL_CLAMP:GL.GL_REPEAT);
					gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, m.ywrapmode<2?GL.GL_CLAMP:GL.GL_REPEAT);
				}
				if (!texenable)
				{
					gl.glEnable(GL.GL_TEXTURE_2D);
					texenable = true;
				}
			}
		}
	}

	// this is cylindrical map
	// todo: add other map types

	protected void renderVertex(Model3d.Face face, Model3d.Material m, int vi)
	{
		Vector3f p = face.getVertex(vi);
		if ( (flags & NO_NORMALS) == 0 )
		{
			Vector3f n = face.getVertexNml(vi);
			gl.glNormal3f(n.x, n.y, n.z);
		}
		if ( m != null && (flags & NO_TEXTURES) == 0 )
		{
			Vector2f t = face.getTexCoord(vi);
			if (t != null)
			{
				gl.glTexCoord2f(t.x, t.y);
			}
		}
		gl.glVertex3f(p.x, p.y, p.z);
	}

/**
	protected void renderTri(Model3d.Face face, Model3d.Material m, int v1, int v2, int v3)
	{
		if (curprim < 0)
		{
			beginGL(GL.GL_TRIANGLE_STRIP);
			renderVertex(face, m, v1);
			renderVertex(face, m, v2);
			renderVertex(face, m, v3);
		}
	}
**/

	void endGL()
	{
		if (curprim >= 0)
		{
			gl.glEnd();
			curprim = vi1 = vi2 = vi3 = -1;
		}
	}

	void beginGL(int prim)
	{
		endGL();
		curprim = prim;
		gl.glBegin(prim);
	}

	protected void renderGL()
	{
		gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_LIGHTING_BIT | GL.GL_TEXTURE_BIT | GL.GL_COLOR_BUFFER_BIT);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL.GL_TEXTURE_2D);
		texenable = false;
		curprim = vi1 = vi2 = vi3 = -1;
		int nf = model.getNumFaces();
		Model3d.Material lastmat = null;

		for (int fi=0; fi<nf; fi++)
		{
			Model3d.Face face = model.getFace(fi);
			Model3d.Material m = face.getMaterial();
			if (m != null && m != lastmat && ( (flags & NO_MATERIALS) == 0) )
			{
				endGL();
				setupMaterial(m);
			}
			int nv = face.getNumVertices();

			if (nv < 3 || (flags & WIREFRAME) != 0 )
			{
				beginGL(GL.GL_LINE_STRIP);
				for (int vi=nv-1; vi>=0; vi--)
				{
					renderVertex(face, m, vi);
				}
				if (nv >= 3)
					renderVertex(face, m, nv-1);
				endGL();
			} else {
				switch (nv)
				{
					case 3:
						if (curprim != GL.GL_TRIANGLES)
							beginGL(GL.GL_TRIANGLES);
						for (int vi=2; vi>=0; vi--)
							renderVertex(face, m, vi);
						break;
					default:
						beginGL(GL.GL_POLYGON);
						// we iterate points in reverse for lightwave :-p
						for (int vi=nv-1; vi>=0; vi--)
						{
							renderVertex(face, m, vi);
						}
						endGL();
//						System.out.println("rendered " + nv + " tris");
						break;
				}
			}

			if (m != null)
				lastmat = m;
		}

		endGL();
		gl.glPopAttrib();
	}

	// we have to do this because we don't want glTexImage calls
	// inside of a display list!
	protected void prepareMaterials()
	{
		texenable = false;
		List matlist = model.getMaterialList();
		Iterator it = matlist.iterator();
		while (it.hasNext())
		{
			Model3d.Material mat = (Model3d.Material)it.next();
			setupMaterial(mat);
		}
	}

	public int hashCode()
	{
		return (model.hashCode() ^ flags);
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof ModelRenderer))
			return false;
		ModelRenderer mr = (ModelRenderer)o;
		return ( model.equals(mr.model) && flags == mr.flags );
	}

}
