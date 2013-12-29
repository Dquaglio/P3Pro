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
		client/Risorsa.java\
		gui/ClientGui.java\
		gui/ServerGui.java

default: classes

classes: $(CLASSES:.java=.class)

start:
	rmiregistry &
	sleep 2
	java server.ServerStarter Razorback1 &
	java server.ServerStarter Razorback2 &
	sleep 2
	java client.ClientStarter Client1 Razorback1 4 A 5 B 4
clean:
	killall -q rmiregistry