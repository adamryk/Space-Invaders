package util;

import game.GameObject;
import game.play.Alien;
import game.play.singleton.AlienShot;

import static game.Game.getGameObjects;
import static game.play.singleton.PlungerShot.getPlungerShot;
import static game.play.singleton.RollingShot.getRollingShot;
import static game.play.singleton.SquigglyShot.getSquigglyShot;

public class SpaceInvadersUtil {

    public static Alien getAnyLiveAlien() {
        Alien alien = null;
        for(GameObject gameObject : getGameObjects())
            if(gameObject instanceof Alien && ((Alien)gameObject).isAlive()) {
                alien = (Alien)gameObject;
                break;
            }
        return alien;
    }

    public static int getLiveAlienCount() {
        int liveAlienCount = 0;
        Alien liveAlien = getAnyLiveAlien();
        Alien iteratorAlien = liveAlien;
        if(iteratorAlien != null) {
            do {
                if(iteratorAlien.isAlive())
                    liveAlienCount++;
                iteratorAlien = iteratorAlien.getNext();
            } while(iteratorAlien != liveAlien);
        }
        return liveAlienCount;
    }

    public static AlienShot[] getAlienShots() {
        return new AlienShot[]{getRollingShot(), getPlungerShot(), getSquigglyShot()};
    }

}