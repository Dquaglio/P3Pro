JFLAGS = -d
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $*.java
CLASSES = \
		server/ServerStarter.java\
		server/RServer.java\
		server/Server.java\
		server/VistaModel.java\
		client/ClientStarter.java\
		client/Client.java\
		client/RClient.java\
		risorsa/Risorsa.java\
		risorsa/RRisorsa.java\
		gui/ClientGui.java\
		gui/ServerGui.java

default: classes

classes: $(CLASSES:.java=.class)

clean: 
	rm -f *.class
start:
	rmiregistry &
	sleep 2
	xterm -e "java server.ServerStarter Razorback1" &
	xterm -e "java server.ServerStarter Razorback2" &
	sleep 2
	xterm -e "java client.ClientStarter Client1 Razorback1 4 A 5 C 4" &
	xterm -e "java client.ClientStarter Client2 Razorback1 6 D 5 C 4" &
	xterm -e "java client.ClientStarter Client3 Razorback2 2 D 5 B 4" &
	xterm -e "java client.ClientStarter Client4 Razorback2 4 A 5 B 4" &
stop:
	killall -q rmiregistry &
	killall -q xterm &
