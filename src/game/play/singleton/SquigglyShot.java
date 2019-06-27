package game.play.singleton;

import processing.core.PShape;

import static game.Game.getPApplet;
import static game.play.singleton.Saucer.getSaucer;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;

public class SquigglyShot extends BlindShot {
    private static SquigglyShot squigglyShot = null;

    private PShape[] children = new PShape[28];

    private SquigglyShot(float x, float y) {
        super(x, y);
        setFiringColumns(new int[] {11, 1, 6, 3, 1, 1, 11, 9, 2, 8, 2, 11, 4, 7, 10});
        create();
    }

    public static SquigglyShot getSquigglyShot() {
        if(squigglyShot == null)
            squigglyShot = new SquigglyShot(0, 0);
        return squigglyShot;
    }

    @Override
    public void create() {
        getPApplet().fill(255, 255, 255);
        setShape(getPApplet().createShape(GROUP));
        for(int i = 0; i < 9; i+=3)
            children[i / 3] = getPApplet().createShape(RECT, i, i, 3, 3);
        children[3] = getPApplet().createShape(RECT, 3, 9, 3, 3);
        for(int i = 0; i < 9; i+=3)
            children[(i / 3) + 4] = getPApplet().createShape(RECT, i, i + 12, 3, 3);
        children[7] = getPApplet().createShape(RECT, 3, 0, 3, 3);
        for(int i = 0; i < 9; i+=3)
            children[(i / 3) + 8] = getPApplet().createShape(RECT, i, 9 - i, 3, 3);
        children[11] = getPApplet().createShape(RECT, 3, 12, 3, 3);
        children[12] = getPApplet().createShape(RECT, 6, 15, 3, 3);
        children[13] = getPApplet().createShape(RECT, 3, 18, 3, 3);
        for(int i = 0; i < 9; i+=3)
            children[(i / 3) + 14] = getPApplet().createShape(RECT, i, 6 - i, 3, 3);
        children[17] = getPApplet().createShape(RECT, 3, 9, 3, 3);
        for(int i = 0; i < 9; i+=3)
            children[(i / 3) + 18] = getPApplet().createShape(RECT, i, 18 - i, 3, 3);
        children[21] = getPApplet().createShape(RECT, 3, 0, 3, 3);
        for(int i = 0; i < 9; i+=3)
            children[(i / 3) + 22] = getPApplet().createShape(RECT, i, i + 3, 3, 3);
        children[25] = getPApplet().createShape(RECT, 3, 12, 3, 3);
        children[26] = getPApplet().createShape(RECT, 0, 15, 3, 3);
        children[27] = getPApplet().createShape(RECT, 3, 18, 3, 3);

        for(int i = 0; i < 7; i++)
            getShape().addChild(children[i]);
    }

    @Override
    public boolean isAbleToFire() {
        boolean ableToFire = false;
        if(getPApplet().frameCount >= getSaucer().getSaucerCycleOverFrameNo())
            ableToFire =  super.isAbleToFire();
        return ableToFire;
    }

    @Override
    public void animate() {
        PShape firstChild = getShape().getChild(0);
        setShape(getPApplet().createShape(GROUP));
        if(firstChild == children[0]) {
            for (int i = 7; i < 14; i++)
                getShape().addChild(children[i]);
        } else if(firstChild == children[7]) {
            for (int i = 14; i < 21; i++)
                getShape().addChild(children[i]);
        } else if(firstChild == children[14]) {
            for (int i = 21; i < 28; i++)
                getShape().addChild(children[i]);
        } else {
            for(int i = 0; i < 7; i++)
                getShape().addChild(children[i]);
        }
    }

}
