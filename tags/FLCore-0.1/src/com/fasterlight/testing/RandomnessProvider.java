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
package com.fasterlight.testing;

import java.util.Random;

import com.fasterlight.vecmath.Vector3d;

/**
  * A good source of randomness, like a really hot cup of tea.
  */
public class RandomnessProvider
{
	Random random;

	//

	public RandomnessProvider()
	{
		random = new Random(0);
	}

	public RandomnessProvider(Random random)
	{
		this.random = random;
	}

	/**
	  * Returns the random object.
	  */
	public Random getRandom()
	{
		return random;
	}

	/**
	  * Returns random number x where 0 <= x < a
	  */
	public double rnd(double a)
	{
		return random.nextDouble()*a;
	}

	/**
	  * Returns random number x where a <= x < b
	  */
	public double rnd(double a, double b)
	{
		return rnd(b-a)+a;
	}

	/**
	  * Returns random number x where 0 <= x < b
	  */
	public long rnd(long a)
	{
		return Math.abs(random.nextLong()) % a;
	}

	/**
	  * Returns random number x where a <= x < b
	  */
	public long rnd(long a, long b)
	{
		return rnd(b-a)+a;
	}

	/**
	  * Returns random number x where 0 <= x < b
	  */
	public int rnd(int a)
	{
		return Math.abs(random.nextInt()) % a;
	}

	/**
	  * Returns random number x where a <= x < b
	  */
	public int rnd(int a, int b)
	{
		return rnd(b-a)+a;
	}

	/**
	  * Returns random angle x where 0 <= x < Math.PI*2
	  */
	public double rndangle()
	{
		return rnd(0, Math.PI*2);
	}

	/**
	  * Returns random angle, but there is 'p' probability
	  * of it being a "special" angle -- that is, a multiple
	  * of PI/4
	  */
	public double rndangle(float p)
	{
		if (random.nextFloat() < p)
			return rndspecialangle();
		else
			return rndangle();
	}

	/**
	  * An angle that is a multiple of PI/4
	  * -PI*2 <= x <= PI*2
	  */
	public double rndspecialangle()
	{
		return rnd(-8,9) * Math.PI/4;
	}

	/**
	  * Returns a random normalized vector.
	  */
	public Vector3d rndvec()
	{
		Vector3d v = new Vector3d(rnd(-1.0,1.0),rnd(-1.0,1.0),rnd(-1.0,1.0));
		v.normalize();
		return v;
	}

	/**
	  * Returns a random normalized vector, with a 'prob'
	  * chance that it is "special" (some verts 0, 1, or -1)
	  * NOT normalized.
	  */
	public Vector3d rndspecialvec(float prob)
	{
		double arr[] = new double[3];
		for (int i=0; i<3; i++)
		{
			if (random.nextFloat() < prob)
			{
				arr[i] = rnd(-1,2);
			} else
				arr[i] = rnd(-1.0,1.0);
		}
		return new Vector3d(arr);
	}

	/**
	  * Returns a random vector with a given magnitude.
	  */
	public Vector3d rndvec(double mag)
	{
		Vector3d v = rndvec();
		v.scale(mag);
		return v;
	}

	public double rndgauss()
	{
		return random.nextGaussian();
	}

	public double rndgauss(double bias, double scale)
	{
		return rndgauss()*scale + bias;
	}

	public double rndexp(double a, double b)
	{
		return (b-a)/(1 + Math.abs(rndgauss())) + a;
	}

	public static double clamp(double x, double a, double b)
	{
		return Math.max(a, Math.min(b, x));
	}
}
