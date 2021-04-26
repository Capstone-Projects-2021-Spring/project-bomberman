
import gameobjects.*;
import util.GameObjectCollection;
import util.Key;
import util.ResourceCollection;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import com.amazonaws.util.NumberUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * JPanel that contains the entire game and game loop logic.
 */
public class GamePanel extends JPanel implements Runnable {

    // Screen size is determined by the map size
    static int panelWidth;
    static int panelHeight;

    public int softwallnumber = 0;
    private Thread thread;
    private boolean running;
    boolean ispaused;
    int resetDelay;
  
    private int mapPhase; // map phase for single player, decide which map to load
   
    boolean tutorial = false;

    private BufferedImage world;
    private Graphics2D buffer;
    private BufferedImage bg;
    private GameHUD gameHUD;
    private GameHUDSingle gameHUDSingle;
    public final int GameType;
    private int mapWidth;
    private int mapHeight;
    private ArrayList<ArrayList<String>> mapLayout;
    private BufferedReader bufferedReader;

    private PrintWriter out;
    private BufferedReader in;
    private int player = -1;


    private HashMap<Integer, Key> controls1;
    private HashMap<Integer, Key> controls2;
    private HashMap<Integer, Key> controls3;
    private HashMap<Integer, Key> controls4;
    private HashMap<Integer, Key> controls5;

    
    //private int enemyAi; //used for enemy ID for enemy generation
    private static double SOFTWALL_RATE;
    
    private static String[] maps = null;
    private static int currentMap = 1;
    private JTextArea messageArea;

    /**
     * Construct game panel and load in a map file.
     *
     * @param filename Name of the map file
     * @param type game, type of the map 
     */
    GamePanel(String filename, int type) {//single player
        this.GameType = 1;//single player
        this.SOFTWALL_RATE = 0.825;
        this.mapPhase = type; // starting map
        this.setFocusable(true);
        this.requestFocus();
        this.setControlSingle();
        this.bg = ResourceCollection.Images.BACKGROUND.getImage();
        this.loadMapFile(filename);
        this.addKeyListener(new GameController(this));
        
        
    }
    GamePanel(String filename) {//multi player
        this.GameType = 0;//multi player
        this.SOFTWALL_RATE = 0.825;
        this.setFocusable(true);
        this.requestFocus();
        this.setControls();
        this.bg = ResourceCollection.Images.BACKGROUND.getImage();
        this.loadMapFile(filename);
        this.addKeyListener(new GameController(this));
        
    }
    GamePanel(String filename, PrintWriter out, BufferedReader in, int player, String[] maps, JTextArea messageArea){//online multiplayer
        this.GameType = 0;//multi player
        this.SOFTWALL_RATE = 1;
        this.setFocusable(true);
        this.requestFocus();
        this.setControlsMultiplayer(player);
        this.out = out;
        this.in = in;
        this.player = player;
        this.maps = maps;
        this.messageArea = messageArea;
        this.bg = ResourceCollection.Images.BACKGROUND.getImage();
        this.loadMapFile(filename);
        this.addKeyListener(new GameController(this));
    }

    /**
     * Initialize the game panel with a HUD, window size, collection of game
     * objects, and start the game loop.
     */
    void init() {
        this.resetDelay = 0;
        GameObjectCollection.init();
        this.gameHUD = new GameHUD();
        this.generateMap();
        this.gameHUD.init();
        this.setPreferredSize(new Dimension(this.mapWidth * 32, (this.mapHeight * 32) + GameWindow.HUD_HEIGHT));
        System.gc();
        this.running = true;
        this.ispaused = false; //set to false for paused
    }

    void initSingle() { // initialize for starting single player
        this.resetDelay = 0;
        GameObjectCollection.init();
        this.gameHUDSingle = new GameHUDSingle();
        this.generateMapSingle();
        this.gameHUDSingle.init();
        this.setPreferredSize(new Dimension(this.mapWidth * 32, (this.mapHeight * 32) + GameWindow.HUD_HEIGHT));
        System.gc();
        this.running = true;
        this.ispaused = false;//set to false for paused
    }

    void tutorial_init(){
        this.tutorial = true;
        this.resetDelay = 0;
        GameObjectCollection.init();
        this.gameHUD = new GameHUD();
        this.generateMap();
        this.gameHUD.init();
        this.setPreferredSize(new Dimension(this.mapWidth * 32, (this.mapHeight * 32) + GameWindow.HUD_HEIGHT));
        System.gc();
        this.running = true;
    }

    void initMultiplayer() { // initialize for starting single player
        this.resetDelay = 0;
        GameObjectCollection.init();
        this.gameHUD = new GameHUD();
        this.generateMapMultiplayer(this.player);
        this.gameHUD.init();
        this.setPreferredSize(new Dimension(this.mapWidth * 32, (this.mapHeight * 32) + GameWindow.HUD_HEIGHT));
        System.gc();
        this.running = true;
    }

