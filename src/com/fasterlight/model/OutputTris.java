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

import java.io.*;

/**
  */
public class OutputTris
{
	public static void main(String[] args)
	throws Exception
	{
		DataInputStream din = new DataInputStream(new FileInputStream(args[0]));
		LWOReader r = new LWOReader(din);
		r.readLWO();

		Model3d m = r.getModel();

		System.out.println("end");
		int numfaces = m.getNumFaces();
		for (int i=0; i<numfaces; i++)
		{
			Model3d.Face face = m.getFace(i);
			int vl = face.vertinds.length;
			if (vl > 1)
			{
				System.out.print("triangle ");
				for (int j=0; j<vl; j++)
				{
					System.out.print(face.vertinds[j] + " ");
				}
				System.out.println("end");
			}
		}
		System.out.println("end");
	}
}
