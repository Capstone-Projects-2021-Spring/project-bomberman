/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thuc Luong's PC
 */
public class Vector {
    public float x;
    public float y;
    
    public static float mapX;
    public static float mapY;
    
    public Vector(){
        x = 0;
        y = 0;
        
    }
    public Vector(float x, float y){
        this.x = x;
        this.y = y;
    }
    
    public void addX(float i){
        x= i++;
        
    }
    public void addY(float i){
        y= i++;
    }
    public void setX(float i){
        x = i;
    }public void setY(float i){
        y = i;
    }
    public void setVector(Vector set){
        this.x = set.x;
        this.y = set.y;
    }
    public void setVector(float x, float y){
        this.x = x;
        this.y = y;
    }
    
    public static void setMapVector(float x, float y){
        mapX = x;
        mapY = y;
    }
    public Vector getMapVector(){
        return new Vector(x - mapX, y - mapY);
    }
    
}
