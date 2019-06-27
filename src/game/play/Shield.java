package game.play;

import game.GameObject;
import game.play.singleton.AlienShot;
import processing.core.PVector;

import java.util.HashMap;

import static game.Game.getGameObjectsMarkedForRemoval;
import static game.Game.getPApplet;
import static game.enumeration.ShotState.*;
import static game.play.Alien.getAlienRackMovedDownCount;
import static game.play.singleton.PlayerShot.getPlayerShot;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;
import static util.SpaceInvadersUtil.getAlienShots;
import static util.SpaceInvadersUtil.getAnyLiveAlien;

public class Shield extends Collidable {
    private static boolean shieldHitByPlayerShot = false;
    private static HashMap<AlienShot, GameObject> alienShotToEntityHit = new HashMap<>();

    private boolean markedForRemoval = false;

    public Shield(float x, float y) {
        super(x, y);
        create();
    }

    @Override
    public void create() {
        getPApplet().fill(255, 0, 0);
        setShape(getPApplet().createShape(GROUP));
        getShape().addChild(getPApplet().createShape(RECT, 0, 0, 3, 3));
    }

    @Override
    public void update() {
        /* Alien collision detection */
        if(getAlienRackMovedDownCount() > 7) {
            Alien startAlien = getAnyLiveAlien();
            Alien iteratorAlien = startAlien;
            if(iteratorAlien != null) {
                do {
                    if(getPos().x >= iteratorAlien.getPos().x && getPos().x <= iteratorAlien.getPos().x + 33 &&
                            getPos().y >= iteratorAlien.getPos().y &&
                            getPos().y <= iteratorAlien.getPos().y + Alien.getHeight()) {
                        getGameObjectsMarkedForRemoval().add(this);
                        markedForRemoval = true;
                    }
                    iteratorAlien = iteratorAlien.getNext();
                } while(iteratorAlien != startAlien);
            }
        }
        /* PlayerShot collision detection and flagging */
        if(getPlayerShot().getState() == FIRED || getPlayerShot().getState() == EXPLODING) {
            if(hasCollidedWith(getPlayerShot())) {
                if(getPlayerShot().getState() == FIRED)
                    shieldHitByPlayerShot = true; // flag impact for PlayerShot
                if(getPlayerShot().getState() == EXPLODING) {
                    shieldHitByPlayerShot = false; // reset flag impact for PlayerShot
                    getGameObjectsMarkedForRemoval().add(this);
                }
            }
        }
        /* AlienShots collision detection and flagging */
        for(AlienShot alienShot : getAlienShots()) {
            if(alienShot.getState() != COCKED) {
                for(PVector childShapePosition : alienShot.getChildShapesPositions()) {
                    if(getPos().equals(childShapePosition)) {
                        getGameObjectsMarkedForRemoval().add(this);
                        if(alienShot.getState() == FIRED && !alienShotToEntityHit.containsKey(alienShot))
                            alienShotToEntityHit.put(alienShot, this); // flag impact for AlienShot
                    }
                }
                if(alienShot.getState() == ABOUT_TO_EXPLODE)
                    alienShotToEntityHit.remove(alienShot); // reset flag impact for AlienShot
            }
        }
    }

    @Override
    public void render() {
        /* markedForRemoval checked here so Shield will not be rendered after a collision is detected with an Alien */
        if(getPApplet().frameCount >= 187 && !markedForRemoval)
            super.render();
    }

    public static boolean isShieldHitByPlayerShot() {
        return shieldHitByPlayerShot;
    }

    public static HashMap<AlienShot, GameObject> getAlienShotToEntityHit() {
        return alienShotToEntityHit;
    }
}