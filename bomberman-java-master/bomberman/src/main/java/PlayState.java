
import java.awt.Color;
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
public class PlayState extends GameStateSingle{
    
    
    public PlayState(GameStateManager game){
        super(game);
    }
    public void update(){
    }
    public void input(keyhandler key){
        if(key.up.down){
            System.out.println("w is pressed");
        }
    }
    public void render(Graphics2D graphics){
        graphics.setColor(Color.red);
        graphics.fillRect(100, 100, 200, 200);
        
    }
    
}
