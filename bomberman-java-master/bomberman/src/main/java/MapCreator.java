import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.nio.file.*;
class MapCreator{

    //Status of the current type of block being placed
    public static String mapBlock = "";
    public static String[][] map = new String[32][32];
    public static Boolean spawn1Set = false;
    public static Boolean spawn2Set = false;
    public static Boolean spawn3Set = false;
    public static Boolean spawn4Set = false;

    public static void main(String args[]){

        //Create Frame
        JFrame frame = new JFrame("Bomber Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setVisible(true);

        //Configure Gridlayout
        GridLayout mapGridLayout = new GridLayout(32,32);

        //Create Map Panel
        JPanel mapPanel = new JPanel();

        //Set the layout for the panel
        mapPanel.setLayout(mapGridLayout);

        //Create buttons for the map panel
        for(int i = 0; i < 32; i++){
            for(int j = 0; j < 32; j++){

                if(i == 0 || j == 0 || i == 31 || j == 31){
                    JButton temp = new JButton("H");
                    //Add the button with listener to grid panel
                    mapPanel.add(temp);
                    map[i][j] = "H";
                }
                else{
                    //Create a button for each grid block
                    JButton temp = new JButton("");

                    //Store Cordinates in button
                    temp.setActionCommand("" + i + "," + j);
                    //Add event listener to update to the type of grid block
                    //the user wants
                    temp.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            //Check to make sure they can only set
                            //one of each spawn point
                            if(mapBlock.equals("1")){
                                if(!spawn1Set){
                                    spawn1Set = true;
                                }
                                else{
                                    return;
                                }
                            }
                            else if(mapBlock.equals("2")){
                                if(!spawn2Set){
                                    spawn2Set = true;
                                }
                                else{
                                    return;
                                }
                            }
                            else if(mapBlock.equals("3")){
                                if(!spawn3Set){
                                    spawn3Set = true;
                                }
                                else{
                                    return;
                                }
                            }
                            else if(mapBlock.equals("4")){
                                if(!spawn4Set){
                                    spawn4Set = true;
                                }
                                else{
                                    return;
                                }
                            }
                            else{
                                if(temp.getText().equals("1")){
                                    spawn1Set = false;
                                }
                                else if (temp.getText().equals("2")){
                                    spawn2Set = false;
                                }
                                else if (temp.getText().equals("3")){
                                    spawn3Set = false;
                                }
                                else if(temp.getText().equals("4")){
                                    spawn4Set = false;
                                }
                            }
                            
                            //Set the block type
                            temp.setText(mapBlock);
                            
                            //Get cordinates
                            String location = temp.getActionCommand();
                            String[] cordinates = location.split(",");
                            int x = Integer.parseInt(cordinates[0]);
                            int y = Integer.parseInt(cordinates[1]);
                            
                            //Update the map
                            map[x][y] = mapBlock;
                        }
                    });
                    //Add the button with listener to grid panel
                    mapPanel.add(temp);
                }
            }
        }

        //Panel for user buttons
        JPanel blockTypePanel = new JPanel();

        //Configure GridLayour
        GridLayout blockTypeLayout = new GridLayout(0,7);

        //Set the layout for the panel
        blockTypePanel.setLayout(blockTypeLayout);

        //Create button for firm wall
        JButton firmWall = new JButton("Hard Wall");
        firmWall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "H";
            }
        });
        
        //Create button for soft wall
        JButton softWall = new JButton("Soft Wall");
        softWall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "S";
            }
        });

        //Create button for spawn point 1
        JButton spawnOne = new JButton("Spawn 1");
        spawnOne.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "1";
            }
        });

        //Create button for spawn point 2
        JButton spawnTwo = new JButton("Spawn 2");
        spawnTwo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "2";
            }
        });

        //Create button for spawn point 3
        JButton spawnThree = new JButton("Spawn 3");
        spawnThree.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "3";
            }
        });

        //Create button for spawn point 4
        JButton spawnFour = new JButton("Spawn 4");
        spawnFour.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "4";
            }
        });

        //Create button for enemy ballon
        JButton enemyBallon = new JButton("Enemy");
        enemyBallon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "EB";
            }
        });

        //Add the buttons to the panel
        blockTypePanel.add(firmWall);
        blockTypePanel.add(softWall);
        blockTypePanel.add(spawnOne);
        blockTypePanel.add(spawnTwo);
        blockTypePanel.add(spawnThree);
        blockTypePanel.add(spawnFour);
        blockTypePanel.add(enemyBallon);

        //Panel for map name and submission
        JPanel submitPanel = new JPanel();

        //Create input and submit button
        JTextField mapName = new JTextField(15);
        JButton submitButton = new JButton("Create");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  String name = mapName.getText();
                  try {
                    FileWriter writer = new FileWriter("./maps/" + name + ".csv");
                    for(int i = 0; i < 32; i++){
                        for(int j = 0; j < 32; j++){
                            String currentTile = map[i][j];
                            if(currentTile == "H"){
                                writer.write("H,");
                            }
                            else if (currentTile == "S"){
                                writer.write("S,");
                            }
                            else if (currentTile == "1"){
                                writer.write("1,");
                            }
                            else if (currentTile == "2"){
                                writer.write("2,");
                            }
                            else if (currentTile == "3"){
                                writer.write("3,");
                            }
                            else if (currentTile == "4"){
                                writer.write("4,");
                            }
                            else if (currentTile == "EB"){
                                writer.write("EB,");
                            }
                            else{
                                writer.write("-1,");
                            }
                        }
                        writer.write("\n");
                    }
                    writer.close();
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
            }
        });

        //Add them to the pane;
        submitPanel.add(mapName);
        submitPanel.add(submitButton);

        //Add the panels to the frame
        frame.add(mapPanel, BorderLayout.NORTH);
        frame.add(blockTypePanel, BorderLayout.CENTER);
        frame.add(submitPanel, BorderLayout.SOUTH);
        frame.pack();
    }

}