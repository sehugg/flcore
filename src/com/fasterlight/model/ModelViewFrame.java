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

import java.awt.*;

import javax.media.opengl.*;

import com.fasterlight.glout.*;

public class ModelViewFrame extends GLOAWTComponent
{
	Model3d model;
	ModelRenderer renderer = null;
	int flags;

	ModelViewCanvas glc;
	TextureCache texcache;

	public ModelViewFrame(int w, int h)
	{
		super(w, h);
	}

	public void setModel(Model3d model)
	{
		this.model = model;
		this.renderer = null;
	}

	public void setFlags(int flags)
	{
		this.flags = flags;
	}

	protected void makeComponents()
	{
		super.makeComponents();
		ModelViewCanvas mvc = new ModelViewCanvas();
		mvc.setSize(ctx.getSize());
		ctx.add(mvc);
		texcache = new TextureCache("file:./texs/", ctx.getGL(), ctx.getGLU(), this);
		texcache.init();
	}

	/// INNER CLASS

	class ModelViewCanvas extends GLOZoomable3DCanvas
	{
		public ModelViewCanvas()
		{
			super();
			fardist = 500f;
		}
		public void renderBackground(GLOContext ctx)
		{
			float[] lightPosition = { -1, 0, 1, 0.0f };
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition, 0);
			gl.glEnable(GL.GL_LIGHTING);
			gl.glEnable(GL.GL_LIGHT0);
			gl.glEnable(GL.GL_DEPTH_TEST);
		}
		/** Renders the scene */
		public void renderObject(GLOContext ctx)
		{
			if (renderer == null && model != null)
				renderer = new ModelRenderer(model, ctx.getGL(), texcache, flags);

			if (model == null)
				return;

			if (renderer != null)
				renderer.render();
		}

		public boolean handleEvent(GLOEvent event)
		{
			if (event instanceof GLOKeyEvent)
			{
				GLOKeyEvent ke = (GLOKeyEvent) event;
			}
			return super.handleEvent(event);
		}

	}

	//// END OF INNER CLASS

	//

	public static void main(String[] args) throws Exception
	{
		String fn = "com/fasterlight/model/test.lwo";
		int flags = 0;
		boolean debug = false;

		for (int i = 0; i < args.length; i++)
		{
			if ("-f".equals(args[i]))
			{
				flags = Integer.parseInt(args[++i]);
			} else if ("-d".equals(args[i]))
			{
				debug = true;
			} else
			{
				fn = args[0];
			}
		}

		Model3d model = Model3d.loadModel(fn, debug);

		ModelViewFrame mvf = new ModelViewFrame(600, 600);
		mvf.setModel(model);
		mvf.setFlags(flags);

		GLCanvas canvas = mvf.createGLCanvas();

		Frame f = new Frame();
		f.setLayout(new BorderLayout());
		f.add(canvas, BorderLayout.CENTER);
		f.pack();
		f.setSize(mvf.getSize());
		f.show();

		mvf.start(canvas, f);
	}
}
