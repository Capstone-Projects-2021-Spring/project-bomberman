
import java.awt.Graphics2D;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thuc Luong's PC
 */
public abstract class GameStateSingle {
    
    private GameStateManager game;
    
    public GameStateSingle(GameStateManager game){
        
        this.game = game;
        
    }
    
    public abstract void update();
    public abstract void input(keyhandler key);
    public abstract void render(Graphics2D buffer);
    
}
