/*
   Copyright (C) 1997,1998,1999
   Kenji Hiranabe, Eiwa System Management, Inc.

   This program is free software.
   Implemented by Kenji Hiranabe(hiranabe@esm.co.jp),
   conforming to the Java(TM) 3D API specification by Sun Microsystems.

   Permission to use, copy, modify, distribute and sell this software
   and its documentation for any purpose is hereby granted without fee,
   provided that the above copyright notice appear in all copies and
   that both that copyright notice and this permission notice appear
   in supporting documentation. Kenji Hiranabe and Eiwa System Management,Inc.
   makes no representations about the suitability of this software for any
   purpose.  It is provided "AS IS" with NO WARRANTY.
*/
package com.fasterlight.util;


import java.io.Serializable;

/**
  * A 2 element point that is represented by single precision
  * inting point x,y coordinates.
  * @version specification 1.1, implementation $Revision: 1.2 $, $Date: 2008-03-15 15:54:02 $
  * @author Kenji hiranabe
  */
public class Point2i extends Tuple2i implements Serializable {
/*
 * $Log: Point2i.java,v $
 * Revision 1.2  2008-03-15 15:54:02  hugg
 * file headers and cleanup
 *
 * Revision 1.1  2004-01-14 02:48:38  Steve
 * ported to JOGL
 *
 * Revision 1.2  2001/03/01 19:00:06  hugg
 * moved com.metagames.game and spif
 *
 * Revision 1.1  2000/03/31 23:04:43  hugg
 * *** empty log message ***
 *
 * Revision 1.1.1.1  1999/11/25 10:55:35  hugg
 *
 *
 * Revision 1.10  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.10  1999/10/05  07:03:50  hiranabe
 * copyright change
 *
 * Revision 1.9  1999/03/04  09:16:33  hiranabe
 * small bug fix and copyright change
 *
 * Revision 1.8  1998/10/16  00:10:11  hiranabe
 * distanceSquared bug(thanks > nhv@laserplot.com)
 *
 * Revision 1.7  1998/10/14  00:49:10  hiranabe
 * API1.1 Beta02
 *
 * Revision 1.6  1998/07/27  04:28:13  hiranabe
 * API1.1Alpha01 ->API1.1Alpha03
 *
 * Revision 1.5  1998/04/10  04:52:14  hiranabe
 * API1.0 -> API1.1 (added constructors, methods)
 *
 * Revision 1.4  1998/04/09  08:18:15  hiranabe
 * minor comment change
 *
 * Revision 1.3  1998/04/09  07:05:18  hiranabe
 * API 1.1
 *
 * Revision 1.2  1998/01/05  06:29:31  hiranabe
 * copyright 98
 *
 * Revision 1.1  1997/11/26  03:00:44  hiranabe
 * Initial revision
 *
 */


    /**
      * Constructs and initializes a Point2i from the specified xy coordinates.
      * @param x the x coordinate
      * @param y the y coordinate
      */
    public Point2i(int x, int y) {
	super(x, y);
    }

    /**
      * Constructs and initializes a Point2i from the specified array.
      * @param p the array of length 2 containing xy in order
      */
    public Point2i(int p[]) {
	super(p);
    }

    /**
      * Constructs and initializes a Point2i from the specified Point2i.
      * @param p1 the Point2i containing the initialization x y data
      */
    public Point2i(Point2i p1) {
	super(p1);
    }

    /**
      * Constructs and initializes a Point2i from the specified Tuple2i.
      * @param t1 the Tuple2i containing the initialization x y data
      */
    public Point2i(Tuple2i t1) {
	super(t1);
    }


    /**
      * Constructs and initializes a Point2i to (0,0).
      */
    public Point2i() {
	// super(); called implicitly.
    }

    /**
      * Computes the square of the distance between this point and point p1.
      * @param  p1 the other point
      */
    public final int distanceSquared(Point2i p1) {
	int dx = x - p1.x;
	int dy = y - p1.y;
	return (dx*dx + dy*dy);
    }

    /**
      * Computes the distance between this point and point p1.
      * @param p1 the other point
      */
    public final float distance(Point2i p1) {
	return (float)Math.sqrt(distanceSquared(p1));
    }

    /**
      * Computes the L-1 (Manhattan) distance between this point and point p1.
      * The L-1 distance is equal to abs(x1-x2) + abs(y1-y2).
      * @param p1 the other point
      */
    public final int distanceL1(Point2i p1) {
	return Math.abs(x-p1.x) + Math.abs(y-p1.y);
    }

    /**
      * Computes the L-infinite distance between this point and point p1.
      * The L-infinite distance is equal to MAX[abs(x1-x2), abs(y1-y2)].
      * @param p1 the other point
      */
    public final int distanceLinf(Point2i p1) {
	return Math.max(Math.abs(x-p1.x), Math.abs(y-p1.y));
    }
}
