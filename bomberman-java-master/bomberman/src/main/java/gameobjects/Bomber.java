package gameobjects;

import util.GameObjectCollection;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Date;

import javax.sound.sampled.*;


/**
 * Bomberman player object to be controlled by a user.
 */
public class Bomber extends Player {

    private Bomb bomb;
    public boolean dead;

    // Animation
    private BufferedImage[][] sprites;
    private int direction;  // 0: up, 1: down, 2: left, 3: right
    public int spriteIndex;
    private int spriteTimer;
    private int GameType= 0;
    // Stats
    private float moveSpeed;
    private int firepower;
    private int maxBombs;
    private int bombAmmo;
    private int bombTimer;
    private boolean pierce;
    private boolean kick;

    //Bot movement variables
    private boolean player;
    private boolean resting; //wating for a new movement command or not
    private float move_x; //the x position of the current movement command
    private float move_y;//the y position of the current movement command
    private boolean bomb_planted;
    private int moveScore;
    private boolean test;
    private boolean gameStart;
    private static int counter = 0;


    /**
     * Constructs a bomber at position with a two-dimensional array of sprites.
     * @param position Coordinates of this object in the game world
     * @param spriteMap 2D array of sprites used for animation
     * @param GameType Game type single or multi
     */
    public Bomber(Point2D.Float position, BufferedImage[][] spriteMap, int GameType, PrintWriter out, int player) {
        super(position, spriteMap[1][0],out,player);
        this.collider.setRect(this.position.x + 3, this.position.y + 16 + 3, this.width - 6, this.height - 16 - 6);
        this.GameType = GameType;
        // Animation
        this.sprites = spriteMap;
        this.direction = 1;     // Facing down
        this.spriteIndex = 0;
        this.spriteTimer = 0;
        
        // Default stats
        this.moveSpeed = 2; //temp change back to 1
        this.firepower = 2;//temp change back to 1
        this.maxBombs = 1;
        this.bombAmmo = this.maxBombs;
        this.bombTimer = 250;
        this.pierce = true; //temp change back to false
        this.kick = false;
    }

    public Bomber(Point2D.Float position, BufferedImage[][] spriteMap, int GameType) {
        super(position, spriteMap[1][0]);
        this.collider.setRect(this.position.x + 3, this.position.y + 16 + 3, this.width - 6, this.height - 16 - 6);
        this.GameType = GameType;
        // Animation
        this.sprites = spriteMap;
        this.direction = 1;     // Facing down
        this.spriteIndex = 0;
        this.spriteTimer = 0;
        
        // Default stats
        this.moveSpeed = 2; //temp change back to 1
        this.firepower = 2;//temp change back to 1
        this.maxBombs = 1;
        this.bombAmmo = this.maxBombs;
        this.bombTimer = 250;
        this.pierce = true; //temp change back to false
        this.kick = false;
        this.player = true;
        this.moveScore = 100;

    }

    public Bomber(Point2D.Float position, BufferedImage[][] spriteMap, boolean player) {
        super(position, spriteMap[1][0]);
        this.collider.setRect(this.position.x + 3, this.position.y + 16 + 3, this.width - 6, this.height - 16 - 6);

        // Animation
        this.sprites = spriteMap;
        this.direction = 1;     // Facing down
        this.spriteIndex = 0;
        this.spriteTimer = 0;

        // Default stats
        this.moveSpeed = 1;
        this.firepower = 1;
        this.maxBombs = 1;
        this.bombAmmo = this.maxBombs;
        this.bombTimer = 250;
        this.pierce = false;
        this.kick = false;

        this.player = player;
        this.resting = true;
        this.bomb_planted = false;
        this.moveScore = 100;
        this.test = true;
        this.gameStart = true;
    }

