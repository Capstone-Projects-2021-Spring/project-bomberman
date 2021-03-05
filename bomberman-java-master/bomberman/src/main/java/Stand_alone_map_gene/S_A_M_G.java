/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Stand_alone_map_gene;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author Cujo
 */
public class S_A_M_G {
    public static void main(String[] args) {

    try (PrintWriter writer = new PrintWriter(new File("src\\main\\resources\\maps_gene.csv"))) {

      StringBuilder sb = new StringBuilder();
      sb.append("id,");
      sb.append(',');
      sb.append("Name");
      sb.append('\n');

      sb.append("1");
      sb.append(',');
      sb.append("Prashant Ghimire");
      sb.append('\n');

      writer.write(sb.toString());

      System.out.println("done!");

    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
    }

  }
}
