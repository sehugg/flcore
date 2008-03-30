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
import java.io.IOException;
import java.util.*;
import java.util.List;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

import com.fasterlight.spif.*;
import com.fasterlight.util.*;

/**
 * Used as the top-most container and as a context for calling the render() methods
 */
public class GLOContext extends GLOContainer
{
	// GLOContext is a singleton -- can only have 1 active at a time
	private static GLOContext CONTEXT;

	protected int nearz = -1;
	protected int farz = 1;

	protected GL gl;
	protected GLU glu;
	protected TextureCache cache;
	protected GLFontServer fontserv;

	protected Rectangle cliprect = new Rectangle();
	protected boolean dirty_proj, dirty_viewport;

	// last known mouse x and y position
	int mousex, mousey;

	// list of components that have captured input
	List captures = new LinkedList();

	GLOComponent focused; // which component is currently focused?
	GLOComponent curr_dialog; // current dialog box active

	// for FPS counting
	long frame_start_msec;
	long fps_time;
	int fps_count;
	float fps_lastfps;

	// for checkError()
	static GLContext glj;

	// size of "world" -- if different from screen coordinates,
	// coordinates will be scaled to fit
	Dimension viewSize;
	protected boolean resizeViewSize = true;

	// minimum size of world -- if set, we won't set world coordinates
	// less than this during alignment
	Dimension minSize = new Dimension(0, 0);
	
	// contains Lists of components -- supports GLOSingletonContainer
	HashMap groups = new HashMap();

	/**
	 * the Command Manager for the context
	 */
	protected GLOCommandManager cmdmgr;

	//

	public GLOContext()
	{
		setCurrent(this);
		ctx = this;
	}

	public GLOContext(GL gl, GLU glu, TextureCache cache, GLFontServer fontserv)
	{
		this();
		init(gl, glu, cache, fontserv);
	}

	public static boolean checkGL()
	{
		return true;
	}

	public GLContext getGLContext()
	{
		return glj;
	}

	public boolean isShowing()
	{
		return isVisible();
	}

	/**
	 * Clears the captures and singleton groups. Call this before loading a new set of components
	 */
	public void reset()
	{
		groups.clear();
		captures.clear();
		focused = null;
		curr_dialog = null;
	}

	/**
	 * override me for one-time initialization
	 */
	protected void init()
	{
		reset();
	}

	public void init(GL gl, GLU glu, TextureCache cache, GLFontServer fontserv)
	{
		this.gl = gl;
		this.glu = glu;
		this.cache = cache;
		this.fontserv = fontserv;
		loadDefaultShaders();
		init();
	}

	public static void setCurrent(GLOContext ctx)
	{
		CONTEXT = ctx;
	}

	public static GLOContext getCurrent()
	{
		return CONTEXT;
	}

	public long getFrameStartMillis()
	{
		return frame_start_msec;
	}

	public void loadDefaultShaders()
	{
		try
		{
			loadShaders("com/fasterlight/glout/shaders.txt");
		} catch (Exception exc)
		{
			exc.printStackTrace();
		}
	}

	public void loadShaders(String filename) throws IOException
	{
		INIFile ini = new CachedINIFile(GLOUtil.getInputStream(filename));
		loadShaders(ini);
	}

	public void loadShaders(INIFile ini) throws IOException
	{
		List v = ini.getSectionNames();
		Iterator it = v.iterator();
		while (it.hasNext())
		{
			String name = (String) it.next();
			Properties props = ini.getSection(name);
			addShader(name, new GLOShader(props));
		}
	}

	public GL getGL()
	{
		return gl;
	}

	public GLU getGLU()
	{
		return glu;
	}

	public TextureCache getTextureCache()
	{
		return cache;
	}

	public GLFontServer getFontServer()
	{
		return fontserv;
	}

	public void set2DProjection()
	{
		int width = w1;
		int height = h1;
		if (isWorldScaled())
		{
			width = viewSize.width;
			height = viewSize.height;
		}
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(x1, x1 + w1, y1 + h1, y1, nearz, farz);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		// if viewSize != component size, rescale to fit
		//		scaleWorldToScreen(gl);
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glDisable(GL.GL_CULL_FACE);
		gl.glVertex3f(0, 0, 0);
		dirty_proj = false;
	}