    // --- MOVEMENT ---
    public void moveUp() {
        this.direction = 0;     // Using sprites that face up
        this.position.setLocation( this.position.x, this.position.y - this.moveSpeed);

    }
    public void moveDown() {
        this.direction = 1;     // Using sprites that face down
        this.position.setLocation(this.position.x, this.position.y + this.moveSpeed);
        System.out.println("WALKING DOWN");
    }
    public void moveLeft() {
        this.direction = 2;     // Using sprites that face left
        this.position.setLocation(this.position.x - this.moveSpeed, this.position.y);
        //System.out.println("WALKING LEFT");
    }
    public void moveRight() {
        this.direction = 3;     // Using sprites that face right
        this.position.setLocation(this.position.x + this.moveSpeed, this.position.y);
        //System.out.println("WALKING RIGHT");
    }



    // --- ACTION ---
    public void plantBomb() {
       
        // Snap bombs to the grid on the map
        float x = Math.round(this.position.getX() / 32) * 32;
        float y = Math.round((this.position.getY() + 16) / 32) * 32;
        Point2D.Float spawnLocation = new Point2D.Float(x, y);

        // Only one tile object allowed per tile; Cannot place a bomb on another object
        for (int i = 0; i < GameObjectCollection.tileObjects.size(); i++) {
             
            GameObject obj = GameObjectCollection.tileObjects.get(i);
            if (obj.collider.contains(spawnLocation)) {
                return;
            }
        }
        
        // Spawn the bomb
        this.bomb = new Bomb(spawnLocation, this.firepower, this.pierce, this.bombTimer, this); // multi player version
        SoundEffect.BOMB.play();
        GameObjectCollection.spawn(bomb);
        this.bombAmmo--;


        if (!this.player){
            System.out.println("BOT HAS PLANTED A BOMB");
            this.bomb_planted = true;
            System.out.println("BOMB current y position is " + this.position.y);
            System.out.println("BOMB current x position is " + this.position.x);
        }
    }

    public void restoreAmmo() {
        this.bombAmmo = Math.min(this.maxBombs, this.bombAmmo + 1);
    }

    // --- POWERUPS ---
    public void addAmmo(int value) {
    	if(super.out != null) {
    		out.println("Player " + super.player + ": addAmmo," + value);
    	}
        System.out.print("Bombs set from " + this.maxBombs);
        this.maxBombs = Math.min(6, this.maxBombs + value);
        this.restoreAmmo();
        System.out.println(" to " + this.maxBombs);
    }
    public void addFirepower(int value) {
    	if(super.out != null) {
    		out.println("Player " + super.player + ": addFirepower," + value);
    	}
        System.out.print("Firepower set from " + this.firepower);
        this.firepower = Math.min(6, this.firepower + value);
        System.out.println(" to " + this.firepower);
    }
    public void addSpeed(float value) {
    	if(super.out != null) {
    		out.println("Player " + super.player + ": addSpeed," + value);
    	}
        System.out.print("Move Speed set from " + this.moveSpeed);
        this.moveSpeed = Math.min(4, this.moveSpeed + value);
        System.out.println(" to " + this.moveSpeed);
    }
    public void setPierce(boolean value) {
    	if(super.out != null) {
    		out.println("Player " + super.player + ": setPierce," + value);
    	}
        System.out.print("Pierce set from " + this.pierce);
        this.pierce = value;
        System.out.println(" to " + this.pierce);
    }
    public void setKick(boolean value) {
    	if(super.out != null) {
    		out.println("Player " + super.player + ": setKick," + value);
    	}
        System.out.print("Kick set from " + this.kick);
        this.kick = value;
        System.out.println(" to " + this.kick);
    }
    public void reduceTimer(int value) {
    	if(super.out != null) {
    		out.println("Player " + super.player + ": reduceTimer," + value);
    	}
        System.out.print("Bomb Timer set from " + this.bombTimer);
        this.bombTimer = Math.max(160, this.bombTimer - value);
        System.out.println(" to " + this.bombTimer);
    }

    /**
     * Used in game HUD to draw the base sprite to the info box.
     * @return The sprite of the bomber facing down
     */
    public BufferedImage getBaseSprite() {
        return this.sprites[1][0];
    }

