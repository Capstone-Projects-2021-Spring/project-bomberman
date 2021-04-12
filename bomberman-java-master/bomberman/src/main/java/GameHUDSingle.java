
import gameobjects.Ai;
import gameobjects.Bomber;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.sound.sampled.*;

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
    protected int playerScore;
    boolean matchSet;
    
    GameHUDSingle(){
        this.player = null;
        this.playerInfo = null;
        this.playerScore = 0;
        this.matchSet = false;
        this.enemies = new Ai[3]; //amount of enemies, must be set to the extract amount of enemies or get null pointer error in code
        
    }
    void init() {
        this.playerScore = 0;
        // Height of the HUD
        int height = GameWindow.HUD_HEIGHT;
        // Width of each player's information in the HUD, 4 players, 4 info boxes
        int infoWidth = GamePanel.panelWidth;
        this.playerInfo = new BufferedImage(infoWidth, height, BufferedImage.TYPE_INT_RGB);
    }
    void init(int playerScore){
        this.playerScore = playerScore;
        // Height of the HUD
        int height = GameWindow.HUD_HEIGHT;
        // Width of each player's information in the HUD, 4 players, 4 info boxes
        int infoWidth = GamePanel.panelWidth;
        this.playerInfo = new BufferedImage(infoWidth, height, BufferedImage.TYPE_INT_RGB);
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
        int deadAi = 0; //this.playerScore;
        for (int i = 0; i < this.enemies.length; i++) { //continueous going through loop, call after call to check on enemy status
            System.out.println(this.enemies[i].isDead()+" enemies["+i+"].isdead: line:59"); 
            if (this.enemies[i].isDead()) {
                deadAi++;
            }
        }
        this.playerScore = deadAi;
        // Check for no enemy standing and conclude the match
        if (deadAi == this.enemies.length) {// check for amount dead to total enemies in arraylist 
//            SoundEffect.VICTORY.play();   
            this.matchSet = true;
        }
        if(this.player.isDead()){
          //  SoundEffect.DEAD.play();
            this.matchSet = true;
        }
        if(this.player.isDestroyed()){
           // SoundEffect.DEAD.play();
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

    /*public enum SoundEffect{
        VICTORY("victory.wav"),
        DEAD("death.wav");
        
        public static enum Volume {
            MUTE, LOW, MEDIUM, HIGH
         }
         
         public static Volume volume = Volume.LOW;
         
         private Clip clip;
         
         SoundEffect(String soundFileName) {
            try {
               String filePath = "C:/Users/jason/Desktop/CapstoneProject/project-bomberman/bomerman-java-master/bomberman/src/main/resources/Sound_Effects/" + soundFileName;
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
      }*/
}
