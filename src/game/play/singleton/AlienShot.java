package game.play.singleton;

import game.GameObject;
import game.enumeration.ShotState;
import game.play.Alien;
import game.play.BottomLine;
import game.play.Collidable;
import game.play.Shield;
import processing.core.PVector;

import java.util.HashMap;
import java.util.Map;

import static game.Game.*;
import static game.enumeration.ShotState.*;
import static game.play.Alien.hasAlienRackReachedPlayerY;
import static game.play.singleton.Player.getPlayer;
import static game.play.singleton.PlayerShot.getPlayerShot;
import static game.play.singleton.RollingShot.getRollingShot;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;
import static util.SpaceInvadersUtil.getAnyLiveAlien;
import static util.SpaceInvadersUtil.getLiveAlienCount;

public abstract class AlienShot extends Collidable {
    private static final int EXPLODING_COUNTDOWN = 10;
    private static final int PLAYER_EXPLODING_COUNTDOWN = 56;
    private static final int PLAYER_Y_POS = 648;

    private static int frameNoToDetermineIfToFire;
    private static AlienShot nextShotToFire;
    private static PVector down = new PVector(0, 12);
    private static boolean playerForcedExploding = false;

    private ShotState state = COCKED;
    private AlienShot next; // the three AlienShots reference each other circularly: 1st -> 2nd -> 3rd -> 1st
    private int stepCount = 0; // counts up every third update, as shots only move every third update
    /*
    allows a delay of x frames to be introduced between the time a shot finishes exploding and the time to check if
    a shot should be fired, as per the original game
    */
    private int frameNoOfNextMove = 0;
    /*
    Integer.MIN_VALUE is used instead of zero because zero is used as a conditional. This allows explode() to determine
    if the var has been set, since explode() is called across multiple updates during the course of a shot exploding
     */
    private int countdownUntilCocking = Integer.MIN_VALUE;
    private int frameNoToAlternateExplodingShape = 0;
    private GameObject impactedEntity = null;

    AlienShot(float x, float y) {
        super(x, y);
    }

    @Override
    public void update() {
        if(getLiveAlienCount() > 0 && getPlayerLives() > 0) {
            if((state == FIRED && hasCollidedWith(getPlayer()) && getPlayer().getAlienShotThatHitPlayer() == this) ||
                    (state == COCKED && hasAlienRackReachedPlayerY() && !playerForcedExploding
                            && getPlayer().getAlienShotThatHitPlayer() == null)) {
                state = EXPLODING;
                impactedEntity = getPlayer();
                if(state == COCKED) {
                    playerForcedExploding = true;
                    setPlayerLives(0);
                }
            }
            if(getLiveAlienCount() < 9)
                down = new PVector(0, 15); // increase shot displacement as per orig game
            if(getPApplet().frameCount >= frameNoToDetermineIfToFire && this == nextShotToFire &&
                    state == COCKED && getPlayer().hasCeaseFireAfterPlayerDeathBeenObserved() &&
                    !hasAlienRackReachedPlayerY() && getLowestStepCountOfOtherShots() >= getReloadRate()) {
                if((this instanceof BlindShot && ((BlindShot)this).isAbleToFire()) || this == getRollingShot()) {
                    fire();
                    stepCount = 1;
                    state = FIRED;
                    frameNoOfNextMove = getPApplet().frameCount + 3;
                }
                nextShotToFire = next;
            }
        }
        if(frameNoOfNextMove == getPApplet().frameCount) {
            if(state == ABOUT_TO_EXPLODE) {
                state = EXPLODING;
            } else if(state == FIRED) {
                getPos().add(down);
                animate();
                stepCount++;
                frameNoOfNextMove = getPApplet().frameCount + 3;
            }
        }
        if(state == FIRED) {
            if(getPlayerShot().getState() == FIRED || getPlayerShot().getState() == EXPLODING) {
                if(hasCollidedWith(getPlayerShot())) {
                    state = ABOUT_TO_EXPLODE;
                    impactedEntity = getPlayerShot();
                }
            }
            HashMap<AlienShot, GameObject> alienShotToImpactedEntity = new HashMap<>();
            alienShotToImpactedEntity.putAll(BottomLine.getAlienShotToEntityHit());
            alienShotToImpactedEntity.putAll(Shield.getAlienShotToEntityHit());
            for(Map.Entry<AlienShot, GameObject> entry: alienShotToImpactedEntity.entrySet()) {
                if(entry.getKey() == this) {
                    impactedEntity = entry.getValue();
                    state = ABOUT_TO_EXPLODE;
                }
            }
        }
        if(state == EXPLODING) {
            explode();
            if(getPApplet().frameCount == getPlayer().getFinishedExplodingFrameNo() && impactedEntity == getPlayer())
                setPlayerLives(getPlayerLives() - 1);
            countdownUntilCocking--;
            if(countdownUntilCocking == 0) {
                create();
                state = COCKED;
                frameNoToDetermineIfToFire = getPApplet().frameCount + 3;
                /* setting stepCount to max means shot will be disregarded by getLowestStepCountOfOtherShots() */
                stepCount = Integer.MAX_VALUE;
                countdownUntilCocking = Integer.MIN_VALUE;
                frameNoToAlternateExplodingShape = 0;
                impactedEntity = null;
            }
        }
    }

    @Override
    public void render() {
        if(state != COCKED)
            super.render();
    }