    /**
     * Checks if this bomber is dead.
     * @return true = dead, false = not dead
     */
    public boolean isDead() {
        return this.dead;
    }

    /**
     * Controls movement, action, and animation.
     */
    @Override
    public void update() {
        this.collider.setRect(this.position.x + 3, this.position.y + 16 + 3, this.width - 6, this.height - 16 - 6);


        while (this.test && !this.player){
           // this.print_tiles();
            this.whereToGO();
            this.test = false;

        }




        if (!this.dead) {
            // Animate sprite
            if ((this.spriteTimer += this.moveSpeed) >= 12) {
                this.spriteIndex++;
                this.spriteTimer = 0;
            }
            if ((!this.UpPressed && !this.DownPressed && !this.LeftPressed && !this.RightPressed) || (this.spriteIndex >= this.sprites[0].length)) {
                this.spriteIndex = 0;
            }
            this.sprite = this.sprites[this.direction][this.spriteIndex];

        if(this.player) {
            // Movement
            if (this.UpPressed) {
                this.moveUp();
                //System.out.println("Bot's current y position is " + this.position.y);
            }
            if (this.DownPressed) {
                this.moveDown();
                //System.out.println("Bot's current y position is " + this.position.y);
            }
            if (this.LeftPressed) {
                this.moveLeft();
            }
            if (this.RightPressed) {
                this.moveRight();
            }


            // Action
            if (this.ActionPressed && this.bombAmmo > 0) {
                
                this.plantBomb();
            }
        }
        } else {
            // Animate dying animation
            if (this.spriteTimer++ >= 30) {
                this.spriteIndex++;
                if (this.spriteIndex < this.sprites[4].length) {
                    this.sprite = this.sprites[4][this.spriteIndex];
                    this.spriteTimer = 0;
                } else if (this.spriteTimer >= 250) {
                    this.destroy();
                }
            }
        }


        if (!this.player) {
            System.out.println("Bot's current y position is " + this.position.y);
            System.out.println("Bot's current x position is " + this.position.x);
            //System.out.println("Current status of Resting is  " + this.resting);
            //System.out.println("Current status of bomb_planted is " + this.bomb_planted);
            //System.out.println("Current number of bombs available is " + this.bombAmmo);

            if (this.bomb_planted){
                System.out.println("RUNNING AWAY");
                moveDown();
                //runAway();
                if(this.maxBombs == this.bombAmmo){
                    this.bomb_planted = false;
                }
            }
            else if(!this.isDead() && resting){
                Generate_movement();
            }
        }
    }

    //AI movement things
    private void runAway(){
        float currentx = this.position.x;
        moveDown();

        moveRight();
        if(currentx != this.position.x){
            return;
        }
        moveLeft();
        if(currentx != this.position.x){
            return;
        }
        moveDown();
        moveRight();
        if(currentx != this.position.x){
            return;
        }
        moveLeft();
        if(currentx != this.position.x){
            return;
        }
    }


    private void Generate_movement(){
        ArrayList<Float> Walls = new ArrayList<Float>();
        System.out.println("Bot current x position " + this.position.x);

        // One map tile is 32 pixels long
        //if resting is true, the bot is ready to find a new movement
        if (this.resting && !this.bomb_planted) {
            //Find a place to move -- Loop thorough every breakable wall's y position and find the closest one, ie current character position - walls y position

            for (int i = 0; i <= GameObjectCollection.tileObjects.size() - 1; i++) {

                if (GameObjectCollection.tileObjects.get(i).isBreakable() && this.position.y - GameObjectCollection.tileObjects.get(i).position.y > 0.0) {

                    //System.out.println("Walls x position " + GameObjectCollection.tileObjects.get(i).position.x);
                    Walls.add(this.position.y - GameObjectCollection.tileObjects.get(i).position.y);


                    //this.resting = false;
                }
            }

            System.out.println("Breakable walls above  = " + Walls.size());
            Collections.sort(Walls); //Sorts in descending order

            if(Walls.size() > 0){
                System.out.println("Distance from closest breakable wall = " + Walls.get(0));
                System.out.println("Closest breakable walls y position = " + (-Walls.get(0) + this.position.y));
                float currentY = this.position.y;

                //This is where movement happens 2
                for (float j =0; j<= Walls.get(0); j+=1.0 ) {
                   // System.out.println("");
                    ExecuteMovementUp(this.position.y, Walls.get(0), currentY);
                }
            }
            this.resting = true;
            Walls.clear();
        }//end of if(resting)





    } //end of movement function




