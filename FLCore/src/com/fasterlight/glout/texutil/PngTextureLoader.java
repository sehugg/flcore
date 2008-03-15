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

import java.awt.image.*;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sixlegs.image.png.PngImage;

/**
 * This is Class implements the PNG texture-loader
 * while using the png-library in package
 * "com.sixlegs.image.png" !
 *
 * @see IOTextureLoader
 * @see TextureLoader
 */
public class PngTextureLoader
extends IOTextureLoader
{
	protected int gray_gl_type;

        public PngTextureLoader(GL gl, GLU glu)
        {
                super(gl, glu);
                gray_gl_type = GL.GL_LUMINANCE;
        }

        /**
          * Set the grayscale color type.
          * Can be GL.GL_ALPHA, GL.GL_LUMINANCE, or GL.GL_INTENSITY
          * (default is GL.GL_LUMINANCE)
          */
        public void setGrayType(int type)
        {
           this.gray_gl_type = type;
        }

        protected boolean readTexture(InputStream is)
        {
          try {
            int len;
            PngImage png = new PngImage(is);

            imageWidth = png.getWidth();
            imageHeight = png.getHeight();

            // Read entire PNG image (doesn't throw exceptions)
            int[] iPixels = new int[imageWidth * imageHeight];

            PixelGrabber pp=new PixelGrabber(png,
                                             0,0,
                                             imageWidth, imageHeight,
                                             iPixels,
                                             0,
                                             imageWidth);
            try
            {
                pp.grabPixels();
            }
            catch (InterruptedException e)
            {
                System.err.println("interrupted waiting for pixel!");
                error=true;
                return false;
            }
            if ((pp.getStatus() & ImageObserver.ABORT) != 0)
            {
                System.err.println("image fetch aborted or errored");
                error=true;
                return false;
            }

            // bitDepth beachten
            switch(png.getColorType())
            {
                case PngImage.COLOR_TYPE_GRAY:
	                 glFormat = gray_gl_type;
                    break;
                case PngImage.COLOR_TYPE_GRAY_ALPHA:
                    glFormat=GL.GL_LUMINANCE_ALPHA;
                    break;
                case PngImage.COLOR_TYPE_RGB:
                    glFormat=GL.GL_RGB;
                    break;
                case PngImage.COLOR_TYPE_RGB_ALPHA:
                    glFormat=GL.GL_RGBA;
                    break;
                case PngImage.COLOR_TYPE_PALETTE:
                	// todo: support COLOR_TYPE_INDEXED?
                    glFormat=GL.GL_RGB;
                    break;
                default:
                    error=true;
                    System.err.println("unsupported format");
                    return false;
            };

            int ncmpts = getComponents();
            pixel = ByteBuffer.allocate(imageWidth * imageHeight * ncmpts);
            byte[] arr = (byte[])pixel.array();

            byte alpha=0;
            byte red=0;
            byte green=0;
            byte blue=0;
            int offset=0;
            int aPixel;
            for(int y=imageHeight-1; y>=0; y--)
            {
              for(int x=0;x<imageWidth;x++)
              {
                aPixel = iPixels[y*imageWidth + x];

                switch (glFormat)
                {
                   case GL.GL_RGBA:
                      arr[offset] = (byte)(aPixel>>16);
                      arr[offset+1] = (byte)(aPixel>>8);
                      arr[offset+2] = (byte)(aPixel>>0);
                      arr[offset+3] = (byte)(aPixel>>24);
                      offset += 4;
                      break;
                   case GL.GL_RGB:
                      arr[offset] = (byte)(aPixel>>16);
                      arr[offset+1] = (byte)(aPixel>>8);
                      arr[offset+2] = (byte)(aPixel>>0);
                      offset += 3;
                      break;
                   case GL.GL_LUMINANCE_ALPHA: // todo: untested
                      arr[offset] = (byte)(aPixel);
                      arr[offset+1] = (byte)(aPixel>>24);
                      offset += 2;
                      break;
                   default:
                      arr[offset] = (byte)(aPixel);
                      offset += 1;
                      break;
                }

              }
            }

            setTextureSize();
            return true;

           } catch (Exception e) {
                System.out.println("An exception occured, while loading a PngTexture");
                System.out.println(e);
                error=true;
           }
           return false;
        }

}

