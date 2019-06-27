package game.play;

import processing.core.PVector;

import static game.Game.*;
import static game.enumeration.ShotState.FIRED;
import static game.play.singleton.Player.getPlayer;
import static game.play.singleton.PlayerShot.getPlayerShot;

public abstract class Alien extends Collidable {
    /*
    BORDER_RIGHT and BORDER_LEFT are the borders between the Alien at the left and rightmost of the rack and the edge
    of the screen when the rack is at the limit of its movement
     */
    private static final int BORDER_RIGHT = 30;
    private static final int BORDER_LEFT = 24;
    private static final PVector LEFT = new PVector(-6, 0);
    private static final PVector DOWN = new PVector(0, 24);
    private static final int TILE_WIDTH = 36;
    private static final int HEIGHT = 21;
    private static final int GAP_BETWEEN_INVADER_TILES = 12;

    private static int alienNumberToRender = 1;
    private static Alien nextAlienToMove;
    private static PVector right = new PVector(6, 0);
    private static int nextMovementFrameNo = 0;
    private static Alien alienHitByPlayerShot = null;
    private static int yGapBelowAlienOnShotSpawn = 21;
    private static int alienRackMovedDownCount = 0;
    private static boolean alienRackReachedPlayerY = false;

    private boolean toBeRendered = false;
    /*
    Aliens are inserted into gameObjects ArrayList starting with the bottom left Alien (the initial movement leader),
    and are inserted from left to right moving upwards row by row
    prev and next Aliens are set based on insertion order and are circular so that 1st.prev -> last, last.next -> 1st
     */
    private Alien prev;
    private Alien next;
    private boolean movementLeader;
    private int colNo; // columns of rack are 1 - 11
    private boolean movingRight = true;
    private boolean atMovementLimit = false;
    /* needed for counting live Aliens since Alien may be dead but not yet removed from gameObjects */
    private boolean alive = true;

    Alien(float x, float y, boolean movementLeader, int colNo) {
        super(x, y);
        this.movementLeader = movementLeader;
        this.colNo = colNo;
    }

    @Override
    public void update() {
        if(getPlayer().hasBeenHitByAlienShot())
            nextMovementFrameNo = getPlayer().getFrameNoToRespawnOn() + 1;
        /* movement */
        if(alienNumberToRender == 56 && getPApplet().frameCount > getPlayer().getFrameNoToRespawnOn() &&
                getPlayerLives() > 0 && !alienRackReachedPlayerY) {
            if(this == nextAlienToMove && getPApplet().frameCount == nextMovementFrameNo) {
                alienHitByPlayerShot = null;
                if(rackAtMovementLimit())
                    setAtMoveLimitTrueAndMovingRightToOppositeForAllAliens();
                if(atMovementLimit) {
                    atMovementLimit = false;
                    getPos().add(DOWN);
                    if(movementLeader) {
                        /* Saucer can only appear after rack has moved down once */
                        alienRackMovedDownCount++;
                        if(getPos().y == 648)
                            alienRackReachedPlayerY = true;
                    }
                }
                if(movingRight)
                    getPos().add(right);
                else
                    getPos().add(LEFT);
                animate();
                nextAlienToMove = next;
                nextMovementFrameNo++;
            }
        }
        if(getPlayerShot().getState() == FIRED && hasCollidedWith(getPlayerShot())) {
            alienHitByPlayerShot = this;
            if(nextAlienToMove == this)
                nextAlienToMove = next;
            if(movementLeader)
                next.movementLeader = true;
            prev.next = next; // maintain references
            next.prev = prev; // maintain references
            if(prev == next) // if last alien
                right = new PVector(9, 0); // increase right movement distance as per orig game
            alive = false;
            /* check aliensNextMovementFrameNo hasn't already been set above due to player flagging an impact */
            if(nextMovementFrameNo < getPApplet().frameCount + 17)
                nextMovementFrameNo = getPApplet().frameCount + 17;
            setPlayerScore(getPlayerScore() + getPoints());
            getGameObjectsMarkedForRemoval().add(this);
            if(getLiveAlienCount() < 9) {
                yGapBelowAlienOnShotSpawn = 24 + HEIGHT; //increase shot spawn distance below Alien
            }
        }
        /* fade Aliens in on game start */
        if(alienNumberToRender != 56 && movementLeader && getPApplet().frameCount == 187 + alienNumberToRender) {
            Alien alien = this;
            int alienNumberToRenderAccumulator = 1;
            while(alienNumberToRenderAccumulator != alienNumberToRender) {
                alien = alien.next;
                alienNumberToRenderAccumulator++;
            }
            alienNumberToRender++;
            alien.toBeRendered = true;
            if(alien.next.movementLeader) {
                nextAlienToMove = alien.next;
                nextMovementFrameNo = getPApplet().frameCount + 1;
            }
        }
    }

    @Override
    public void render() {
        if(toBeRendered && alive)
            super.render();
    }

    private int getNoOfAlienColsToRight() {
        Alien alien = this;
        int columnNumber = 0;
        do {
            if(alien.colNo > columnNumber)
                columnNumber = alien.colNo;
            alien = alien.next;
        } while(alien != this);
        return columnNumber - this.colNo;
    }

    private int getNoOfAlienColsToLeft() {
        Alien alien = this;
        int columnNumber = Integer.MAX_VALUE;
        do {
            if(alien.colNo < columnNumber)
                columnNumber = alien.colNo;
            alien = alien.next;
        } while(alien != this);
        return colNo - columnNumber;
    }

    private void setAtMoveLimitTrueAndMovingRightToOppositeForAllAliens(){
        Alien alien = this;
        do {
            alien.atMovementLimit = true;
            alien.movingRight ^= true;
            alien = alien.next;
        } while(alien != this);
    }

    private boolean rackAtMovementLimit() {
        return movementLeader && (movingRight && getPos().x + TILE_WIDTH +
                ((TILE_WIDTH + GAP_BETWEEN_INVADER_TILES) * getNoOfAlienColsToRight()) + BORDER_RIGHT >= 672
                || !movingRight && getPos().x -
                ((TILE_WIDTH + GAP_BETWEEN_INVADER_TILES) * getNoOfAlienColsToLeft()) - BORDER_LEFT <= 0);
    }

    private int getLiveAlienCount() {
        int liveAlienCount = 0;
        Alien alien = next;
        do {
            if(alien.isAlive())
                liveAlienCount++;
            alien = alien.next;
        } while(alien != next);
        return liveAlienCount;
    }

    public static boolean hasAlienRackReachedPlayerY() {
        return alienRackReachedPlayerY;
    }

    public static int getAlienRackMovedDownCount() {
        return alienRackMovedDownCount;
    }

    public static Alien getAlienHitByPlayerShot() {
        return alienHitByPlayerShot;
    }

    float getPotentialXPosOfShotSpawn() {
        return getPos().x + 15;
    }

    public PVector getPotentialSpawnPosOfShot() {
        return new PVector(getPotentialXPosOfShotSpawn(), getPos().y + yGapBelowAlienOnShotSpawn + HEIGHT);
    }

    static int getHeight() {
        return HEIGHT;
    }

    public Alien getNext() {
        return next;
    }

    public void setNext(Alien next) {
        this.next = next;
    }

    public void setPrev(Alien prev) {
        this.prev = prev;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getColNo() {
        return colNo;
    }

    boolean isMovingRight() {
        return movingRight;
    }

    public abstract void animate();
    public abstract PVector getExplosionPos();
    public abstract int getPoints();
}
