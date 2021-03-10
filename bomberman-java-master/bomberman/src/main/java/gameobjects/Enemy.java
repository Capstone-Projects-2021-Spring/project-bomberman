package gameobjects;


import util.GameObjectCollection;
import util.ResourceCollection;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;



public class Enemy extends TileObject {

    // Animation
    private BufferedImage[][] sprites;
    private int spriteIndex;
    private int spriteTimer;

    // Stats
    private int speed;

    /**
     * Passing parameters to GameObject Constructor
     * @param position
     * @param sprite
     */

    public Enemy(Point2D.Float position, BufferedImage sprite) {
        super(position, sprite);
    }




    /*
    Collision Handler
     */

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
    public boolean isBreakable() {
        return true;
    }


    @Override
    public void update() {

    }

    @Override
    public void onDestroy() {

    }
}
