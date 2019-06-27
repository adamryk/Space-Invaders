package game.play.singleton;

import game.enumeration.SaucerState;
import game.hud.DrawableString;
import game.play.Collidable;
import processing.core.PVector;

import static game.Game.*;
import static game.enumeration.DrawableStringType.SAUCER_SCORE_VALUE;
import static game.enumeration.SaucerState.FLYING;
import static game.enumeration.SaucerState.WAITING_FOR_TRIP;
import static game.enumeration.ShotState.*;
import static game.play.Alien.getAlienRackMovedDownCount;
import static game.play.Alien.hasAlienRackReachedPlayerY;
import static game.play.singleton.PlayerShot.getPlayerShot;
import static game.play.singleton.SquigglyShot.getSquigglyShot;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;
import static util.SpaceInvadersUtil.getLiveAlienCount;

public class Saucer extends Collidable {
    private static final int SAUCER_COUNTDOWN = 1536;
    private static final int EXPLODING_DURATION = 20;
    private static final int POINTS_DISPLAYING_DURATION = 72;

    private static Saucer saucer = null;
    private static int[] scoringTable = {100, 50, 50, 100, 150, 100, 100, 50, 300, 100, 100, 100, 50, 150, 100};

    /* a saucer cycle ends when saucer either goes off-screen or saucer points are finished displaying */
    private int saucerCycleOverFrameNo = 0;
    private SaucerState state = WAITING_FOR_TRIP;
    private int countdownToTrip = SAUCER_COUNTDOWN;
    private PVector horizontal;
    private int nextMovementFrameNumber = 0;

    private Saucer(float x, float y) {
        super(x, y);
        create();
    }

    public static Saucer getSaucer() {
        if(saucer == null)
            saucer = new Saucer(0, 0);
        return saucer;
    }

    @Override
    public void create() {
        setShape(getPApplet().createShape(GROUP));
        getPApplet().fill(252, 0, 255);
        for(int i = 15; i <= 30; i+=3)
            getShape().addChild(getPApplet().createShape(RECT, i, 0, 3, 3));
        for(int i = 9; i <= 36; i+=3)
            getShape().addChild(getPApplet().createShape(RECT, i, 3, 3, 3));
        for(int i = 6; i <= 39; i+=3)
            getShape().addChild(getPApplet().createShape(RECT, i, 6, 3, 3));
        for(int i = 3; i <= 39; i+=9)
            for(int j = 0; j <= 3; j+=3)
                getShape().addChild(getPApplet().createShape(RECT, i + j, 9, 3, 3));
        for(int i = 0; i <= 45; i+=3)
            getShape().addChild(getPApplet().createShape(RECT, i, 12, 3, 3));
        for(int i = 6; i <= 33; i+=27)
            for(int j = 0; j <= 6; j+=3)
                getShape().addChild(getPApplet().createShape(RECT, i + j, 15, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 21, 15, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 24, 15, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 9, 18, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 36, 18, 3, 3));
    }

    @Override
    public void update() {
        if(getPlayerLives() > 0 && getLiveAlienCount() > 0 && !hasAlienRackReachedPlayerY()) {
            if(getAlienRackMovedDownCount() > 0)
                countdownToTrip--;
            /* check if it's time for a trip */
            if(countdownToTrip <= 0 && state == WAITING_FOR_TRIP && getLiveAlienCount() > 7) {
                if(getSquigglyShot().getState() == COCKED) {
                    setStartPosAndDirection();
                    saucerCycleOverFrameNo = getPApplet().frameCount + 279;
                    nextMovementFrameNumber = getPApplet().frameCount + 3;
                    state = FLYING;
                    countdownToTrip = SAUCER_COUNTDOWN;
                }
            }
            /* check if there's a collision with PlayerShot, trip is over, or it's time to move*/
            if(nextMovementFrameNumber == getPApplet().frameCount && state == FLYING) {
                if((getPlayerShot().getState() == FIRED || getPlayerShot().getState() == ABOUT_TO_EXPLODE) &&
                        hasCollidedWith(getPlayerShot())) {
                    state = SaucerState.EXPLODING;
                    saucerCycleOverFrameNo = getPApplet().frameCount + EXPLODING_DURATION + POINTS_DISPLAYING_DURATION;
                } else if((horizontal.x == 6 && getPos().x == 585) || (horizontal.x == -6 && getPos().x == 36)) {
                    state = WAITING_FOR_TRIP;
                } else {
                    getPos().add(horizontal);
                    nextMovementFrameNumber = getPApplet().frameCount + 3;
                }
            }
            if(state == SaucerState.EXPLODING) {
                if(getPApplet().frameCount == saucerCycleOverFrameNo - POINTS_DISPLAYING_DURATION) {
                    int saucerPoints = scoringTable[getPlayerShot().getShotCount() % 15];
                    String strSaucerPoints = String.format("%3d", saucerPoints);
                    getGameObjectsToBeAdded().add(new DrawableString(getPos().x - 15,
                            getPos().y, strSaucerPoints, SAUCER_SCORE_VALUE, new int[] {252, 0, 255}));
                    setPlayerScore(getPlayerScore() + saucerPoints);
                }
                if(getPApplet().frameCount == saucerCycleOverFrameNo) {
                    state = WAITING_FOR_TRIP;
                }
            }
        }
    }

    @Override
    public void render() {
        if(state == FLYING)
            super.render();
    }

    private void setStartPosAndDirection() {
        if(getPlayerShot().getShotCount() % 2 == 0) {
            setPos(new PVector(588,123));
            horizontal = new PVector(-6, 0);
        } else {
            setPos(new PVector(45,123));
            horizontal = new PVector(6, 0);
        }
    }

    public int getSaucerCycleOverFrameNo() {
        return saucerCycleOverFrameNo;
    }

    int getNextMovementFrameNumber() {
        return nextMovementFrameNumber;
    }

    SaucerState getState() {
        return state;
    }
}
