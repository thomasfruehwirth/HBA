JFLAGS = -g
JC = javac

.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) *.java -classpath /usr/lib/jvm/java-7-oracle/jre/lib/plugin.jar 

CLASSES = Communicator.java\
			SwitchImageButton.java\
			ImagePane.java\
			ZoomListener.java\
			ResizeListener.java\
			Listener.java

default: classes

classes: $(CLASSES:.java=.class)

pack:
	jar cvf Communicator.jar *.class
	jarsigner Communicator.jar hbakey

clean:
	rm *.class
	rm *~
