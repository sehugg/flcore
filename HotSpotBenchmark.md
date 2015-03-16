Object creation is considerably fast in JDK 1.3, but there are often instances where objects are created unnecessarily.  One such instance is when objects are created inside of a method and not passed or stored anywhere outside that method.  In C++, such variables would be stored on the stack and destructed when the method exits.

In Java, object creation incurs a heavy cost and must be avoided for tight loops.  Such a performance penalty often causes the programmer to use unnatural techniques, like static temporaries, when trying to work around the problem.

I propose Hotspot could add an optimizer which stores objects on-stack and improve performance drastically (2x) for some applications.

Consider the following code:

```
import javax.vecmath.*;

public class BenchHotspot
{
	Vector3d total = new Vector3d();
	
        static Vector3d sqr(Vector3d a)
        {
                return new Vector3d(a.x*a.x, a.y*a.y, a.z*a.z);
        }

        static void sqrInPlace(Vector3d a)
        {
                a.x *= a.x;
                a.y *= a.y;
                a.z *= a.z;
        }

        static void vecmath1()
        {
                Vector3d v1 = sqr(sqr(sqr(new Vector3d(1,2,3))));
		total.add(v1);
        }

        static void vecmath2()
        {
                Vector3d v = new Vector3d(1,2,3);
                sqrInPlace(v);
                sqrInPlace(v);
                sqrInPlace(v);
		total.add(v);
        }

        public static void main(String[] args)
        {
                int iters = 1000000;
                for (int i=0; i<iters/5; i++) {
                        vecmath1();
                        vecmath2();
                }
                long t1 = System.currentTimeMillis();
                for (int i=0; i<iters; i++)
                        vecmath1();
                long t2 = System.currentTimeMillis();
                for (int i=0; i<iters; i++)
                        vecmath2();
                long t3 = System.currentTimeMillis();
                System.out.println("time 1: " + (t2-t1) + "ms\ttime 2: " + (t3-t2) + " ms");
        }
}
```

Here I have two methods which each perform the same operation: squaring the components of a 3-vector 3 times.  The first method, vecmath1(), performs it in a functional style, which is more natural for this type of application.  Unfortunately, this method creates 4 objects each time it is run.  The second method, vecmath2(), creates only 1 object on each run but performs the same operation.  It does this by passing the Vector3d object to the method sqrInPlace(), which squares the vector without returning it.

On my test runs, vecmath1() was consistently 3 times slower than vecmath2(). (I tried swapping the order of execution to avoid Hotspot compilation slowdown, but it had no effect).

I propose that Hotspot could perform an aggressive optimization to remove the superfluous object creations in vecmath1().  It could do this by performing an analysis on the bytecodes that goes something like this:

  * Each reference (local slot) and parameter in a method can have one or more of these flags: NEW, MIGRATED, STORED, RETURNED.

  * When an object is allocated with the 'new' keyword, it is flagged as NEW.

  * If a method A could possibly store a reference B in one of its fields or in an array, reference B is marked as STORED.

  * If a method could possibly pass a reference A to a method B, the reference A's flags are unioned with the flags of the parameter in method B, and the reference A is marked as MIGRATED. (Note that for this to work, method B must be analyzed before method A, so recursion has to be handled)

  * If a method returns a parameter which has NEW and no other flags, the entire method is marked as RETURNS-NEW.

  * If a slot is assigned the value of a method that is marked RETURNS-NEW, the slot is marked NEW.

  * If a reference could possibly be returned by a method, the reference is marked RETURNED.

  * If an object is marked NEW and has no other flags, it may safely be deallocated upon exiting the method.

(Please take all this with a grain of salt, it's not a rigorous analysis, but I have done a sample implementation.)  This analysis can be performed during the bytecode analysis phase, as it is similar to bytecode verification.  I'm sure I forgot quite a few things in this description of the algorithm.  Also, if the objects are known to reside on the stack in a certain location, I'm sure many more code optimizations could take place.

Anyway, having this optimization in Hotspot would greatly improve my vector math code.  So you gotta do it!! :)