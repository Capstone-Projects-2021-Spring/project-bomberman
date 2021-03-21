//Imports
import java.net.*;
import java.io.*;

public class bomberManServer {
    // Socket for the server default 80
    private ServerSocket serverSocket;
    //Keep track of the people connected
    public static int peopleConnected = 0;

    //Initialize server
    public static void main(String[] args) {
        bomberManServer server = new bomberManServer();
        server.start(80);
    }

    //Start server
    public void start(int port) {
        try{
            serverSocket = new ServerSocket(port);
            while (true){
                new bomberClient(serverSocket.accept()).start();
            }
        }catch(Exception e){
            System.out.println("Error creating new socket to connect to client");
        }
    }

    public void stop() {
        try{
            serverSocket.close();
        } catch (Exception e){
            System.out.println("Error closing server socket");
        }
    }

    private static class bomberClient extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public bomberClient(Socket socket) {
            this.clientSocket = socket;
            //Keep track of people connected to the server
            peopleConnected += 1;
        }

        public void run() {
            try{
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));

                String inputLine;
                //While there is an incoming stream keep it coming
                while ((inputLine = in.readLine()) != null) {
                    if ("detach".equals(inputLine)) {
                        out.println("detached");
                        break;
                    }
                    //Let the client know how many people are connected
                    out.println("total connected: " + peopleConnected);
                }
                //When there is no more incoming
                in.close();
                out.close();
                clientSocket.close();
                //Update to let the server know someone left
                peopleConnected -= 1;
            } catch (Exception e){
                System.out.println("Error on client thread");
            }

        }
    }
}