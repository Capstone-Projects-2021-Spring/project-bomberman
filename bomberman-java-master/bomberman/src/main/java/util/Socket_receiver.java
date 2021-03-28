/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Cujo
 */
public class Socket_receiver {

    private static final int playerNumber = 4;
    private static final int playerUpdateInfoNum = 10;

    public static void main(String[] args) throws IOException {
        //jframe consle
        JFrame frame = new JFrame();
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = frame.getContentPane();
        container.setLayout(new FlowLayout());

        JTextField textField = new JTextField();
        textField.setPreferredSize(new Dimension(150, 25));
        JTextArea jTextArea = new JTextArea();
        jTextArea.setColumns(10);
        jTextArea.setRows(10);

        JLabel label = new JLabel("consle command will appear here");

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String input = textField.getText();
                System.out.println("Input: " + input);

                label.setText(input);
            }
        });
        
        frame.add(jTextArea);
        container.add(textField);
        container.add(okButton);
        container.add(label);

        frame.setVisible(true);
        /*
        //test receiver
        String test = "x=1,y=2-0,FP=2-Down=1,right=1-0";

        String[][] printTest = printTest = parsing(test);
        for (int i = 0; i < playerNumber; i++) {
            for (int j = 0; j < printTest[i].length; j++) {

                jTextArea.append(printTest[i][j] + "\t"); //print each commad
                //jframe goes here to print command
                //run commandcall()

            }
            jTextArea.append("Player" + i+"\n");
        }*/
        

        
        while(true){
        Socket sc = new Socket("3.19.57.244",81);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
        String test = br.readLine();
        //test receiver

        String[][] printTest = printTest = parsing(test);
        for (int i = 0; i < playerNumber; i++) {
            for (int j = 0; j < printTest[i].length; j++) {

                jTextArea.append(printTest[i][j] + "\t"); //print each commad
                //jframe goes here to print command
                command_call(printTest[i][j]);

            }
            jTextArea.append("Player" + i+"\n");
        }
        
        sc.close();
        }
    }
    

    public static String[][] parsing(String Stream_data_input) {

        String[][] processed = new String[playerNumber][playerUpdateInfoNum];
        //split String by |
        String[] player_update = Stream_data_input.split("-");

        for (int i = 0; i < playerNumber; i++) {
            processed[i] = player_update[i].split(",");
        }

        return processed;
    }

    public static void command_call(String command) {
        String[] splited_command = command.split("=");
        switch (splited_command[0]) {
            case "Down":
                //call
                break;
            case "UP":
                //call
                break;
            case "RIGHT":
                //call
                break;
            case "LEFT":
                //call
                break;
            case "FP":
                //call
                //bomber.firepower=Splited_command[2];
                break;
            case "BA":
                //call
                //bomber.bombAmmo=Splited_command[2];
                break;
            case "MS":
                //call 
                //bomber.moveSpeed=Splited_command[2];
                break;
            case "SI":
                //call
                //bomber.spriteIndex=Splited_command[2];
                break;
            case "ST":
                //call
                //bomber.spriteTimer=Splited_command[2];
                break;
            case "MB":
                //call
                //bomber.maxBombs=Splited_command[2];
                break;
            case "BT":
                //call 
                //bomber.bombTimer=Splited_command[2];
                break;
            case "PI":
                //call
                //bomber.pierce=Splited_command[2];
                break;
            case "KICK":
                //call 
                //bomber.kick=Splited_command[2];
                break;
            case "DI":
                //call
                //bomber.direction=Splited_command[2];
                break;
            case "X":
                //call 
                break;
            case "Y":
                //call
                break;
            default:
                //print error command
                break;
        }
    }

}
