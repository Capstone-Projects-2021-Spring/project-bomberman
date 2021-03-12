
import java.awt.Graphics2D;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thuc Luong's PC
 */
public class GameStateManager {
    
    ArrayList<GameStateSingle> states;
    public static final int PLAY = 0;
    public static final int MENU = 1;
    public static final int PAUSE = 2;
    public static final int GAMEOVER = 3;
    public static Vector map;
    public GameStateManager(){
        states = new ArrayList<>();
        states.add(new PlayState(this));
        map = new Vector(GamePanelSingle.width, GamePanelSingle.height);
        Vector.setMapVector(map.x, map.y);
        
        
    }
    public void update(){
        Vector.setMapVector(map.x, map.y);
        for(int i = 0; i < states.size(); i++){
            states.get(i).update();
        }
    }
    public void input(keyhandler key){
        for(int i = 0; i < states.size(); i++){
            states.get(i).input(key);
        }
    }
    public void render(Graphics2D graphic){
        for(int i = 0; i < states.size(); i++){
            states.get(i).render(graphic);
        }
    }
    public void pop(int state){
        states.remove(state);
    }
    public void add(int state){
        if(state == PLAY){
            states.add(new PlayState(this));
        }if(state == PAUSE){
            states.add(new PauseState(this));
        }if(state == GAMEOVER){
            states.add(new GameOverState(this));
        }if(state == MENU){
            states.add(new MenuState(this));
        }
    }
    
}
