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

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.fasterlight.vecmath.*;

/**
 * Represents a view volume, as computed from an OpenGL projection & model
 * matrix. Answers view-volume questions. from "viewcull.c"
 */
public class ViewVolumeD
{
	/* Storage for the six planes, left right top bottom near far */
	double planeEqs[][] = new double[6][4];

	double ocEcMat[] = new double[16];
	double ecCcMat[] = new double[16];
	double ocCcMat[] = new double[16];
	int viewport[] = new int[4];

	double manproj[] = new double[3];

	Matrix4d ocCc = new Matrix4d();

	public double[] getCombinedMatrix()
	{
		return ocCcMat;
	}

	/**
	 * Returns true if point 'p' is inside of plane 'i' (0-5).
	 */
	public boolean isPtInPlane(Vector3d p, int i)
	{
		return distFromPlane(p, i) > 0;
	}

	/**
	 * Returns distance to plane 'i' (0-5).
	 */
	public double distFromPlane(Vector3d p, int i)
	{
		double[] peq = planeEqs[i];
		return (peq[0] * p.x + peq[1] * p.y + peq[2] * p.z + peq[3]);
	}

	/**
	 * Tests if a point lies outside the view volume.
	 */
	public boolean containsPt(Vector3d p)
	{
		for (int i = 0; i < 6; i++)
		{
			if (!isPtInPlane(p, i))
				return false;
		}
		return true;
	}

	/**
	 * Tests if a sphere intersects the view volume -- that is, if it is
	 * "trivially accepted"
	 */
	public boolean intersectsSphere(Vector3d cen, double rad)
	{
		// make bounding box for sphere
		int flags = 0xff;
		Vector3d p = new Vector3d();
		for (int j = 0; j < 8; j++) // box corner
		{
			p.x = cen.x + (((j & 1) == 0) ? rad : -rad);
			p.y = cen.y + (((j & 2) == 0) ? rad : -rad);
			p.z = cen.z + (((j & 4) == 0) ? rad : -rad);
			flags &= getFrustumFlags(p);
			// as soon as we get 2 points on different planes,
			// or 1 inside the frustum, it intersects
			if (flags == 0)
				return true;
		}
		return false; // volume contains no points
	}

	/**
	 * Tests if the view volume includes a sphere -- that is, if it is not
	 * "trivially rejected"
	 */
	public boolean containsSphere(Vector3d cen, double rad)
	{
		// make bounding box for sphere, cull against all planes
		Vector3d p = new Vector3d();
		for (int i = 0; i < 6; i++) // plane #
		{
			int flags = 0;
			for (int j = 0; j < 8; j++) // box corner
			{
				p.x = cen.x + (((j & 1) == 0) ? rad : -rad);
				p.y = cen.y + (((j & 2) == 0) ? rad : -rad);
				p.z = cen.z + (((j & 4) == 0) ? rad : -rad);
				if (!isPtInPlane(p, i))
					return false;
			}
		}
		return true; // volume contains all points
	}

	/**
	 * Tests if a sphere intersects the view volume -- that is, if it is
	 * "trivially accepted"
	 */
	public boolean intersectsBox(Vector3d cen, Vector3d ext)
	{
		// make bounding box for sphere
		int flags = 0xff;
		Vector3d p = new Vector3d();
		for (int j = 0; j < 8; j++) // box corner
		{
			p.x = cen.x + (((j & 1) == 0) ? ext.x : -ext.x);
			p.y = cen.y + (((j & 2) == 0) ? ext.y : -ext.y);
			p.z = cen.z + (((j & 4) == 0) ? ext.z : -ext.z);
			flags &= getFrustumFlags(p);
			// as soon as we get 2 points on different planes,
			// or 1 inside the frustum, it intersects
			if (flags == 0)
				return true;
		}
		return false; // volume contains no points
	}

	/**
	 * Tests if the view volume includes a sphere -- that is, if it is not
	 * "trivially rejected"
	 */
	public boolean containsBox(Vector3d cen, Vector3d ext)
	{
		// make bounding box for sphere, cull against all planes
		Vector3d p = new Vector3d();
		for (int i = 0; i < 6; i++) // plane #
		{
			int flags = 0;
			for (int j = 0; j < 8; j++) // box corner
			{
				p.x = cen.x + (((j & 1) == 0) ? ext.x : -ext.x);
				p.y = cen.y + (((j & 2) == 0) ? ext.y : -ext.y);
				p.z = cen.z + (((j & 4) == 0) ? ext.z : -ext.z);
				if (!isPtInPlane(p, i))
					return false;
			}
		}
		return true; // volume contains all points
	}

	/**
	 * Tests against all 6 planes and returns bitmask with a plane's
	 * corresponding bit set if the point is outside of that plane.
	 */
	public int getFrustumFlags(Vector3d p)
	{
		int flags = 0;
		for (int i = 0; i < 6; i++)
		{
			if (!isPtInPlane(p, i))
				flags |= (1 << i);
		}
		return flags;
	}

	public int checkBoxAgainstPlane(Vector3d cen, Vector3d ext, float maxrad,
			int i)
	{
		// early-out if bounding sphere is not inside of plane
		int flags = checkSphereAgainstPlane(cen, maxrad, i);
		if (flags == 0 || flags == 0xff)
			return flags;
		flags = 0;
		Vector3d p = new Vector3d();
		for (int j = 0; j < 8; j++) // box corner
		{
			p.x = cen.x + (((j & 1) == 0) ? ext.x : -ext.x);
			p.y = cen.y + (((j & 2) == 0) ? ext.y : -ext.y);
			p.z = cen.z + (((j & 4) == 0) ? ext.z : -ext.z);
			if (isPtInPlane(p, i))
				flags |= (1 << j);
		}
		return flags;
	}

