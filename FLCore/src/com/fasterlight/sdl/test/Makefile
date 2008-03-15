
tests:
	cd ../native ; make
	/usr/local/jdk1.1.8/bin/java -verbosegc -Djava.library.path=../native com.fasterlight.sdl.test.SpriteTest

sdltest: sdltest.c
	${CC} -I/usr/include/SDL -o $@ sdltest.c -lSDL -lpthread
