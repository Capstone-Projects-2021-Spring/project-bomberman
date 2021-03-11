import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.nio.file.*;
class MapCreator{

    //Status of the current type of block being placed
    public static String mapBlock = "";

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
        for(int i = 0; i < 1024; i++){
            //Create a button for each grid block
            JButton temp = new JButton("");
            //Add event listener to update to the type of grid block
            //the user wants
            temp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                      temp.setText(mapBlock);
                }
             });
            //Add the button with listener to grid panel
            mapPanel.add(temp);
        }

        //Panel for user buttons
        JPanel blockTypePanel = new JPanel();

        //Configure GridLayour
        GridLayout blockTypeLayout = new GridLayout(0,3);

        //Set the layout for the panel
        blockTypePanel.setLayout(blockTypeLayout);

        //Create button for firm wall
        JButton firmWall = new JButton("Firm Wall");
        firmWall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "FW";
            }
         });
        
        //Create button for soft wall
        JButton softWall = new JButton("Soft Wall");
        softWall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "SW";
            }
        });

        //Create button for crate
        JButton crate = new JButton("Crate");
        crate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                  mapBlock = "C";
            }
        });

        //Add the buttons to the panel
        blockTypePanel.add(firmWall);
        blockTypePanel.add(softWall);
        blockTypePanel.add(crate);

        //Add the panels to the frame
        frame.add(mapPanel, BorderLayout.NORTH);
        frame.add(blockTypePanel, BorderLayout.SOUTH);
        frame.pack();
    }

}