	public int checkSphereAgainstPlane(Vector3d cen, float maxrad, int i)
	{
		double planeDist = distFromPlane(cen, i);
		if (planeDist < -maxrad)
			return 0;
		else if (planeDist > maxrad)
			return 0xff; // trivially accepted
		else
			return 0x99; // dont know

	}

	/*
	 * Calculates the six view volume planes in object coordinate (OC) space.
	 *
	 * Algorithm:
	 *
	 * A view volume plane in OC is transformed into CC by multiplying it by the
	 * inverse of the combined ModelView and Projection matrix (M).
	 * Algebraically, this is written: -1 P M = P oc cc
	 *
	 * The resulting six view volume planes in CC are: [ -1 0 0 1 ] [ 1 0 0 1 ] [
	 * 0 -1 0 1 ] [ 0 1 0 1 ] [ 0 0 -1 1 ] [ 0 0 1 1 ]
	 *
	 * To transform the CC view volume planes into OC, we simply multiply the CC
	 * plane equations by the combined ModelView and Projection matrices using
	 * standard vector-matrix multiplication. Algebraically, this is written: P
	 * M = P cc oc
	 *
	 * Since all of the CC plane equation components are 0, 1, or -1, full
	 * vector- matrix multiplication is overkill. For example, the first element
	 * of the first OC plane equation is computed as: A = -1 * m0 + 0 * m1 + 0 *
	 * m2 + 1 * m3 This simplifies to: A = m3 - m0
	 *
	 * Other terms simpliofy similarly. In fact, all six plane equations can be
	 * computed as follows: [ m3-m0 m7-m4 m11-m8 m15-m12 ] [ m3+m0 m7+m4 m11+m8
	 * m15+m12 ] [ m3-m1 m7-m5 m11-m9 m15-m13 ] [ m3+m1 m7+m5 m11+m9 m15+m13 ] [
	 * m3-m2 m7-m6 m11-m10 m15-m14 ] [ m3+m2 m7+m6 m11+m10 m15+m14 ]
	 */
	public void setup(GL gl)
	{
		/* Get the modelview and projection matrices */
		gl.glGetDoublev(GL.GL_MODELVIEW_MATRIX, ocEcMat, 0);
		gl.glGetDoublev(GL.GL_PROJECTION_MATRIX, ecCcMat, 0);
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

		Matrix4d ocEc = GLOUtil.toMatrix4d(ocEcMat);
		Matrix4d ecCc = GLOUtil.toMatrix4d(ecCcMat);
		ocCc.mul(ocEc, ecCc);
		ocCcMat = GLOUtil.toArrayd(ocCc);

		/* Calculate the six OC plane equations. */
		planeEqs[0][0] = ocCcMat[3] - ocCcMat[0];
		planeEqs[0][1] = ocCcMat[7] - ocCcMat[4];
		planeEqs[0][2] = ocCcMat[11] - ocCcMat[8];
		planeEqs[0][3] = ocCcMat[15] - ocCcMat[12];

		planeEqs[1][0] = ocCcMat[3] + ocCcMat[0];
		planeEqs[1][1] = ocCcMat[7] + ocCcMat[4];
		planeEqs[1][2] = ocCcMat[11] + ocCcMat[8];
		planeEqs[1][3] = ocCcMat[15] + ocCcMat[12];

		planeEqs[2][0] = ocCcMat[3] + ocCcMat[1];
		planeEqs[2][1] = ocCcMat[7] + ocCcMat[5];
		planeEqs[2][2] = ocCcMat[11] + ocCcMat[9];
		planeEqs[2][3] = ocCcMat[15] + ocCcMat[13];

		planeEqs[3][0] = ocCcMat[3] - ocCcMat[1];
		planeEqs[3][1] = ocCcMat[7] - ocCcMat[5];
		planeEqs[3][2] = ocCcMat[11] - ocCcMat[9];
		planeEqs[3][3] = ocCcMat[15] - ocCcMat[13];

		planeEqs[4][0] = ocCcMat[3] + ocCcMat[2];
		planeEqs[4][1] = ocCcMat[7] + ocCcMat[6];
		planeEqs[4][2] = ocCcMat[11] + ocCcMat[10];
		planeEqs[4][3] = ocCcMat[15] + ocCcMat[14];

		planeEqs[5][0] = ocCcMat[3] - ocCcMat[2];
		planeEqs[5][1] = ocCcMat[7] - ocCcMat[6];
		planeEqs[5][2] = ocCcMat[11] - ocCcMat[10];
		planeEqs[5][3] = ocCcMat[15] - ocCcMat[14];
	}

	public boolean projectCoordsGLU(GLU glu, Vector3d v)
	{
		if (!glu.gluProject(v.x, v.y, v.z, ocEcMat, 0, ecCcMat, 0, viewport, 0,
				manproj, 0))
			return false;
		v.set(manproj[0], manproj[1], manproj[2]);
		return true;
	}

	public boolean projectCoords(Vector3d v)
	{
		Vector4d v4 = new Vector4d(v.x, v.y, v.z, 1);
		ocCc.transform(v4);
		if (v4.w == 0)
			return false;
		v4.scale(1 / v4.w);
		v.x = viewport[0] + (1 + v4.x) * viewport[2] / 2;
		v.y = viewport[1] + (1 + v4.y) * viewport[3] / 2;
		v.z = (1 + v4.z) / 2;
		return true;
	}

}