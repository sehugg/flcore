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
package com.fasterlight.glout.texutil;

import java.net.URL;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * This abstract Class defines the interface
 * for ALL texture loaders !
 *
 * @see TextureTool
 * @see	GLImageCanvas
 */
public abstract class TextureLoader
extends TextureTool
{
	protected TextureLoader(GL gl, GLU glu)
	{ super(gl, glu); }

    /**
     * Loads an Texture
     *
     * @param fname The filename
     */
     public abstract boolean readTexture(String fname);

    /**
     * Loads an Texture
     *
     * @param base The base URL
     * @param uri  The additional uri for the base URL
     */
     public abstract boolean readTexture(URL base, String uri);
}

