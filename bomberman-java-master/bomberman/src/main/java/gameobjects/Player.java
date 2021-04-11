package gameobjects;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;

/**
 * Player class for game objects that will be controlled by the user.
 */
public abstract class Player extends GameObject {

    protected boolean UpPressed = false;
    protected boolean DownPressed = false;
    protected boolean LeftPressed = false;
    protected boolean RightPressed = false;
    protected boolean ActionPressed = false;
    public PrintWriter out = null;
    public int player = 0;

    /**
     * Passing parameters to GameObject constructor.
     * @param position
     * @param sprite
     */
    Player(Point2D.Float position, BufferedImage sprite, PrintWriter out, int player) {
        super(position, sprite);
        this.out = out;
        this.player = player;
    }

    Player(Point2D.Float position, BufferedImage sprite) {
        super(position, sprite);
        this.out = out;
        this.player = player;
    }

    public void toggleUpPressed() {
        this.UpPressed = true;
        if(out != null) {
        	out.println("Player " + player + ": Up");
        }
    }
    public void toggleDownPressed() {
        this.DownPressed = true;
        if(out != null) {
        	out.println("Player " + player + ": Down");
        }
    }
    public void toggleLeftPressed() {
        this.LeftPressed = true;
        if(out != null) {
        	out.println("Player " + player + ": Left");
        }
    }
    public void toggleRightPressed() {
        this.RightPressed = true;
        if(out != null) {
        	out.println("Player " + player + ": Right");
        }
    }
    public void toggleActionPressed() {
        this.ActionPressed = true;
        if(out != null) {
        	out.println("Player " + player + ": Bomb");
        }
    }

    public void unToggleUpPressed() {
        this.UpPressed = false;
        if(out != null) {
        	out.println("Player " + player + ": UpR");
        }
    }
    public void unToggleDownPressed() {
        this.DownPressed = false;
        if(out != null) {
        	out.println("Player " + player + ": DownR");
        }
    }
    public void unToggleLeftPressed() {
        this.LeftPressed = false;
        if(out != null) {
        	out.println("Player " + player + ": LeftR");
        }
    }
    public void unToggleRightPressed() {
        this.RightPressed = false;
        if(out != null) {
        	out.println("Player " + player + ": RightR");
        }
    }
    public void unToggleActionPressed() {
        this.ActionPressed = false;
        if(out != null) {
        	out.println("Player " + player + ": BombR");
        }
    }

}
