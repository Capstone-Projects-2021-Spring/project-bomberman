
import gameobjects.Ai;
import gameobjects.Bomber;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thuc Luong's PC
 */
public class GameHUDSingle {
    
    private Bomber player;
    private Ai[] enemies;
    private BufferedImage playerInfo;
    private int playerScore;
    boolean matchSet;
    
    GameHUDSingle(){
        
        this.player = null;
        this.playerInfo = null;
        this.playerScore = 0;
        this.matchSet = false;
        this.enemies = new Ai[3]; //amount of enemies, must be set to the extract amount of enemies or get null pointer error in code
        
    }
    void init() {
        // Height of the HUD
        int height = GameWindow.HUD_HEIGHT;
        // Width of each player's information in the HUD, 4 players, 4 info boxes
        int infoWidth = GamePanel.panelWidth;
        this.playerInfo = new BufferedImage(infoWidth, height, BufferedImage.TYPE_INT_RGB);
        //this.enemies[0] = new BufferedImage(BufferedImage.TYPE_INT_ARGB);
   
   
    }
    BufferedImage getPlinfo() {
        return this.playerInfo;
    }
    void assignAi(Ai enemies, int enemyID){
        this.enemies[enemyID] = enemies;
    }
    void assignPlayer(Bomber player) {
        this.player = player;
    }
    public void updateScore() {
        // Count dead AI's
        int deadAi = 0;
        for (int i = 0; i < this.enemies.length; i++) { //continueous going through loop, call after call to check on enemy status
            System.out.println(this.enemies[i].isDead()+" enemies["+i+"].isdead: line:59"); 
            if (this.enemies[i].isDead()) {
                deadAi++;
                System.out.println(deadAi+" dead, line:61"); 
                System.out.println(playerScore+" score, line:62");
            }
        }
        this.playerScore = deadAi;
        // Check for no enemy standing and conclude the match
        System.out.println(deadAi+" dead,line:68"); 
        if (deadAi == this.enemies.length) {// check for amount dead to total enemies in arraylist
            System.out.println(deadAi+" line:69"); 
            for (int i = 0; i < this.enemies.length; i++) {
                System.out.println(this.enemies[i].isDead()+" enemies["+i+"].isdead: line:72"); 
                if (!this.enemies[i].isDead()) {
                    this.playerScore++;
                    this.matchSet = true;
                }
            }
       //
        }else if (deadAi >= this.enemies.length) {
            // This should only be reached two or more of the last players die at the same time
            this.matchSet = true;
        }
        
    }
     void drawHUD() {
        Graphics playerGraphics = this.playerInfo.createGraphics();
                
        // Clean info boxes
        playerGraphics.clearRect(0, 0, playerInfo.getWidth(), playerInfo.getHeight());

        // Set border color for player
        playerGraphics.setColor(Color.WHITE);    // Player 1 info box border color
        
       
        Font font = new Font("Courier New", Font.BOLD, 24);
            // Draw border and sprite
        playerGraphics.drawRect(1, 1, this.playerInfo.getWidth() - 2, this.playerInfo.getHeight() - 2);
        playerGraphics.drawImage(this.player.getBaseSprite(), 0, 0, null);

            // Draw score
        playerGraphics.setFont(font);
        playerGraphics.setColor(Color.WHITE);
        playerGraphics.drawString("" + this.playerScore, this.playerInfo.getWidth() / 2, 32);

            // Dispose
        playerGraphics.dispose();
        
    }
}
