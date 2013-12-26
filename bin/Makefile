JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java
CLASSES = \
	server/ServerStarter.java\
	client/ClientStarter.java
		
default: classes

classes: $(CLASSES:.java=.class)

start:
	rmiregistry &
	sleep 2
	java server.ServerStarter Razorback1 &
	java server.ServerStarter Razorback2
	sleep 2
	java client.ClientStarter C1 Razorback1 3 A 1 B 4 C 6 &
	java client.ClientStarter C2 Razorback2 4 A 1 D 2 C 6 &
		
clean:
	$(RM) client/*.class
	$(RM) gui/*.class
	$(RM) server/*.class