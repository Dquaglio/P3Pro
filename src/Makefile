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
clean:
	$(RM) client/*.class
	$(RM) gui/*.class
	$(RM) server/*.class