    //playerY = current location
    //wallY = destination location
    //baseY = start location
    //This function is meant to be called by a for loop - the iterator is the distance from the start to location
    private void ExecuteMovementUp(float playerY, float wallY, float baseY) {
        if ((playerY / 32) > (int) (((-wallY + baseY) + 12.0) / 32) && this.resting) {
            this.resting = false;
            System.out.println("MOVING UP");
            moveUp();


        }
    }
    private void ExecuteMovementDown(float playerY, float wallY, float baseY) {
        if ((playerY / 32) > (int) (((-wallY + baseY) + 12.0) / 32) && this.resting) {
            this.resting = false;
            System.out.println("MOVING DOWN");
            moveDown();


        }
    }

    //playerY = current location
    //wallY = destination location
    //baseY = start location
    //loop iteratior is the distrance between start and end point










    //Tile objects array list goes from 0 - a max x and y
    //each tile is 32x32

    /**1. Find max X and Y
     * 2. Find all Tile Objects between 1 and MaxX-1 && 1 and MaxY-1 (X between 1 and 13 and Y between 1 and 11)
     * 3. Create an number list to represent the different tiles (0 = empty space, 1 = Soft Walls, 2 = Hard Walls, 3 = Ourselves)
     * 4. Find all the empty space
     */



    public void print_tiles(){
        for (int i = 0; i <= GameObjectCollection.tileObjects.size() - 1; i++) {
            System.out.println("Object : " + (GameObjectCollection.tileObjects.get(i))+ "        X position: "+ GameObjectCollection.tileObjects.get(i).position.x/32.0 + "       Y position: " +GameObjectCollection.tileObjects.get(i).position.y/32.0 );



            System.out.println(GameObjectCollection.tileObjects.size());

        }
    }


    public float findMaxX (){
        float maxX = 0.0f;
        float temp = GameObjectCollection.tileObjects.get(0).position.x/32;

        for (int i = 1; i <= GameObjectCollection.tileObjects.size() - 1; i++) {
            if (GameObjectCollection.tileObjects.get(i).position.x / 32 > temp)
                temp = GameObjectCollection.tileObjects.get(i).position.x / 32;
        }
        maxX = temp;
        System.out.println("MAX X value is " + maxX);
        return maxX;
    }
    public float findMaxY (){
        float maxY = 0.0f;
        float temp = GameObjectCollection.tileObjects.get(0).position.y/32;

        for (int i = 1; i <= GameObjectCollection.tileObjects.size() - 1; i++) {
            if (GameObjectCollection.tileObjects.get(i).position.y / 32 > temp)
                temp = GameObjectCollection.tileObjects.get(i).position.y / 32;
        }
        maxY = temp;
        System.out.println("MAX Y value is " + maxY);
        return maxY;
    }

