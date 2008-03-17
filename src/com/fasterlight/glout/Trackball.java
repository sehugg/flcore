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

import com.fasterlight.vecmath.*;

// todo: use Orientation
public class Trackball
{
	Quat4f q = new Quat4f(0,0,0,1);
	Quat4f q2 = new Quat4f();
	Quat4f qdest = new Quat4f(0,0,0,1);
	Quat4f qmod = new Quat4f(0,0,0,1);
	Vector3f p1 = new Vector3f();
	Vector3f p2 = new Vector3f();
	Vector3f a = new Vector3f();
	Vector3f d = new Vector3f();
	float tbsize;
	int iters=0;
	double interp = 0.25;

	public Trackball(float size)
	{
		this.tbsize = size;
	}

	public void rotate(float p1x, float p1y, float p2x, float p2y)
	{
		if (p1x == p2x && p1y == p2y)
    		return;

    	p1.set(p1x, p1y, projectSphere(tbsize,p1x,p1y));
    	p2.set(p2x, p2y, projectSphere(tbsize,p2x,p2y));
    	a.cross(p2,p1);
    	d.sub(p1,p2);
    	double t = d.length()/(2*tbsize);
		if (t > 1.0) t = 1.0;
    	if (t < -1.0) t = -1.0;
   	float phi = (float)(2.0 * Math.asin(t));

   	a.normalize();
   	AxisAngle4f aa = new AxisAngle4f(a.x, a.y, a.z, phi);
   	q2.set(aa);
//   	System.out.println("q=" + q + ", aa=" + aa + ", q2=" + q2);
   	concat(q2,qdest,qdest);
   	// after 90 iters, normalize
   	if (iters++ > 90)
   	{
   		iters=0;
   		qdest.normalize();
   	}
//   	System.out.println("newq=" + q );
	}

	public void update()
	{
		Quat4f dest = getDest();
		if (interp > 0 && interp < 1)
		{
			q.interpolate(dest, interp);
		} else
			q.set(dest);
	}

	public Quat4f getDest()
	{
		Quat4f q = new Quat4f(qdest);
		q.mul(qmod);
		return q;
	}

	public void setModifier(Quat4d tm)
	{
		qmod.set(new Quat4f(tm));
	}

	public void setModifier(Quat4f tm)
	{
		qmod.set(tm);
	}

	public void setModifier(Matrix3f tm)
	{
		qmod.set(tm);
	}

	public void setTarget(Quat4d tq)
	{
		qdest.set((float)tq.x, (float)tq.y, (float)tq.z, (float)tq.w);
	}

	public void setTarget(Quat4f tq)
	{
		qdest.set(tq);
	}

	static void concat(Quat4f q1, Quat4f q2, Quat4f dest)
	{
		Vector3f t1 = new Vector3f(q1.x,q1.y,q1.z);
		Vector3f t2 = new Vector3f(q2.x,q2.y,q2.z);
		float dot = t1.dot(t2);
		Vector3f t3 = new Vector3f();
		t3.cross(t2,t1);
//		System.out.println("t1=" + t1 + ", t2=" + t2 + ", t3=" + t3 + ", dot=" + dot + ", dest=" + dest);
		Vector3f tf = new Vector3f();
		t1.scale(q2.w);
		t2.scale(q1.w);
		tf.add(t1,t2);
		tf.add(t3);
//		System.out.println("t3=" + t3);
		dest.set(tf.x, tf.y, tf.z, q1.w*q2.w - dot);
	}

	public Quat4f getQuat()
	{
		return q;
	}

/*
 * Project an x,y pair onto a sphere of radius r OR a hyperbolic sheet
 * if we are away from the center of the sphere.
 */
static float
projectSphere(float r, float x, float y)
{
    double d, t, z;

    d = Math.sqrt(x*x + y*y);
    if (d < r * 0.70710678118654752440) {    /* Inside sphere */
        z = Math.sqrt(r*r - d*d);
    } else {           /* On hyperbola */
        t = r / 1.41421356237309504880;
        z = t*t / d;
    }
    return (float)z;
}

public static void main(String[] args)
{
	Trackball ball = new Trackball(1);
	ball.rotate(0,0,0.1f,0.1f);
}

}
