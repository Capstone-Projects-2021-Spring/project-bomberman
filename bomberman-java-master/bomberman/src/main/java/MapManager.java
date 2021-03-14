import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import java.nio.file.*;
import com.amazonaws.*;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.partitions.model.Region;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CreateBucketRequest;
class MapManager{
    public static void main(String args[]){
    	//Create AWS Objects
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIATZZ6LHXNIHI6PCWU", "nJNomgXnz/C8W2m5ma7p1Os1s4F2ygvlnQontDCK");
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion("us-east-1").build();
    	
        //Create Frame
        JFrame frame = new JFrame("Bomber Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,400);
        frame.setVisible(true);
        //Get the map names
        final File folder = new File("../maps");
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
                        Path dest = Paths.get("../maps/" + selectedFile.getName());
                        Files.copy(src,dest,StandardCopyOption.REPLACE_EXISTING);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        JButton uploadMap = new JButton("Upload Map");
        uploadMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String currentMap = list.getSelectedValue();
                String currentMapPath = "../maps/" + list.getSelectedValue();
                String bucket = "bombermanmaps";
                uploadMap(s3,bucket,currentMap,currentMapPath);
            }
        });
        buttonPanel.add(addMap);
        buttonPanel.add(uploadMap);
        //Add panel to the frame
        frame.add(buttonPanel, BorderLayout.SOUTH);
        		
    }

    public static ArrayList<String> getMapFiles(final File directory){
        ArrayList<String> files = new ArrayList<String>();
        for(final File fileEntry : directory.listFiles()){
            files.add(fileEntry.getName());
        }
        return files;
    }
    
    public static void uploadMap(final AmazonS3 s3, String bucket, String key, String filePath) {
    	try {
        	s3.putObject(bucket,key,new File(filePath));
        }
        catch (AmazonS3Exception e) {
        	System.err.println(e.getErrorMessage());
        }
    }
}