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

import java.io.PrintStream;

import javax.media.opengl.GL;

import com.fasterlight.util.Plane4f;
import com.fasterlight.vecmath.*;

/**
  * Represents a view volume, as computed from an OpenGL
  * projection & model matrix.  Answers view-volume questions.
  * from "viewcull.c"
  */
public class ViewVolume
{
	/* Storage for the six planes, left right top bottom near far */
	float planeEqs[][] = new float[6][4];

	/* This mask tells how many planes to test */
	byte numPlanes = 6;
	byte frustumMask = 0x3f;

	public static final int LEFT		= 1;
	public static final int RIGHT 	= 2;
	public static final int TOP		= 4;
	public static final int BOTTOM	= 8;
	public static final int NEAR		= 16;
	public static final int FAR		= 32;

	private Vector3f tmp1f = new Vector3f();
	private Vector3d tmp1d = new Vector3d();

	//

	public void setNumPlanes(int np)
	{
		if (np < 0 || np > 6)
			throw new IllegalArgumentException("R U crazy? " + np);
		this.numPlanes = (byte)np;
		this.frustumMask = (byte)((1<<np)-1);
	}

	/**
	  * Returns true if point 'p' is inside of plane 'i' (0-5).
	  */
	public boolean isPtInPlane(Vector3f p, int i)
	{
		return distFromPlane(p, i) > 0;
	}

	/**
	  * Returns distance to plane 'i' (0-5).
	  */
	public float distFromPlane(Vector3f p, int i)
	{
		float[] peq = planeEqs[i];
		return (peq[0]*p.x + peq[1]*p.y + peq[2]*p.z + peq[3]);
	}

	/**
	  * Tests if a point lies outside the view volume.
	  */
	public boolean containsPt(Vector3f p)
	{
		for (int i=0; i<numPlanes; i++)
		{
			if (!isPtInPlane(p, i))
				return false;
		}
		return true;
	}


	/**
	  * Tests if a sphere intersects the view volume -- that is,
	  * if it is "trivially accepted"
	  */
	public boolean intersectsSphere(Vector3f cen, float rad)
	{
		// make bounding box for sphere
		int flags=0xff;
		Vector3d p = tmp1d;
		for (int j=0; j<8; j++) //box corner
		{
			p.x = cen.x + (((j&1)==0) ? rad : -rad);
			p.y = cen.y + (((j&2)==0) ? rad : -rad);
			p.z = cen.z + (((j&4)==0) ? rad : -rad);
			flags &= getFrustumFlags(p);
			// as soon as we get 2 points on different planes,
			// or 1 inside the frustum, it intersects
			if (flags == 0)
				return true;
		}
		return false; // volume contains no points
	}


	/**
	  * Tests if the view volume includes a sphere -- that is,
	  * if it is "completely accepted"
	  */
	public boolean containsSphere(Vector3f cen, float rad)
	{
		// make bounding box for sphere, cull against all planes
		Vector3f p = tmp1f;
		for (int i=0; i<numPlanes; i++) //plane #
		{
			int flags=0;
			for (int j=0; j<8; j++) //box corner
			{
				p.x = cen.x + (((j&1)==0) ? rad : -rad);
				p.y = cen.y + (((j&2)==0) ? rad : -rad);
				p.z = cen.z + (((j&4)==0) ? rad : -rad);
				if (!isPtInPlane(p, i))
					return false;
			}
		}
		return true; // volume contains all points
	}

	/**
	  * Tests against all numPlanes planes and returns bitmask
	  * with a plane's corresponding bit set if the point
	  * is outside of that plane.
	  */
	public int getFrustumFlags(Vector3f p)
	{
		int flags=0;
		for (int i=0; i<numPlanes; i++)
		{
			if (!isPtInPlane(p, i))
				flags |= (1<<i);
		}
		return flags;
	}

	// double-precision routines

	public boolean isPtInPlane(Vector3d p, int i)
	{
		return distFromPlane(p, i) > 0;
	}

	public double distFromPlane(Vector3d p, int i)
	{
		float[] peq = planeEqs[i];
		return (peq[0]*p.x + peq[1]*p.y + peq[2]*p.z + peq[3]);
	}

	public boolean containsPt(Vector3d p)
	{
		for (int i=0; i<numPlanes; i++)
		{
			if (!isPtInPlane(p, i))
				return false;
		}
		return true;
	}

	public boolean intersectsSphere(Vector3d cen, double rad)
	{
		// make bounding box for sphere
		int flags=0xff;
		Vector3d p = tmp1d;
		for (int j=0; j<8; j++) //box corner
		{
			p.x = cen.x + (((j&1)==0) ? rad : -rad);
			p.y = cen.y + (((j&2)==0) ? rad : -rad);
			p.z = cen.z + (((j&4)==0) ? rad : -rad);
			flags &= getFrustumFlags(p);
			// as soon as we get 2 points on different planes,
			// or 1 inside the frustum, it intersects
			if (flags == 0)
				return true;
		}
		return false;
	}

