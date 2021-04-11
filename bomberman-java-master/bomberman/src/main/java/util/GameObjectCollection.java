package util;

import gameobjects.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Collection of all game objects that exist in the game world.
 * Game objects stored in these collections will be observed and painted.
 */
public class GameObjectCollection {

    public static List<List<? extends GameObject>> gameObjects;

    // Tile objects are bombs, walls, and powerups
    public static ArrayList<TileObject> tileObjects;
    public static ArrayList<Explosion> explosionObjects;
    public static ArrayList<Bomber> bomberObjects;
    /**
     * Initialize the collections that will contain all game objects in the game world.
     */
    public static void init() {
        gameObjects = new ArrayList<>();

        tileObjects = new ArrayList<>();
        explosionObjects = new ArrayList<>();
        bomberObjects = new ArrayList<>();

        gameObjects.add(tileObjects);
        gameObjects.add(explosionObjects);
        gameObjects.add(bomberObjects);
    }

    /**
     * Add a game object to the collection to be observed and painted.
     * @param spawnObj Game object to be added
     */
    public static void spawn(TileObject spawnObj) {
        tileObjects.add(spawnObj);
    }
    public static void spawn(Explosion spawnObj) {
        explosionObjects.add(spawnObj);
    }
    public static void spawn(Bomber spawnObj) {
        bomberObjects.add(spawnObj);
    }


    /**
     * Sort object lists by y position. Used to draw objects in order according to y position.
     */
    public static void sortTileObjects() {
        tileObjects.sort(Comparator.comparing(GameObject::getPositionY));
    }
    public static void sortExplosionObjects() {
        explosionObjects.sort(Comparator.comparing(GameObject::getPositionY));
    }
    public static void sortBomberObjects() {
        bomberObjects.sort(Comparator.comparing(GameObject::getPositionY));
    }

}
