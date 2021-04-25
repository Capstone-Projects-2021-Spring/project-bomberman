package gameobjects;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * Player class for game objects that will be controlled by the user.
 */
public abstract class Player extends GameObject {

    protected boolean UpPressed = false;
    protected boolean DownPressed = false;
    protected boolean LeftPressed = false;
    protected boolean RightPressed = false;
    protected boolean ActionPressed = false;

    /**
     * Passing parameters to GameObject constructor.
     * 
     * @param position
     * @param sprite
     */
    Player(Point2D.Float position, BufferedImage sprite) {
        super(position, sprite);
    }

    public void toggleUpPressed() {
        this.UpPressed = true;
        if (out != null) {
            out.println("Player " + player + ": Up Pressed");
        }
    }

    public void toggleDownPressed() {
        this.DownPressed = true;
        if (out != null) {
            out.println("Player " + player + ": Down Pressed");
        }
    }

    public void toggleLeftPressed() {
        this.LeftPressed = true;
        if (out != null) {
            out.println("Player " + player + ": Left Pressed");
        }
    }

    public void toggleRightPressed() {
        this.RightPressed = true;
        if (out != null) {
            out.println("Player " + player + ": Right Pressed");
        }
    }

    public void toggleActionPressed() {
        this.ActionPressed = true;
        if (out != null) {
            out.println("Player " + player + ": Bomb Placed");
        }
    }

    public void unToggleUpPressed() {
        this.UpPressed = false;
        if (out != null) {
            out.println("Player " + player + ": Up Released");
        }
    }

    public void unToggleDownPressed() {
        this.DownPressed = false;
        if (out != null) {
            out.println("Player " + player + ": Down Released");
        }
    }

    public void unToggleLeftPressed() {
        this.LeftPressed = false;
        if (out != null) {
            out.println("Player " + player + ": Left Released");
        }
    }

    public void unToggleRightPressed() {
        this.RightPressed = false;
        if (out != null) {
            out.println("Player " + player + ": Right Released");
        }
    }

    public void unToggleActionPressed() {
        this.ActionPressed = false;
        if (out != null) {
            out.println("Player " + player + ": Bomb Released");
        }
    }

}
