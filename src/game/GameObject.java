package game;

import processing.core.PApplet;
import processing.core.PShape;
import processing.core.PVector;

import static game.Game.getPApplet;

public abstract class GameObject {
    private static PApplet pApplet = getPApplet();

    private PShape shape;
    private PVector pos;

    public GameObject(float x, float y) {
        pos = new PVector(x, y);
    }

    public void render() {
        pApplet.pushMatrix(); // Stores the current transform
        pApplet.translate(pos.x, pos.y);
        pApplet.shape(shape, 0, 0);
        pApplet.popMatrix(); // Restore the transform
    }

    public PShape getShape() {
        return shape;
    }

    public void setShape(PShape p) {
        shape = p;
    }

    public PVector getPos() {
        return pos;
    }

    protected void setPos(PVector pos) {
        this.pos = pos;
    }

    public abstract void create();
    public abstract void update();
}
