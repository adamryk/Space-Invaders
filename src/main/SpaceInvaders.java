package main;

import game.Game;
import processing.core.PApplet;

public class SpaceInvaders extends PApplet {
    private Game game;

    public static void main(String[] args) {
        PApplet.main("main.SpaceInvaders");
    }

    public void settings() {
        size(672, 780);	// "sim" pixel size for this resolution is 3 x 3
    }

    public void setup() {
        game = new Game(this);
    }

    public void draw() {
        game.execute();
    }

    public void keyPressed() {
        game.getKeys()[keyCode] = true;
    }

    public void keyReleased() {
        game.getKeys()[keyCode] = false;
    }
}
