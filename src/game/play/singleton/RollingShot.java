package game.play.singleton;

import game.play.Alien;
import processing.core.PShape;

import static game.Game.getPApplet;
import static game.play.singleton.Player.getPlayer;
import static processing.core.PApplet.dist;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;

public class RollingShot extends AlienShot {
    private static RollingShot rollingShot = null;

    private PShape[] children = new PShape[36];

    private RollingShot(float x, float y) {
        super(x, y);
        create();
    }

    public static RollingShot getRollingShot() {
        if(rollingShot == null)
            rollingShot = new RollingShot(0, 0);
        return rollingShot;
    }

    @Override
    public void create() {
        getPApplet().fill(255, 255, 255);
        setShape(getPApplet().createShape(GROUP));
        for(int i = 0; i < 21; i+=3)
            children[i / 3] = getPApplet().createShape(RECT, 3, i, 3, 3);
        children[7] = getPApplet().createShape(RECT, 0, 6, 3, 3);
        children[8] = getPApplet().createShape(RECT, 6, 9, 3, 3);
        children[9] = getPApplet().createShape(RECT, 0, 15, 3, 3);
        children[10] = getPApplet().createShape(RECT, 6, 18, 3, 3);
        for(int i = 0; i < 21; i+=3)
            children[(i / 3) + 11] = getPApplet().createShape(RECT, 3, i, 3, 3);
        for(int i = 0; i < 21; i+=3)
            children[(i / 3) + 18] = getPApplet().createShape(RECT, 3, i, 3, 3);
        children[25] = getPApplet().createShape(RECT, 6, 0, 3, 3);
        children[26] = getPApplet().createShape(RECT, 0, 3, 3, 3);
        children[27] = getPApplet().createShape(RECT, 6, 9, 3, 3);
        children[28] = getPApplet().createShape(RECT, 0, 12, 3, 3);
        for(int i = 0; i < 21; i+=3)
            children[(i / 3) + 29] = getPApplet().createShape(RECT, 3, i, 3, 3);
        for(int i = 0; i < 11; i++)
            getShape().addChild(children[i]);
    }

    @Override
    public void fire() {
        float shortestDistance = Float.MAX_VALUE;
        for(Alien alien : getColNoToLowestAlienInColumn().values()) {
            float dist = dist(
                    alien.getPotentialSpawnPosOfShot().x, 0, getPlayer().getPos().x + 18, 0);
            if(dist < shortestDistance) {
                shortestDistance = dist;
                setPos(alien.getPotentialSpawnPosOfShot());
            }
        }
    }

    @Override
    public void animate() {
        PShape firstChild = getShape().getChild(0);
        setShape(getPApplet().createShape(GROUP));
        if(firstChild == children[0]) {
            for (int i = 11; i < 18; i++)
                getShape().addChild(children[i]);
        } else if(firstChild == children[11]) {
            for (int i = 18; i < 29; i++)
                getShape().addChild(children[i]);
        } else if(firstChild == children[18]) {
            for (int i = 29; i < 36; i++)
                getShape().addChild(children[i]);
        } else {
            for(int i = 0; i < 11; i++)
                getShape().addChild(children[i]);
        }
    }
}
