/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Cujo
 */
public class Socket_receiver {

    private static final int playerNumber = 4;
    private static final int playerUpdateInfoNum = 10;

    public static void main(String[] args) {
        //test receiver
        String test= "x=1,y=2|0,FP=2|Down=1,right=1,|";
        
        String[][] printTest = parsing(test);
        
        for (int i = 0; i < playerNumber; i++) {
            for (int j = 0; j < playerUpdateInfoNum; j++) {

                System.out.print(printTest[i][j]);

            }
            System.out.println("next Player"+i);
        }
    }

    public static String[][] parsing(String Stream_data_input) {

        String[][] processed = new String[playerNumber][playerUpdateInfoNum];
        //split String by |
        String[] player_update = Stream_data_input.split("|");

        for (int i = 0; i < playerNumber; i++) {
            processed[i] = player_update[i].split(",");
        }

        return processed;
    }
}