    HashMap<Integer, Alien> getColNoToLowestAlienInColumn() {
        HashMap<Integer, Alien> colNoToLowestAlienInColumn = new HashMap<>();
        Alien alien = getAnyLiveAlien();
        Alien iteratorAlien = alien;
        if(iteratorAlien != null) {
            do {
                if(iteratorAlien.isAlive() && !colNoToLowestAlienInColumn.containsKey(iteratorAlien.getColNo()))
                    colNoToLowestAlienInColumn.put(iteratorAlien.getColNo(), iteratorAlien);
                iteratorAlien = iteratorAlien.getNext();
            } while(iteratorAlien != alien);
        }
        return colNoToLowestAlienInColumn;
    }

    private int getLowestStepCountOfOtherShots() {
        AlienShot alienShot = next;
        int lowestStepCountOfOtherShots = Integer.MAX_VALUE;
        do {
            if(alienShot.state != COCKED && alienShot.stepCount < lowestStepCountOfOtherShots)
                lowestStepCountOfOtherShots = alienShot.stepCount;
            alienShot = alienShot.next;
        } while(alienShot != this);
        return lowestStepCountOfOtherShots;
    }

    private int getReloadRate() {
        int reloadRate = 7;
        if(getPlayerScore() <= 300)
            reloadRate = 48;
        else if (getPlayerScore() < 1000)
            reloadRate = 16;
        else if(getPlayerScore() < 2000)
            reloadRate = 11;
        else if(getPlayerScore() < 3000)
            reloadRate = 8;
        return reloadRate;
    }

    private void explode() {
        if(impactedEntity instanceof Player) {
            if(countdownUntilCocking == Integer.MIN_VALUE)
                countdownUntilCocking = PLAYER_EXPLODING_COUNTDOWN;
            getPApplet().fill(0, 254, 254);
            float exPosX = getPlayer().getPos().x - getPos().x;
            float exPosY = PLAYER_Y_POS - getPos().y;
            if(frameNoToAlternateExplodingShape == 0 ||
                    getPApplet().frameCount == frameNoToAlternateExplodingShape) {
                frameNoToAlternateExplodingShape = getPApplet().frameCount + 5;
                if(getShape().getChildCount() == 22) {
                    setShape(getPApplet().createShape(GROUP));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 12, exPosY, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 27, exPosY + 3, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 12, exPosY + 6, 3, 6));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 18, exPosY + 6, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 24, exPosY + 6, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 3, exPosY + 9, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 15, exPosY + 12, 6, 6));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 24, exPosY + 12, 6, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX - 3, exPosY + 15, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 9, exPosY + 15, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 24, exPosY + 15, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 30, exPosY + 15, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 3, exPosY + 18, 24, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 33, exPosY + 18, 3, 6));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX, exPosY + 21, 30, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 39, exPosY + 21, 3, 3));
                } else {
                    setShape(getPApplet().createShape(GROUP));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 3, exPosY, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 33, exPosY, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX - 6, exPosY + 3, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 12, exPosY + 3, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 27, exPosY + 3, 6, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 39, exPosY + 3, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 3, exPosY + 6, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 18, exPosY + 6, 6, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 12, exPosY + 9, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 36, exPosY + 9, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX - 3, exPosY + 12, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 6, exPosY + 12, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 12, exPosY + 12, 6, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 24, exPosY + 12, 6, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 39, exPosY + 12, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX, exPosY + 15, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 15, exPosY + 15, 9, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 33, exPosY + 15, 3, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 3, exPosY + 18, 27, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX, exPosY + 21, 6, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 9, exPosY + 21, 21, 3));
                    getShape().addChild(getPApplet().createShape(RECT, exPosX + 36, exPosY + 21, 3, 3));
                }
            }
        } else {
            if(countdownUntilCocking == Integer.MIN_VALUE) {
                countdownUntilCocking = EXPLODING_COUNTDOWN;
                getPApplet().fill(255, 255, 255);
            }
            if(impactedEntity instanceof BottomLine && getShape().getChildCount() != 14) {
                setShape(getPApplet().createShape(GROUP));
                getShape().addChild(getPApplet().createShape(RECT, 0, 696 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -6, 699 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 6, 699 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 0, 702 - getPos().y, 6, 3));
                getShape().addChild(getPApplet().createShape(RECT, 9, 702 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 0, 705 - getPos().y, 9, 12));
                getShape().addChild(getPApplet().createShape(RECT, -3, 705 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -6, 708 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -3, 711 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 9, 711 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -6, 714 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -3, 717 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 3, 717 - getPos().y, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 9, 717 - getPos().y, 3, 3));
            } else if((impactedEntity instanceof Shield || impactedEntity instanceof PlayerShot) &&
                    getShape().getChildCount() != 26) {
                setShape(getPApplet().createShape(GROUP));
                getShape().addChild(getPApplet().createShape(RECT, 0, 6, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -6, 9, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 6, 9, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 0, 12, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 3, 12, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 9, 12, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -3, 15, 3, 3));
                for(int i = 0; i < 9; i+=3)
                    for(int j = 15; j < 27; j+=3)
                        getShape().addChild(getPApplet().createShape(RECT, i, j, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -6, 18, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -3, 21, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 9, 21, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -6, 24, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, -3, 27, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 3, 27, 3, 3));
                getShape().addChild(getPApplet().createShape(RECT, 9, 27, 3, 3));
            }
        }
    }

    public static void setNextShotToFire(AlienShot nextShotToFire) {
        AlienShot.nextShotToFire = nextShotToFire;
    }

    public static PVector getAlienShotDown() {
        return down;
    }

    public void setNext(AlienShot next) {
        this.next = next;
    }

    public ShotState getState() {
        return state;
    }

    public float getBottomPosY() {
        return getPos().y + 21;
    }

    public abstract void animate();
    public abstract void fire();
}
