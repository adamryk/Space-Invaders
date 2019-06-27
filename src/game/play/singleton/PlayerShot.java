package game.play.singleton;

import game.enumeration.SaucerState;
import game.enumeration.ShotState;
import game.play.Collidable;
import processing.core.PVector;

import static game.Game.getPApplet;
import static game.enumeration.SaucerState.FLYING;
import static game.enumeration.ShotState.*;
import static game.play.Alien.getAlienHitByPlayerShot;
import static game.play.Shield.isShieldHitByPlayerShot;
import static game.play.singleton.Player.getPlayer;
import static game.play.singleton.Saucer.getSaucer;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;
import static util.SpaceInvadersUtil.getAlienShots;

public class PlayerShot extends Collidable {
    private static final int ALIEN_EXPLODING_COUNTDOWN = 16;
    private static final int SHIELD_AND_TOP_GAME_BOARD_EXPLODING_COUNTDOWN = 15;
    private static final int SAUCER_EXPLODING_COUNTDOWN = 21;
    private static final PVector UP = new PVector(0, -12);

    private static PlayerShot playerShot = null;

    private boolean beenRenderedInInitialPosition = false;
    private ShotState state = COCKED;
    private int countdownUntilCocking;
    private boolean hitAlienShot = false;
    private boolean hitSaucer = false;
    private int shotCount = 0; // used to determine Saucer points value

    private PlayerShot(float x, float y) {
        super(x, y);
        create();
    }

    public static PlayerShot getPlayerShot() {
        if(playerShot == null)
            playerShot = new PlayerShot(0, 0);
        return playerShot;
    }

    @Override
    public void create() {
        getPApplet().fill(255, 255, 255);
        setShape(getPApplet().createShape(GROUP));
        for(int i = 0; i < 12; i+=3)
            getShape().addChild(getPApplet().createShape(RECT, 0, i, 3, 3));
    }

    @Override
    public void update() {
        if(state == FIRED) {
            if(beenRenderedInInitialPosition) {
                if((getSaucer().getState() == FLYING || getSaucer().getState() == SaucerState.EXPLODING) &&
                        hasCollidedWith(getSaucer())) {
                    state = ABOUT_TO_EXPLODE;
                    hitSaucer = true;
                }
                for(AlienShot alienShot : getAlienShots()) {
                    if(alienShot.getState() == FIRED || alienShot.getState() == ABOUT_TO_EXPLODE)
                        if(hasCollidedWith(alienShot))
                            hitAlienShot = true;
                }
            } else {
                beenRenderedInInitialPosition = true;
            }
        } else if(state == EXPLODING) {
            countdownUntilCocking--;
            if(countdownUntilCocking == 0) {
                hitAlienShot = false;
                create();
                state = COCKED;
            }
        }
        if(beenRenderedInInitialPosition && (state == FIRED || state == ABOUT_TO_EXPLODE)) {
            if((state == FIRED && (isShieldHitByPlayerShot() || getAlienHitByPlayerShot() != null ||
                    getPos().y == 108 || hitAlienShot)) || (state == ABOUT_TO_EXPLODE && hitSaucer
                    && getSaucer().getNextMovementFrameNumber() == getPApplet().frameCount)) {
                state = EXPLODING;
                beenRenderedInInitialPosition = false;
                explode();
            } else if(state == FIRED) {
                getPos().add(UP);
            }
        }
    }

    @Override
    public void render() {
        if(state == FIRED || state == EXPLODING)
            super.render();
    }

    void fire() {
        if(state == COCKED) {
            setPos(new PVector(getPlayer().getPos().x + 18, 636));
            state = FIRED;
        }
    }

