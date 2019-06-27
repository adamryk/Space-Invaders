package game.play.singleton;

import game.play.Alien;

import java.util.Map;

public abstract class BlindShot extends AlienShot {
    private int colsToRemove = 0;
    private int[] firingColumns;

    BlindShot(float x, float y) {
        super(x, y);
    }

    public boolean isAbleToFire() {
        boolean ableToFire = false;
        if(getFiringColumns().length != 0) {
            for(int firingCol = 0; firingCol < getFiringColumns().length; firingCol++) {
                for(Map.Entry<Integer, Alien> entry : getColNoToLowestAlienInColumn().entrySet()) {
                    if(!ableToFire && entry.getKey() == getFiringColumns()[firingCol]) {
                        ableToFire = true;
                    }
                }
                if(!ableToFire)
                    colsToRemove++;
            }
        }
        return ableToFire;
    }

    /*
    sorts firingColumns so that next column to be fired from is last in array, and removes columns with no Aliens in
    them
     */
    private void processFiringColumns() {
        if(colsToRemove == getFiringColumns().length) {
            setFiringColumns(new int[]{});
        } else if(colsToRemove == getFiringColumns().length - 1) {
            setFiringColumns(new int[] {getFiringColumns()[getFiringColumns().length - 1]});
        } else if(getFiringColumns().length > 1) {
            int[] newFiringCols = new int[getFiringColumns().length - colsToRemove];
            int newLast = getFiringColumns()[colsToRemove];
            System.arraycopy(getFiringColumns(), 1 + colsToRemove, newFiringCols, 0,
                    getFiringColumns().length - 1 - colsToRemove);
            newFiringCols[getFiringColumns().length - 1 - colsToRemove] = newLast;
            setFiringColumns(newFiringCols);
        }
    }

    @Override
    public void fire() {
        processFiringColumns();
        colsToRemove = 0;
        Alien firingAlien = getColNoToLowestAlienInColumn().get(getFiringColumns()[getFiringColumns().length - 1]);
        setPos(firingAlien.getPotentialSpawnPosOfShot());
    }

    private int[] getFiringColumns() {
        return firingColumns;
    }

    void setFiringColumns(int[] firingColumns) {
        this.firingColumns = firingColumns;
    }
}