	/*
	 * public void scaleWorldToScreen(GL gl) { if (isWorldScaled()) 1f/viewSize.width,
	 * h1*1f/viewSize.height, 1); }
	 */

	public void setMinimumSize(int width, int height)
	{
		this.minSize = new Dimension(width, height);
	}
	
	public Dimension getMinimumSize()
	{
		return new Dimension(minSize);
	}
	
	public void setViewSize(int x, int y)
	{
		viewSize = new Dimension(x, y);
	}

	public void setViewSize(Dimension d)
	{
		this.viewSize = (d != null) ? new Dimension(d) : null;
	}

	public void resize(int w, int h) 
	{
		if (resizeViewSize)
		{
			setViewSize(w, h);
		}
		else
		{
			int oldw = this.getWidth();
			int oldh = this.getHeight();
			setViewSize(w, h);
			// stretch only x or y direction depending on aspect
			float relaspect = w*1.0f/h - minSize.width*1.0f/minSize.height;
			if (relaspect >= 0)
			{
				w = Math.round(minSize.width * (1+relaspect));
				h = minSize.height;
			} else {
				w = minSize.width;
				h = Math.round(minSize.height * (1-relaspect));
			}
			setSize(w, h);
			if (w != oldw || h != oldh)
			{
				System.out.println("Resize ui to " + w + " x " + h);
				reanchor(w-oldw,h-oldh);
			}
		}
	}

	public boolean isWorldScaled()
	{
		return (viewSize != null && (viewSize.width != w1 || viewSize.height != h1));
	}

	public int xworld2scrn(int x)
	{
		return isWorldScaled() ? x * viewSize.width / this.w1 : x;
	}

	public int yworld2scrn(int y)
	{
		return isWorldScaled() ? y * viewSize.height / this.h1 : y;
	}

	public float xworld2scrn(float x)
	{
		return isWorldScaled() ? x * viewSize.width / this.w1 : x;
	}

	public float yworld2scrn(float y)
	{
		return isWorldScaled() ? y * viewSize.height / this.h1 : y;
	}

	public int xscrn2world(int x)
	{
		return isWorldScaled() ? x * this.w1 / viewSize.width : x;
	}

	public int yscrn2world(int y)
	{
		return isWorldScaled() ? y * this.h1 / viewSize.height : y;
	}

	public float xscrn2world(float x)
	{
		return isWorldScaled() ? x * this.w1 / viewSize.width : x;
	}

	public float yscrn2world(float y)
	{
		return isWorldScaled() ? y * this.h1 / viewSize.height : y;
	}

	public void scrn2world(Point p)
	{
		if (isWorldScaled())
		{
			p.x = (p.x * w1 + viewSize.width - 1) / viewSize.width;
			p.x = (p.y * h1 + viewSize.height - 1) / viewSize.height;
		}
	}

	public void world2scrn(Point p)
	{
		if (isWorldScaled())
		{
			p.x = p.x * viewSize.width / w1;
			p.x = p.y * viewSize.height / h1;
		}
	}

	public void scrn2world(Rectangle r)
	{
		if (isWorldScaled())
		{
			r.x = (r.x * w1 + viewSize.width - 1) / viewSize.width;
			r.y = (r.y * h1 + viewSize.height - 1) / viewSize.height;
			r.width = (r.width * w1 + viewSize.width - 1) / viewSize.width;
			r.height = (r.height * h1 + viewSize.height - 1) / viewSize.height;
		}
	}

	public void world2scrn(Rectangle r)
	{
		if (isWorldScaled())
		{
			r.x = r.x * viewSize.width / w1;
			r.y = r.y * viewSize.height / h1;
			r.width = r.width * viewSize.width / w1;
			r.height = r.height * viewSize.height / h1;
		}
	}

	public Dimension getViewSize()
	{
		return this.viewSize;
	}

	public int getViewWidth()
	{
		return (this.viewSize != null) ? this.viewSize.width : w1;
	}

	public int getViewHeight()
	{
		return (this.viewSize != null) ? this.viewSize.height : h1;
	}

