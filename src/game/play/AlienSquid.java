package game.play;

import processing.core.PShape;
import processing.core.PVector;

import static game.Game.getPApplet;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;

public class AlienSquid extends Alien {
    private PShape[] children = new PShape[18];

    public AlienSquid(float x, float y, boolean movementLeader, int colNo) {
        super(x, y, movementLeader, colNo);
        getPApplet().fill(37, 232, 36);
        children[0] = getPApplet().createShape(RECT, 12, 15, 3, 3);
        children[1] = getPApplet().createShape(RECT, 21, 15, 3, 3);
        children[2] = getPApplet().createShape(RECT, 9, 18, 3, 3);
        children[3] = getPApplet().createShape(RECT, 15, 18, 3, 3);
        children[4] = getPApplet().createShape(RECT, 18, 18, 3, 3);
        children[5] = getPApplet().createShape(RECT, 24, 18, 3, 3);
        children[6] = getPApplet().createShape(RECT, 6, 21, 3, 3);
        children[7] = getPApplet().createShape(RECT, 12, 21, 3, 3);
        children[8] = getPApplet().createShape(RECT, 21, 21, 3, 3);
        children[9] = getPApplet().createShape(RECT, 27, 21, 3, 3);
        children[10] = getPApplet().createShape(RECT, 9, 15, 3, 3);
        children[11] = getPApplet().createShape(RECT, 15, 15, 3, 3);
        children[12] = getPApplet().createShape(RECT, 18, 15, 3, 3);
        children[13] = getPApplet().createShape(RECT, 24, 15, 3, 3);
        children[14] = getPApplet().createShape(RECT, 6, 18, 3, 3);
        children[15] = getPApplet().createShape(RECT, 27, 18, 3, 3);
        children[16] = getPApplet().createShape(RECT, 9, 21, 3, 3);
        children[17] = getPApplet().createShape(RECT, 24, 21, 3, 3);
        create();
        for(int i = 0; i <= 9; i++)
            getShape().addChild(children[i]);
    }

    @Override
    public void create() {
        getPApplet().fill(37, 232, 36);
        setShape(getPApplet().createShape(GROUP));
        for(int i = 3; i <= 9; i+=3)
            for(int j = 18; j <= 15 + (2 * i); j+=3)
                getShape().addChild(getPApplet().createShape(RECT, j - i, i - 3, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 6, 9, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 9, 9, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 15, 9, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 18, 9, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 24, 9, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 27, 9, 3, 3));
        for(int i = 6; i <= 27; i+=3)
            getShape().addChild(getPApplet().createShape(RECT, i, 12, 3, 3));
    }

    @Override
    public void animate() {
        if(getShape().getChildCount() == 36) {
            create();
            for(int i = 10; i <= 17; i++)
                getShape().addChild(children[i]);
        } else {
            create();
            for(int i = 0; i <= 9; i++)
                getShape().addChild(children[i]);
        }
    }

    @Override
    public float getPotentialXPosOfShotSpawn() {
        float shotSpawnPosX = getPos().x + 9;
        if(isMovingRight())
            shotSpawnPosX = getPos().x + 15;
        return shotSpawnPosX;
    }

    @Override
    public PVector getExplosionPos() {
        return new PVector(getPos().x - 3, getPos().y);
    }

    @Override
    public int getPoints() {
        return 30;
    }

}