    /**
     * Loads the map file into buffered reader or load default map when no file
     * is given. The file should be a file with strings separated by commas ",".
     * Preferred .csv file.
     *
     * @param mapFile Name of the map file
     */
    private void loadMapFile(String mapFile) {
        // Loading map file
        try {
            if (mapFile.equalsIgnoreCase("single")) {
                switch (this.mapPhase) { // change maps depending on mapPhase
                    case 1:
                        this.bufferedReader = new BufferedReader(ResourceCollection.FileSINGLE1.SINGLE1.getFile());
                        break;
                    case 2:
                        this.bufferedReader = new BufferedReader(ResourceCollection.FileSINGLE2.SINGLE2.getFile());
                        break;
                    case 3:
                        this.bufferedReader = new BufferedReader(ResourceCollection.FileSINGLE3.SINGLE3.getFile());
                        break;
                    default:
                        exit();
                        break;
                }
                
            } else {
                this.bufferedReader = new BufferedReader(new FileReader(mapFile));
            }
        } catch (IOException | NullPointerException e) {
            // Load default map when map file could not be loaded
            System.err.println(e + ": Cannot load map file, loading default map");
            this.bufferedReader = new BufferedReader(ResourceCollection.Files.DEFAULT_MAP.getFile());
        }

        // Parsing map data from file
        this.mapLayout = new ArrayList<>();
        try {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.isEmpty()) {
                    continue;
                }
                // Split row into array of strings and add to array list
                mapLayout.add(new ArrayList<>(Arrays.asList(currentLine.split(","))));
            }
        } catch (IOException | NullPointerException e) {
            System.out.println(e + ": Error parsing map data");
            e.printStackTrace();
        }
    }

    /**
     * Generate the map given the map file. The map is grid based and each tile
     * is 32x32. Create game objects depending on the string.
     */
    public void generateMap() {
        // Map dimensions
        this.mapWidth = mapLayout.get(0).size();
        this.mapHeight = mapLayout.size();
        panelWidth = this.mapWidth * 32;
        panelHeight = this.mapHeight * 32;

        this.world = new BufferedImage(this.mapWidth * 32, this.mapHeight * 32, BufferedImage.TYPE_INT_RGB);

        // Generate entire map
        for (int y = 0; y < this.mapHeight; y++) {
            for (int x = 0; x < this.mapWidth; x++) {
                switch (mapLayout.get(y).get(x)) {
                    case ("S"):
                    	if (Math.random() < SOFTWALL_RATE) {
                            BufferedImage sprSoftWall = ResourceCollection.Images.SOFT_WALL.getImage();
                            Wall softWall = new Wall(new Point2D.Float(x * 32, y * 32), sprSoftWall, true);
                            GameObjectCollection.spawn(softWall);

                            softwallnumber++;
                        }
                        break;

                   
                    case ("H"):     // Hard wall; unbreakable
                        // Code used to choose tile based on adjacent tiles
                        int code = 0;
                        if (y > 0 && mapLayout.get(y - 1).get(x).equals("H")) {
                            code += 1;  // North
                        }
                        if (y < this.mapHeight - 1 && mapLayout.get(y + 1).get(x).equals("H")) {
                            code += 4;  // South
                        }
                        if (x > 0 && mapLayout.get(y).get(x - 1).equals("H")) {
                            code += 8;  // West
                        }
                        if (x < this.mapWidth - 1 && mapLayout.get(y).get(x + 1).equals("H")) {
                            code += 2;  // East
                        }
                        BufferedImage sprHardWall = ResourceCollection.getHardWallTile(code);
                        Wall hardWall = new Wall(new Point2D.Float(x * 32, y * 32), sprHardWall, false);
                        GameObjectCollection.spawn(hardWall);
                        break;

                    case ("1"):     // Player 1; Bomber
                        BufferedImage[][] sprMapP1 = ResourceCollection.SpriteMaps.PLAYER_1.getSprites();
                        Bomber player1 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP1,GameType);
                        PlayerController playerController1 = new PlayerController(player1, this.controls1);
                        this.addKeyListener(playerController1);
                        this.gameHUD.assignPlayer(player1, 0);
                        GameObjectCollection.spawn(player1);
                        break;

                    case ("2"):     // Player 2; Bomber
                        BufferedImage[][] sprMapP2 = ResourceCollection.SpriteMaps.PLAYER_2.getSprites();
                        Bomber player2 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP2,GameType);
                        PlayerController playerController2 = new PlayerController(player2, this.controls2);
                        this.addKeyListener(playerController2);
                        this.gameHUD.assignPlayer(player2, 1);
                        GameObjectCollection.spawn(player2);
                        break;

                    case ("3"):     // Player 3; Bomber
                        BufferedImage[][] sprMapP3 = ResourceCollection.SpriteMaps.PLAYER_3.getSprites();
                        Bomber player3 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP3,GameType);
                        PlayerController playerController3 = new PlayerController(player3, this.controls3);
                        this.addKeyListener(playerController3);
                        this.gameHUD.assignPlayer(player3, 2);
                        GameObjectCollection.spawn(player3);
                        break;

                    case ("4"):     // Player 4; Bomber
                        BufferedImage[][] sprMapP4 = ResourceCollection.SpriteMaps.PLAYER_4.getSprites();

                        Bomber player4 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP4, GameType);
                        PlayerController playerController4 = new PlayerController(player4, this.controls4);
                        this.addKeyListener(playerController4);
                        this.gameHUD.assignPlayer(player4, 3);
                        GameObjectCollection.spawn(player4);
                        break;

                    case ("PB"):    // Powerup Bomb
                        Powerup powerBomb = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Bomb);
                        GameObjectCollection.spawn(powerBomb);
                        break;

                    case ("PU"):    // Powerup Fireup
                        Powerup powerFireup = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Fireup);
                        GameObjectCollection.spawn(powerFireup);
                        break;

                    case ("PM"):    // Powerup Firemax
                        Powerup powerFiremax = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Firemax);
                        GameObjectCollection.spawn(powerFiremax);
                        break;

                    case ("PS"):    // Powerup Speed
                        Powerup powerSpeed = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Speed);
                        GameObjectCollection.spawn(powerSpeed);
                        break;

                    case ("PP"):    // Powerup Pierce
                        Powerup powerPierce = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Pierce);
                        GameObjectCollection.spawn(powerPierce);
                        break;

                    case ("PK"):    // Powerup Kick
                        Powerup powerKick = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Kick);
                        GameObjectCollection.spawn(powerKick);
                        break;

                    case ("PT"):    // Powerup Timer
                        Powerup powerTimer = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Timer);
                        GameObjectCollection.spawn(powerTimer);
                        break;

                    case ("EB"):    //Enemy Balloon
