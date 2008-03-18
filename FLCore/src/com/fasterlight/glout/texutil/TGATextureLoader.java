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

import java.io.*;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.util.BufferUtil;

/**
 * This is Class implements a TGA texture-loader !
 * At this time, this loader only supports
 * loading files, which are saved with
 * the TGATextureGrabber !
 * This means: 24 bpp, RGB, uncompressed, no-colormap,
 *             no image id-field !
 *
 * @see IOTextureLoader
 * @see TextureLoader
 */
public class TGATextureLoader
extends IOTextureLoader
{
  public TGATextureLoader(GL gl, GLU glu)
  {
	super(gl, glu);
  }

  protected boolean readTexture(InputStream is)
  {
       try {
           int cc;

	   glFormat=GL.GL_RGB;

	   DataInputStream reader = new DataInputStream ( is );

	    //write TGA header
	    reader.readByte(); //ID length, 0 because no image id field
	    reader.readByte(); //no color map
	    cc = reader.readByte(); //image type (24 bit RGB, uncompressed)
	    if(cc!=2)
	    {
		    reader.close();
		    System.out.println("TGATextureLoader: File is not 24bit RGB Data !");
		    error=true;
		    return false;
	    }
	    reader.readShort(); //color map origin, ignore because no color map
	    reader.readShort(); //color map length, ignore because no color map
	    reader.readByte(); //color map entry size, ignore because no color map
	    reader.readShort(); //x origin
	    reader.readShort(); //y origin

	    cc = reader.readByte(); // image width low byte
	    short s = (short)((short)cc & 0x00ff);
	    cc = reader.readByte(); // image width high byte
	    s = (short) ( (short)( ((short)cc & 0x00ff)<<8 ) | s );
	    imageWidth = s;

	    cc = reader.readByte(); // image height low byte
	    s = (short)((short)cc & 0x00ff);
	    cc = reader.readByte(); // image height high byte
	    s = (short) ( (short)( ((short)cc & 0x00ff)<<8 ) | s );
	    imageHeight = s;

	    cc=reader.readByte(); // 24bpp
	    if(cc!=24)
	    {
		    reader.close();
		    System.out.println("TGATextureLoader: File is not 24bpp Data !");
		    error=true;
		    return false;
	    }
	    reader.readByte(); //description bits

	    if(3!=getComponents())
	    {
		    reader.close();
			System.out.println("TGATextureLoader: Currenly only RGB (24bit) data is supported !");
		    error=true;
		    return false;
	    }

	    pixel=BufferUtil.newByteBuffer(imageWidth * imageHeight * 3);

	    //read TGA image data
	    byte[] arr = new byte[pixel.limit()];
	    reader.read(arr, 0, pixel.limit());
	    reader.close();
	    
	    //process image data:
	    // TGA pixels should be written in BGR format,
	    // so R & B should be switched
	    byte tmp;
	    for (int i=0; i<imageWidth*imageHeight*3; i+=3)
	    {
	      tmp=arr[i];
	      arr[i]=arr[i+2];
	      arr[i+2]=tmp;
	    }

	    ((ByteBuffer)pixel).put(arr);
	    pixel.rewind();
	    
	    setTextureSize();
	    return true;

    } catch (Exception ex) {
	    System.out.println("An exception occured, while loading a TGATexture");
	    System.out.println(ex);
	    error=true;
    }
    return false;
  }
}

