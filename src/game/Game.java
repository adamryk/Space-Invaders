package game;

import game.hud.DrawableString;
import game.hud.PlayerTankLivesGraphic;
import game.play.*;
import processing.core.PApplet;

import java.util.ArrayList;

import static game.enumeration.DrawableStringType.*;
import static game.play.singleton.AlienShot.setNextShotToFire;
import static game.play.singleton.Player.getPlayer;
import static game.play.singleton.PlayerShot.getPlayerShot;
import static game.play.singleton.PlungerShot.getPlungerShot;
import static game.play.singleton.RollingShot.getRollingShot;
import static game.play.singleton.Saucer.getSaucer;
import static game.play.singleton.SquigglyShot.getSquigglyShot;
import static util.SpaceInvadersUtil.getLiveAlienCount;

public class Game {
    private static PApplet pApplet;
    private static ArrayList<GameObject> gameObjects = new ArrayList<>();
    private static ArrayList<GameObject> gameObjectsMarkedForRemoval = new ArrayList<>();
    private static ArrayList<GameObject> gameObjectsToBeAdded = new ArrayList<>();
    private static boolean [] keys = new boolean[1000];
    private static int playerScore;
    private static int playerLives = 3;

    private boolean gameOver = false;

    public Game(PApplet pApplet) {
        Game.pApplet = pApplet;
        pApplet.background(0);
        pApplet.noStroke();
        setupAndInitialise();
    }

    public void execute() {
        pApplet.background(0);
        if(isGameIsEndingInThisFrame())
            gameObjectsToBeAdded.add(new DrawableString(228, 168, "", GAME_OVER_FADING_IN,
                    new int[]{255, 255, 255}));
        addGameObjects();
        doGameLoop();
        removeGameObjects();
    }

    private void doGameLoop() {
        for(GameObject gameObject : gameObjects) {
            gameObject.update();
            gameObject.render();
        }
    }

    private void setupAndInitialise() {
        /*
        insertion order into gameObjects is important as functions for updating and rendering depend on it
         */

        /* begin add and initialise Aliens */
        int initBorder = 78; // initial border between left of screen and left of alien rack
        for (int i = 0; i < 11; i++) {
            if(i == 0)
                gameObjects.add(new AlienBigHead(initBorder + (48 * i), 384, true,
                        i + 1));
            else
                gameObjects.add(new AlienBigHead(initBorder + (48 * i), 384, false, i + 1));
        }
        for (int i = 0; i < 11; i++)
            gameObjects.add(new AlienBigHead(initBorder + (48 * i), 336, false, i + 1));
        for (int i = 0; i < 11; i++)
            gameObjects.add(new AlienGotArms(initBorder + 3 + (48 * i), 288, false, i + 1));
        for (int i = 0; i < 11; i++)
            gameObjects.add(new AlienGotArms(initBorder + 3 + (48 * i), 240, false, i + 1));
        for (int i = 0; i < 11; i++)
            gameObjects.add(new AlienSquid(initBorder + 6 + (48 * i), 192, false, i + 1));

        for(int i = 0; i < gameObjects.size(); i++) {
            if(gameObjects.get(i) instanceof Alien) {
                for(int j = 0; j < gameObjects.size(); j++) {
                    if(gameObjects.get(j) instanceof Alien) {
                        if(j == i)
                            ((Alien)gameObjects.get(j)).setPrev(((Alien)gameObjects.get(j + 54)));
                        else
                            ((Alien)gameObjects.get(j)).setPrev((Alien)gameObjects.get(j - 1));
                        if(j < gameObjects.size() - 1)
                            ((Alien)gameObjects.get(j)).setNext((Alien)gameObjects.get(j + 1));
                        else
                            ((Alien)gameObjects.get(j)).setNext((Alien)gameObjects.get(i));
                    }
                }
                break;
            }
        }
        /* end add and initialise Aliens */

        /* add Shields */
        for(int s = 0; s < 408; s += 135) {
            for(int col = 0; col < 15; col += 3)
                for(int row = 12; row < 48 + col; row += 3)
                    gameObjects.add(new Shield(96 + s + col, 576 + row - col));
            for(int col = 0; col < 6; col += 3)
                for(int row = 0; row < 42 - col; row += 3)
                    gameObjects.add(new Shield(111 + s + col, 576 + row));
            for(int col = 0; col < 21; col += 3)
                for(int row = 0; row < 36; row += 3)
                    gameObjects.add(new Shield(117 + s + col, 576 + row));
            for(int col = 0; col < 6; col += 3)
                for(int row = 0; row < 39 + col; row += 3)
                    gameObjects.add(new Shield(138 + s + col, 576 + row));
            for(int row = 0; row < 48; row += 3)
                gameObjects.add(new Shield(144 + s, 576 + row));
            for(int col = 0; col < 15; col += 3)
                for(int row = 0; row < 48 - col; row += 3)
                    gameObjects.add(new Shield(147 + s + col, 576 + row + col));
        }

        gameObjects.add(new BottomLine(0, 717));

        /* add and configure singletons */
        gameObjects.add(getSaucer());
        gameObjects.add(getPlayer());
        gameObjects.add(getPlayerShot());
        getRollingShot().setNext(getPlungerShot());
        gameObjects.add(getRollingShot());
        getPlungerShot().setNext(getSquigglyShot());
        gameObjects.add(getPlungerShot());
        getSquigglyShot().setNext(getRollingShot());
        gameObjects.add(getSquigglyShot());
        setNextShotToFire(getRollingShot());

        /* add hud elements */
        gameObjects.add(new DrawableString(276, 27, "score", UNCHANGING, new int[]{255, 255, 255}));
        gameObjects.add(new DrawableString(288, 72, "0000", FLASHING, new int[]{255, 255, 255}));
        gameObjects.add(new DrawableString(288, 336, "play", FADING_OUT, new int[]{255, 255, 255}));
        gameObjects.add(new DrawableString(24, 720, "3", LIVES, new int[]{255, 255, 255}));
        gameObjects.add(new PlayerTankLivesGraphic(78, 720));
    }

    private void removeGameObjects() {
        if(!gameObjectsMarkedForRemoval.isEmpty()) {
            gameObjects.removeAll(gameObjectsMarkedForRemoval);
            gameObjectsMarkedForRemoval.clear();
        }
    }

    private void addGameObjects() {
        if(!gameObjectsToBeAdded.isEmpty()) {
            gameObjects.addAll(gameObjectsToBeAdded);
            gameObjectsToBeAdded.clear();
        }
    }

    private boolean isGameIsEndingInThisFrame() {
        if(!gameOver && (playerLives == 0 || getLiveAlienCount() == 0))
            gameOver = true;
        return gameOver;
    }

    public static PApplet getPApplet() {
        return pApplet;
    }

    public static ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    public static ArrayList<GameObject> getGameObjectsMarkedForRemoval() {
        return gameObjectsMarkedForRemoval;
    }

    public static ArrayList<GameObject> getGameObjectsToBeAdded() {
        return gameObjectsToBeAdded;
    }

    public static boolean[] getKeys() {
        return keys;
    }

    public static int getPlayerScore() {
        return playerScore;
    }

    public static void setPlayerScore(int playerScore) {
        Game.playerScore = playerScore;
    }

    public static int getPlayerLives() {
        return playerLives;
    }

    public static void setPlayerLives(int playerLives) {
        Game.playerLives = playerLives;
    }
}
