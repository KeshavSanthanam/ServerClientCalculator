JAVAC = javac
JAVAFLAGS = -g

all: MathServer MathClient
MathServer: MathServer.java
	$(JAVAC) $(JAVAFLAGS) MathServer.java
MathClient: MathClient.java
	$(JAVAC) $(JAVAFLAGS) MathClient.java
clean:
	rm -f *.class