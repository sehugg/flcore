
JSDL

A Java(TM) interface for SDL
Version 0.1


This is a initial attempt at creating a set of bindings to SDL from Java.
The only one that is really complete is the Joystick API, and that's because
that's the particular part I need right now :) The Event section is also
pretty well done, though not tested much.  Video and Audio are partially
done.

The code should pretty much explain itself.  I've tried to make things
sort of object-oriented, in keeping with the clean design of the SDL API.
Note that I cache jfieldID and other structures to help speed things up a
bit.

(Also -- I know it's bad form to have C source code in your Java classpath,
but I like it there)

Oh, and you'll want to edit that native/Makefile for your system (if Win32,
see win32-notes.txt)

Use at your own risk.  I have only tested with SDL 1.1.5.

Good luck!


--

Steven Hugg
hugg@fasterlight.com