	private void setClipRegion(Rectangle r)
	{
		int height = h1;
		if (isWorldScaled())
		{
			r = new Rectangle(r);
			world2scrn(r);
			height = viewSize.height;
		}
		gl.glScissor(r.x, (height - r.y) - r.height, r.width, r.height);
	}

	/**
	 * Renders the given component. This method should be called instead of "cmpt.render(ctx)"
	 * because it sets up the appropriate parameters.
	 */
	public void renderComponent(GLOComponent cmpt)
	{
		if (!cmpt.isVisible())
			return;

		// if it's wigged out, draw a red rectangle and exit
		if (cmpt.isWiggedOut())
		{
			gl.glColor3f(1, 0, 0);
			cmpt.drawBox();
			return;
		}

		// set the projection, if dirty, or if custom
		if (cmpt.is3d())
		{
			cmpt.setProjection(this);
			dirty_proj = true;
		} else if (dirty_proj)
		{
			set2DProjection();
		}
		// set the viewport, if dirty, or if needs clipping
		// todo: intersect previous viewport
		Rectangle oldcliprect = null;
		if (cmpt.needsClipping())
		{
			oldcliprect = cliprect;
			cliprect = cliprect.intersection(cmpt.getBounds());
			setClipRegion(cliprect);
			gl.glEnable(GL.GL_SCISSOR_TEST);
		}

		try
		{
			// render the component
			cmpt.render(this);
		} catch (Exception exc)
		{
			exc.printStackTrace(); // todo: log it
			cmpt.setWiggedOut(true);
		}

		// get the old rectangle back
		if (oldcliprect != null)
		{
			cliprect = oldcliprect;
			if (cliprect.width == w1 && cliprect.height == h1)
				gl.glDisable(GL.GL_SCISSOR_TEST);
			else
				setClipRegion(cliprect);
		}
	}

	private void computeSceneStartTime()
	{
		long t = System.currentTimeMillis();
		//		SDLTimer.getTimeMillis();
		//		long t = System.currentTimeMillis();
		// if timer resolution sux, like on Windoze, then we have
		// to guess :-p
		if (t <= frame_start_msec)
			frame_start_msec += (long) (1000 / Math.max(fps_lastfps, 1));
		else
			frame_start_msec = t;
	}

	public void renderScene()
	{
		// todo: clear bits
		dirty_proj = true;
		dirty_viewport = true;
		computeSceneStartTime();
		cliprect.setRect(0, 0, w1, h1);
		renderChildren(this);
		computeFPS();
	}

	void computeFPS()
	{
		fps_count++;
		long syst = getFrameStartMillis();
		if (syst > (fps_time + 1000))
		{
			fps_lastfps = (syst - fps_time) * fps_count / 1000.0f;
			fps_time = syst;
			fps_count = 0;
		}
	}

	public float getLastFPS()
	{
		return fps_lastfps;
	}

