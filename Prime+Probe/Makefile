JAVAC = javac
//DEBUG = -g
CFLAGS = $(DEBUG) -deprecation

sim_cache:
	$(JAVAC) sim.java
	@echo "Compilation complete!\nUse 'java sim command-line arguments' to run\nEg: 'java sim hybridPSC 8 14 10 5 0.7 gcc_trace.txt'"

clean:
	-rm *.class

