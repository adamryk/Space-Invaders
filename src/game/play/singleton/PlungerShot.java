package game.play.singleton;

import processing.core.PShape;

import static game.Game.getPApplet;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;
import static util.SpaceInvadersUtil.getLiveAlienCount;

public class PlungerShot extends BlindShot {
    private static PlungerShot plungerShot = null;

    private PShape[] children = new PShape[32];

    private PlungerShot(float x, float y) {
        super(x, y);
        setFiringColumns(new int[] {1, 7, 1, 1, 1, 4, 11, 1, 6, 3, 1, 1, 11, 9, 2, 8});
        create();
    }

    public static PlungerShot getPlungerShot() {
        if(plungerShot == null)
            plungerShot = new PlungerShot(0, 0);
        return plungerShot;
    }

    @Override
    public void create() {
        getPApplet().fill(255, 255, 255);
        setShape(getPApplet().createShape(GROUP));
        for(int i = 0; i < 18; i+=3)
            children[i / 3] = getPApplet().createShape(RECT, 3, i, 3, 3);
        children[6] = getPApplet().createShape(RECT, 0, 9, 3, 3);
        children[7] = getPApplet().createShape(RECT, 6, 9, 3, 3);
        for(int i = 0; i < 18; i+=3)
            children[(i / 3) + 8] = getPApplet().createShape(RECT, 3, i, 3, 3);
        children[14] = getPApplet().createShape(RECT, 0, 6, 3, 3);
        children[15] = getPApplet().createShape(RECT, 6, 6, 3, 3);
        for(int i = 0; i < 18; i+=3)
            children[(i / 3) + 16] = getPApplet().createShape(RECT, 3, i, 3, 3);
        children[22] = getPApplet().createShape(RECT, 0, 0, 3, 3);
        children[23] = getPApplet().createShape(RECT, 6, 0, 3, 3);
        for(int i = 0; i < 18; i+=3)
            children[(i / 3) + 24] = getPApplet().createShape(RECT, 3, i, 3, 3);
        children[30] = getPApplet().createShape(RECT, 0, 15, 3, 3);
        children[31] = getPApplet().createShape(RECT, 6, 15, 3, 3);
        for(int i = 0; i < 8; i++)
            getShape().addChild(children[i]);
    }

    @Override
    public boolean isAbleToFire() {
        boolean ableToFire = false;
        if(getLiveAlienCount() != 1)
            ableToFire =  super.isAbleToFire();
        return ableToFire;
    }

    @Override
    public void animate() {
        PShape firstChild = getShape().getChild(0);
        setShape(getPApplet().createShape(GROUP));
        if(firstChild == children[0]) {
            for(int i = 8; i < 16; i++)
                getShape().addChild(children[i]);
        } else if(firstChild == children[8]) {
            for(int i = 16; i < 24; i++)
                getShape().addChild(children[i]);
        } else if(firstChild == children[16]) {
            for(int i = 24; i < 32; i++)
                getShape().addChild(children[i]);
        } else {
            for(int i = 0; i < 8; i++)
                getShape().addChild(children[i]);
        }
    }

    @Override
    public float getBottomPosY() {
        return getPos().y + 18;
    }

}