    public ArrayList<TileObject> drawMapTileObj(float xMax, float yMax ){
        ArrayList<TileObject> tileObj = new ArrayList<>();
        ArrayList<Integer> tileObjInt = new ArrayList<>();
        ArrayList <int[]> emptyA=new ArrayList<>();

        for(int i=0; i<((xMax-1)*(yMax-1));i++){
            tileObjInt.add(0);
        }

        for (int i = 1; i <= GameObjectCollection.tileObjects.size() - 1; i++) {
            if (GameObjectCollection.tileObjects.get(i).position.y / 32 >= 1 && GameObjectCollection.tileObjects.get(i).position.y / 32 <= yMax -1
                    && GameObjectCollection.tileObjects.get(i).position.x / 32 >= 1 && GameObjectCollection.tileObjects.get(i).position.x / 32 <= xMax -1 ){
                int x = (int)(GameObjectCollection.tileObjects.get(i).position.x/32);
                int y = (int)(GameObjectCollection.tileObjects.get(i).position.y/32)-1;
                //System.out.println("x:"+x);
                //System.out.println("Y:"+y);
                tileObj.add(GameObjectCollection.tileObjects.get(i));

                if(GameObjectCollection.tileObjects.get(i).isBreakable()) {
                    tileObjInt.set(y*((int)xMax-1)+x-1,1);

                }

                else if(!GameObjectCollection.tileObjects.get(i).isBreakable()){
                    tileObjInt.set(y*((int)xMax-1)+x-1,2);
                }

            }
        }

        for(int i=0; i<(yMax-1);i++){
            for(int j=0; j<(xMax-1);j++){
                if(tileObjInt.get(i*((int)xMax-1)+j)==0){
                    int[] empty = {i,j};
                    emptyA.add(empty);
                }
                //System.out.print(tileObjInt.get(i*((int)xMax-1)+j)+" .");
            }
            //System.out.println();
        }

        /*for(int j = 0; j <= tileObj.size() - 1; j++){
            System.out.println("Object : " + (tileObj.get(j))+ "        X position: "+ tileObj.get(j).position.x/32.0 + "       Y position: " +tileObj.get(j).position.y/32.0 );
            //System.out.println("Int Tile List at " +  j + " = " + tileObjInt.get(j));
        }*/

        return tileObj;
    }

    public ArrayList<Integer> drawMap(float xMax, float yMax ){
        ArrayList<TileObject> tileObj = new ArrayList<>();
        ArrayList<Integer> tileObjInt = new ArrayList<>();
        ArrayList <int[]> emptyA=new ArrayList<>();

        for(int i=0; i<((xMax-1)*(yMax-1));i++){
            tileObjInt.add(0);
        }

        for (int i = 1; i <= GameObjectCollection.tileObjects.size() - 1; i++) {
            if (GameObjectCollection.tileObjects.get(i).position.y / 32 >= 1 && GameObjectCollection.tileObjects.get(i).position.y / 32 <= yMax -1
                    && GameObjectCollection.tileObjects.get(i).position.x / 32 >= 1 && GameObjectCollection.tileObjects.get(i).position.x / 32 <= xMax -1 ){
                int x = (int)(GameObjectCollection.tileObjects.get(i).position.x/32);
                int y = (int)(GameObjectCollection.tileObjects.get(i).position.y/32)-1;
                //System.out.println("x:"+x);
                //System.out.println("Y:"+y);
                tileObj.add(GameObjectCollection.tileObjects.get(i));

                if(GameObjectCollection.tileObjects.get(i).isBreakable()) {
                    tileObjInt.set(y*((int)xMax-1)+x-1,1);

                }

                else if(!GameObjectCollection.tileObjects.get(i).isBreakable()){
                    tileObjInt.set(y*((int)xMax-1)+x-1,2);
                }

            }
        }

        for(int i=0; i<(yMax-1);i++){
            for(int j=0; j<(xMax-1);j++){
                if(tileObjInt.get(i*((int)xMax-1)+j)==0){
                    int[] empty = {i,j};
                    emptyA.add(empty);
                }
                //System.out.print(tileObjInt.get(i*((int)xMax-1)+j)+" .");
            }
            //System.out.println();
        }


        return tileObjInt;
    }


