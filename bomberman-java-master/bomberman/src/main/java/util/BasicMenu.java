package util;
import java.lang.Runtime;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class BasicMenu{

    public static void main(String[] args){// Menu(){
        JFrame frame = new JFrame("Bomber Man-menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);
        JPanel Menu = new JPanel();
        frame.add(Menu, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        JButton LocalGame = new JButton("Start Local Game");
         
        LocalGame.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){              
                try {
                    System.out.println("BUTTON pushed");
                    Runtime.getRuntime().exec("java ../GameLauncher.java");
                    System.out.println("BUTTON released");
                    
                }   catch (Exception e) {
                    System.err.println(e + ": Program args not given");
                }
            }
        });
       
        buttonPanel.add(LocalGame);
        frame.add(buttonPanel, BorderLayout.NORTH);
    }
}

/*   JFrame frame = new JFrame("Bomber Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);
        JPanel Menu = new JPanel();
        frame.add(Menu, BorderLayout.NORTH);
        JPanel buttonPanel = new JPanel();
        JButton LocalGame = new JButton("Start Local Game");

        LocalGame.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                RunGame(args);
            }
          
        });

        buttonPanel.add(LocalGame);
        frame.add(buttonPanel, BorderLayout.NORTH);
    }*/