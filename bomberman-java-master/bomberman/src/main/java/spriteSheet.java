/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 *
 * @author Thuc Luong's PC
 */
public class spriteSheet {
    private BufferedImage SPRITESHEET = null;
    private BufferedImage[][] spriteArray;
    private final int FILE_SIZE = 32;
    public int w;
    public int h;
    private int wSprite;
    private int hSprite;
    private String file;

    public spriteSheet(String file) {
        this.file = file;
        w = FILE_SIZE;
        h = FILE_SIZE;

        //System.out.println("Loading: " + file + "...");
        SPRITESHEET = loadSprite(file);

        wSprite = SPRITESHEET.getWidth() / w;
        hSprite = SPRITESHEET.getHeight() / h;
        loadSpriteArray();
    }

    public spriteSheet(BufferedImage sprite, String name, int w, int h) {
        this.w = w;
        this.h = h;

        //System.out.println("Loading: " + name + "...");
        SPRITESHEET = sprite;

        wSprite = SPRITESHEET.getWidth() / w;
        hSprite = SPRITESHEET.getHeight() / h;
        loadSpriteArray();
        
    }

    public spriteSheet(String file, int w, int h) {
        this.w = w;
        this.h = h;
        this.file = file;

        //System.out.println("Loading: " + file + "...");
        SPRITESHEET = loadSprite(file);

        wSprite = SPRITESHEET.getWidth() / w;
        hSprite = SPRITESHEET.getHeight() / h;
        loadSpriteArray();
    }

    public void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public void setWidth(int i) {
        w = i;
        wSprite = SPRITESHEET.getWidth() / w;
    }

    public void setHeight(int i) {
        h = i;
        hSprite = SPRITESHEET.getHeight() / h;
    }

    public int getWidth() { return w; }
    public int getHeight() { return h; }
    public int getRows() { return hSprite; }
    public int getCols() { return wSprite; }
    public int getTotalTiles() { return wSprite * hSprite; }
    public String getFilename() { return file; }

    private BufferedImage loadSprite(String file) {
        BufferedImage sprite = null;
        try {
            sprite = ImageIO.read(getClass().getClassLoader().getResourceAsStream(file));
        } catch (Exception e) {
            System.out.println("ERROR: could not load file: " + file);
        }
        return sprite;
    }

    public void loadSpriteArray() {
        spriteArray = new BufferedImage[hSprite][wSprite];

        for (int y = 0; y < hSprite; y++) {
            for (int x = 0; x < wSprite; x++) {
                spriteArray[y][x] = getSprite(x, y);
            }
        }
    }

    public BufferedImage getSprite(int x, int y) {
        return SPRITESHEET.getSubimage(x * w, y * h, w, h);
    }

    public BufferedImage getSubimage(int x, int y, int w, int h) {
        return SPRITESHEET.getSubimage(x, y, w, h);
    }

    public BufferedImage[] getSpriteArray(int i) {
        return spriteArray[i];
    }

    public BufferedImage[][] getSpriteArray2() {
        return spriteArray;
    }

    public static void drawArray(Graphics2D g, ArrayList<BufferedImage> img, Vector pos, int width, int height, int xOffset, int yOffset) {
        float x = pos.x;
        float y = pos.y;

        for (int i = 0; i < img.size(); i++) {
            if (img.get(i) != null) {
                g.drawImage(img.get(i), (int) x, (int) y, width, height, null);
            }
            x += xOffset;
            y += yOffset;
        }
    }

   


}
