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

import java.io.*;

import javax.media.opengl.GL;

import com.fasterlight.vecmath.*;

/**
  * Various GLOUT utils
  */
public class GLOUtil
{
	/**
	  * Don't instantiate!
	  */
	private GLOUtil()
	{
	}

	public static double sqr(double x)
	{
		return x*x;
	}

	public static int sign2(double x)
	{
		return (x<0)?-1:1;
	}

	public static int sign(double x)
	{
		return (x<0)?-1:(x>0)?1:0;
	}

	public static float[][] to2dArray(Matrix3d m1)
	{
		float[][] m = new float[4][4];
		for (int y=0; y<3; y++)
			for (int x=0; x<3; x++)
			{
				m[y][x] = (float)(m1.getElement(y,x));
			}
		m[3][3] = 1f;
		return m;
	}

	public static float[][] to2dArray(Matrix3f m1)
	{
		float[][] m = new float[4][4];
		for (int y=0; y<3; y++)
			for (int x=0; x<3; x++)
			{
				m[y][x] = m1.getElement(y,x);
			}
		m[3][3] = 1f;
		return m;
	}

	public static float[] toArray(Matrix3f m1)
	{
		float[] m = new float[4*4];
		for (int y=0; y<3; y++)
			for (int x=0; x<3; x++)
			{
				m[y*4+x] = m1.getElement(y,x);
			}
		m[15] = 1f;
		return m;
	}

	public static float[] toArray(Matrix3d m1)
	{
		float[] m = new float[4*4];
		for (int y=0; y<3; y++)
			for (int x=0; x<3; x++)
			{
				m[y*4+x] = (float)m1.getElement(y,x);
			}
		m[15] = 1f;
		return m;
	}

	public static float[] toArray(Matrix4f m1)
	{
		float[] m = new float[4*4];
		for (int y=0; y<4; y++)
			for (int x=0; x<4; x++)
			{
				m[y*4+x] = m1.getElement(y,x);
			}
		return m;
	}

	public static float[] toArray(Matrix4d m1)
	{
		float[] m = new float[4*4];
		for (int y=0; y<4; y++)
			for (int x=0; x<4; x++)
			{
				m[y*4+x] = (float)m1.getElement(y,x);
			}
		return m;
	}

	public static Matrix4f toMatrix4(float[] m)
	{
		Matrix4f mat = new Matrix4f();
		for (int y=0; y<4; y++)
			for (int x=0; x<4; x++)
			{
				mat.setElement(y,x,m[y*4+x]);
			}
		return mat;
	}

	public static Matrix4f getGLMatrix4(GL gl, int type)
	{
		float[] m = new float[16];
		gl.glGetFloatv(type, m, 0);
		return toMatrix4(m);
	}

	public static Matrix3f toMatrix3(float[] m)
	{
		Matrix3f mat = new Matrix3f();
		for (int y=0; y<3; y++)
			for (int x=0; x<3; x++)
			{
				mat.setElement(y,x,m[y*3+x]);
			}
		return mat;
	}

	public static Matrix3f getGLMatrix3(GL gl, int type)
	{
		float[] m = new float[16];
		gl.glGetFloatv(type, m, 0);
		return toMatrix3(m);
	}

	//

	public static double[] toArrayd(Matrix3f m1)
	{
		double[] m = new double[4*4];
		for (int y=0; y<3; y++)
			for (int x=0; x<3; x++)
			{
				m[y*4+x] = m1.getElement(y,x);
			}
		m[15] = 1f;
		return m;
	}

	public static double[] toArrayd(Matrix3d m1)
	{
		double[] m = new double[4*4];
		for (int y=0; y<3; y++)
			for (int x=0; x<3; x++)
			{
				m[y*4+x] = m1.getElement(y,x);
			}
		m[15] = 1f;
		return m;
	}

	public static double[] toArrayd(Matrix4f m1)
	{
		double[] m = new double[4*4];
		for (int y=0; y<4; y++)
			for (int x=0; x<4; x++)
			{
				m[y*4+x] = m1.getElement(y,x);
			}
		return m;
	}

	public static double[] toArrayd(Matrix4d m1)
	{
		double[] m = new double[4*4];
		for (int y=0; y<4; y++)
			for (int x=0; x<4; x++)
			{
				m[y*4+x] = m1.getElement(y,x);
			}
		return m;
	}

	public static Matrix4d toMatrix4d(double[] m)
	{
		Matrix4d mat = new Matrix4d();
		for (int y=0; y<4; y++)
			for (int x=0; x<4; x++)
			{
				mat.setElement(y,x,m[y*4+x]);
			}
		return mat;
	}

	public static Matrix4d toMatrix4d(float[] m)
	{
		Matrix4d mat = new Matrix4d();
		for (int y=0; y<4; y++)
			for (int x=0; x<4; x++)
			{
				mat.setElement(y,x,m[y*4+x]);
			}
		return mat;
	}

	public static Matrix4d getGLMatrix4d(GL gl, int type)
	{
		double[] m = new double[16];
		gl.glGetDoublev(type, m, 0);
		return toMatrix4d(m);
	}