	public boolean containsSphere(Vector3d cen, double rad)
	{
		// make bounding box for sphere, cull against all planes
		Vector3d p = tmp1d;
		for (int i=0; i<numPlanes; i++) //plane #
		{
			int flags=0;
			for (int j=0; j<8; j++) //box corner
			{
				p.x = cen.x + (((j&1)==0) ? rad : -rad);
				p.y = cen.y + (((j&2)==0) ? rad : -rad);
				p.z = cen.z + (((j&4)==0) ? rad : -rad);
				if (!isPtInPlane(p, i))
				{
					flags |= (1<<j);
					if (flags == 0xff)
						return false; // all points trivially culled
				}
			}
		}
		return true; // volume contains all points
	}

	public int getFrustumFlags(Vector3d p)
	{
		int flags=0;
		for (int i=0; i<numPlanes; i++)
		{
			if (!isPtInPlane(p, i))
				flags |= (1<<i);
		}
		return flags;
	}

	/* Calculates the six view volume planes in object coordinate (OC) space.

       Algorithm:

       A view volume plane in OC is transformed into CC by multiplying it by
       the inverse of the combined ModelView and Projection matrix (M).
       Algebraically, this is written:
              -1
         P   M   = P
          oc        cc

       The resulting six view volume planes in CC are:
         [ -1  0  0  1 ]
         [  1  0  0  1 ]
         [  0 -1  0  1 ]
         [  0  1  0  1 ]
         [  0  0 -1  1 ]
         [  0  0  1  1 ]

       To transform the CC view volume planes into OC, we simply multiply
       the CC plane equations by the combined ModelView and Projection matrices
       using standard vector-matrix multiplication. Algebraically, this is written:
         P   M = P
          cc      oc

       Since all of the CC plane equation components are 0, 1, or -1, full vector-
       matrix multiplication is overkill. For example, the first element of the
       first OC plane equation is computed as:
         A = -1 * m0 + 0 * m1 + 0 * m2 + 1 * m3
       This simplifies to:
         A = m3 - m0

       Other terms simpliofy similarly. In fact, all six plane equations can be
       computed as follows:
         [ m3-m0  m7-m4  m11-m8  m15-m12 ]
         [ m3+m0  m7+m4  m11+m8  m15+m12 ]
         [ m3-m1  m7-m5  m11-m9  m15-m13 ]
         [ m3+m1  m7+m5  m11+m9  m15+m13 ]
         [ m3-m2  m7-m6  m11-m10 m15-m14 ]
         [ m3+m2  m7+m6  m11+m10 m15+m14 ]
     */
	public void setup(GL gl)
	{
		float ocEcMat[] = new float[16];
		float ecCcMat[] = new float[16];
		float ocCcMat[] = new float[16];

		/* Get the modelview and projection matrices */
	   gl.glGetFloatv (GL.GL_MODELVIEW_MATRIX, ocEcMat, 0);
	   gl.glGetFloatv (GL.GL_PROJECTION_MATRIX, ecCcMat, 0);
/*
	   for (int i=0; i<16; i++)
		   System.out.print(ocEcMat[i] + " ");
		System.out.println();
		for (int i=0; i<16; i++)
		   System.out.print(ecCcMat[i] + " ");
		System.out.println();
*/
	   Matrix4d ocEc = GLOUtil.toMatrix4d(ocEcMat);
	   Matrix4d ecCc = GLOUtil.toMatrix4d(ecCcMat);
	   Matrix4d ocCc = new Matrix4d();
	   ocCc.mul(ocEc, ecCc);
	   ocCcMat = GLOUtil.toArray(ocCc);
/*
		for (int i=0; i<16; i++)
		   System.out.print(ocCcMat[i] + " ");
		System.out.println("\n-");
*/
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

      for (int i=0; i<6; i++)
      	normalizePlane(i);

	}

	public void setPlane(int i, Plane4f plane)
	{
		planeEqs[i][0] = plane.x;
		planeEqs[i][1] = plane.y;
		planeEqs[i][2] = plane.z;
		planeEqs[i][3] = plane.w;
		normalizePlane(i);
	}

	private void normalizePlane(int i)
	{
		float[] arr = planeEqs[i];
		float x = arr[0];
		float y = arr[1];
		float z = arr[2];
		float w = arr[3];
		double len = Math.sqrt(x*x+y*y+z*z+w*w);
		if (len > 0)
		{
			double r = 1/len;
			arr[0] = (float)(x*r);
			arr[1] = (float)(y*r);
			arr[2] = (float)(z*r);
			arr[3] = (float)(w*r);
		}
	}

	public void setup(Plane4f[] planes)
	{
		if (planes.length != 6)
			throw new IllegalArgumentException("Need 6 planes, not " + planes.length);
		for (int i=0; i<6; i++)
		{
			setPlane(i, planes[i]);
		}
	}

	public void printStats(PrintStream out)
	{
		for (int p=0; p<6; p++)
		{
			System.out.println("Plane " + p + ":\t" +
				planeEqs[p][0] + '\t' + planeEqs[p][1]	+ '\t' +
				planeEqs[p][2] + '\t' + planeEqs[p][3]);
		}
	}

}
