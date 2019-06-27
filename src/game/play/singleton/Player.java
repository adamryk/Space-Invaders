package game.play.singleton;

import game.play.Collidable;
import processing.core.PVector;

import static game.Game.*;
import static game.enumeration.ShotState.FIRED;
import static game.play.Alien.hasAlienRackReachedPlayerY;
import static game.play.singleton.PlayerShot.getPlayerShot;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;
import static util.SpaceInvadersUtil.getAlienShots;
import static util.SpaceInvadersUtil.getLiveAlienCount;

public class Player extends Collidable {
    private static final int INITIAL_SPAWN_FRAME_NO = 316;
    private static final int WIDTH = 39;

    private final char left = 'a';
    private final char right = 'd';
    private final char shoot = ' ';

    private static Player player = null;

    private int frameNoToRespawnOn;
    /* allows for a delay after player respawn before AlienShot processing begins, as per orig game */
    private int ceasefireNoOfFrames = 364;
    private boolean beenHitByAlienShot = false;
    private AlienShot alienShotThatHitPlayer = null;
    private boolean shootPressedThenReleased = true;

    private Player(float x, float y) {
        super(x, y);
        create();
    }

    public static Player getPlayer(){
        if(player == null)
            player = new Player(54, 648);
        return player;
    }

    @Override
    public void create() {
        getPApplet().fill(0, 254, 254);
        setShape(getPApplet().createShape(GROUP));
        getShape().addChild(getPApplet().createShape(RECT, 18, 0, 3, 3));
        for(int i = 15; i <= 21; i+=3)
            for(int j = 3; j <= 6; j+=3)
                getShape().addChild(getPApplet().createShape(RECT, i, j, 3, 3));
        for(int i = 3; i <= 33; i+=3)
            getShape().addChild(getPApplet().createShape(RECT, i, 9, 3, 3));
        for(int i = 0; i <= 36; i+=3)
            for(int j = 12; j <= 21; j+=3)
                getShape().addChild(getPApplet().createShape(RECT, i, j, 3, 3));
    }

    @Override
    public void update() {
        if(!isKeyPressed(shoot))
            shootPressedThenReleased = true;
        if(getPApplet().frameCount == frameNoToRespawnOn) {
            create();
            setPos(new PVector(54, 648));
        }
        if(beenHitByAlienShot) {
            setShape(getPApplet().createShape());
            ceasefireNoOfFrames = getPApplet().frameCount + 231;
            alienShotThatHitPlayer = null;
            beenHitByAlienShot = false;
        }
        if(isSpawned() && getPlayerLives() > 0 && getLiveAlienCount() > 0 && !hasAlienRackReachedPlayerY()) {
            if(isKeyPressed(left) && getPos().x > 54)
                getPos().add(-3, 0);
            if(isKeyPressed(right) && getPos().x + WIDTH < 600)
                getPos().add(3, 0);
            if(isKeyPressed(shoot) && shootPressedThenReleased) {
                shootPressedThenReleased = false;
                getPlayerShot().fire();
            }

            /* AlienShots collision detection and flagging */
            for(AlienShot alienShot : getAlienShots()) {
                if(alienShot.getState() == FIRED && hasCollidedWith(alienShot)) {
                    beenHitByAlienShot = true;
                    frameNoToRespawnOn = getPApplet().frameCount + 183;
                    /* serves as  flag for AlienShots and also prevents more than one of them hitting the Player */
                    alienShotThatHitPlayer = alienShot;
                }
            }
        } else if(isSpawned() && hasAlienRackReachedPlayerY()) {
            frameNoToRespawnOn = getPApplet().frameCount + 183;
        }
    }

    @Override
    public void render() {
        if(isSpawned() && getPlayerLives() > 0 && !hasAlienRackReachedPlayerY())
            super.render();
    }

    public int getFinishedExplodingFrameNo() {
        return frameNoToRespawnOn - 128;
    }

    private boolean isKeyPressed(int k) {
        if(getKeys().length >= k)
            return getKeys()[k] || getKeys()[Character.toUpperCase(k)];
        return false;
    }

    AlienShot getAlienShotThatHitPlayer() {
        return alienShotThatHitPlayer;
    }

    private boolean isSpawned() {
        return getPApplet().frameCount >= INITIAL_SPAWN_FRAME_NO &&
                getPApplet().frameCount >= frameNoToRespawnOn;
    }

    boolean hasCeaseFireAfterPlayerDeathBeenObserved() {
        return getPApplet().frameCount > ceasefireNoOfFrames;
    }

    public int getFrameNoToRespawnOn() {
        return frameNoToRespawnOn;
    }

    public boolean hasBeenHitByAlienShot() {
        return beenHitByAlienShot;
    }
}