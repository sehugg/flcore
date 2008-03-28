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
import java.util.*;

import javax.media.opengl.GL;

import com.fasterlight.spif.*;

public abstract class GLOComponent implements PropertyAware
{
	protected GLOContext ctx;
	protected int x1, y1, w1, h1;
	protected Point origin = new Point();
	protected GLOComponent parent;
	protected Map shaders;
	protected boolean dragging;
	protected boolean visible = true;
	protected boolean wiggedout; // if error occurs...
	protected String cmptname;
	protected int alignFlags = 0;
	
	public static final int ALIGN_LEFT   = 1;
	public static final int ALIGN_RIGHT  = 2;
	public static final int ALIGN_TOP    = 4;
	public static final int ALIGN_BOTTOM = 8;

	//

	public GLOComponent()
	{
		ctx = GLOContext.getCurrent();
	}

	/**
	 * Load from properties file
	 */
	public void load(Properties props)
	{
		PropertyUtil.setFromProps(this, props);
	}

	/**
	 * Returns the parent container
	 */
	public final GLOComponent getParent()
	{
		return parent;
	}

	protected void setParent(GLOComponent cmpt)
	{
		this.parent = cmpt;
		computeOrigin();
	}

	public boolean hasParent(GLOComponent cmpt)
	{
		GLOComponent p = getParent();
		while (p != null)
		{
			if (p == cmpt)
				return true;
			p = p.getParent();
		}
		return false;
	}

	public boolean isVisible()
	{
		return getVisible();
	}

	public boolean getVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	void setWiggedOut(boolean b)
	{
		this.wiggedout = b;
	}

	boolean isWiggedOut()
	{
		return wiggedout;
	}

	/**
	 * Returns true if it is visible, and if it is showing on screen
	 */
	public boolean isShowing()
	{
		return getParent() != null && isVisible() && getParent().shows(this);
	}

	/**
	 * @returns true if we want to receive clicked events
	 */
	public boolean isReceiving()
	{
		return isVisible();
	}

	public String getName()
	{
		return cmptname;
	}

	public void setName(String name)
	{
		this.cmptname = name;
	}

	/**
	 * Gets the top-level context, if there is one, or null.
	 */
	public GLOContext getContext()
	{
		return ctx;
	}

	/**
	 * Returns the component at a given point (world coords). Default behavior is to return 'this'
	 * if the point is inside this component, or null if outside. Containers will override this to
	 * suit their own nefarious needs.
	 */
	public GLOComponent getComponentAt(int x, int y)
	{
		return containsPoint(x, y) ? this : null;
	}

	/**
	 * Returns true if this component contains 'cmpt' as a direct child and will show it on screen
	 */
	public boolean shows(GLOComponent cmpt)
	{
		return false;
	}

	/**
	 * If this component has children, returns the component named 'name'
	 */
	public GLOComponent getChildNamed(String name)
	{
		return null;
	}

	/**
	 * Iterates over children of this component. May return 'null' if no children.
	 */
	public Iterator getChildren()
	{
		return null;
	}

	/**
	 * Returns whether or not to use default projection (2D)
	 */
	public boolean is3d()
	{
		return false;
	}

	/**
	 * Sets a user-definable projection, whenever is3d() is true. Must leave in GL.GL_MODEL_VIEW
	 * state.
	 */
	public void setProjection(GLOContext ctx)
	{
	}

	/**
	 * Controls whether or not glViewport is called prior to calling render()
	 */
	public boolean needsClipping()
	{
		return false;
	}

	public void setPosition(int x, int y)
	{
		this.x1 = x;
		this.y1 = y;
		computeOrigin();
	}

	public void setSize(int w, int h)
	{
		this.w1 = w;
		this.h1 = h;
		fixSize();
	}

	public void setPosition(Point pos)
	{
		setPosition(pos.x, pos.y);
	}

	public void setSize(Dimension dim)
	{
		setSize(dim.width, dim.height);
	}

	/**
	 * Adjusts size to fit minimum size limits.
	 */
	protected void fixSize()
	{
		Dimension d = getMinimumSize();
		w1 = (w1 >= d.width) ? w1 : d.width;
		h1 = (h1 >= d.height) ? h1 : d.height;
	}

