/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import gameobjects.GameObject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.JPanel;
import util.Key;
/**
 *
 * @author Thuc Luong's PC
 */
public class GamePanelSingle extends JPanel implements Runnable{
    
    public static int width;
    public static int height;
    
    private Thread thread;
    private boolean running;
    
    public static int oldFrameCount;
    public static int oldTickCount;
    public static int tickCount;
    
    private BufferedImage world;
    private Graphics2D buffer;
    private BufferedImage bg;
    private GameHUD gameHUD;
    
    private GameStateManager game;
    private keyhandler keys;
    
    public GamePanelSingle(int width, int height){
        this.width = width;
        this.height = height;
        this.setPreferredSize(new Dimension(width, height));
        this.setFocusable(true);
        this.requestFocus();
        
        
    }
    
    @Override
    public void addNotify() {
        super.addNotify();

        if (this.thread == null) {
            this.thread = new Thread(this, "GameThread");
            this.thread.start();
        }
    }
    void init() {
        world = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        buffer = (Graphics2D) world.getGraphics();
        
        keys = new keyhandler(this);
        game = new GameStateManager();
        this.running = true;
        
    }
    public void run() {
        init();

        final double GAME_HERTZ = 64.0;
        final double TBU = 1000000000 / GAME_HERTZ; // Time Before Update

        final int MUBR = 3; // Must Update before render

        double lastUpdateTime = System.nanoTime();
        double lastRenderTime;

        final double TARGET_FPS = 1000;
        final double TTBR = 1000000000 / TARGET_FPS; // Total time before render

        int frameCount = 0;
        int lastSecondTime = (int) (lastUpdateTime / 1000000000);
        oldFrameCount = 0;

        tickCount = 0;
        oldTickCount = 0;

        while (running) {

            double now = System.nanoTime();
            int updateCount = 0;
            while (((now - lastUpdateTime) > TBU) && (updateCount < MUBR)) {
                update();
                input(keys);
                lastUpdateTime += TBU;
                updateCount++;
                tickCount++;
                // (^^^^) We use this varible for the soul purpose of displaying it
            }

            if ((now - lastUpdateTime) > TBU) {
                lastUpdateTime = now - TBU;
            }

            input(keys);
            render();
            draw();
            lastRenderTime = now;
            frameCount++;

            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime) {
                if (frameCount != oldFrameCount) {
                    System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
                    oldFrameCount = frameCount;
                }

                if (tickCount != oldTickCount) {
                    System.out.println("NEW SECOND (T) " + thisSecond + " " + tickCount);
                    oldTickCount = tickCount;
                }
                tickCount = 0;
                frameCount = 0;
                lastSecondTime = thisSecond;
            }

            while (now - lastRenderTime < TTBR && now - lastUpdateTime < TBU) {
                Thread.yield();

                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    System.out.println("ERROR: yielding thread");
                }

                now = System.nanoTime();
            }

        }
    }
    public void update(){
       
        game.update();
        
    }
    public void render(){
        if(buffer != null){
            buffer.setColor(Color.red);
            buffer.fillRect(0, 0, WIDTH, HEIGHT);
            game.render(buffer);
        }
    }
    public void draw(){
        Graphics temp = (Graphics) this.getGraphics();
        temp.drawImage(world, 0, 0,width,height,null);
        temp.dispose();
    }
    public void input(keyhandler keys){
        game.input(keys);
    }
    
    void exit() {
        this.running = false;
    }
}
