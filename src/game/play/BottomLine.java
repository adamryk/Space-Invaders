package game.play;

import game.GameObject;
import game.play.singleton.AlienShot;
import processing.core.PShape;
import processing.core.PVector;

import java.util.HashMap;

import static game.Game.getPApplet;
import static game.enumeration.ShotState.*;
import static game.play.singleton.AlienShot.getAlienShotDown;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;
import static util.SpaceInvadersUtil.getAlienShots;

public class BottomLine extends GameObject {
    private static HashMap<AlienShot, GameObject> alienShotToEntityHit = new HashMap<>();

    public BottomLine(float x, float y) {
        super(x, y);
        create();
    }

    @Override
    public void create() {
        getPApplet().fill(154, 5, 8);
        setShape(getPApplet().createShape(GROUP));
        for(int i = 0; i < 672; i+=3)
            getShape().addChild(getPApplet().createShape(RECT, i, 0, 3, 3));

    }

    @Override
    public void update() {
        /* AlienShot collision detection and flagging*/
        for(AlienShot alienShot : getAlienShots()) {
            if(alienShot.getState() == FIRED && alienShot.getBottomPosY() + getAlienShotDown().y - 3 >= getPos().y) {
                alienShotToEntityHit.put(alienShot, this); // flag impact for AlienShot
            } else if(alienShot.getState() == ABOUT_TO_EXPLODE) {
                alienShotToEntityHit.remove(alienShot); // reset flag impact for AlienShot
            } else if(alienShot.getState() == EXPLODING) {
                for(PVector shotChildShapePos : alienShot.getChildShapesPositions()) {
                    for(PShape childShape : getShape().getChildren()) {
                        if(new PVector(getPos().x + childShape.getParams()[0],
                                getPos().y + childShape.getParams()[1]).equals(shotChildShapePos))
                            childShape.setFill(getPApplet().color(0, 0, 0));
                    }
                }
            }
        }
    }

    @Override
    public void render() {
        if(getPApplet().frameCount >= 187)
            super.render();
    }

    public static HashMap<AlienShot, GameObject> getAlienShotToEntityHit() {
        return alienShotToEntityHit;
    }
}