	protected boolean dispatchEvent(GLOEvent event, GLOComponent target)
	{
		if (debug)
			System.out.println("Dispatch " + event + " to " + target);
		try
		{
			return target.handleEvent(event);
		} catch (GLOUserException gloue)
		{
			GLOMessageBox.showOk(gloue.getMessage());
			return false;
		} catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public boolean deliverEvent(GLOEvent event, GLOComponent target)
	{
		while (target != null)
		{
			if (dispatchEvent(event, target))
				return true;
			target = target.getParent();
		}
		return false;
	}

	/**
	 * Deliver an event to components which registered to receive captured events. Returns true if
	 * any captured components handled the event. Only delivers to "showing" components.
	 */
	protected boolean deliverToCaptures(GLOEvent event)
	{
		if (captures.size() == 0)
			return false;

		boolean b = false;
		Object[] caparr = captures.toArray();
		for (int i = 0; i < caparr.length; i++)
		{
			CaptureRec cr = (CaptureRec) caparr[i];
			if (debug)
				System.out.println("cr=" + cr.evtype + "," + cr.cmpt);
			if (cr.evtype.isInstance(event) && cr.cmpt.hasParent(this))
			{
				if (dispatchEvent(event, cr.cmpt))
					b = true;
			}
		}
		return b;
	}

	public boolean deliverEvent(GLOEvent event, int x, int y)
	{
		if (deliverToCaptures(event))
			return true;
		GLOComponent target = getComponentAt(x, y);
		if (target != null)
		{
			return deliverEvent(event, target);
		} else
			return false;
	}

	public boolean deliverEvent(GLOMouseEvent event)
	{
		return deliverEvent(event, event.getX(), event.getY());
	}

	public boolean deliverEvent(GLOKeyEvent event)
	{
		// if a cmpt is focused, deliver to it
		if (focused != null)
		{
			// if cmpt is not in the hierarchy, set it to null
			if (!focused.hasParent(this))
				focused = null;
		}
		if (focused != null)
		{
			return deliverEvent(event, focused);
		} else
			return deliverEvent(event, this);
	}

	public void setMousePos(int newx, int newy)
	{
		if (newx != mousex || newy != mousey)
		{
			deliverEvent(new GLOMouseMovedEvent(this, newx, newy));
			this.mousex = newx;
			this.mousey = newy;
		}
	}

	public void beginEventCapture(GLOComponent cmpt, Class evtype)
	{
		CaptureRec cr = new CaptureRec(cmpt, evtype);
		captures.add(cr);
	}

	public void endEventCapture(GLOComponent cmpt, Class evtype)
	{
		CaptureRec cr = new CaptureRec(cmpt, evtype);
		captures.remove(cr);
	}

	public void requestFocus(GLOComponent cmpt)
	{
		if (cmpt == focused)
			return;
		if (focused != null)
			deliverEvent(new GLOFocusEvent(this, focused, false), focused);
		this.focused = cmpt;
		deliverEvent(new GLOFocusEvent(this, cmpt, true), focused);
		if (debug)
			System.out.println("Changed focus to " + focused);
	}

	public GLOComponent getFocused()
	{
		return focused;
	}

	public void loadDialog(String path)
	{
		if (curr_dialog != null && curr_dialog.getParent() == this)
		{
			this.remove(curr_dialog);
		}
		curr_dialog = null;
		try
		{
			GLOLoader loader = new GLOLoader(this);
			curr_dialog = loader.load(path);
			curr_dialog.center();
		} catch (IOException ioe)
		{
			throw new PropertyRejectedException("Couldn't load dialog '" + path + "': " + ioe);
		}
	}

	//

	class CaptureRec
	{
		GLOComponent cmpt;
		Class evtype;
		CaptureRec(GLOComponent cmpt, Class evtype)
		{
			this.cmpt = cmpt;
			this.evtype = evtype;
		}
		public int hashCode()
		{
			return (cmpt.hashCode() ^ evtype.hashCode());
		}
		public boolean equals(Object o)
		{
			if (o == null || !(o instanceof CaptureRec))
				return false;
			CaptureRec cr = (CaptureRec) o;
			return (cr.cmpt.equals(cmpt) && cr.evtype.equals(evtype));
		}
	}

	public GLOComponent getDescendantNamed(GLOComponent cmpt, String name)
	{
		if (name == null)
			return null;
		if (name.equals(cmpt.getName()))
			return cmpt;
		Iterator it = cmpt.getChildren();
		if (it == null)
			return null;
		while (it.hasNext())
		{
			GLOComponent c = (GLOComponent) it.next();
			c = getDescendantNamed(c, name);
			if (c != null)
				return c;
		}
		return null;
	}

	public boolean debug = false;

	// GROUP stuff

	public List getGroup(String groupid)
	{
		List l = (List) groups.get(groupid);
		if (l == null)
		{
			l = new ArrayList();
			groups.put(groupid, l);
		}
		return l;
	}

	//

	public PropertyAware getPropertyTop()
	{
		return this;
	}

	public GLOCommandManager getCommandManager()
	{
		return cmdmgr;
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOContext.class);

	static {
		prophelp.registerGet("fps", "getLastFPS");
		prophelp.registerGet("focused", "getFocused");
		prophelp.registerSet("loaddialog", "loadDialog", String.class);
	}

	public Object getProp(String key)
	{
		if (key.startsWith("$$"))
			return getDescendantNamed(this, key.substring(2));
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
		} catch (PropertyRejectedException e)
		{
			super.setProp(key, value);
		}
	}

}
