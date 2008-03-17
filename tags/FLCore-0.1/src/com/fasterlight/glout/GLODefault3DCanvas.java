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

import java.awt.*;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.fasterlight.spif.*;
import com.fasterlight.vecmath.*;

public class GLODefault3DCanvas extends GLOContainer
{
	public float neardist = 1.0f;
	public float fardist = 50.0f;

	protected int beginx, beginy;
	protected boolean trackball_drag;
	protected boolean translate_drag;
	protected boolean gain_focus = true;

	protected Trackball ball = new Trackball(0.5f);
	protected float xtrans, ytrans;

	protected boolean initialized;

	protected float MAX_FOV = 120f;
	protected float MIN_FOV = 0.1f;
	protected float MAX_DIST = 120f;
	protected float MIN_DIST = 0.001f;
	protected float FOV_MOVE_SPEED = 0.2f;

	protected GLOSmoother fovSmoother = new GLOSmoother(60.0f);
	protected GLOSmoother viewdistSmoother = new GLOSmoother(25.0f);

	/**
	  * This is set when render() is called
	  */
	protected float fov;
	public double sin_fov, cos_fov, rad_fov;

	///

	public boolean is3d()
	{
		return true;
	}

	public boolean needsClipping()
	{
		return true;
	}

	// not a good idea to call with super() --
	// call setPerspective() instead

	public void setProjection(GLOContext ctx)
	{
		GL gl = ctx.getGL();

		Rectangle r = this.getBounds();
		ctx.world2scrn(r);
		gl.glViewport(r.x, ctx.getViewHeight() - r.y - r.height, r.width, r.height);

		// do this here because we need it for the call to setPerspective(),
		// but we don't want to call it in render() in case there is
		// an overridden setPerspective()
		updateFOVParams();

		setPerspective(ctx);
		gl.glLoadIdentity();

	}

	protected void updateFOVParams()
	{
		fov = getFOV();
		rad_fov = Math.toRadians(fov);
		sin_fov = Math.sin(rad_fov);
		cos_fov = Math.cos(rad_fov);
	}

