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

import javax.media.opengl.GL;

public class TGATextureGrabber
extends TextureGrabber
{
  public TGATextureGrabber(GL gl)
  {
    super(gl);
  }

  public boolean write2File(OutputStream os)
  {
    try {
	    DataOutputStream fout= new DataOutputStream(os);

	    //write TGA header
	    fout.writeByte(0); //ID length, 0 because no image id field
	    fout.writeByte(0); //no color map
	    fout.writeByte(2); //image type (24 bit RGB, uncompressed)
	    fout.writeShort(0); //color map origin, ignore because no color map
	    fout.writeShort(0); //color map length, ignore because no color map
	    fout.writeByte(0); //color map entry size, ignore because no color map
	    fout.writeShort(0); //x origin
	    fout.writeShort(0); //y origin
	    short s = (short)width;
	    fout.writeByte((byte)(s & 0x00ff));      //image width low byte
	    fout.writeByte((byte)((s & 0xff00)>>8)); //image width high byte
	    s = (short)height;
	    fout.writeByte((byte)(s & 0x00ff));      //image height low byte
	    fout.writeByte((byte)((s & 0xff00)>>8)); //image height high byte
	    fout.writeByte(24); //bpp
	    fout.writeByte(0); //description bits

	    byte[] arr = pixels.array();
	    //process image data:
	    // TGA pixels should be written in BGR format,
	    // so R en B should be switched
	    byte tmp;
	    for (int i=0; i<(width*height*3); i+=3) {
	      tmp=arr[i];
	      arr[i]=arr[i+2];
	      arr[i+2]=tmp;
	    }

	    //write TGA image data
	    fout.write(pixels.array(), 0, pixels.limit());

	    fout.flush();
	    fout.close();
    } catch (Exception ex) {
    	System.out.println("TGATextureGrabber.write2File <os> failed !\n"+ex);
	return false;
    }
    return true;
  }
}

