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
    public PrintWriter out;
    public int player;

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

    public void toggleUpPressed() {
        this.UpPressed = true;
        out.println("Player " + player + ": Up");
    }
    public void toggleDownPressed() {
        this.DownPressed = true;
        out.println("Player " + player + ": Down");
    }
    public void toggleLeftPressed() {
        this.LeftPressed = true;
        out.println("Player " + player + ": Left");
    }
    public void toggleRightPressed() {
        this.RightPressed = true;
        out.println("Player " + player + ": Right");
    }
    public void toggleActionPressed() {
        this.ActionPressed = true;
        out.println("Player " + player + ": Bomb");
    }

    public void unToggleUpPressed() {
        this.UpPressed = false;
    }
    public void unToggleDownPressed() {
        this.DownPressed = false;
    }
    public void unToggleLeftPressed() {
        this.LeftPressed = false;
    }
    public void unToggleRightPressed() {
        this.RightPressed = false;
    }
    public void unToggleActionPressed() {
        this.ActionPressed = false;
    }

}