	protected void setPerspective(GLOContext ctx)
	{
		GL gl = ctx.getGL();
		GLU glu = ctx.getGLU();

		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gloPerspective(gl, fov, (float) w1 / (float) h1, neardist, fardist);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	protected void translateView(int x, int y)
	{
		int size = getEffectivePixelSize();
		float factor = (float)(sin_fov*getViewDistance()/size);
		xtrans += (x-beginx)*factor;
		ytrans += (beginy-y)*factor;

		beginx = x;
		beginy = y;
	}

	private int getEffectivePixelSize()
	{
		return Math.min(w1, h1);
	}

	protected void rotateView(int x, int y)
	{
		int width = w1;
		int height = h1;

		Point o = getOrigin();
		int size = getEffectivePixelSize();
		float x1 = (beginx - o.x - width / 2) * 2f / size;
		float x2 = (x - o.x - width / 2) * 2f / size;
		float y1 = - (beginy - o.y - height / 2) * 2f / size;
		float y2 = - (y - o.y - height / 2) * 2f / size;

		rotateTrackball(x1, y1, x2, y2);

		beginx = x;
		beginy = y;
	}

	protected void rotateTrackball(float x1, float y1, float x2, float y2)
	{
		ball.rotate(x1, y1, x2, y2);
	}

	public boolean handleEvent(GLOEvent event)
	{
		if (event instanceof GLOMouseButtonEvent)
		{
			// rotate the display using the Trackball class
			GLOMouseButtonEvent mev = (GLOMouseButtonEvent) event;
			if (mev.isPressed(1) || mev.isPressed(2))
			{
				event.getContext().beginEventCapture(this, GLOMouseEvent.class);
				beginx = mev.x;
				beginy = mev.y;
				if ((mev.flags & 1) != 0)
					trackball_drag = true;
				else if ((mev.flags & 2) != 0)
					translate_drag = true;
				if (gain_focus)
					event.getContext().requestFocus(this);
				return true;
			}
			else if (mev.isReleased(1) || mev.isReleased(2))
			{
				event.getContext().endEventCapture(this, GLOMouseEvent.class);
				trackball_drag = false;
				translate_drag = false;
				return true;
			}
		}
		else if (event instanceof GLOMouseMovedEvent)
		{
			if (trackball_drag)
			{
				GLOMouseMovedEvent mev = (GLOMouseMovedEvent) event;
				rotateView(mev.x, mev.y);
				return true;
			}
			else if (translate_drag)
			{
				GLOMouseMovedEvent mev = (GLOMouseMovedEvent) event;
				translateView(mev.x, mev.y);
				return true;
			}
		}
		else if (event instanceof GLOFocusEvent)
		{
			if (gain_focus)
				return true;
		}

		return super.handleEvent(event);
	}

	public void renderBackground(GLOContext ctx)
	{
	}

	public void renderForeground(GLOContext ctx)
	{
		renderChildren(ctx);
	}

	public void renderObject(GLOContext ctx)
	{
	}

	protected void rotateByTrackball(GL gl)
	{
		Matrix3f mat = getTrackballMatrix();
		float[] marr = GLOUtil.toArray(mat);
		gl.glMultMatrixf(marr, 0);
	}

	public void rotateByInverseTrackball(GL gl)
	{
		Matrix3f mat = getTrackballMatrix();
		mat.invert();
		float[] marr = GLOUtil.toArray(mat);
		gl.glMultMatrixf(marr, 0);
	}

	protected Matrix3f getTrackballMatrix()
	{
		Matrix3f mat = new Matrix3f();
		mat.set(ball.getQuat());
		return mat;
	}

	protected void init(GLOContext ctx)
	{
		// override me
	}

	protected void clearBackground(GL gl)
	{
		// todo: don't clear if full-screen
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
	}

	protected void transformForView(GL gl)
	{
		gl.glLoadIdentity();
		gl.glTranslatef(xtrans, ytrans, -getViewDistance());
		rotateByTrackball(gl);
	}

	public void getCurrentViewpoint(Vector3f viewpoint)
	{
		viewpoint.set(-xtrans, -ytrans, getViewDistance());
		this.getTrackballMatrix().transform(viewpoint);
	}

	public void render(GLOContext ctx)
	{
		if (!initialized)
		{
			init(ctx);
			initialized = true;
		}

		GL gl = ctx.getGL();

		clearBackground(gl);

		renderBackground(ctx);

		gl.glPushMatrix();
		gl.glPushAttrib(GL.GL_ENABLE_BIT | GL.GL_POLYGON_BIT);

		transformForView(gl);

		renderObject(ctx);

		gl.glPopAttrib();
		gl.glPopMatrix();

		renderForeground(ctx);

		update();
	}

	protected void update()
	{
		ball.update();
	}

	public float getFOV()
	{
		return fovSmoother.getValue();
	}

	public void setFOV(float fov)
	{
		fovSmoother.setValue(Math.min(MAX_FOV, Math.max(MIN_FOV, fov)));
	}

	public float getTargetFOV()
	{
		return fovSmoother.getTarget();
	}

	public void setTargetFOV(float targfov)
	{
		fovSmoother.setTarget(Math.min(MAX_FOV, Math.max(MIN_FOV, targfov)));
	}

	public float getViewDistance()
	{
		return viewdistSmoother.getValue();
	}

	public void setViewDistance(float viewdist)
	{
		viewdistSmoother.setValue(Math.min(MAX_DIST, Math.max(MIN_DIST, viewdist)));
	}

	public void setTargetViewDistance(float viewdist)
	{
		viewdistSmoother.setTarget(Math.min(MAX_DIST, Math.max(MIN_DIST, viewdist)));
	}

	public float getTargetViewDistance()
	{
		return viewdistSmoother.getTarget();
	}

	public void zoom(float x)
	{
		setTargetFOV(getTargetFOV() / x);
	}

	public void closer(float x)
	{
		setTargetViewDistance(getTargetViewDistance() / x);
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLODefault3DCanvas.class);

	static {
		prophelp.registerGetSet("fov", "FOV", float.class);
		prophelp.registerGetSet("targfov", "TargetFOV", float.class);
		prophelp.registerGetSet("viewdist", "ViewDistance", float.class);
		prophelp.registerGetSet("targviewdist", "TargetViewDistance", float.class);
		prophelp.registerSet("zoom", "zoom", float.class);
		prophelp.registerSet("closer", "closer", float.class);
	}

	public Object getProp(String key)
	{
		Object o = prophelp.getProp(this, key);
		if (o == null)
			o = super.getProp(key);
		return o;
	}

	public void setProp(String key, Object value)
	{
		try
		{
			prophelp.setProp(this, key, value);
		}
		catch (PropertyRejectedException e)
		{
			super.setProp(key, value);
		}
	}

	public static void gloPerspective(GL gl, float fov, float aspect,
			float near, float far)
	{
		float range = near * (float)Math.tan(Math.toRadians(fov / 2));
		gl.glFrustum(-range * aspect, range * aspect, -range, range, near, far);
	}
}