    //take bot postion as input
    private int[] findClosest(Point2D.Float position,ArrayList<TileObject> tileObj){
        int x=-1,y=-1;//-1 if not find anything
        double sd=100000000;
        for(int j = 0; j <= tileObj.size() - 1; j++){
            if(tileObj.get(j).isBreakable()){
                float xdiffrence=Math.abs(position.x-tileObj.get(j).position.x);
                float xdffpow2=xdiffrence*xdiffrence;
                float ydiffrence=Math.abs(position.y-tileObj.get(j).position.y);
                float ydffpow2=ydiffrence*ydiffrence;
                double d = Math.sqrt(xdffpow2+ydffpow2);
                if(d<sd){
                    sd=d;
                    x=(int)tileObj.get(j).position.x/32;
                    y=(int)tileObj.get(j).position.y/32;
                }
            }
        }
        int[] arrayIndex={x,y};
        return arrayIndex;
    }

    /*
    1. Split the array into rows
    2. Every MaxY -1 will be a new row
     */

    /*
    Step1 ) Find our player position in regards to the map array
        We have our player x and y /32
     */


    private void whereToGO (){

        ArrayList<Integer> tileObjInt = new ArrayList<>();
        float xMax = this.findMaxX();
        float yMax = this.findMaxY();
        tileObjInt = drawMap(xMax,yMax );
        int tile = 32;

        //Find spawn location or current location on the grid
        //Finding our first move of the game
        if (this.gameStart) {
            this.gameStart = false;
            float xPlayerSpawn = (float) Math.ceil(this.position.x / 32);
            float yPlayerSpawn = (float) Math.ceil(this.position.y / 32);

            //System.out.println("Spawn X " + xPlayerSpawn);
            //System.out.println("Spawn Y " + yPlayerSpawn);

            //yPosition-1 * (Row size) + xPosition-1
            tileObjInt.set((int)(yPlayerSpawn-1)*((int)xMax-1)+(int)xPlayerSpawn-1, 3);
            printArrayGrid(tileObjInt);

            int[] closest=findClosest(this.position,drawMapTileObj(xMax,yMax));
            System.out.println("Closest x:"+closest[0]+"Closest y:"+closest[1]);
            //System.out.println(drawMapTileObj(xMax,yMax).size());
            //System.out.println(tileObjInt.size());
            //System.out.println(GameObjectCollection.tileObjects.size());

            //System.out.println("Closest Box: " + tileObjInt.get((int)(closest[1]-1)*((int)xMax-1)+(int)closest[0]-1));
            tileObjInt.get((int)(closest[1]-1)*((int)xMax-1)+(int)closest[0]-1);

            if (closest[1] == (int)yPlayerSpawn){ //this means they are on the same row
                int xDiff = closest[0] - (int)xPlayerSpawn;
                if (xDiff >0){ //right

                }
                else{           //left
                    xDiff = Math.abs(xDiff)-1;
                    for(int i =0; i<= xDiff*tile/moveSpeed; i++){
                        moveLeft();
                    }
                }
            }


        }
        else{
            float xCurrent = this.position.x / 32;
            float yCurrent = this.position.x / 32;



        }


            }













    public void printArrayGrid(ArrayList<Integer> Intlist){
        float xMax = this.findMaxX();
        float yMax = this.findMaxY();


        for(int i=0; i<(yMax-1);i++){
            for(int j=0; j<(xMax-1);j++){
                if(Intlist.get(i*((int)xMax-1)+j)==0){
                }
                System.out.print(Intlist.get(i*((int)xMax-1)+j)+" .");
            }
            System.out.println();
        }
    }











    @Override
    public void onCollisionEnter(GameObject collidingObj) {
        collidingObj.handleCollision(this);
    }

    @Override
    public void handleCollision(Wall collidingObj) {
        this.solidCollision(collidingObj);
      if(!this.player)
        if (collidingObj.isBreakable()){
           this.plantBomb();


        }

    }


    public void handleCollision(Bomber collidingObj) {
        if(!this.player){
            this.plantBomb();
        }
    }

