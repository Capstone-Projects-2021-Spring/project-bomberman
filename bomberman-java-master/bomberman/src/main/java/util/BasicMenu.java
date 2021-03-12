import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.nio.file.*;

class BasicMenu{
    public static void main(String[] args){
        //Create Frame
        JFrame frame = new JFrame("Bomber Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);

        JPanel buttonPanel = new JPanel();
        //Create add map button
        JButton Start = new JButton("Start Game");

        buttonPanel.add(Start);


        Start.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                            try{
                            //GameLauncher method = new GameLauncher();
                            GameLauncher.main();
                                //GameLauncher.main(args); //this should run the main function in GameLauncher.java
                            }
                            catch(IOException e){
                                e.printStackTrace();
                            }
                    }
              });
        }



        //Add panel to the frame
        //frame.add(buttonPanel, BorderLayout.NORTH);
}
        /*
        Look into JFrame

        final File folder = new File("./maps");
        ArrayList<String> maps = getMapFiles(folder);
        JList<String> list = new JList(maps.toArray());
        //Set styling for the list
        list.setBounds(80,80,50,50);
        list.setLayoutOrientation(JList.VERTICAL);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        //Create panel for the list
        JPanel listPanel = new JPanel();
        listPanel.add(list);
        //Add the panel to the frame
        frame.add(listPanel, BorderLayout.NORTH);

        //Create Panel for button
        JPanel buttonPanel = new JPanel();
        //Create add map button
        JButton addMap = new JButton("Add Map");
        //Create action listener
        addMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JFileChooser fileChooser = new JFileChooser();
                int retVal = fileChooser.showOpenDialog(null);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                    try{
                        Path src = Paths.get(selectedFile.getPath());
                        Path dest = Paths.get("./maps/" + selectedFile.getName());
                        Files.copy(src,dest,StandardCopyOption.REPLACE_EXISTING);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        buttonPanel.add(addMap);
        //Add panel to the frame
        frame.add(buttonPanel, BorderLayout.SOUTH);*/