//                        BufferedImage EB = ResourceCollection.Images.ENEMY_BAlLOON.getImage();
//                        Enemy enemyBalloon = new Enemy(new Point2D.Float(x * 32, y * 32), EB);
//                        GameObjectCollection.spawn(enemyBalloon);
//
//                        break;
//                    	BufferedImage[][] sprMapP5 = ResourceCollection.SpriteMaps.PLAYER_1.getSprites();
//	                	Bomber player5 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP5,GameType,1);
//	                	PlayerController playerController5 = new PlayerController(player5, this.controls5);
//	                	this.addKeyListener(playerController5);
//	//                	this.gameHUD.assignPlayer(player5, 0);
//	                	GameObjectCollection.spawn(player5);
//	                	break;
	                	
	                	BufferedImage[][] sprMapP5 = ResourceCollection.SpriteMaps.PLAYER_1.getSprites();
	                	Bomber player5 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP5,false);
	                	PlayerController playerController5 = new PlayerController(player5, this.controls5);
	                	this.addKeyListener(playerController5);
	//                	this.gameHUD.assignPlayer(player5, 0);
	                	GameObjectCollection.spawn(player5);
	                	break;

                    default:
                        break;
                }
            }
        }
    }

    public void generateMapSingle() { //single player version
        // Map dimensions
        this.mapWidth = mapLayout.get(0).size();
        this.mapHeight = mapLayout.size();
        panelWidth = this.mapWidth * 32;
        panelHeight = this.mapHeight * 32;
        int enemyID = 0; // enemy ID/ amount
        this.world = new BufferedImage(this.mapWidth * 32, this.mapHeight * 32, BufferedImage.TYPE_INT_RGB);

        // Generate entire map
        for (int y = 0; y < this.mapHeight; y++) {
            for (int x = 0; x < this.mapWidth; x++) {
                switch (mapLayout.get(y).get(x)) {
                    case ("S"):     // Soft wall; breakable
                        if (Math.random() < SOFTWALL_RATE) {
                            BufferedImage sprSoftWall = ResourceCollection.Images.SOFT_WALL.getImage();
                            Wall softWall = new Wall(new Point2D.Float(x * 32, y * 32), sprSoftWall, true);
                            GameObjectCollection.spawn(softWall);
                            softwallnumber++;
                        }
                        break;

                    case ("H"):     // Hard wall; unbreakable
                        // Code used to choose tile based on adjacent tiles
                        int code = 0;
                        if (y > 0 && mapLayout.get(y - 1).get(x).equals("H")) {
                            code += 1;  // North
                        }
                        if (y < this.mapHeight - 1 && mapLayout.get(y + 1).get(x).equals("H")) {
                            code += 4;  // South
                        }
                        if (x > 0 && mapLayout.get(y).get(x - 1).equals("H")) {
                            code += 8;  // West
                        }
                        if (x < this.mapWidth - 1 && mapLayout.get(y).get(x + 1).equals("H")) {
                            code += 2;  // East
                        }
                        BufferedImage sprHardWall = ResourceCollection.getHardWallTile(code);
                        Wall hardWall = new Wall(new Point2D.Float(x * 32, y * 32), sprHardWall, false);
                        GameObjectCollection.spawn(hardWall);
                        break;

                    case ("1"):     // Player 1; Bomber
                        BufferedImage[][] sprMapP1 = ResourceCollection.SpriteMaps.PLAYER_1.getSprites();
                        Bomber player1 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP1, GameType);
                        PlayerController playerController1 = new PlayerController(player1, this.controls1);
                        this.addKeyListener(playerController1);
                        this.gameHUDSingle.assignPlayer(player1);
                        GameObjectCollection.spawn(player1);
                        break;
                    case ("A1"):     // AI 1; enemy
                    	BufferedImage[][] sprMapA1 = ResourceCollection.SpriteMaps.PLAYER_2.getSprites();
                    	Bomber enemy1 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapA1,GameType,1);
	                    //PlayerController playerController5 = new PlayerController(enemy1, this.controls5);
	                    //this.addKeyListener(playerController5);
	                    this.gameHUDSingle.assignAi(enemy1, enemyID);
	                    enemyID++;
	                    GameObjectCollection.spawn(enemy1);
	                    break;
//	                    BufferedImage[][] sprMapP5 = ResourceCollection.SpriteMaps.PLAYER_1.getSprites();
//	                	Bomber player5 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP5,false);
//	                	PlayerController playerController5 = new PlayerController(player5, this.controls5);
//	                	this.addKeyListener(playerController5);
//	//                	this.gameHUD.assignPlayer(player5, 0);
//	                	GameObjectCollection.spawn(player5);
//	                	break;
                    case ("PB"):    // Powerup Bomb
                        Powerup powerBomb = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Bomb);
                        GameObjectCollection.spawn(powerBomb);
                        break;

                    case ("PU"):    // Powerup Fireup
                        Powerup powerFireup = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Fireup);
                        GameObjectCollection.spawn(powerFireup);
                        break;

                    case ("PM"):    // Powerup Firemax
                        Powerup powerFiremax = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Firemax);
                        GameObjectCollection.spawn(powerFiremax);
                        break;

                    case ("PS"):    // Powerup Speed
                        Powerup powerSpeed = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Speed);
                        GameObjectCollection.spawn(powerSpeed);
                        break;

                    case ("PP"):    // Powerup Pierce
                        Powerup powerPierce = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Pierce);
                        GameObjectCollection.spawn(powerPierce);
                        break;

                    case ("PK"):    // Powerup Kick
                        Powerup powerKick = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Kick);
                        GameObjectCollection.spawn(powerKick);
                        break;

                    case ("PT"):    // Powerup Timer
                        Powerup powerTimer = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Timer);
                        GameObjectCollection.spawn(powerTimer);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    public void generateMapMultiplayer(int player) {
        // Map dimensions
        this.mapWidth = mapLayout.get(0).size();
        this.mapHeight = mapLayout.size();
        panelWidth = this.mapWidth * 32;
        panelHeight = this.mapHeight * 32;

        this.world = new BufferedImage(this.mapWidth * 32, this.mapHeight * 32, BufferedImage.TYPE_INT_RGB);

        // Generate entire map
        for (int y = 0; y < this.mapHeight; y++) {
            for (int x = 0; x < this.mapWidth; x++) {
                
                if(mapLayout.get(y).get(x).equals("S")) {     // Soft wall; breakable
                    if (Math.random() < SOFTWALL_RATE) {
                        BufferedImage sprSoftWall = ResourceCollection.Images.SOFT_WALL.getImage();
                        Wall softWall = new Wall(new Point2D.Float(x * 32, y * 32), sprSoftWall, true, true);
                        GameObjectCollection.spawn(softWall);
                        softwallnumber++;
                    }
                }
                    
                
                else if(mapLayout.get(y).get(x).equals("GS")) {      //Generate Soft wall
                    BufferedImage sprSoftWall = ResourceCollection.Images.SOFT_WALL.getImage();
                    Wall softWall = new Wall(new Point2D.Float(x * 32, y * 32), sprSoftWall, true, true);
                    GameObjectCollection.spawn(softWall);
                }


                else if(mapLayout.get(y).get(x).equals("H")) {     // Hard wall; unbreakable
                    // Code used to choose tile based on adjacent tiles
                    int code = 0;
                    if (y > 0 && mapLayout.get(y - 1).get(x).equals("H")) {
                        code += 1;  // North
                    }
                    if (y < this.mapHeight - 1 && mapLayout.get(y + 1).get(x).equals("H")) {
                        code += 4;  // South
                    }
                    if (x > 0 && mapLayout.get(y).get(x - 1).equals("H")) {
                        code += 8;  // West
                    }
                    if (x < this.mapWidth - 1 && mapLayout.get(y).get(x + 1).equals("H")) {
                        code += 2;  // East
                    }
                    BufferedImage sprHardWall = ResourceCollection.getHardWallTile(code);
                    Wall hardWall = new Wall(new Point2D.Float(x * 32, y * 32), sprHardWall, false);
                    GameObjectCollection.spawn(hardWall);
                }

              
                else if(mapLayout.get(y).get(x).equals("PB")) {    // Powerup Bomb
                    Powerup powerBomb = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Bomb);
                    GameObjectCollection.spawn(powerBomb);
                }

                else if(mapLayout.get(y).get(x).equals("PU")) {   // Powerup Fireup
                    Powerup powerFireup = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Fireup);
                    GameObjectCollection.spawn(powerFireup);
                }

                else if(mapLayout.get(y).get(x).equals("PM")) {    // Powerup Firemax
                    Powerup powerFiremax = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Firemax);
                    GameObjectCollection.spawn(powerFiremax);
                }

                else if(mapLayout.get(y).get(x).equals("PS")) {    // Powerup Speed
                    Powerup powerSpeed = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Speed);
                    GameObjectCollection.spawn(powerSpeed);
                }

                else if(mapLayout.get(y).get(x).equals("PP")) {    // Powerup Pierce
                    Powerup powerPierce = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Pierce);
                    GameObjectCollection.spawn(powerPierce);
                }

                else if(mapLayout.get(y).get(x).equals("PK")) {    // Powerup Kick
                    Powerup powerKick = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Kick);
                    GameObjectCollection.spawn(powerKick);
                }

                else if(mapLayout.get(y).get(x).equals("PT")) {    // Powerup Timer
                    Powerup powerTimer = new Powerup(new Point2D.Float(x * 32, y * 32), Powerup.Type.Timer);
                    GameObjectCollection.spawn(powerTimer);
                }

                else if(mapLayout.get(y).get(x).equals("EB")) {  //Enemy Balloon
                    BufferedImage EB = ResourceCollection.Images.ENEMY_BAlLOON.getImage();
                    Enemy enemyBalloon = new Enemy(new Point2D.Float(x * 32, y * 32), EB);
                    GameObjectCollection.spawn(enemyBalloon);	
                }
                
                else if(mapLayout.get(y).get(x).equals("AB")) { 
                	BufferedImage[][] sprMapP3 = ResourceCollection.SpriteMaps.PLAYER_2.getSprites();
                    Bomber player3 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP3,false);
                    PlayerController playerController3 = new PlayerController(player3, this.controls2);
                    this.addKeyListener(playerController3);
                    GameObjectCollection.spawn(player3);
                }
                
                else {
                	if(!mapLayout.get(y).get(x).contains("-")) {
	                	if(Integer.parseInt(mapLayout.get(y).get(x)) == player + 1) {
	                		BufferedImage[][] sprMapP1 = ResourceCollection.SpriteMaps.PLAYER_1.getSprites();
	                        Bomber player1 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP1, GameType,out,player + 1);
	                        PlayerController playerController1 = new PlayerController(player1, this.controls1);
	                        this.addKeyListener(playerController1);
	                        if(player < 4) {
	                        	this.gameHUD.assignPlayer(player1, player);
	                        }
	                        GameObjectCollection.spawn(player1);
	                	}
	                	else {
	                		BufferedImage[][] sprMapP2 = ResourceCollection.SpriteMaps.PLAYER_2.getSprites();
	                        Bomber player2 = new Bomber(new Point2D.Float(x * 32, y * 32 - 16), sprMapP2, GameType,out,Integer.parseInt(mapLayout.get(y).get(x)));
	                        PlayerController playerController2 = new PlayerController(player2, this.controls2);
	                        this.addKeyListener(playerController2);
	                        if(Integer.parseInt(mapLayout.get(y).get(x))-1 < 4) {
	                        	this.gameHUD.assignPlayer(player2, Integer.parseInt(mapLayout.get(y).get(x))-1);
	                        }
	                        GameObjectCollection.spawn(player2);
	                	}
                	}
                }

                
            }
        }
    }

    /**
     * Initialize default key bindings for all players.
     */
    private void setControls() {
        
        this.controls1 = new HashMap<>();
        this.controls2 = new HashMap<>();
        this.controls3 = new HashMap<>();
        this.controls4 = new HashMap<>();
        this.controls5 = new HashMap<>();

        // Set Player 1 controls
        this.controls1.put(KeyEvent.VK_UP, Key.up);
        this.controls1.put(KeyEvent.VK_DOWN, Key.down);
        this.controls1.put(KeyEvent.VK_LEFT, Key.left);
        this.controls1.put(KeyEvent.VK_RIGHT, Key.right);
        this.controls1.put(KeyEvent.VK_SLASH, Key.action);

        // Set Player 2 controls
        this.controls2.put(KeyEvent.VK_W, Key.up);
        this.controls2.put(KeyEvent.VK_S, Key.down);
        this.controls2.put(KeyEvent.VK_A, Key.left);
        this.controls2.put(KeyEvent.VK_D, Key.right);
        this.controls2.put(KeyEvent.VK_E, Key.action);

        // Set Player 3 controls
        this.controls3.put(KeyEvent.VK_T, Key.up);
        this.controls3.put(KeyEvent.VK_G, Key.down);
        this.controls3.put(KeyEvent.VK_F, Key.left);
        this.controls3.put(KeyEvent.VK_H, Key.right);
        this.controls3.put(KeyEvent.VK_Y, Key.action);

        // Set Player 4 controls
        this.controls4.put(KeyEvent.VK_I, Key.up);
        this.controls4.put(KeyEvent.VK_K, Key.down);
        this.controls4.put(KeyEvent.VK_J, Key.left);
        this.controls4.put(KeyEvent.VK_L, Key.right);
        this.controls4.put(KeyEvent.VK_O, Key.action);


    }
    
     private void setControlSingle() {
        
        this.controls1 = new HashMap<>();
         // Set Player 1 controls
         this.controls1.put(KeyEvent.VK_UP, Key.up);
         this.controls1.put(KeyEvent.VK_DOWN, Key.down);
         this.controls1.put(KeyEvent.VK_LEFT, Key.left);
         this.controls1.put(KeyEvent.VK_RIGHT, Key.right);
         this.controls1.put(KeyEvent.VK_SPACE, Key.action);

    }

    private void setControlsMultiplayer(int player){
        this.controls1 = new HashMap<>();
        this.controls2 = new HashMap<>();
        this.controls1.put(KeyEvent.VK_W, Key.up);
        this.controls1.put(KeyEvent.VK_S, Key.down);
        this.controls1.put(KeyEvent.VK_A, Key.left);
        this.controls1.put(KeyEvent.VK_D, Key.right);
        this.controls1.put(KeyEvent.VK_E, Key.action);
    }

    /**
     * When ESC is pressed, close the game
     */
    void exit() {
        this.running = false;
    }
    
    public void pauseGame(){
        this.ispaused = true;
    }
    public void unPause(){
        this.ispaused = false;
    }
    
  

    /**
     * When F5 is pressed, reset game object collection, collect garbage,
     * reinitialize game panel, reload map
     */
    void resetGame() { //reset for multiplayer
        this.init();
    }
    void resetGameSingle(){ //single player reset
        this.initSingle();
    }
    void resetGameMultiplayer(){
        this.initMultiplayer();
    }

    /**
     * Reset only the map, keeping the score
     */
    private void resetMap() { // reset for multi local co-op
        GameObjectCollection.init();
        this.generateMap();
        System.gc();
    }
    private void resetMapSingle(){ // reset map for single player
        GameObjectCollection.init();
        this.generateMapSingle();
        System.gc();
    }
    private void resetMapMultiplayer(){
        GameObjectCollection.init();
        this.loadMapFile("./currentMap.csv");
        this.generateMapMultiplayer(player);
        System.gc();
    }
    private void nextMap(int playerScore){ // hopefully loads next map
        this.mapPhase++;
        this.loadMapFile("single");
        this.resetDelay = 0;
        GameObjectCollection.init();
        this.generateMapSingle();
        this.gameHUDSingle.init(playerScore);
        this.setPreferredSize(new Dimension(this.mapWidth * 32, (this.mapHeight * 32) + GameWindow.HUD_HEIGHT));
        GameLauncher.window.pack();
        System.gc();
    }

    public void addNotify() {
        super.addNotify();

        if (this.thread == null) {
            this.thread = new Thread(this, "GameThread");
            this.thread.start();
        }
    }
    

    /**
     * The game loop. The loop repeatedly calls update and repaints the panel.
     * Also reports the frames drawn per second and updates called per second
     * (ticks).
     */
    public synchronized void MenuPaused(){
        while(this.ispaused){
            try{
               wait(); 
            }catch(Exception e){
                System.out.print(e);
            }
        }
    }
    public synchronized void MenuUnPaused(){
        this.ispaused = true;
        notifyAll();
    }
    
    @Override
    public void run() {
        long timer = System.currentTimeMillis();
        long lastTime = System.nanoTime();

        final double NS = 1000000000.0 / 60.0; // Locked ticks per second to 60
        double delta = 0;
        int fps = 0;    // Frames per second
        int ticks = 0;  // Ticks/Updates per second; should be 60 at all times

        // Count FPS, Ticks, and execute updates
        while (this.running) {
            MenuPaused();
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / NS;
            lastTime = currentTime;

            if (delta >= 1) {
                if(GameType == 1){
                    this.updateSingle(); //single player update
                }else{
                	if(player == -1) {
                		this.update();
                	}
                	else {
                		this.updateMultiplayer();
                	}
                }
                ticks++;
                delta--;
            }

            this.repaint();
            fps++;

            // Update FPS and Ticks counter every second
//            if (System.currentTimeMillis() - timer > 1000) {
//                timer = System.currentTimeMillis();
//                //GameLauncher.window.update(fps, ticks);
//                fps = 0;
//                ticks = 0;
//            }
        }

        System.exit(0);
    }

    /**
     * The update method that loops through every game object and calls update.
     * Checks collisions between every two game objects. Deletes game objects
     * that are marked for deletion. Checks if a player is a winner and updates
     * score, then reset the map.
     */
    private void update() {
        GameObjectCollection.sortBomberObjects();
        
        // Loop through every game object arraylist
        for (int list = 0; list < GameObjectCollection.gameObjects.size(); list++) {
            for (int objIndex = 0; objIndex < GameObjectCollection.gameObjects.get(list).size();) {
                GameObject obj = GameObjectCollection.gameObjects.get(list).get(objIndex);
                if(obj instanceof Bomber) {
	                if(((Bomber) obj).bot == true){
	                	double randominput = Math.random();
	                	if(randominput < 0.25) {
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                	}
	                	else if (randominput < 0.5 && randominput >= 0.25) {
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                	}
	                	else if (randominput < 0.75 && randominput >= 0.5) {
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                	}
	                	else if (randominput < 1 && randominput >= 0.75) {
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                	}
	                }
                }
                obj.update();
                if (obj.isDestroyed()) {
                    // Destroy and remove game objects that were marked for deletion
                    obj.onDestroy();
                    //do something here for checking all objects are destroyed
                    GameObjectCollection.gameObjects.get(list).remove(obj);
                } else {
                    for (int list2 = 0; list2 < GameObjectCollection.gameObjects.size(); list2++) {
                        for (int objIndex2 = 0; objIndex2 < GameObjectCollection.gameObjects.get(list2).size(); objIndex2++) {
                            GameObject collidingObj = GameObjectCollection.gameObjects.get(list2).get(objIndex2);
                            // Skip detecting collision on the same object as itself
                            if (obj == collidingObj) {
                                continue;
                            }

                            // Visitor pattern collision handling
                            if (obj.getCollider().intersects(collidingObj.getCollider())) {
                                // Use one of these
                                collidingObj.onCollisionEnter(obj);
//                                obj.onCollisionEnter(collidingObj);
                            }
                        }
                    }
                    objIndex++;
                }
            }
        }

        // Check for the last bomber to survive longer than the others and increase score
        // Score is added immediately so there is no harm of dying when you are the last one
        // Reset map when there are 1 or less bombers left
        if (!this.gameHUD.matchSet) {
          
            this.gameHUD.updateScore();
        } else {
            // Checking size of array list because when a bomber dies, they do not immediately get deleted
            // This makes it so that the next round doesn't start until the winner is the only bomber object on the map
            if (GameObjectCollection.bomberObjects.size() <= 1) {
            	
            	
                this.resetMap();
                this.gameHUD.matchSet = false;
            }
        }

        // Used to prevent resetting the game really fast
        this.resetDelay++;

        try {
            Thread.sleep(1000 / 144);
        } catch (InterruptedException ignored) {
        }
    }
    private void updateSingle() {
        GameObjectCollection.sortBomberObjects();
        GameObjectCollection.sortEnemyobjects();
        // Loop through every game object arraylist
        for (int list = 0; list < GameObjectCollection.gameObjects.size(); list++) {
            for (int objIndex = 0; objIndex < GameObjectCollection.gameObjects.get(list).size();) {
                GameObject obj = GameObjectCollection.gameObjects.get(list).get(objIndex);
                if(obj instanceof Bomber) {
	                if(((Bomber) obj).bot == true){
	                	double randominput = Math.random();
	                	if(randominput < 0.25) {
	                		((Bomber) obj).moveLeft();
	                	}
	                	else if (randominput < 0.5 && randominput >= 0.25) {
	                		((Bomber) obj).moveRight();
	                	}
	                	else if (randominput < 0.75 && randominput >= 0.5) {
	                		((Bomber) obj).moveUp();
	                	}
	                	else if (randominput < 1 && randominput >= 0.75) {
	                		((Bomber) obj).moveDown();
	                	}
	                }
                }
                obj.update();
                if (obj.isDestroyed()) {
                    // Destroy and remove game objects that were marked for deletion
                    obj.onDestroy();
                    //do something here for checking all objects are destroyed
                    GameObjectCollection.gameObjects.get(list).remove(obj);
                } else {
                    for (int list2 = 0; list2 < GameObjectCollection.gameObjects.size(); list2++) {
                        for (int objIndex2 = 0; objIndex2 < GameObjectCollection.gameObjects.get(list2).size(); objIndex2++) {
                            GameObject collidingObj = GameObjectCollection.gameObjects.get(list2).get(objIndex2);
                            // Skip detecting collision on the same object as itself
                            if (obj == collidingObj) {
                                continue;
                            }

                            // Visitor pattern collision handling
                            if (obj.getCollider().intersects(collidingObj.getCollider())) {
                                // Use one of these
                                collidingObj.onCollisionEnter(obj);
//                                obj.onCollisionEnter(collidingObj);
                            }
                        }
                    }
                    objIndex++;
                }
            }
        }
        // Check for enemies that is dead and increase score
        // Score is added immediately so there is no harm of dying when you are the last one
        // Reset map when there are 1 or less bombers left
        if (!this.gameHUDSingle.matchSet) { // check if the game has not already been done, if not check for score kills
            if(this.mapPhase > 1){

                this.gameHUDSingle.RetainUpdateScore();
            }else{
                this.gameHUDSingle.updateScore();
            }
        } else {
            // Checking size of array list because when a enemy dies, they do not immediately get deleted
            if (GameObjectCollection.enemyObjects.isEmpty()) { // this should be change map when all enemies Ai are dead
                this.resetMapSingle();//(gameHUDSingle.playerScore);
                this.gameHUDSingle.matchSet = false;
            }
//            }else if(GameObjectCollection.bomberObjects.isEmpty()){ // this should be reset the map back to stage 1 when my character dies
//                this.resetMapSingle();
//                this.gameHUDSingle.matchSet = false;
//            }
        }
        // Used to prevent resetting the game really fast
        this.resetDelay++;

        try {
            Thread.sleep(1000 / 144);
        } catch (InterruptedException ignored) {
        }
    }

    private void updateMultiplayer() {
    	//out.println("a");
        GameObjectCollection.sortBomberObjects();
        String line = null;
        int person = -1;
        String action = "";
        Long time = (long) 0;
		try {
			line = in.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(line);
        if(line.startsWith("Left ")) {
        	String[] personParts = line.split(",");
        	person = Integer.parseInt(personParts[0].replace("Left ", ""));
        	action = "Disconnected";
        }
        else if(line.startsWith("Player ")) {
        	String[] parts = line.replace("Player ", "").split(": ");
        	person = Integer.parseInt(parts[0]);
        	action = parts[1];
        	time = Long.parseLong(parts[2]);
        	messageArea.append("**SERVER**: Player " + parts[0] + ": " + parts[1] + " (Delay " + (System.currentTimeMillis() - time) +" ms)\n");
        }
        else if (line.startsWith("MESSAGE")) {
        	messageArea.append(line.substring(8) + "\n");
        }
        else if (line.startsWith("Server Stats: ")) {
        	String stats = line.replace("Server Stats: ","").replace(" | ", "\n");
            messageArea.append("**SERVER**: Requested Statistics \n" + stats);
        }
        // Loop through every game object arraylist
        for (int list = 0; list < GameObjectCollection.gameObjects.size(); list++) {
            for (int objIndex = 0; objIndex < GameObjectCollection.gameObjects.get(list).size();) {
                GameObject obj = GameObjectCollection.gameObjects.get(list).get(objIndex);
                if(obj instanceof Bomber) {
                	if(((Player) obj).player == person){
	                	if(action.equals("Left Pressed")) {
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                		((Bomber) obj).moveLeft();
	                	}
	                	else if(action.equals("Right Pressed")) {
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                		((Bomber) obj).moveRight();
	                	}
	                	else if(action.equals("Up Pressed")) {
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                		((Bomber) obj).moveUp();
	                	}
	                	else if(action.equals("Down Pressed")) {
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                		((Bomber) obj).moveDown();
	                	}
	                	else if(action.equals("Bomb Placed")) {
	                		((Bomber) obj).plantBomb();
	                		((Bomber) obj).plantBomb();
	                		((Bomber) obj).plantBomb();
	                		((Bomber) obj).plantBomb();
	                	}
	                	else if(action.equals("Disconnected")) {
	                		obj.destroy();
	                	}
	                	else {
	                		String[] actionPair = action.split(",");
	                		if(actionPair[0].equals("addAmmo")) {
	                			((Bomber) obj).addAmmo(Integer.parseInt(actionPair[1]));
	                		}
	                		else if(actionPair[0].equals("addFirepower")) {
	                			((Bomber) obj).addFirepower(Integer.parseInt(actionPair[1]));
	                		}
	                		else if(actionPair[0].equals("addSpeed")) {
	                			((Bomber) obj).addSpeed(Float.parseFloat(actionPair[1]));
	                		}
	                		else if(actionPair[0].equals("setPierce")) {
	                			((Bomber) obj).setPierce(Boolean.parseBoolean(actionPair[1]));
	                		}
	                		else if(actionPair[0].equals("setKick")) {
	                			((Bomber) obj).setKick(Boolean.parseBoolean(actionPair[1]));
	                		}
	                		else if(actionPair[0].equals("reduceTimer")) {
	                			((Bomber) obj).reduceTimer(Integer.parseInt(actionPair[1]));
	                		}
	                	}
                	}
                }
                obj.update();
                if (obj.isDestroyed()) {
                    // Destroy and remove game objects that were marked for deletion
                    obj.onDestroy();
                    //do something here for checking all objects are destroyed
                    GameObjectCollection.gameObjects.get(list).remove(obj);
                } else {
                    for (int list2 = 0; list2 < GameObjectCollection.gameObjects.size(); list2++) {
                        for (int objIndex2 = 0; objIndex2 < GameObjectCollection.gameObjects.get(list2).size(); objIndex2++) {
                            GameObject collidingObj = GameObjectCollection.gameObjects.get(list2).get(objIndex2);
                            // Skip detecting collision on the same object as itself
                            if (obj == collidingObj) {
                                continue;
                            }

                            // Visitor pattern collision handling
                            if (obj.getCollider().intersects(collidingObj.getCollider())) {
                                // Use one of these
                                collidingObj.onCollisionEnter(obj);
//                                obj.onCollisionEnter(collidingObj);
                            }
                        }
                    }
                    objIndex++;
                }
            }
        }

        // Check for the last bomber to survive longer than the others and increase score
        // Score is added immediately so there is no harm of dying when you are the last one
        // Reset map when there are 1 or less bombers left
        if (!this.gameHUD.matchSet) {
            this.gameHUD.updateScore();
        } else {
            // Checking size of array list because when a bomber dies, they do not immediately get deleted
            // This makes it so that the next round doesn't start until the winner is the only bomber object on the map
            if (GameObjectCollection.bomberObjects.size() <= 1) {
            	String[] currentMapData = maps[currentMap].split("N");
                File file = new File("./currentMap.csv");      
        		FileWriter filewriter = null;
    			try {
					filewriter = new FileWriter(file);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		for(int i = 0; i < currentMapData.length; i++) {
        			try {
        				if(currentMapData[i].startsWith(",")) {
        					filewriter.write(currentMapData[i].substring(1)+"\n");
        				}
        				else {
        					filewriter.write(currentMapData[i]+"\n");
        				}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
        		try {
					filewriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		if(currentMap == 99) {
        			currentMap = 0;
        		}
        		else {
        			currentMap++;
        		}
                this.resetMapMultiplayer();
                this.gameHUD.matchSet = false;
            }
        }

        // Used to prevent resetting the game really fast
        //this.resetDelay++;

        try {
            Thread.sleep(1000 / 144);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        this.buffer = this.world.createGraphics();
        this.buffer.clearRect(0, 0, this.world.getWidth(), this.world.getHeight());
        super.paintComponent(g2);
        
        if(GameType == 1){ //if single player
            this.gameHUDSingle.drawHUD();
        }else{
            this.gameHUD.drawHUD();
        }

        // Draw background
        for (int i = 0; i < this.world.getWidth(); i += this.bg.getWidth()) {
            for (int j = 0; j < this.world.getHeight(); j += this.bg.getHeight()) {
                this.buffer.drawImage(this.bg, i, j, null);
            }
        }

        // Draw game objects
        for (int i = 0; i < GameObjectCollection.gameObjects.size(); i++) {
            for (int j = 0; j < GameObjectCollection.gameObjects.get(i).size(); j++) {
                GameObject obj = GameObjectCollection.gameObjects.get(i).get(j);
                obj.drawImage(this.buffer);
//                obj.drawCollider(this.buffer);
            }
        }

        // Draw HUD
        if(GameType == 1){ // if single player
            int infoBoxWidth = panelWidth;
            g2.drawImage(this.gameHUDSingle.getPlinfo(), infoBoxWidth * 0, 0, null);
        } else {
            int infoBoxWidth = panelWidth / 4;
            g2.drawImage(this.gameHUD.getP1info(), infoBoxWidth * 0, 0, null);
            g2.drawImage(this.gameHUD.getP2info(), infoBoxWidth * 1, 0, null);
            g2.drawImage(this.gameHUD.getP3info(), infoBoxWidth * 2, 0, null);
            g2.drawImage(this.gameHUD.getP4info(), infoBoxWidth * 3, 0, null);
        }
       
        // Draw game world offset by the HUD
        g2.drawImage(this.world, 0, GameWindow.HUD_HEIGHT, null);

        g2.dispose();
        this.buffer.dispose();
    }

}

/**
 * Used to control the game
 */
class GameController  extends JFrame implements KeyListener {

    private GamePanel gamePanel;

    /**
     * Construct a universal game controller key listener for the game.
     *
     * @param gamePanel Attach game controller to this game panel
     */
    GameController(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Key events for general game operations such as exit
     *
     * @param e Keyboard key pressed
     */
    @Override
    public void keyPressed(KeyEvent e){
        // Close game
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { //instead of exit we want it pause
            System.out.println("Escape key pressed: Pausing game");
            System.out.println("Pause State: "+gamePanel.ispaused);
            if(gamePanel.ispaused == false){ // if it is running currently, then pause 
                gamePanel.unPause();
                MenuPanel.ShowPausePanel(e);
            }else{
                gamePanel.MenuUnPaused();
                
            }
             //set pause to true
            //this.gamePanel.exit();
        }

        // Display controls
        if (e.getKeyCode() == KeyEvent.VK_F1) {
            System.out.println("F1 key pressed: Displaying help");

            String[] columnHeaders = {"", "White", "Black", "Red", "Blue"};
            Object[][] controls = {
                {"Up", "Up", "W", "T", "I"},
                {"Down", "Down", "S", "G", "K"},
                {"Left", "Left", "A", "F", "J"},
                {"Right", "Right", "D", "H", "L"},
                {"Bomb", "SPACE", "E", "Y", "O"},
                {"", "", "", "", ""},
                {"Help", "F1", "", "", ""},
                {"Reset", "F5", "", "", ""},
                {"Exit", "ESC", "", "", ""}};

            JTable controlsTable = new JTable(controls, columnHeaders);
            JTableHeader tableHeader = controlsTable.getTableHeader();

            // Wrap JTable inside JPanel to display
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(tableHeader, BorderLayout.NORTH);
            panel.add(controlsTable, BorderLayout.CENTER);

            JOptionPane.showMessageDialog(this.gamePanel, panel, "Controls", JOptionPane.PLAIN_MESSAGE);
        }

        // Reset game
        // Delay prevents resetting too fast which causes the game to crash
        if (e.getKeyCode() == KeyEvent.VK_F5) {
            if (this.gamePanel.resetDelay >= 20) {
                System.out.println("F5 key pressed: Resetting game");
                if(this.gamePanel.GameType == 1){
                    this.gamePanel.resetGameSingle();
                }else{
                    this.gamePanel.resetGame();
                }
                
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
 class MenuPanel { 
     
    private static final int ALPHA = 175; // how much see-thru. 0 to 255
    private static final Color GP_BG = new Color(0, 0, 0, ALPHA);
    private static DeDialogPanel Panel = new DeDialogPanel();  // jpanel shown in JDialog
    
    public MenuPanel(){
        
    }
    
    public static void ShowPausePanel(KeyEvent e){
        
        Component comp = (Component) e.getSource();
        if (comp == null) {
            return;
        }
        
        JPanel glassPane = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                // magic to make it dark without side-effects
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        
        // more magic below
        glassPane.setOpaque(false); 
        glassPane.setBackground(GP_BG);     

        // get the rootpane container, here the JFrame, that holds the JButton
        RootPaneContainer win = (RootPaneContainer) SwingUtilities.getWindowAncestor(comp);
        win.setGlassPane(glassPane);  // set the glass pane
        glassPane.setVisible(true);  // and show the glass pane

        // create a *modal* JDialog
        JDialog dialog = new JDialog((Window)win, "", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.getContentPane().add(Panel);  // add its JPanel to it
        dialog.setUndecorated(true); // give it no borders (if desired)
        dialog.pack(); // size it
        dialog.setLocationRelativeTo((Window) win); // ** Center it over the JFrame **
        dialog.setVisible(true);  // display it, pausing the GUI below it

        // at this point the dialog is no longer visible, so get rid of glass pane
        glassPane.setVisible(false);
        
        
    }

}
@SuppressWarnings("serial")
class DeDialogPanel extends JPanel {
    private static final Color BackGroundColor = new Color(123, 63, 0);

    public DeDialogPanel() {
        JLabel pausedLabel = new JLabel("PAUSED");
        pausedLabel.setForeground(Color.ORANGE);
        JPanel pausedPanel = new JPanel();
        pausedPanel.setOpaque(false);
        pausedPanel.add(pausedLabel);

        setBackground(BackGroundColor);
        int eb = 15;
        setBorder(BorderFactory.createEmptyBorder(eb, eb, eb, eb));
        setLayout(new GridLayout(0, 1, 10, 10));
        add(pausedPanel);
        add(new JButton(new FooAction("RESUME")));
        add(new JButton(new FooAction("RESTART")));
        add(new JButton(new FooAction("EXIT TO MAP")));
    }

    // simple action -- all it does is to make the dialog no longer visible
    private class FooAction extends AbstractAction {
        public FooAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Component comp = (Component) e.getSource();
            Window win = SwingUtilities.getWindowAncestor(comp);
            win.dispose();  // here -- dispose of the JDialog
        }

    }
}
