/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Stand_alone_map_gene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Random;

/**
 *
 * @author Cujo
 */
public class S_A_M_G_1 {

    public static int player_count = 4;

    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }


    public static int findIndex(int arr[][], int t, int c) {
        int index = -1;
        for (int i = 0; i < player_count; i++) {
            if (arr[c][i] == t) {
                index = i;
            }
        }
        return index;
    }

    public static void rand_map(String s) {

        Random rand = new Random();
        int length = rand.nextInt(50)+21;

        try ( PrintWriter writer = new PrintWriter(new File(s))) {

            StringBuilder sb = new StringBuilder();
            

            //player location
            int[][] location = new int[2][player_count];//0 for row 1 for col
            for (int i = 0; i < player_count; i++) {
                while (true) {

                    int rand_row = (rand.nextInt(length) / 4) * (i+1) + 1;
                    if (contains(location[0], rand_row)) {
                        continue;
                    }
                    location[0][i] = rand_row;
                    break;
                }
                while (true) {
                    int rand_col = (rand.nextInt(length) / 4) * (i+1) + 1;
                    if (contains(location[1], rand_col)) {
                        continue;
                    }
                    location[1][i] = rand_col;
                    break;
                }
            }
            for (int i = 0;
                    i < length;
                    i++) {

                if (i == 0 || i == (length - 1)) {
                    //top and bottem row is 'h'
                    for (int j = 0; j < length; j++) {
                        sb.append("h,");
                    }
                    sb.append('\n');
                    continue;
                }

                for (int j = 0; j < length; j++) {
                    int rand_spawn = rand.nextInt();
                    if (contains(location[0], i) && contains(location[1], j)) {
                        //spawn point control

                        int player_j = findIndex(location, j, 1);
                        int player_i = findIndex(location, i, 0);
                        if (player_i == player_j) {
                            //System.out.println(player_count);
                            if(player_j==1){
                            sb.append(Integer.toString(player_j + 1));
                            sb.append(",");
                            j++;
                            }else{
                               sb.append(Integer.toString(3));
                                sb.append(",");
                                j++; 
                            }
                            if (location[1][player_j] <= length - 2) {
                                sb.append("-1,");
                                j++;
                                sb.append("-1,");
                                j++;
                            } else {
                                if (sb.charAt(i * length + j - 2) == '-') {
                                    continue;
                                }
                                sb.replace((i * length + j - 2), (i * length + j - 1), "-1,");
                                continue;
                            }
                        }
                    }

                    int rand_obj = rand.nextInt(10);
                    if (j == 0 || j == length - 1) {
                        sb.append("h,");
                        //first and last col is 'h'
                        continue;
                    }
                    if (rand_obj < 1) {
                        //empty space '-1'

                        sb.append("-1,");
                    } else if (rand_obj < 3) {
                        //soild wall 'h'
                        sb.append("h,");
                    } else {
                        //weak wall 's'
                        sb.append("s,");
                    }
                }
                sb.append('\n');
            }

            /*for (int i = 0; i < player_count; i++) {
                if (location[0][i] <= length - 2) {
                    if (sb.charAt(((location[0][i] + 1) * length + location[1][i] - 1)) == '-') {
                        continue;
                    }
                    sb.replace(((location[0][i]+1) * length + location[1][i] - 2), ((location[0][i]+1) * length + location[1][i]), "-1,");

                } else {
                    if (sb.charAt(((location[0][i] - 2) * length + location[1][i] - 1)) == '-') {
                        continue;
                    }
                    sb.replace(((location[0][i] - 1) * length + location[1][i] - 2), ((location[0][i] - 1) * length + location[1][i]), "-1,");

                }
            }*/ //up and below is nearly impossible to locate

            writer.write(sb.toString());

            

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
    public static void rand_map_10(){
        //i for map name
            for(int i=0; i<10 ;i++){
            String s = "src\\main\\resources\\default"+String.valueOf(i)+".csv";
            rand_map(s);
            }
    }
}
