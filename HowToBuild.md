Copy the `build.properties.defaults` file to `build.properties`. Uncomment the `jogl.platform` line which represents your platform (Windows, OS X, Linux)

## Building ##

Run Ant file `build.xml` with target `get-dependencies`. This will download and unzip libraries to the "lib" directory.

If using Eclipse, refresh the project to build. If not, run Ant target `jar` to compile source and make the `FLCore.jar` file.