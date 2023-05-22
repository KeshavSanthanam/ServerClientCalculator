import java.io.*;
import java.net.*;
import java.util.*;
public class MathClient {
    // define IP and port for server
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 5001;
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket(SERVER_IP, SERVER_PORT);
            // set 10 second timeout
            clientSocket.setSoTimeout(10000); 
            PrintWriter print = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("NOTE: Input should be a binary operation on two integers with one space separating each operand from the operator. Type 'close' to end connection.\n======================");
            System.out.print("Enter your name: ");
            String clientName = inFromUser.readLine();
            System.out.println("Connecting to Math Server...");
            print.println(clientName);
            String response = inFromServer.readLine();
            // check if server is connected
            if (response.equals("CONNECTED")) { 
                System.out.println("Connected to Math Server.");
                System.out.println("You can now send math calculation requests (+, -, *, /, %).");
                System.out.println("NOTE: '/' is for integer division.");
            } else {
                System.out.println("Failed to connect to Math Server. Exiting...");
                clientSocket.close();
                System.exit(0);
            }
            // request that the server perform the calculation
            String request;
            while (true) {
                System.out.println("Enter math calculation requests (or type 'close' to disconnect client):");
                request = inFromUser.readLine();
                if (request.equals("close")) {
                    break;
                }
                print.println(request);
                response = inFromServer.readLine();
                System.out.println("Result returned to client: " + response);
            }
            print.close();
            inFromServer.close();
            inFromUser.close();
            clientSocket.close();
        } catch (SocketTimeoutException e) {
            System.out.println("Timeout Exception");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}