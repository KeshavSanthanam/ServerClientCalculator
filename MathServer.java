import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
public class MathServer {
    private static final int PORT = 5001;
    private static ConcurrentHashMap<String, LocalDateTime> connectedClients = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        // establish connection between server and client
        try {
            ServerSocket serverSocket;
            Socket tempSocket;
            System.out.println("MathServer.java is running on port " + PORT);
            while (true) { // will terminate on error (try-catch block)
                serverSocket = new ServerSocket(PORT);
                tempSocket = serverSocket.accept();
                System.out.println("MathClient.java attached: " + tempSocket);
                // multithreading (one for each client)
                MathServerThread MSthread = new MathServerThread(tempSocket);
                MSthread.start();
                serverSocket.close();
            }
        } catch (SocketException err) {
            System.out.println("SocketException");
        } catch (IOException e) {
            System.out.println("Error in MathServer due to missing client connection");
        } 
    }
    public static class MathServerThread extends Thread { // client-specific thread
        private Socket MSsocket;
        private PrintWriter print;
        private BufferedReader input;
        private String clientName;
        public MathServerThread(Socket socket) {
            this.MSsocket = socket;
        }
        @Override
        public void run() {
            try {
                print = new PrintWriter(MSsocket.getOutputStream(), true);
                input = new BufferedReader(new InputStreamReader(MSsocket.getInputStream()));
                // client info-gathering
                clientName = input.readLine();
                System.out.println("Client attached: " + clientName);
                LocalDateTime attachedTime = LocalDateTime.now();
                connectedClients.put(clientName, attachedTime);
                System.out.println("Client " + clientName + " attached at " + attachedTime);
                print.println("CONNECTED"); // client connection complete
                String request;
                while ((request = input.readLine()) != null) {
                    System.out.println("Client " + clientName + " requested: " + request);
                    if (!(request.equals("CLOSE_CONNECTION"))) {
                        String response = processRequest(request);
                        print.println(response);
                    }
                }
                LocalDateTime finalTime = LocalDateTime.now();
                long seconds = ChronoUnit.SECONDS.between(attachedTime, finalTime);
                long ms = ChronoUnit.MILLIS.between(attachedTime, finalTime) % 1000;
                double time = (double)(seconds)+((double)(ms)/1000);
                System.out.println("Duration of client connection: " + time + " seconds.");
                disconnectClient();
            } catch (IOException e) {
                System.out.println("Error in MathServer due to missing client connection");
            }
        }
        String processRequest(String request) {
            int result = 0;
            request = request.trim();
            ArrayList<String> parsedRequest = new ArrayList<String>();
            // parse request and store in parsedRequest
            int front = 0;
            int back = 0;
            while (back < request.length()) {
                if (request.charAt(back) == ' ') {
                    String word = request.substring(front, back);
                    parsedRequest.add(word);
                    front = back + 1;
                }
                back++;
            }
            String lastWord = request.substring(front, back);
            parsedRequest.add(lastWord);
            // perform computation
            try {
                if (parsedRequest.size() != 3) {
                    return "Invalid input. See format above.";
                }
                else if (parsedRequest.get(1).equals("+")) {
                    result = Integer.parseInt(parsedRequest.get(0)) + Integer.parseInt(parsedRequest.get(2));
                }
                else if (parsedRequest.get(1).equals("-")) {
                    result = Integer.parseInt(parsedRequest.get(0)) - Integer.parseInt(parsedRequest.get(2));
                }
                else if (parsedRequest.get(1).equals("*")) {
                    result = Integer.parseInt(parsedRequest.get(0)) * Integer.parseInt(parsedRequest.get(2));
                }
                else if (parsedRequest.get(1).equals("/")) {
                    result = Integer.parseInt(parsedRequest.get(0)) / Integer.parseInt(parsedRequest.get(2));
                }
                else if (parsedRequest.get(1).equals("%")) {
                    result = Integer.parseInt(parsedRequest.get(0)) % Integer.parseInt(parsedRequest.get(2));
                }
                else { // space-delimited string in middle is not a valid operand 
                    return "Invalid input. See format above.";
                }
            } catch(NumberFormatException e) { // not a valid int input 
                return "Invalid input. See format above.";
            } catch(NullPointerException e) {
                return "Invalid input. See format above.";
            }
            return ""+result; // this is just a quick way of converting int to string
        }
        private void disconnectClient() throws IOException {
            // remove from map and close client stuff
            connectedClients.remove(clientName);
            System.out.println("Client " + clientName + " disconnected.");
            print.println("DISCONNECTED");
            print.close();
            input.close();
            MSsocket.close();
        }
    }
}