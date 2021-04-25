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

public class bomberClient{

    BufferedReader in;
    PrintWriter out;
    String name = "";
    int player;
    JFrame frame2 = new JFrame("Bomberman");
    ArrayList<String> people = new ArrayList<String>();
    DefaultListModel model = new DefaultListModel();
    JList<String> list = new JList(model);
    JTextField textField = new JTextField(40);
    JTextArea messageArea = new JTextArea(8, 40);
    JButton start = new JButton("Start");
    JButton ready = new JButton("Ready");
    String map = "cool_map.csv";
    boolean crazybombs = false;

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
            	if(textField.getText().equalsIgnoreCase("!help")) {
            		 messageArea.append("**SERVER**: Server Commands \n!help\n!MAPOPTIONS\n!SETMAP <mapname>\n!GETMAP\n!RANDOM\n!POWERUP\n!ADDBOT\n!REMOVEBOT\n!ServerStats\n");
            	}
            	else{
            		out.println(textField.getText());
            	}
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
        messageArea.append("**SERVER**: Welcome to bomberman! Use the !help command.\n");
        
        String[] mapData;
    
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
            	String [] parts = line.replace("CanStart ","").split(" ");
                player = Integer.parseInt(parts[0]);
                mapData = parts[1].split("X");
                messageArea.append("**SERVER**: Match Starting...\n");
                break;
            }
            else if (line.startsWith("MAPOPTIONS ")) {
                String[] maps = line.replace("MAPOPTIONS ","").split(",");
                messageArea.append("**SERVER**: Map Options:\n");
                for(int i = 0; i < maps.length; i++){
                    messageArea.append(maps[i] + "\n");
                }
            }
            else if (line.startsWith("MAPSET ")) {
                map = line.replace("MAPSET ","");
                messageArea.append("**SERVER**: Game map set to " + map + "\n");
            }
            else if (line.startsWith("ADDBOT ")) {
                int bots = Integer.parseInt(line.replace("ADDBOT ",""));
                messageArea.append("**SERVER**: Number of bots set to " + bots + "\n");
            }
            else if (line.startsWith("REMOVEBOT ")) {
                int bots = Integer.parseInt(line.replace("REMOVEBOT ",""));
                messageArea.append("**SERVER**: Number of bots set to " + bots + "\n");
            }
            else if (line.startsWith("CRAZYBOMBS ")) {
                crazybombs = Boolean.parseBoolean(line.replace("CRAZYBOMBS ",""));
                messageArea.append("**SERVER**: Crazy bombs set to " + crazybombs + "\n");
            }
            else if (line.startsWith("POWERUP ")) {
                Boolean powerup = Boolean.parseBoolean(line.replace("POWERUP ",""));
                messageArea.append("**SERVER**: Powerup set to " + powerup + "\n");
            }
            else if (line.startsWith("RANDOM ")) {
            	Boolean randomGen = Boolean.parseBoolean(line.replace("RANDOM ",""));
                messageArea.append("**SERVER**: Random map generation set to " + randomGen + "\n");
            }
            else if (line.startsWith("Server Stats: ")) {
            	String stats = line.replace("Server Stats: ","").replace(" | ", "\n");
                messageArea.append("**SERVER**: Requested Statistics \n" + stats);
            }
            else if (line.startsWith("Left ")) {
            	String[] leftParts = line.split(",");
                for(int z = 0; z < model.getSize(); z++){
                    Object value = model.elementAt(z);
                    if(value.toString().equals(leftParts[1] + " (Ready)") || value.toString().equals(leftParts[1])){
                        model.removeElementAt(z);
                    }
                }
                messageArea.append("**SERVER**: " + leftParts[1] + " left the match\n");
            }

        }
        
        //Create map file
        String[] currentMapData = mapData[0].split("N");
        File file = new File("./currentMap.csv");      
		FileWriter filewriter = null;
		try {
			filewriter = new FileWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < currentMapData.length; i++) {
			if(currentMapData[i].startsWith(",")) {
				filewriter.write(currentMapData[i].substring(1)+"\n");
			}
			else {
				filewriter.write(currentMapData[i]+"\n");
			}
		}
		filewriter.close();
        
        
        ResourceCollection.readFiles();
        ResourceCollection.init();

        GamePanel game;
        try {
            game = new GamePanel("./currentMap.csv",out,in,player,mapData,messageArea);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println(e + ": Program args not given");
            game = new GamePanel(null,out,in,player,mapData,messageArea);
        }

        game.initMultiplayer();
        GameWindow window = new GameWindow(game);

        System.gc();
    }
            
}