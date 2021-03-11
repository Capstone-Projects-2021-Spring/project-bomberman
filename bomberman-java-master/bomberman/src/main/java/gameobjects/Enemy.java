package gameobjects;


import util.GameObjectCollection;
import util.ResourceCollection;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;



public class Enemy extends TileObject {

    // Images
    private static BufferedImage image;


    // Stats
    private int speed;
    private boolean dead;
    private int direction;


    /**
     * Passing parameters to GameObject Constructor
     * @param position
     * @param sprite
     */

    public Enemy(Point2D.Float position, BufferedImage sprite) {
        super(position, sprite);
        this.image = ResourceCollection.Images.ENEMY_BAlLOON.getImage();
        this.collider.setRect(this.position.x + 3, this.position.y + 16 + 3, this.width - 6, this.height - 16 - 6);
        //set stats
        this.speed =2;
        this.dead=false;
        this.direction =1; //facing down





    }




    // --- MOVEMENT ---
    private void moveUp() {
        this.direction = 0;     // Using sprites that face up
        this.position.setLocation(this.position.x, this.position.y - this.speed);
    }
    private void moveDown() {
        this.direction = 1;     // Using sprites that face down
        this.position.setLocation(this.position.x, this.position.y + this.speed);
    }
    private void moveLeft() {
        this.direction = 2;     // Using sprites that face left
        this.position.setLocation(this.position.x - this.speed, this.position.y);
    }
    private void moveRight() {
        this.direction = 3;     // Using sprites that face right
        this.position.setLocation(this.position.x + this.speed, this.position.y);
    }








  //  Collision Handler


    @Override
    public void onCollisionEnter(GameObject collidingObj) {
        collidingObj.handleCollision(this);
    }

    @Override
    public void handleCollision(Bomber collidingObj) {
        collidingObj.handleCollision(this);

    }

    @Override
    public void handleCollision(Wall collidingObj) {
        collidingObj.handleCollision(this);
    }

    @Override
    public void handleCollision(Bomb collidingObj) {
        this.destroy();
    }


    @Override
    public void handleCollision(Explosion collidingObj) {
        this.destroy();


    }

    @Override
    public boolean isBreakable() {
        return true;
    }


    @Override
    public void update() {

    }

    @Override
    public void onDestroy() {

    }

    public boolean isDead() {
        return this.dead;
    }

}
