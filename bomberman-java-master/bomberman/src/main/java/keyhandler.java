
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thuc Luong's PC
 */
public class keyhandler implements KeyListener{

    
    
    private static List<keyhandlerSystem> controls = new ArrayList<keyhandlerSystem>();
    
    public class keyhandlerSystem {
        public int press, absorbs;
        public boolean down, clicked;
        
        public keyhandlerSystem(){
            controls.add(this);
        }
        public void toggle(boolean pressed){
            if(pressed != down){
                down = pressed;
            }if(pressed){
                press++;
            }
        }
        public void timepress(){
            if(absorbs < press){
                absorbs++;
                clicked = true;
            }else{
                clicked = false;
            }
        }
    }
    public keyhandler(GamePanelSingle game) {
        game.addKeyListener(this);
    }

    public void releaseAll() {
        for(int i = 0; i < controls.size(); i++) {
            controls.get(i).down = false;
        }
    }

    public void tick() {
        for(int i = 0; i < controls.size(); i++) {
            controls.get(i).timepress();
        }
    }
    public keyhandlerSystem up = new keyhandlerSystem();
    public keyhandlerSystem down = new keyhandlerSystem();
    public keyhandlerSystem left = new keyhandlerSystem();
    public keyhandlerSystem right = new keyhandlerSystem();
    public keyhandlerSystem action = new keyhandlerSystem();
    public keyhandlerSystem menu = new keyhandlerSystem();
    public keyhandlerSystem enter = new keyhandlerSystem();
    public keyhandlerSystem escape = new keyhandlerSystem();
    public keyhandlerSystem shift = new keyhandlerSystem();
    public keyhandlerSystem f5 = new keyhandlerSystem();
    
    public void toggle(KeyEvent e, boolean pressed) {
        if(e.getKeyCode() == KeyEvent.VK_W) up.toggle(pressed);
        if(e.getKeyCode() == KeyEvent.VK_S) down.toggle(pressed);
        if(e.getKeyCode() == KeyEvent.VK_A) left.toggle(pressed);
        if(e.getKeyCode() == KeyEvent.VK_D) right.toggle(pressed);
        if(e.getKeyCode() == KeyEvent.VK_SPACE) action.toggle(pressed);
        if(e.getKeyCode() == KeyEvent.VK_E) menu.toggle(pressed);
        if(e.getKeyCode() == KeyEvent.VK_ENTER) enter.toggle(pressed);
        if(e.getKeyCode() == KeyEvent.VK_ESCAPE) escape.toggle(pressed);
        if(e.getKeyCode() == KeyEvent.VK_F1) f5.toggle(pressed);
        if(e.getKeyCode() == KeyEvent.VK_SHIFT) shift.toggle(pressed);
    }
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
         toggle(e, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
         toggle(e, false);
    }
    
    
    
}