	/**
	 * Center component in its parent.
	 */
	public void center()
	{
		if (parent == null)
			return;
		setPosition((parent.w1 - w1) / 2, (parent.h1 - h1) / 2);
	}

	public final int getX()
	{
		return x1;
	}

	public final int getY()
	{
		return y1;
	}

	public final int getWidth()
	{
		return w1;
	}

	public final int getHeight()
	{
		return h1;
	}

	public final Point getPosition()
	{
		return new Point(x1, y1);
	}

	public final Dimension getSize()
	{
		return new Dimension(w1, h1);
	}

	/**
	 * Minimum size that this component can allow, ever. This value cannot depend on children of
	 * the given component, rather it should depend on the settings of the component.
	 */
	public Dimension getMinimumSize()
	{
		return new Dimension(0, 0);
	}

	/**
	 * Gets the global origin of this component client area -- should be used in render() function
	 * when determining where to draw. NOTE: should be new object returned
	 */
	protected void computeOrigin()
	{
		if (parent != null)
		{
			Point p = parent.getOrigin();
			parent.offsetPoint(p);
			origin.x = p.x + x1;
			origin.y = p.y + y1;
		} else
		{
			origin.x = x1;
			origin.y = y1;
		}
		// now recompute children
		Iterator it = this.getChildren();
		if (it != null)
		{
			while (it.hasNext())
			{
				((GLOComponent) it.next()).computeOrigin();
			}
		}
	}

	public Point getOrigin()
	{
		// todo: new point?
		return new Point(origin);
	}

	public Point getOrigin_unsafe()
	{
		return origin;
	}

	/**
	 * Returns rectangle representing screen coords. of this component.
	 */
	public final Rectangle getBounds()
	{
		return new Rectangle(origin.x, origin.y, w1, h1);
	}

	/**
	 * Used by scroll boxes and the like to adjust the offset of their children. Default does
	 * nothing.
	 */
	protected void offsetPoint(Point p)
	{
	}

	public void local2scrn(Point p)
	{
		Point o = getOrigin();
		p.x += o.x;
		p.y += o.y;
	}

	public void scrn2local(Point p)
	{
		Point o = getOrigin();
		p.x -= o.x;
		p.y -= o.y;
	}

	/**
	 * Does this component contain a given point? (world coords)
	 */
	public boolean containsPoint(int x, int y)
	{
		Point o = getOrigin();
		return (x >= o.x && x < o.x + w1 && y >= o.y && y < o.y + h1);
	}

	/**
	 * Does this component intersect another (partially or fully)?
	 */
	public boolean intersects(GLOComponent cmpt)
	{
		int rx = cmpt.origin.x;
		int ry = cmpt.origin.y;
		int x = this.origin.x;
		int y = this.origin.y;
		return !((rx + cmpt.w1 <= x) || (ry + cmpt.h1 <= y) || (rx >= x + w1) || (ry >= y + h1));
		/**
		 * Rectangle r1 = this.getBounds(); Rectangle r2 = cmpt.getBounds(); return
		 * (r1.intersects(r2));
		 */
	}

	/**
	 * For container components that need to align child controls, this method does it. Containers
	 * probably will recursively call this method on their children. Default behavior does nothing.
	 * A layout() method should only change positions of their child components, not the sizes. It
	 * may however change the size of the parent container to fit the children.
	 */
	public void layout()
	{
	}

	/**
	 * Align size of component to parent based on alignFlags.
	 */
	public void align()
	{
		if (alignFlags != 0 && parent != null)
		{
			if ((alignFlags & ALIGN_LEFT) != 0)
				x1 = parent.w1 - w1;
			if ((alignFlags & ALIGN_RIGHT) != 0)
				w1 = parent.w1 - x1;
			if ((alignFlags & ALIGN_TOP) != 0)
				y1 = parent.h1 - h1;
			if ((alignFlags & ALIGN_BOTTOM) != 0)
				h1 = parent.h1 - y1;
			System.out.println("align " + this + " to " + getSize());
		}
	}
	
	public void setAlignFlags(String flagset)
	{
		int flags = 0;
		flagset = flagset.toLowerCase();
		for (int i=0; i<flagset.length(); i++)
		{
			char ch = flagset.charAt(i);
			switch (ch)
			{
			case 'l' : flags |= ALIGN_LEFT; break;
			case 'r' : flags |= ALIGN_RIGHT; break;
			case 't' : flags |= ALIGN_TOP; break;
			case 'b' : flags |= ALIGN_BOTTOM; break;
			}
		}
		alignFlags = flags;
	}

