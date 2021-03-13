
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
    private BufferedImage playerInfo;
    private int playerScore;
    boolean matchSet;
    int Wallobjects;
    
    GameHUDSingle(){
        
        this.player = null;
        this.playerInfo = null;
        this.playerScore = 0;
        this.matchSet = false;
        this.Wallobjects = 0;
        
    }
    void init() {
        // Height of the HUD
        int height = GameWindow.HUD_HEIGHT;
        // Width of each player's information in the HUD, 4 players, 4 info boxes
        int infoWidth = GamePanel.panelWidth/1;
        this.playerInfo = new BufferedImage(infoWidth, height, BufferedImage.TYPE_INT_RGB);
        this.Wallobjects = 0;
        
    }
    BufferedImage getPlayerinfo() {
        return this.playerInfo;
    }
    int getwall(){
        return this.Wallobjects;
    }
    void assignPlayer(Bomber player) {
        this.player = player;
    }
    void assignNumWall(GamePanel game){
        this.Wallobjects = game.softwallnumber;
    }
    public void updateScore() {
        // Count dead players
        int deadSoftWalls = 0;
        if(Wallobjects <= 0) {
            this.matchSet = true;
        }
       //need work 
       //
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
