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
import java.awt.event.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import com.fasterlight.io.IOUtil;

public class GLOAWTComponent
	implements GLEventListener, KeyListener, MouseListener, MouseMotionListener, Runnable
{
	protected GLOContext ctx;
	protected TextureCache cache;
	protected GLFontServer fontserv;
	protected boolean clearAll = false;
	protected GL gl;
	protected GLU glu;
	protected Dimension size = new Dimension();
	private GLCanvas canvas;

	//

	public GLOAWTComponent(int w, int h)
	{
		size.setSize(w, h);
	}

	public Dimension getSize()
	{
		return new Dimension(size);
	}

	public int getWidth()
	{
		return size.width;
	}

	public int getHeight()
	{
		return size.height;
	}

	public GLOContext getContext()
	{
		return ctx;
	}

	protected GLOContext makeContext()
	{
		return new GLOContext();
	}

	protected void makeComponents()
	{
		ctx = makeContext();
		ctx.init(gl, glu, cache, fontserv);
		ctx.setSize(size.width, size.height);
	}

	///

	/** Gets called exactly once when GLComponent.initialize() is invoked */
	public void init(GLAutoDrawable drawable)
	{
		gl = drawable.getGL();
		glu = new GLU();
		/** Set the clear colour for the framebuffer */
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glShadeModel(GL.GL_SMOOTH);

		/** Setup the scene lighting */
		float[] lightPosition = { 1.0f, 1.0f, 1.0f, 0.0f };
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition, 0);

		/** Disable some states */
		gl.glDisable(GL.GL_CULL_FACE);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_LIGHT0);
		gl.glDisable(GL.GL_NORMALIZE);

		// TODO
		String urlbase = "file://.";
		try
		{
			urlbase = IOUtil.findBaseURLForResource("uitexs/hstripe.png");
		} catch (Exception ioe)
		{
			// ...
		}
		cache = new TextureCache(urlbase, gl, glu, this);
		cache.init();

		fontserv = new GLFontServer(gl, glu, cache);
		fontserv.init();
		//				fontserv.setFont("font1-ALPHA");
		fontserv.setTextSize(8, -10); //todo: const

		makeComponents();

		// add listeners...
		// todo: why does GL4Java add a mouse listener???
		//    	this.addMouseListener(this);
		glutReportErrors("init()");
	}

	/** Handles viewport resizing */
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		size = new Dimension(w, h);
		if (ctx != null)
			ctx.resize(w, h);
	}

	///

	/** Renders the scene */
	public void display(GLAutoDrawable drawable)
	{
		gl = drawable.getGL();
		glu = new GLU();
		/** Clear the framebuffer */
		gl.glMatrixMode(GL.GL_MODELVIEW);
		try
		{
			render();

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private static int getModifiers(MouseEvent evt)
	{
		int x = 0;
		int mod = evt.getModifiers();
		if ((mod & MouseEvent.BUTTON1_MASK) != 0)
			x |= 1;
		if ((mod & MouseEvent.BUTTON3_MASK) != 0)
			x |= 2;
		return x;
	}

	/** Handles key pressed event */
	public void keyPressed(KeyEvent evt)
	{
		if (ctx == null)
			return;
		int mod = evt.getModifiers();
		mod &= GLOKeyEvent.MOD_ALL;
		//System.out.println(evt);
		ctx.deliverEvent(
			new GLOKeyEvent(ctx, evt.getModifiers(), evt.getKeyCode(), evt.getKeyChar(), true));
	}

	/** Handles key typed events */
	public void keyTyped(KeyEvent evt)
	{
	}

	/** Handles key released events */
	public void keyReleased(KeyEvent evt)
	{
		if (ctx == null)
			return;
		ctx.deliverEvent(
			new GLOKeyEvent(ctx, evt.getModifiers(), evt.getKeyCode(), evt.getKeyChar(), false));
	}

	/** Mouse Listener */
	public void mouseClicked(MouseEvent evt)
	{
	}

	public void mouseEntered(MouseEvent evt)
	{
	}

	public void mouseExited(MouseEvent evt)
	{
	}

	public void mousePressed(MouseEvent evt)
	{
		if (ctx == null)
			return;
		int x = ctx.xscrn2world(evt.getX());
		int y = ctx.yscrn2world(evt.getY());
		ctx.deliverEvent(new GLOMouseButtonEvent(ctx, x, y, getModifiers(evt), true));
	}

	public void mouseReleased(MouseEvent evt)
	{
		if (ctx == null)
			return;
		int x = ctx.xscrn2world(evt.getX());
		int y = ctx.yscrn2world(evt.getY());
		ctx.deliverEvent(new GLOMouseButtonEvent(ctx, x, y, getModifiers(evt), false));
	}

	/** MouseMotionListener */
	public void mouseDragged(MouseEvent evt)
	{
		if (ctx == null)
			return;
		int x = ctx.xscrn2world(evt.getX());
		int y = ctx.yscrn2world(evt.getY());
		ctx.setMousePos(x, y);
	}

	public void mouseMoved(MouseEvent evt)
	{
		if (ctx == null)
			return;
		int x = ctx.xscrn2world(evt.getX());
		int y = ctx.yscrn2world(evt.getY());
		ctx.setMousePos(x, y);
	}

	///

	public void render()
	{
		if (clearAll)
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		if (ctx != null)
			ctx.renderScene();
	}

	// ANIMATION STUFF

	class Engine extends Thread
	{
		boolean running = true;
		public void run()
		{
			while (running)
			{
				try
				{
					EventQueue.invokeAndWait(GLOAWTComponent.this);
				} catch (Exception ee)
				{
					ee.printStackTrace();
				} catch (Error ee)
				{
					ee.printStackTrace();
					throw ee;
				}
			}
		}
	}

	public void run()
	{
		canvas.display();
	}

	public void addListeners(Component listener)
	{
		listener.addKeyListener(this);
		listener.addMouseListener(this);
		listener.addMouseMotionListener(this);
	}

	public void start(GLCanvas canvas, Component listener)
	{
		this.canvas = canvas;

		addListeners(listener);
		listener.requestFocus();

		Thread thread = new Engine();
		thread.setDaemon(true);
		thread.start();
	}

	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2)
	{
		// TODO Auto-generated method stub
	}

	public GLCanvas createGLCanvas()
	{
		GLCapabilities capabilities = new GLCapabilities();
		return createGLCanvas(capabilities);
	}

	public GLCanvas createGLCanvas(GLCapabilities capabilities)
	{
		GLCanvas canvas = new GLCanvas(capabilities);

		canvas.addGLEventListener(this);
		canvas.setSize(getWidth(), getHeight());
		return canvas;
	}

	boolean glutReportErrors(String message)
	{
		int error;

		//	System.out.println("glutReportErrors()");
		while ((error = gl.glGetError()) != GL.GL_NO_ERROR)
		{
			System.out.println(message + ": " + glu.gluErrorString(error));
		}
		return error == GL.GL_NO_ERROR;
	}

}