	/**
	 * Render the component. NOTE: This should only be called from GLOContext so that the
	 * appropriate clipping rectangle, projection matrix, and other parameters may be set.
	 */
	public abstract void render(GLOContext ctx);

	/**
	 * Event handler mechanism Return true if handled
	 */
	public boolean handleEvent(GLOEvent event)
	{
		return false;
	}

	public void addShader(String name, GLOShader shader)
	{
		if (shaders == null)
			shaders = new HashMap();
		shaders.put(name, shader);
	}

	public void addShadersFrom(GLOComponent cmpt)
	{
		if (cmpt.shaders != null)
		{
			Iterator it = cmpt.shaders.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry me = (Map.Entry) it.next();
				addShader((String) me.getKey(), (GLOShader) me.getValue());
			}
		}
	}

	public GLOShader getShader(String name)
	{
		GLOShader s = null;
		if (shaders != null)
			s = (GLOShader) shaders.get(name);
		if (s == null && parent != null)
			s = parent.getShader(name);
		if (s == null && ctx != null && ctx != this)
			s = ctx.getShader(name);
		return s;
	}

	public void setShader(GLOShader shader)
	{
		if (shader != null)
			shader.set(ctx);
	}

	public GLOShader setShader(String name)
	{
		GLOShader shader = getShader(name);
		if (shader != null)
			shader.set(ctx);
		else
			System.out.println("shader " + name + " not found in " + this);
		return shader;
	}

	protected void beginDrag(GLOEvent event)
	{
		if (!dragging)
		{
			dragging = true;
			event.getContext().beginEventCapture(this, GLOMouseEvent.class);
		}
	}

	protected void endDrag(GLOEvent event)
	{
		if (dragging)
		{
			dragging = false;
			event.getContext().endEventCapture(this, GLOMouseEvent.class);
		}
	}

	protected boolean isDragging()
	{
		return dragging;
	}

	public void notifyDataChanged()
	{
		ctx.deliverEvent(new GLODataChangedEvent(ctx, this), this);
	}

	public void notifyAction(Object obj)
	{
		ctx.deliverEvent(new GLOActionEvent(ctx, obj), this);
	}

	// RAISE, LOWER, CLOSE

	public void raise()
	{
		if (parent instanceof GLOContainer)
			 ((GLOContainer) parent).raiseChild(this);
	}

	public void lower()
	{
		if (parent instanceof GLOContainer)
			 ((GLOContainer) parent).lowerChild(this);
	}

	/**
	 * If b is true, sets component visible and raises it. Otherwise, hides the component.
	 */
	public void setRaised(boolean b)
	{
		if (b)
		{
			setVisible(true);
			raise();
		} else
			setVisible(false);
	}

	/**
	 * @return true if component is visible and is topmost
	 */
	public boolean getRaised()
	{
		if (isVisible() && (parent instanceof GLOContainer))
		{
			GLOContainer cont = (GLOContainer) parent;
			int l = cont.getChildCount();
			if (l > 0 && cont.getChild(l - 1) == this)
				return true;
		}
		return false;
	}

	public void close()
	{
		if (parent instanceof GLOContainer)
			 ((GLOContainer) parent).remove(this);
	}

	// RENDERING FUNCTIONS

	public void drawBox()
	{
		drawBox(ctx, origin.x, origin.y, w1, h1);
	}

	public static void drawBox(GLOContext ctx, Rectangle r)
	{
		drawBox(ctx, r.x, r.y, r.width, r.height);
	}

	public static void drawBox(GLOContext ctx, float x, float y, float w, float h)
	{
		GL gl = ctx.getGL();
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex2f(x, y);
		gl.glVertex2f(x + w, y);
		gl.glVertex2f(x + w, y + h);
		gl.glVertex2f(x, y + h);
		gl.glEnd();
	}

	public void drawTexturedBox()
	{
		drawTexturedBox(ctx, origin.x, origin.y, w1, h1);
	}

	public static void drawTexturedBox(GLOContext ctx, Rectangle r)
	{
		drawTexturedBox(ctx, r.x, r.y, r.width, r.height);
	}

	public static void drawTexturedBox(GLOContext ctx, int x, int y, int w, int h)
	{
		GL gl = ctx.getGL();
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, 1);
		gl.glVertex2i(x, y);
		gl.glTexCoord2f(1, 1);
		gl.glVertex2i(x + w, y);
		gl.glTexCoord2f(1, 0);
		gl.glVertex2i(x + w, y + h);
		gl.glTexCoord2f(0, 0);
		gl.glVertex2i(x, y + h);
		gl.glEnd();
	}

	public static void drawTexturedBox(GLOContext ctx, float x, float y, float w, float h)
	{
		GL gl = ctx.getGL();
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(x, y);
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(x + w, y);
		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(x + w, y + h);
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(x, y + h);
		gl.glEnd();
	}

	public String toString()
	{
		String s = super.toString();
		int pos = s.lastIndexOf('.');
		if (pos >= 0 && pos < s.length() - 2)
			s = s.substring(pos + 1);
		return s + '(' + getName() + ')';
	}

	// PROPERTY HELPER FNS

	public PropertyAware getPropertyTop()
	{
		if (parent != null)
			return parent.getPropertyTop();
		else
			return ctx.getPropertyTop();
	}

	public Object getForPropertyKey(String key)
	{
		try
		{
			if (key != null && key.startsWith("."))
				return PropertyEvaluator.get(this, key.substring(1));
			else
				return PropertyEvaluator.get(getPropertyTop(), key);
		} catch (PropertyNotFoundException pnfe)
		{
			return null;
		}
	}

	public void setForPropertyKey(String key, Object value)
	{
		try
		{
			if (key != null && key.startsWith("."))
				PropertyEvaluator.set(this, key.substring(1), value);
			else
				PropertyEvaluator.set(getPropertyTop(), key, value);
		} catch (Exception pre)
		{
			System.out.println("Error setting property " + key + " to " + value);
			pre.printStackTrace();
		}
	}

	public Object getForPropertyKey(PropertyEvaluator pe)
	{
		try
		{
			return pe.get(pe.isLocal() ? this : getPropertyTop());
		} catch (PropertyNotFoundException pnfe)
		{
			return null;
		}
	}

	public void setForPropertyKey(PropertyEvaluator pe, Object value)
	{
		try
		{
			pe.set(pe.isLocal() ? this : getPropertyTop(), value);
		} catch (Exception pre)
		{
			System.out.println("Error setting property " + pe + " to " + value);
			pre.printStackTrace(System.out);
		}
	}

	public String getKey(PropertyEvaluator pe)
	{
		return (pe != null) ? pe.getKey() : null;
	}

	// PROPERTIES

	private static PropertyHelper prophelp = new PropertyHelper(GLOComponent.class);

	static {
		prophelp.registerGet("x", "getX");
		prophelp.registerGet("y", "getY");
		prophelp.registerGet("width", "getWidth");
		prophelp.registerGet("height", "getHeight");
		prophelp.registerGet("parent", "getParent");
		prophelp.registerGet("showing", "isShowing");
		prophelp.registerGet("context", "getContext");
		prophelp.registerGetSet("visible", "Visible", boolean.class);
		prophelp.registerGetSet("raised", "Raised", boolean.class);
		prophelp.registerGetSet("name", "Name", String.class);
		prophelp.registerSet("alignflags", "setAlignFlags", String.class);
	}

	public Object getProp(String key)
	{
		if (key.startsWith("$"))
		{
			return getChildNamed(key.substring(1));
		} else
		{
			return prophelp.getProp(this, key);
		}
	}

	public void setProp(String key, Object value)
	{
		if ("x".equals(key))
			setPosition(PropertyUtil.toInt(value), getY());
		else if ("y".equals(key))
			setPosition(getX(), PropertyUtil.toInt(value));
		else if ("width".equals(key))
			setSize(PropertyUtil.toInt(value), getHeight());
		else if ("height".equals(key))
			setSize(getWidth(), PropertyUtil.toInt(value));
		else if ("type".equals(key));
		else if ("parent".equals(key));
		else if (key.startsWith("shader$") && value != null)
			addShader(key.substring(7), ctx.getShader(value.toString()));
		else
			prophelp.setProp(this, key, value);
	}

}
