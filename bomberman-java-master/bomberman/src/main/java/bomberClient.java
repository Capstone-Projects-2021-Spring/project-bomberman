// Imports
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class bomberClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public static void main(String[] args){

        //Create viewing components
        JFrame frame = new JFrame("Bomber Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        //Create list of players
        DefaultListModel model = new DefaultListModel();
        JList<String> list = new JList(model);
        for(int x = 0; x < 4; x++){model.addElement("");}
        frame.add(list);

        //Create new client
        bomberClient client = new bomberClient();
        //Currently local host but IP will change to game server IP
        client.startConnection("127.0.0.1", 80);
        Boolean keepGoing = true;
        Boolean allConnected = false;

        //Configuration for start button
        JPanel matchButton = new JPanel();
        JButton startMatch = new JButton("Start Match");
        matchButton.add(startMatch);
        matchButton.setVisible(false);
        frame.add(matchButton,BorderLayout.SOUTH);

        //Keep data coming from server
        while(keepGoing){
            String response = client.sendMessage("a");
            int players = Integer.parseInt(response);
            allConnected = true;
            for(int i = 0; i < players; i++){
                model.set(i,"Player "+ (i+1) +": Connected");
            }
            for(int j = players; j < 4; j++){
                model.set(j,"Player "+ (j+1) +": Not Connected");
                allConnected = false;
            }
            if(allConnected){
                matchButton.setVisible(true);
            }
            else{
                matchButton.setVisible(false);
            }
            if(response.equals("detached")){
                keepGoing = false;
            }
        }
    }

    //function to connect to server
    public void startConnection(String ip, int port) {
        try{
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (Exception e){
            out.println("Error connecting to server");
        }
    }

    //function to send data to server and get response from server
    public String sendMessage(String msg) {
        try{
            out.println(msg);
            String resp = in.readLine();
            return resp;
        } catch (Exception e){
            return null;
        }
    }

    //killing the connection with the server
    public void stopConnection() {
        try{
            in.close();
            out.close();
            clientSocket.close();
        }catch (Exception e){
            out.println("Error closing connection with server");
        }
    }
}