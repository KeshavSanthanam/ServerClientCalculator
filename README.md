# ServerClientCalculator
This Java project uses multithreading to connect one or more instances of a client class (MathClient.java) to a single server (MathServer.java) for the purposes of performing basic mathematical calculations using logic contained at the server-level. 

## Concepts used
Transmission Control Protocol (TCP): allowed for a reliable and constant connection between server and each client until client terminates it's connection (via manual user input)

Multithreading: allowed for multiple synchronous client connections while avoiding identity mismanagement/loss of requests from client(s)

## Additional Files
A Java Makefile is included to allow for faster compilation from the command line. 
