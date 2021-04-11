package gameobjects;



import gameobjects.Player;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thuc Luong's PC
 */
public class Ai extends Player{
    
    private boolean dead;

    // Animation
    private BufferedImage[][] sprites;
    private int direction;  // 0: up, 1: down, 2: left, 3: right
    private int spriteIndex;
    private int spriteTimer;
    
    private float moveSpeed;
    
    public Ai(Point2D.Float position, BufferedImage[][] spriteMap){
        super(position, spriteMap[1][0]);
        this.collider.setRect(this.position.x + 3, this.position.y + 16 + 3, this.width - 6, this.height - 16 - 6);
        // Animation
        this.sprites = spriteMap;
        this.direction = 1;     // Facing down
        this.spriteIndex = 0;
        this.spriteTimer = 0;  
        //default stats
        this.moveSpeed = 1;
        this.dead = false;
        
  
    }
    
    // --- MOVEMENT ---
    private void moveUp() {
        this.direction = 0;     // Using sprites that face up
        this.position.setLocation(this.position.x, this.position.y - this.moveSpeed);
    }
    private void moveDown() {
        this.direction = 1;     // Using sprites that face down
        this.position.setLocation(this.position.x, this.position.y + this.moveSpeed);
    }
    private void moveLeft() {
        this.direction = 2;     // Using sprites that face left
        this.position.setLocation(this.position.x - this.moveSpeed, this.position.y);
    }
    private void moveRight() {
        this.direction = 3;     // Using sprites that face right
        this.position.setLocation(this.position.x + this.moveSpeed, this.position.y);
    }
    
    /**
     * Used in game HUD to draw the base sprite to the info box.
     * @return The sprite of the bomber facing down
     */
    public BufferedImage getBaseSprite() {
        return this.sprites[1][0];
        
    }
    public boolean isDead() {
        return this.dead;
    }
    
    
    
    
    @Override
    public void update() {
        this.collider.setRect(this.position.x + 3, this.position.y + 16 + 3, this.width - 6, this.height - 16 - 6);

        if (!this.dead) {
            // Animate sprite
            if ((this.spriteTimer += this.moveSpeed) >= 12) {
                this.spriteIndex++;
                this.spriteTimer = 0;
            }
            if ((!this.UpPressed && !this.DownPressed && !this.LeftPressed && !this.RightPressed) || (this.spriteIndex >= this.sprites[0].length)) {
              this.spriteIndex = 0;
            }
            this.sprite = this.sprites[this.direction][this.spriteIndex];

            // Movement
            if (this.UpPressed) {
                this.moveUp();
            }
            if (this.DownPressed) {
                this.moveDown();
            }
            if (this.LeftPressed) {
                this.moveLeft();
            }
            if (this.RightPressed) {
                this.moveRight();
            }

        } else {
            // Animate dying animation
            if (this.spriteTimer++ >= 30) {
                this.spriteIndex++;
                if (this.spriteIndex < this.sprites[4].length) {
                    this.sprite = this.sprites[4][this.spriteIndex];
                    this.spriteTimer = 0;
                } else if (this.spriteTimer >= 250) {
                    this.destroy();
                }
            }
        }
    }
    @Override
    public void onCollisionEnter(GameObject collidingObj) {
        collidingObj.handleCollision(this);
    }
    @Override
    public void handleCollision(Wall collidingObj) {
        this.solidCollision(collidingObj);
    }
    @Override
    public void handleCollision(Explosion collidingObj) {
        if (!this.dead) {
            this.dead = true;
            this.spriteIndex = 0;
            this.destroy();
        }
    }
    
    @Override
    public void handleCollision(Bomb collidingObj) {
        if (!this.dead) {
            this.dead = true;
            this.spriteIndex = 0;
            //this.destroy();
        }
    }
    

    
}
