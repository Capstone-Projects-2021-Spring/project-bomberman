//Imports
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
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

public class bombermanServer{
    //Server port
    private static final int serverPort = 80;
    //player aliases
    private static ArrayList<String> players = new ArrayList<String>();
    //player lobby status
    private static ArrayList<Boolean> ready = new ArrayList<Boolean>();
    //output object for each client
    private static HashSet<PrintWriter> socketWriters = new HashSet<PrintWriter>();
    //map to be played
    private static String map = "cool_map.csv";
    //crazy bomb status
    private static Boolean crazybombs = false;
    //power up status
    private static Boolean powerup = false;
    //activate random map generation
    private static Boolean randomGen = false;
    //number of ai
    private static int bots = 0;
    
    //AWS Credentials
    private static BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIATZZ6LHXNIHI6PCWU", "nJNomgXnz/C8W2m5ma7p1Os1s4F2ygvlnQontDCK");
    private static final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion("us-east-2").build();

    //Server Map List
    private static ArrayList<String> serverMaps;
    
    //main method
    public static void main(String[] args) throws Exception {
        //Tell console server started
        System.out.println("Bomber Man Server Live...");
        //open listener on port 80
        ServerSocket listener = new ServerSocket(serverPort);
        
        serverMaps = new ArrayList<String>();
    	ObjectListing maps = s3.listObjects("bombermanmaps");
    	List<S3ObjectSummary> obj = maps.getObjectSummaries();
    	for(S3ObjectSummary o: obj) {
    		serverMaps.add("" + o.getKey());
    	}
        try {
            //launch new clients as they connect
            while (true) {
                new bomberClient(listener.accept()).start();
            }
        } finally {
            //when its over close it
            listener.close();
        }
    }

    //bomber client
    private static class bomberClient extends Thread {
        //Users name
        private String user;
        //Lobby status
        private Boolean readyStatus;
        //Socket and IO objects
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        //set socket for the client
        public bomberClient(Socket socket) {
            this.socket = socket;
        }