	public static Matrix3d toMatrix3d(double[] m)
	{
		Matrix3d mat = new Matrix3d();
		for (int y=0; y<3; y++)
			for (int x=0; x<3; x++)
			{
				mat.setElement(y,x,m[y*3+x]);
			}
		return mat;
	}

	public static Matrix3d getGLMatrix3d(GL gl, int type)
	{
		double[] m = new double[16];
		gl.glGetDoublev(type, m, 0);
		return toMatrix3d(m);
	}

	private static float[] matrixArrayf = new float[16];
	static { matrixArrayf[15] = 1.0f; }

	public static void glMultMatrixf(GL gl, Matrix3f mat)
	{
		float[] arr = matrixArrayf;
		arr[0]=mat.m00; arr[1]=mat.m01;  arr[2]=mat.m02;
		arr[4]=mat.m10; arr[5]=mat.m11;  arr[6]=mat.m12;
		arr[8]=mat.m20; arr[9]=mat.m21; arr[10]=mat.m22;
		arr[15]=1;
		gl.glMultMatrixf(arr, 0);
	}

	public static void glMultMatrixd(GL gl, Matrix3d mat)
	{
		float[] arr = matrixArrayf;
		arr[0]=(float)mat.m00; arr[1]=(float)mat.m01;  arr[2]=(float)mat.m02;
		arr[4]=(float)mat.m10; arr[5]=(float)mat.m11;  arr[6]=(float)mat.m12;
		arr[8]=(float)mat.m20; arr[9]=(float)mat.m21; arr[10]=(float)mat.m22;
		gl.glMultMatrixf(arr, 0);
	}

	//

	public static InputStream getInputStream(String path)
	throws IOException
	{
		InputStream in = ClassLoader.getSystemResourceAsStream(path);
		if (in == null)
			throw new IOException("Could not find resource " + path);
		return in;
	}

	public static void grabTGABytes(String path, byte[] arr, int offset, int len)
	throws IOException
	{
		InputStream in = getInputStream(path);
		grabTGABytes(in, arr, offset, len);
		in.close();
	}

	public static void grabTGABytes(InputStream in, byte[] arr, int offset, int len)
	throws IOException
	{
		DataInputStream din = new DataInputStream(in);
		din.skip(786); // todo: read header?
		din.readFully(arr, offset, len);
	}

	public static void grabTGARGB(String path, int[] arr, int offset, int len)
	throws IOException
	{
		InputStream in = getInputStream(path);
		in = new BufferedInputStream(in);
		grabTGARGB(in, arr, offset, len);
		in.close();
	}

	public static void grabTGARGB(InputStream in, int[] arr, int offset, int len)
	throws IOException
	{
		DataInputStream din = new DataInputStream(in);
		din.skip(18); // todo: read header?
		for (int i=0; i<len; i++)
		{
			arr[i] = (din.read()&0xff) + ((din.read()&0xff)<<8) + ((din.read()&0xff)<<16);
		}
	}

	//

	public static void grabPNMBytes(String path, byte[] arr, int offset, int len)
	throws IOException
	{
		InputStream in = getInputStream(path);
		grabPNMBytes(in, arr, offset, len);
		in.close();
	}

	public static void grabPNMBytes(InputStream in, byte[] arr, int offset, int len)
	throws IOException
	{
		DataInputStream din = new DataInputStream(in);
		din.skip(15); // todo: read header?
		din.readFully(arr, offset, len);
	}

	public static void grabPNMRGB(String path, int[] arr, int offset, int len)
	throws IOException
	{
		InputStream in = getInputStream(path);
		in = new BufferedInputStream(in);
		grabPNMRGB(in, arr, offset, len);
		in.close();
	}

	public static void grabPNMRGB(InputStream in, int[] arr, int offset, int len)
	throws IOException
	{
		DataInputStream din = new DataInputStream(in);
		din.skip(15); // todo: read header?
		for (int i=0; i<len; i++)
		{
			arr[i] = (din.read()&0xff) + ((din.read()&0xff)<<8) + ((din.read()&0xff)<<16);
		}
	}

	//

	public static int toByte(float x)
	{
		return Math.max(0, Math.min(255, (int)(x*255.45f)));
	}

	public static int rgb2int(float r, float g, float b)
	{
		return toByte(r) + (toByte(g)<<8) + (toByte(b)<<16);
	}

	public static int rgba2int(float r, float g, float b, float a)
	{
		return toByte(r) + (toByte(g)<<8) + (toByte(b)<<16) + (toByte(a)<<24);
	}

	public static void gloPerspective(GL gl, float fov, float aspect, float near, float far)
	{
		float mat[] = new float[16];
		float fovrad2 = (float)Math.toRadians(fov)/2;
		float f = (float) (Math.cos(fovrad2) / Math.sin(fovrad2));
		mat[0+4*0] = aspect;
		mat[1+4*1] = f;
		mat[2+4*2] = near-far;
		mat[3+4*2] = 2*near-far;
		mat[2+4*3] = -1;
		gl.glMultMatrixf(mat, 0);
	}

}
