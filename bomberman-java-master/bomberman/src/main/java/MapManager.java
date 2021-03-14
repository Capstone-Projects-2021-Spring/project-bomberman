import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.*;
import java.util.List;
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
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
class MapManager{
    public static void main(String args[]){
    	//Create AWS Objects
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIATZZ6LHXNIHI6PCWU", "nJNomgXnz/C8W2m5ma7p1Os1s4F2ygvlnQontDCK");
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion("us-east-2").build();
    	
        //Create Frame
        JFrame frame = new JFrame("Bomber Man");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        //Get the map names
        final File folder = new File("../maps");
        ArrayList<String> maps = getMapFiles(folder);
        DefaultListModel model = new DefaultListModel();
        JList<String> list = new JList(model);
        for(int x = 0; x < maps.size(); x++) {
        	if(maps.get(x).contains(".csv")) {
        		model.addElement(maps.get(x));
        	}
        }
        list.setModel(model);
        //Set styling for the list
        list.setBounds(80,80,50,50);
        list.setLayoutOrientation(JList.VERTICAL);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        //Create panel for the list
        JPanel listPanel = new JPanel();
        listPanel.add(new JScrollPane(list));
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
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.csv","csv");
                fileChooser.setFileFilter(filter);
                int retVal = fileChooser.showOpenDialog(null);
                if (retVal == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                    try{
                        Path src = Paths.get(selectedFile.getPath());
                        Path dest = Paths.get("../maps/" + selectedFile.getName());
                        Files.copy(src,dest,StandardCopyOption.REPLACE_EXISTING);
                        model.addElement(selectedFile.getName());
                        list.setModel(model);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        
        //Make panels for download
        DefaultListModel modelDownload = new DefaultListModel();
        JList<String> listDownload = new JList(modelDownload);
        ArrayList<String> serverMaps = getServerMaps(s3);
        for(int y = 0; y < serverMaps.size(); y++) {
        	if(serverMaps.get(y).contains(".csv")) {
        		modelDownload.addElement(serverMaps.get(y));
        	}
        }
        listDownload.setModel(modelDownload);
        //Set styling for the list
        listDownload.setBounds(80,80,50,50);
        listDownload.setLayoutOrientation(JList.VERTICAL);
        DefaultListCellRenderer renderer2 = (DefaultListCellRenderer) listDownload.getCellRenderer();
        renderer2.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel listDownloadPanel = new JPanel();
        listDownloadPanel.add(listDownload);
        listDownloadPanel.setVisible(false);
        frame.add(listDownloadPanel, BorderLayout.NORTH);
        
        JPanel buttonPanel2 = new JPanel();
        //Create add map button
        JButton download = new JButton("Download");
        JButton back = new JButton("Back");
        
        
        buttonPanel2.add(download);
        buttonPanel2.add(back);
        buttonPanel2.setVisible(false);
        frame.add(buttonPanel2, BorderLayout.CENTER);

        
        
        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	listPanel.setVisible(true);
                buttonPanel.setVisible(true);
                listDownloadPanel.setVisible(false);
                buttonPanel2.setVisible(false);
            }
        });
        
        download.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	String currentMap = listDownload.getSelectedValue();
            	if(!(currentMap == null)) {
            		String bucket = "bombermanmaps";
                	S3Object retMap = downloadMap(s3,bucket,currentMap);
                	InputStream reader = new BufferedInputStream(
        			   retMap.getObjectContent());
        			File file = new File("../Maps/" + currentMap);      
        			OutputStream writer = null;
					try {
						writer = new BufferedOutputStream(new FileOutputStream(file));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

        			int read = -1;

        			try {
						while ( ( read = reader.read() ) != -1 ) {
						    writer.write(read);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

        			try {
						writer.flush();
						writer.close();
	        			reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
        			model.addElement(currentMap);
                }
            }
        });
        
        JButton uploadMap = new JButton("Upload Map");
        uploadMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String currentMap = list.getSelectedValue();
                if(!(currentMap == null)) {
	                String currentMapPath = "../maps/" + list.getSelectedValue();
	                String bucket = "bombermanmaps";
                	uploadMap(s3,bucket,currentMap,currentMapPath);
                	modelDownload.addElement(currentMap);
                }
            }
        });
        JButton downloadMap = new JButton("Download Map");
        downloadMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	listPanel.setVisible(false);
                buttonPanel.setVisible(false);
                listDownloadPanel.setVisible(true);
                buttonPanel2.setVisible(true);
            }
        });
        JButton deleteMap = new JButton("Delete");
        deleteMap.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	String currentMap = list.getSelectedValue();
                if(!(currentMap == null)) {
	                String currentMapPath = "../maps/" + list.getSelectedValue();
	                File fileToDelete = new File(currentMapPath);
	                fileToDelete.delete();
                	model.removeElement(currentMap);
                }
            }
        });
        
        GridLayout blockTypeLayout = new GridLayout(0,4);
        buttonPanel.setLayout(blockTypeLayout);
        
        buttonPanel.add(addMap);
        buttonPanel.add(uploadMap);
        buttonPanel.add(downloadMap);
        buttonPanel.add(deleteMap);
        
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
    
    public static S3Object downloadMap(final AmazonS3 s3, String bucket, String key) {
    	S3Object retMap = null;
    	try {
        	retMap = s3.getObject(bucket, key);
        }
        catch (AmazonS3Exception e) {
        	System.err.println(e.getErrorMessage());
        }
    	return retMap;
    }
    
    public static ArrayList<String> getServerMaps(final AmazonS3 s3){
    	ArrayList<String> serverMaps = new ArrayList<String>();
    	ObjectListing maps = s3.listObjects("bombermanmaps");
    	List<S3ObjectSummary> obj = maps.getObjectSummaries();
    	for(S3ObjectSummary o: obj) {
    		serverMaps.add("" + o.getKey());
    	}
		return serverMaps;
    }
}