        //client thread
        public void run() {
            try {
                //instantiate io objects
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
        
                // get the players alias
                while (true) {
                    //Send user command
                    out.println("SendUser");
                    user = in.readLine();
                    if (user == null) {
                        return;
                    }
                    //check if the players list has the name already
                    synchronized (players) {
                        //make sure the user isn't taken
                        if (!players.contains(user)) {
                            //add a new player
                            players.add(user);
                            //add a new false status
                            ready.add(false);
                            break;
                        }
                    }
                }

                //make a list of all the players
                String playerList = "";
                for(String player : players){
                    playerList += player + ",";
                }
                
                //add socket output
                socketWriters.add(out);

                //send everyone the updated list
                for (PrintWriter writer : socketWriters) {
                    writer.println("UserList: " + playerList);
                }
        
                //handle incoming messages
                int position = 0;
                while (true) {
                    //get incoming message
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    else if (input.startsWith("ReadyUp ")){
                        position = players.indexOf(input.replace("ReadyUp ",""));
                        ready.set(position,true);
                        for (PrintWriter writer : socketWriters) {
                            writer.println("ReadiedUp " + input.replace("ReadyUp ",""));
                        }
                    }
                    else if (input.startsWith("ReadyDown ")){
                        position = players.indexOf(input.replace("ReadyDown ",""));
                        ready.set(position,false);
                        for (PrintWriter writer : socketWriters) {
                            writer.println("ReadiedDown " + input.replace("ReadyDown ",""));
                        }
                    }
                    else if (input.startsWith("Start")){
                        if(ready.contains(false)){
                            for (PrintWriter writer : socketWriters) {
                                writer.println("NotReady");
                            }
                        }
                        else{
                            //Need to process the map to be played 
                        	S3Object retMap = s3.getObject("bombermanmaps", map);
                        	InputStream reader = new BufferedInputStream(
                     			   retMap.getObjectContent());
                 			File file = new File("./" + map);      
                 			OutputStream filewriter = null;
         					try {
         						filewriter = new BufferedOutputStream(new FileOutputStream(file));
         					} catch (FileNotFoundException e) {
         						e.printStackTrace();
         					}

                 			int read = -1;

                 			try {
         						while ( ( read = reader.read() ) != -1 ) {
         						    filewriter.write(read);
         						}
         					} catch (IOException e) {
         						e.printStackTrace();
         					}

                 			try {
         						filewriter.flush();
         						filewriter.close();
         	        			reader.close();
         					} catch (IOException e) {
         						e.printStackTrace();
         					}
                 			String mapString = "";
                 			File myObj = new File("./" + map);
             		        Scanner myReader = new Scanner(myObj);
             		        while (myReader.hasNextLine()) {
             		        	String data = myReader.nextLine();
             		        	mapString += data + "N";
             		        }
             		        myReader.close();
             		        
             		       String allmaps = "";
             		        //Create 100 maps
             		        if(!randomGen) {
	             		        for(int v = 0; v < 100; v++) {
	             		        	String [] entries = mapString.split(",");
	                  		        for(int i = 0; i < entries.length; i ++) {
	                  		        	if(!entries[i].contains("-")) {
	                  		        		try {
	                  		        			int entry = Integer.parseInt(entries[i]);
	                  		        			entries[i] = "-1";
	                  		        		}
	                  		        		catch (Exception e) {}
	                  		        	}
	                  		        }
		             		        int toSet = 1;
		             		        int botsSet = 0;
		             		        Random rand = new Random();
		             		        while (toSet <= players.size()) {
		             		        	for(int i = 0; i < entries.length; i ++) {
		             		        		int random = rand.nextInt(100);
		             		        		if(toSet <= players.size() && random < 3 && !entries[i].equals("H") && !entries[i].contains("N")) {
		             		        			entries[i] = "" + toSet;
		             		        			toSet++;
		             		        		}
		             		        		if(random >= 3 && random <= 5 && !entries[i].equals("H") && !entries[i].contains("N") && botsSet < bots) {
		             		        			entries[i] = "AB";
		             		        			botsSet++;
		             		        		}
		             		        		
		             		        		if(toSet > players.size() && botsSet == bots) {
		             		        			break;
		             		        		}		
		             		        	}
		             		        }
		             		        
		             		       if(powerup) { 
			             		       for(int i = 0; i < entries.length; i ++) {
			             		    	    if(!entries[i].contentEquals("S")) {
			             		    	    	continue;
			             		    	    }
			        		        		int random = rand.nextInt(100);
			        		        		if(random < 100 && random >= 97) {
			        		        			entries[i] = "PB";
			        		        		}
			        		        		if(random < 97 && random >= 94) {
			        		        			entries[i] = "PU";
			        		        		}
			        		        		if(random < 94 && random >= 91) {
			        		        			entries[i] = "PM";
			        		        		}
			        		        		if(random < 91 && random >= 88) {
			        		        			entries[i] = "PS";
			        		        		}
			        		        		if(random < 88 && random >= 85) {
			        		        			entries[i] = "PP";
			        		        		}
			        		        		if(random < 85 && random >= 82) {
			        		        			entries[i] = "PK";
			        		        		}
			        		        		if(random < 82 && random >= 79) {
			        		        			entries[i] = "PT";
			        		        		}
			        		        		
			        		        	}
		             		        }
		             		        allmaps += String.join(",",entries) + "X";
	             		        }
             		        }
             		        else {
             		        	//Randomly generate the maps
             		        	Random rand = new Random();
             		        	for(int v = 0; v < 100; v++) {
             		        		int position1 = 0;
             		        		String[] entries = new String[1500];
	                  		        for(int i = 0; i < 50; i ++) {
	                  		        	entries[position1] = "H";
	                  		        	position1++;
	                  		        }
	                  		        entries[49] += "N";
	                  		        for(int j = 0; j < 28; j++) {
	                  		        	for(int i = 0; i < 50; i++) {
	                  		        		if(i == 0) {
	                  		        			entries[position1] = "H";
	                  		        		}
	                  		        		else if (i == 49) {
	                  		        			entries[position1] = "HN";
	                  		        		}
	                  		        		else {
	                  		        			int random = rand.nextInt(100);
	                  		        			if(random <= 40 && random > 10) {
	                  		        				entries[position1] = "S";
	                  		        			}
	                  		        			else if(random <= 10) {
	                  		        				entries[position1] = "H";
	                  		        			}
	                  		        			else {
	                  		        				entries[position1] = "-1";
	                  		        			}
	                  		        			
	                  		        		}
	                  		        		position1++;
	                  		        	}
	                  		        }
	                  		        for(int i = 0; i < 50; i ++) {
	                  		        	entries[position1] = "H";
	                  		        	position1++;
	                  		        }
	                  		        entries[1499] += "N";
		             		        int toSet = 1;
		             		        int botsSet = 0;
		             		        while (toSet <= players.size()) {
		             		        	for(int i = 0; i < entries.length; i ++) {
		             		        		int random = rand.nextInt(100);
		             		        		if(toSet <= players.size() && random < 3 && !entries[i].equals("H") && !entries[i].contains("N")) {
		             		        			entries[i] = "" + toSet;
		             		        			toSet++;
		             		        		}
		             		        		
		             		        		if(random >= 3 && random <= 5 && !entries[i].equals("H") && !entries[i].contains("N") && botsSet < bots) {
		             		        			entries[i] = "AB";
		             		        			botsSet++;
		             		        		}
		             		        		
		             		        		if(toSet > players.size() && botsSet == bots) {
		             		        			break;
		             		        		}	
		             		        	}
		             		        }
		             		        
		             		       if(powerup) { 
			             		       for(int i = 0; i < entries.length; i ++) {
			             		    	    if(!entries[i].contentEquals("S")) {
			             		    	    	continue;
			             		    	    }
			        		        		int random = rand.nextInt(100);
			        		        		if(random < 100 && random >= 97) {
			        		        			entries[i] = "PB";
			        		        		}
			        		        		if(random < 97 && random >= 94) {
			        		        			entries[i] = "PU";
			        		        		}
			        		        		if(random < 94 && random >= 91) {
			        		        			entries[i] = "PM";
			        		        		}
			        		        		if(random < 91 && random >= 88) {
			        		        			entries[i] = "PS";
			        		        		}
			        		        		if(random < 88 && random >= 85) {
			        		        			entries[i] = "PP";
			        		        		}
			        		        		if(random < 85 && random >= 82) {
			        		        			entries[i] = "PK";
			        		        		}
			        		        		if(random < 82 && random >= 79) {
			        		        			entries[i] = "PT";
			        		        		}
			        		        		
			        		        	}
		             		        }
		             		        allmaps += String.join(",",entries) + "X";
	             		        }
             		        }
             		        
                            int q = 0;
                            for (PrintWriter writer : socketWriters) {
                                writer.println("CanStart " + q + " " + allmaps);
                                q++;
                            }
                        }
                    }
                    else if (input.startsWith("Player")){
                        for (PrintWriter writer : socketWriters) {
                            writer.println(input + ": " + System.currentTimeMillis());
                        }
                    }
                    else if (input.startsWith("!ADDBOT")){
                    	bots++;
                        for (PrintWriter writer : socketWriters) {
                            writer.println("ADDBOT " + bots);
                        }
                    }
                    else if (input.startsWith("!REMOVEBOT")){
                        if(bots != 0){
                            bots--;
                        }
                        for (PrintWriter writer : socketWriters) {
                            writer.println("REMOVEBOT " + bots);
                        }
                    }
                    else if (input.startsWith("!MAPOPTIONS")){
                        String maps = "";
                        for(String m : serverMaps){
                            maps += m + ",";
                        }
                        for (PrintWriter writer : socketWriters) {
                            writer.println("MAPOPTIONS " + maps);
                        }
                    }
                    else if (input.startsWith("!SETMAP ")){
                        String temp = input.replace("!SETMAP ","");
                        if(serverMaps.contains(temp)) {
                        	map = temp;
                        }
                        for (PrintWriter writer : socketWriters) {
                            writer.println("MAPSET " + map);
                        }
                    }
                    else if (input.startsWith("!GETMAP")){
                        for (PrintWriter writer : socketWriters) {
                            writer.println("MAPSET " + map);
                        }
                    }
                    else if (input.startsWith("!CRAZYBOMBS")){
                        if(crazybombs == true){
                            crazybombs = false;
                        }
                        else{
                            crazybombs = true;
                        }
                        for (PrintWriter writer : socketWriters) {
                            writer.println("CRAZYBOMBS " + crazybombs);
                        }
                    }
                    else if (input.startsWith("!POWERUP")){
                    	if(powerup == true){
                            powerup = false;
                        }
                        else{
                            powerup = true;
                        }
                        for (PrintWriter writer : socketWriters) {
                            writer.println("POWERUP " + powerup);
                        }
                    }
                    else if (input.startsWith("!RANDOM")){
                    	if(randomGen == true){
                            randomGen = false;
                        }
                        else{
                            randomGen = true;
                        }
                        for (PrintWriter writer : socketWriters) {
                            writer.println("RANDOM " + randomGen);
                        }
                    }
                    else{
                        for (PrintWriter writer : socketWriters) {
                            writer.println("MESSAGE " + user + ": " + input);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
                //If the person left remove them
                if (user != null) {
                    for (PrintWriter writer : socketWriters) {
                        writer.println("Left " + players.indexOf(user) + "," + user);
                    }
                    ready.remove(players.indexOf(user));
                    players.remove(user);
                }
                //remove them from the list of receivers 
                if (out != null) {
                    socketWriters.remove(out);
                }
                try {
                    //close it
                    socket.close();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }
}