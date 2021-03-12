
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
    
    public GameStateManager(){
        states = new ArrayList<>();
        states.add(new PlayState(this));
        
        
    }
    public void update(){
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
    
}
