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

public class ModelRendererVtxArr
extends ModelRenderer
{
	public ModelRendererVtxArr(Model3d model, GL gl)
	{
		super(model, gl);
	}

	public ModelRendererVtxArr(Model3d model, GL gl, int flags)
	{
		super(model, gl, flags);
	}

	public ModelRendererVtxArr(Model3d model, GL gl, TextureCache texcache, int flags)
	{
		super(model, gl, texcache, flags);
	}


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

	float[] getVertexArrayFromList(List list)
	{
		int n = list.size();
		float[] arr = new float[n*3];
		for (int i=0; i<n; i++)
		{
			Vector3f v = (Vector3f)list.get(i);
			arr[i*3+0] = v.x;
			arr[i*3+1] = v.y;
			arr[i*3+2] = v.z;
		}
		return arr;
	}

	protected void renderGL()
	{
		gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_LIGHTING_BIT);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL.GL_TEXTURE_2D);

		texenable = false;
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
					/**
					case 3:
						renderTri(face, m, 2, 1, 0);
						break;
					case 4:
						renderTri(face, m, 2, 1, 0);
						renderTri(face, m, 3, 2, 0);
						break;
					**/
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
