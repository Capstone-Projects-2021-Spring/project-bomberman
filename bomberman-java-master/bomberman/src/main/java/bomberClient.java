//imports
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import util.ResourceCollection;

import java.util.*;
import java.util.List;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.nio.file.*;
import java.net.Socket;

public class bomberClient {

    BufferedReader in;
    PrintWriter out;
    String name = "";
    JFrame frame2 = new JFrame("Bomberman");
    ArrayList<String> people = new ArrayList<String>();
    DefaultListModel model = new DefaultListModel();
    JList<String> list = new JList(model);
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    JButton start = new JButton("Start");
    JButton ready = new JButton("Ready");

    //main method
    public static void main(String[] args) throws Exception {
        //declare client
        bomberClient client = new bomberClient();
        //frame settings
        client.frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame2.setVisible(true);
        //start client
        client.run();
    }

    //client constructor
    public bomberClient() {
        // Layout GUI
        frame2.setSize(400,400);
        model.addElement("Players:");
        list.setModel(model);
        list.setPreferredSize(new Dimension(200, 200));
        list.setLayoutOrientation(JList.VERTICAL);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        frame2.getContentPane().add(list, "West");
        textField.setEditable(true);
        messageArea.setEditable(false);
        GridLayout mapGridLayout = new GridLayout(1,2);
        JPanel p = new JPanel();
        p.setLayout(mapGridLayout);
        p.add(ready);
        p.add(start);
        frame2.getContentPane().add(textField, "North");
        frame2.getContentPane().add(new JScrollPane(messageArea), "Center");
        frame2.getContentPane().add(p, "South");
        frame2.pack();

        //action listener for messenger
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });

        //action listener for ready button
        ready.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(ready.getText().equals("Ready")){
                    out.println("ReadyUp " + name);
                    ready.setText("Unready");
                }
                else{
                    out.println("ReadyDown " + name);
                    ready.setText("Ready");
                }
                
            }
        });

        //action listener for start button
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println("Start");            
            }
        });

    }

    //get name frame
    private String getName() {
    return JOptionPane.showInputDialog(
                        frame2,
                        "Your alias",
                        "Alias",
                        JOptionPane.PLAIN_MESSAGE);
    }

    //server function
    void run() throws IOException {

        // Make connection and initialize streams
        String serverAddress = "127.0.0.1";
        // Set up socket and IO streams
        Socket socket = new Socket(serverAddress, 80);
        in = new BufferedReader(new InputStreamReader(
                                    socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    
        // handle messages from the server
        while (true) {
            String line = in.readLine();
            if (line.startsWith("SendUser")) {
            name = getName();
            out.println(name);
            } else if (line.startsWith("UserList: ")) {
                String playerList = line.replace("UserList: ","");
                String[] p = playerList.split(",");
                for(int i = 0; i < p.length; i++){
                    if(p[i] != ""){
                        if(!people.contains(p[i])){
                            model.addElement(p[i]);
                            people.add(p[i]);
                        }
                    }
                }
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            } else if (line.startsWith("ReadiedUp ")) {
                for(int z = 0; z < model.getSize(); z++){
                    Object value = model.elementAt(z);
                    if(value.toString().equals(line.replace("ReadiedUp ",""))){
                        model.setElementAt(line.replace("ReadiedUp ","") + " (Ready)",z);
                    }
                }
                messageArea.append("**SERVER**: "+ line.replace("ReadiedUp ","") + " is ready" + "\n");
            }
            else if (line.startsWith("ReadiedDown ")) {
                for(int z = 0; z < model.getSize(); z++){
                    Object value = model.elementAt(z);
                    if(value.toString().equals(line.replace("ReadiedDown ","")+" (Ready)")){
                        model.setElementAt(line.replace("ReadiedDown ",""),z);
                    }
                }
                messageArea.append("**SERVER**: "+ line.replace("ReadiedDown ","") + " is no longer ready" + "\n");
            }
            else if (line.startsWith("NotReady")) {
                messageArea.append("**SERVER**: Game can not start till everyone is ready!\n");
            }
            else if (line.startsWith("CanStart")) {
                messageArea.append("**SERVER**: Match Starting...\n");
                break;
            }
            else if (line.startsWith("Left ")) {
                for(int z = 0; z < model.getSize(); z++){
                    Object value = model.elementAt(z);
                    if(value.toString().equals(line.replace("Left ","")+" (Ready)") || value.toString().equals(line.replace("Left ",""))){
                        model.removeElementAt(z);
                    }
                }
                messageArea.append("**SERVER**: " + line.replace("Left ","") + " left the match\n");
            }
             
        }
        ResourceCollection.readFiles();
        ResourceCollection.init();

        GamePanel game;
        try {
            game = new GamePanel("../Maps/cool_map.csv",out);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e + ": Program args not given");
            game = new GamePanel(null,out);
        }

        game.init();
        GameWindow window = new GameWindow(game);

        System.gc();
        while(true) {
        	String line = in.readLine();
        }
    }
            
}