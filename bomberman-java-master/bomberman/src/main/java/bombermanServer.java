//Imports
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                            int q = 0;
                            for (PrintWriter writer : socketWriters) {
                                writer.println("CanStart " + q);
                                q++;
                            }
                        }
                    }
                    else if (input.startsWith("Player")){
                        System.out.println(input);
                        for (PrintWriter writer : socketWriters) {
                            writer.println(input);
                        }
                    }
                    else if (input.startsWith("!MAPOPTIONS ")){
                        String maps = "";
                        for(String m : serverMaps){
                            maps += m + ",";
                        }
                        for (PrintWriter writer : socketWriters) {
                            writer.println("MAPOPTIONS" + maps);
                        }
                    }
                    else if (input.startsWith("!SETMAP ")){
                        map = input.replace("SETMAP ","");
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