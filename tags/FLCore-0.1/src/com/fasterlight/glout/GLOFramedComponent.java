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

import java.awt.Point;
import java.util.Iterator;

import javax.media.opengl.GL;

import com.fasterlight.spif.*;
import com.fasterlight.vecmath.Vector2f;

	/**
	  * A frame that may contain another component inside.
	  */
	public class GLOFramedComponent extends GLOContainer
	{
		protected int bxs = 16, bys = 16; //todo: const
		protected float tofs = 0.25f; //todo: const
		protected String title;
		protected boolean gotshaders = false;
		protected int quadflags = 7;

		public GLOFramedComponent()
		{
		}

		public GLOFramedComponent(GLOComponent child)
		{
			this();
			add(child);
		}

		// todo: this is wanky
		void checkShaders()
		{
			if (!gotshaders)
			{
				GLOContext ctx = getContext();
				if (ctx != null)
				{
					GLOShader shader = getFrameShader();
					if (shader != null)
					{
						bxs = shader.xsize;
						bys = shader.ysize;
						tofs = shader.frameratio;
					}
				}
			}
		}

		public void setContent(GLOComponent child)
		{
			removeAllChildren();
			if (child != null)
				add(child);
		}

		public GLOComponent getContent()
		{
			if (getChildCount() > 0)
				return getChild(0);
			else
				return null;
		}

		public String getTitle()
		{
			return title;
		}

		public void setTitle(String title)
		{
			this.title = title;
		}

		public int getQuadFlags()
		{
			return quadflags;
		}

		public void setQuadFlags(int quadflags)
		{
			this.quadflags = quadflags;
		}

		public void layout()
		{
			checkShaders();
			super.layout();
			// line up children horiz.
			Iterator it = getChildren();
			int x = 0, y = 0;
			while (it.hasNext())
			{
				GLOComponent cmpt = (GLOComponent) it.next();
				cmpt.setPosition(bxs + x, bys);
				x += cmpt.getWidth();
				y = Math.max(y, cmpt.getHeight());
			}
			this.setSize(x + bxs * 2, y + bys * 2);
		}

		public GLOShader getFrameShader()
		{
			return getShader("frame");
		}

		public GLOShader getTitleShader()
		{
			return getShader("title");
		}

		public void render(GLOContext ctx)
		{
			checkShaders();
			Point o = getOrigin();
			GL gl = ctx.getGL();

			getFrameShader().set(ctx);

			int xx1 = o.x;
			int xx2 = o.x + bxs;
			int xx3 = o.x + w1 - bxs;
			int xx4 = o.x + w1;

			int yy1 = o.y;
			int yy2 = o.y + bys;
			int yy3 = o.y + h1 - bys;
			int yy4 = o.y + h1;

			if ((quadflags & 1) != 0)
			{
				gl.glBegin(GL.GL_QUAD_STRIP);

				gl.glTexCoord2f(0, 1);
				gl.glVertex2i(xx1, yy1);
				gl.glTexCoord2f(0, 1 - tofs);
				gl.glVertex2i(xx1, yy2);

				gl.glTexCoord2f(tofs, 1);
				gl.glVertex2i(xx2, yy1);
				gl.glTexCoord2f(tofs, 1 - tofs);
				gl.glVertex2i(xx2, yy2);

				gl.glTexCoord2f(1 - tofs, 1);
				gl.glVertex2i(xx3, yy1);
				gl.glTexCoord2f(1 - tofs, 1 - tofs);
				gl.glVertex2i(xx3, yy2);

				gl.glTexCoord2f(1, 1);
				gl.glVertex2i(xx4, yy1);
				gl.glTexCoord2f(1, 1 - tofs);
				gl.glVertex2i(xx4, yy2);

				gl.glEnd();
			}

			if ((quadflags & 2) != 0)
			{
				gl.glBegin(GL.GL_QUAD_STRIP);

				gl.glTexCoord2f(0, 1 - tofs);
				gl.glVertex2i(xx1, yy2);
				gl.glTexCoord2f(0, tofs);
				gl.glVertex2i(xx1, yy3);

				gl.glTexCoord2f(tofs, 1 - tofs);
				gl.glVertex2i(xx2, yy2);
				gl.glTexCoord2f(tofs, tofs);
				gl.glVertex2i(xx2, yy3);

				gl.glTexCoord2f(1 - tofs, 1 - tofs);
				gl.glVertex2i(xx3, yy2);
				gl.glTexCoord2f(1 - tofs, tofs);
				gl.glVertex2i(xx3, yy3);

				gl.glTexCoord2f(1, 1 - tofs);
				gl.glVertex2i(xx4, yy2);
				gl.glTexCoord2f(1, tofs);
				gl.glVertex2i(xx4, yy3);

				gl.glEnd();
			}

			if ((quadflags & 4) != 0)
			{
				gl.glBegin(GL.GL_QUAD_STRIP);

				gl.glTexCoord2f(0, tofs);
				gl.glVertex2i(xx1, yy3);
				gl.glTexCoord2f(0, 0);
				gl.glVertex2i(xx1, yy4);

				gl.glTexCoord2f(tofs, tofs);
				gl.glVertex2i(xx2, yy3);
				gl.glTexCoord2f(tofs, 0);
				gl.glVertex2i(xx2, yy4);

				gl.glTexCoord2f(1 - tofs, tofs);
				gl.glVertex2i(xx3, yy3);
				gl.glTexCoord2f(1 - tofs, 0);
				gl.glVertex2i(xx3, yy4);

				gl.glTexCoord2f(1, tofs);
				gl.glVertex2i(xx4, yy3);
				gl.glTexCoord2f(1, 0);
				gl.glVertex2i(xx4, yy4);

				gl.glEnd();
			}

			// draw the title
			if (title != null)
			{
				GLFontServer fs = ctx.getFontServer();
				getTitleShader().set(ctx);
				Vector2f d = fs.getStringSize(title);
				fs.drawText(
					title,
					o.x + w1 / 2 - d.x / 2,
					o.y + bys / 2 + d.y / 2);
			}

			super.render(ctx);
		}

		// PROPERTIES

		private static PropertyHelper prophelp =
			new PropertyHelper(GLOFramedComponent.class);

		static {
			prophelp.registerGetSet("title", "Title", String.class);
			prophelp.registerGetSet("content", "Content", GLOComponent.class);
			prophelp.registerGetSet("quadflags", "QuadFlags", int.class);
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
			} catch (PropertyRejectedException e)
			{
				super.setProp(key, value);
			}
		}

	}
