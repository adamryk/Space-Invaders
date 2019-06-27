package game.play;

import processing.core.PShape;
import processing.core.PVector;

import static game.Game.getPApplet;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;

public class AlienGotArms extends Alien {
    private PShape[] children = new PShape[18];

    public AlienGotArms(float x, float y, boolean movementLeader, int colNo) {
        super(x, y, movementLeader, colNo);
        getPApplet().fill(1, 255, 255);
        for(int i = 3; i <= 9; i+=3)
            children[(i / 3) - 1] = getPApplet().createShape(RECT, 3, i, 3, 3);
        for(int i = 3; i <= 9; i+=3)
            children[(i / 3) + 2] = getPApplet().createShape(RECT, 33, i, 3, 3);
        children[6] = getPApplet().createShape(RECT, 6, 15, 3, 3);
        children[7] = getPApplet().createShape(RECT, 30, 15, 3, 3);
        children[8] = getPApplet().createShape(RECT, 6, 21, 3, 3);
        children[9] = getPApplet().createShape(RECT, 30, 21, 3, 3);
        children[10] = getPApplet().createShape(RECT, 3, 15, 3, 3);
        children[11] = getPApplet().createShape(RECT, 3, 18, 3, 3);
        children[12] = getPApplet().createShape(RECT, 33, 15, 3, 3);
        children[13] = getPApplet().createShape(RECT, 33, 18, 3, 3);
        children[14] = getPApplet().createShape(RECT, 12, 21, 3, 3);
        children[15] = getPApplet().createShape(RECT, 15, 21, 3, 3);
        children[16] = getPApplet().createShape(RECT, 21, 21, 3, 3);
        children[17] = getPApplet().createShape(RECT, 24, 21, 3, 3);
        create();
        for(int i = 0; i <= 9; i++)
            getShape().addChild(children[i]);
    }

    @Override
    public void create() {
        getPApplet().fill(1, 255, 255);
        setShape(getPApplet().createShape(GROUP));
        getShape().addChild(getPApplet().createShape(RECT, 9, 0, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 27, 0, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 12, 3, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 24, 3, 3, 3));
        for(int i = 9; i <= 27; i+=3)
            getShape().addChild(getPApplet().createShape(RECT, i, 6, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 6, 9, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 9, 9, 3, 3));
        for(int i = 15; i <= 21; i++)
            getShape().addChild(getPApplet().createShape(RECT, i, 9, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 27, 9, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 30, 9, 3, 3));
        for(int i = 3; i <= 33; i++)
            getShape().addChild(getPApplet().createShape(RECT, i, 12, 3, 3));
        for(int i = 9; i <= 27; i++)
            getShape().addChild(getPApplet().createShape(RECT, i, 15, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 9, 18, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 27, 18, 3, 3));
    }

    @Override
    public void animate() {
        if(getShape().getChildCount() == 84) {
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
    public PVector getExplosionPos() {
        return new PVector(getPos().x + 3, getPos().y);
    }

    @Override
    public int getPoints() {
        return 20;
    }

}
