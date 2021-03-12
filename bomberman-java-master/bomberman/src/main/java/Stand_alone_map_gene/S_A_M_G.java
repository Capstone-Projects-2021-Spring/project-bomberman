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
public class S_A_M_G {

    public static boolean contains(final int[] arr, final int key) {
        return Arrays.stream(arr).anyMatch(i -> i == key);
    }

    public static void printRow(int[] row) {
        for (int i : row) {
            System.out.print(i);
            System.out.print("\t");
        }
        System.out.println();
    }

    public static int findIndex(int arr[][], int t) {
        int index=-1;
        for(int i=0; i<player_count;i++){
            if(arr[1][i]==t){
                index = i;
            }
        }
        return index;
    }

    public static void main(String[] args) {

        System.out.println("enter deseire length");
        Scanner in = new Scanner(System.in);
        int length = in.nextInt();

        try ( PrintWriter writer = new PrintWriter(new File("src\\main\\resources\\maps_gene.csv"))) {

            StringBuilder sb = new StringBuilder();
            Random rand = new Random();
            int player_count = 4;
            //player location
            int[][] location = new int[2][player_count];//0 for row 1 for col
            for (int i = 0; i < player_count; i++) {
                while (true) {

                    int rand_row = rand.nextInt(length - 3)+1;
                    if (contains(location[0], rand_row)) {
                        continue;
                    }
                    location[0][i] = rand_row;
                    break;
                }
                while (true) {
                    int rand_col = rand.nextInt(length - 3)+1;
                    if (contains(location[1], rand_col)) {
                        continue;
                    }
                    location[1][i] = rand_col;
                    break;
                }
            }

            for (int[] row : location) {
                printRow(row);//print player locaiton
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
                        int player=findIndex(location,j);
                        System.out.println(player_count);
                        sb.append(Integer.toString(player));
                        sb.append(",");
                        
                        if(location[1][(player-1)]==length-2){
                            sb.append("-1");
                            j++;
                        }else{
                            if(sb.charAt(i*j+j-2)=='-'){
                                continue;
                            }
                            sb.replace((i*j+j-2),(i*j+j-1),"-1");
                            continue;
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

            writer.write(sb.toString());

            System.out.println(
                    "done!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }
}