    /**
     * Die immediately if not dead. This bomber is also killed.
     * @param collidingObj The explosion that kills this bomber
     */
    @Override
    public void handleCollision(Explosion collidingObj) {
        if (!this.dead) {
            SoundEffect.DEAD.play();
            this.dead = true;
            this.spriteIndex = 0;
        }
    }

    /**
     * Bombs act as walls if the bomber is not already within the a certain distance as the bomb.
     * This is also the big and ugly kicking logic. Touching this code is very dangerous and can introduce
     * bugs to the kicking logic including stopping the bomb from moving.
     * (ie. if the bomber is not standing on the bomb)
     * @param collidingObj Solid bomb
     */
    @Override
    public void handleCollision(Bomb collidingObj) {
        Rectangle2D intersection = this.collider.createIntersection(collidingObj.collider);
        
        // Vertical collision
        if (intersection.getWidth() >= intersection.getHeight() && intersection.getHeight() <= 6 && Math.abs(this.collider.getCenterX() - collidingObj.collider.getCenterX()) <= 8) {
            if (this.kick && !collidingObj.isKicked()) {
                // From the top
                if (intersection.getMaxY() >= this.collider.getMaxY() && this.DownPressed) {
                    collidingObj.setKicked(true, KickDirection.FromTop);
                }
                // From the bottom
                if (intersection.getMaxY() >= collidingObj.collider.getMaxY() && this.UpPressed) {
                    collidingObj.setKicked(true, KickDirection.FromBottom);
                }
            }
            this.solidCollision(collidingObj);
        }
        // Horizontal collision
        if (intersection.getHeight() >= intersection.getWidth() && intersection.getWidth() <= 6 && Math.abs(this.collider.getCenterY() - collidingObj.collider.getCenterY()) <= 8) {
            if (this.kick && !collidingObj.isKicked()) {
                // From the left
                if (intersection.getMaxX() >= this.collider.getMaxX() && this.RightPressed) {
                    collidingObj.setKicked(true, KickDirection.FromLeft);
                }
                // From the right
                if (intersection.getMaxX() >= collidingObj.collider.getMaxX() && this.LeftPressed) {
                    collidingObj.setKicked(true, KickDirection.FromRight);
                }
            }
            this.solidCollision(collidingObj);
        }
    }

    /**
     * Get powerup bonus depending on the type.
     * @param collidingObj Powerup that provides the bonus
     */
    @Override
    public void handleCollision(Powerup collidingObj) {
        collidingObj.grantBonus(this);
        collidingObj.destroy();
    }
    @Override
    public void handleCollision(Ai collidingObj) {
        if (!this.dead) {
            this.dead = true;
            this.spriteIndex = 0;
        }

    }

    /**
     *
     * @param collidingObj
     */
    @Override
    public void handleCollision(Enemy collidingObj) {
        if (!this.dead) {
            this.dead = true;
            this.spriteIndex = 0;
        }

    }


    public enum SoundEffect{
        DEAD("death.wav"),
        BOMB("bombput.wav");
        
        
        public static enum Volume {
            MUTE, LOW, MEDIUM, HIGH
         }
         
         public static Volume volume = Volume.LOW;
         
         private Clip clip;
         
         SoundEffect(String soundFileName) {
            try {
               String filePath = "./src/main/resources/Sound_Effects/" + soundFileName;
               File soundEffect = new File(filePath);
               AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundEffect);
               clip = AudioSystem.getClip();
               clip.open(audioInputStream);
            } catch (UnsupportedAudioFileException e) {
               e.printStackTrace();
            } catch (IOException e) {
               e.printStackTrace();
            } catch (LineUnavailableException e) {
               e.printStackTrace();
            }
         }
         
         // Play or Re-play the sound effect from the beginning, by rewinding.
         public void play() {
            if (volume != Volume.MUTE) {
               if (clip.isRunning())
                  clip.stop();   
               clip.setFramePosition(0); 
               clip.start();     
            }
         }
         
         // Optional static method to pre-load all the sound files.
         static void init() {
            values(); // calls the constructor for all the elements
         }
      }
}