    private void explode() {
        shotCount++;
        setShape(getPApplet().createShape(GROUP));
        getPApplet().fill(255, 255, 255);
        if(isShieldHitByPlayerShot() || getPos().y == 108 || hitAlienShot) {
            countdownUntilCocking = SHIELD_AND_TOP_GAME_BOARD_EXPLODING_COUNTDOWN;
            getShape().addChild(getPApplet().createShape(RECT, -9, -6, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, 3, -6, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, 12, -6, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, -3, -3, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, 9, -3, 3, 3));
            for (int col = -6; col < 12; col += 3)
                for (int row = 0; row < 12; row += 3)
                    getShape().addChild(getPApplet().createShape(RECT, col, row, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, -9, 3, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, -9, 6, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, 12, 3, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, 12, 6, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, -3, 12, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, 6, 12, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, -9, 15, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, 0, 15, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, 12, 15, 3, 3));
        } else if(getAlienHitByPlayerShot() != null) {
            countdownUntilCocking = ALIEN_EXPLODING_COUNTDOWN;
            PVector expPos = getAlienHitByPlayerShot().getExplosionPos().add(
                    new PVector(-getPos().x, -getPos().y));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x + 12, expPos.y, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x + 24, expPos.y, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x + 15, expPos.y + 3, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x + 21, expPos.y + 3, 3, 3));
            for(int i = 3; i < 12; i+=3)
                getShape().addChild(getPApplet().createShape(RECT, expPos.x + i, expPos.y + i, 3, 3));
            for(int i = 0; i < 9; i+=3)
                getShape().addChild(getPApplet().createShape(
                        RECT, expPos.x + 33 - i, expPos.y + 3 + i, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x, expPos.y + 12, 6, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x + 33, expPos.y + 12, 6, 3));
            for(int i = 0; i < 9; i+=3)
                getShape().addChild(getPApplet().createShape(
                        RECT, expPos.x + 9 - i, expPos.y + 15 + i, 3, 3));
            for(int i = 3; i < 12; i+=3)
                getShape().addChild(getPApplet().createShape(
                        RECT, expPos.x + i + 24, expPos.y + 12 + i, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x + 15, expPos.y + 18, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x + 21, expPos.y + 18, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x + 12, expPos.y + 21, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPos.x + 24, expPos.y + 21, 3, 3));
        } else { // Saucer explode
            getPApplet().fill(252, 0, 255);
            countdownUntilCocking = SAUCER_EXPLODING_COUNTDOWN;
            float expPosX = getSaucer().getPos().x - getPos().x;
            float expPosY = getSaucer().getPos().y - getPos().y;
            getShape().addChild(getPApplet().createShape(RECT, expPosX - 3, expPosY, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 6, expPosY, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 12, expPosY, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 33, expPosY, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 39, expPosY, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 48, expPosY, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX, expPosY + 3, 3, 3));
            for(int i = 0; i < 9; i+=3)
                getShape().addChild(getPApplet().createShape(
                        RECT, expPosX + 27 + i, expPosY + 3 + i, 6, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 45, expPosY + 3, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX - 9, expPosY + 6, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX - 3, expPosY + 6, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 9, expPosY + 6, 12, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 6, expPosY + 9, 21, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 39, expPosY + 9, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 48, expPosY + 9, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 3, expPosY + 12, 9, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 39, expPosY + 12, 3, 3));
            for(int i = 15; i < 33; i+=6)
                getShape().addChild(getPApplet().createShape(RECT, expPosX + i, expPosY + 12, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 42, expPosY + 12, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 51, expPosY + 12, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX - 3, expPosY + 15, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 9, expPosY + 15, 15, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX - 9, expPosY + 18, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 12, expPosY + 18, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 18, expPosY + 18, 3, 3));
            for(int i = 0; i < 9; i+=3)
                getShape().addChild(getPApplet().createShape(
                        RECT, expPosX + 30 + i, expPosY + 18 - i, 6, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 45, expPosY + 18, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX - 3, expPosY + 21, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX - 3, expPosY + 21, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 9, expPosY + 21, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 21, expPosY + 21, 3, 3));
            getShape().addChild(getPApplet().createShape(RECT, expPosX + 36, expPosY + 21, 3, 3));
        }
    }

    public ShotState getState() {
        return state;
    }

    int getShotCount() {
        return shotCount;
    }
}
