package game.hud;

import game.GameObject;

import static game.Game.*;
import static processing.core.PConstants.GROUP;
import static processing.core.PConstants.RECT;

public class PlayerTankLivesGraphic extends GameObject {
    public PlayerTankLivesGraphic(float x, float y) {
        super(x, y);
        create();
    }

    @Override
    public void create() {
        getPApplet().fill(0, 254, 254);
        setShape(getPApplet().createShape(GROUP));
        getShape().addChild(getPApplet().createShape(RECT, 18, 0, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 15, 3, 9, 6));
        getShape().addChild(getPApplet().createShape(RECT, 3, 9, 33, 3));
        getShape().addChild(getPApplet().createShape(RECT, 0, 12, 39, 12));
        getShape().addChild(getPApplet().createShape(RECT, 66, 0, 3, 3));
        getShape().addChild(getPApplet().createShape(RECT, 63, 3, 9, 6));
        getShape().addChild(getPApplet().createShape(RECT, 51, 9, 33, 3));
        getShape().addChild(getPApplet().createShape(RECT, 48, 12, 39, 12));
    }

    @Override
    public void update() {
        if(getPlayerLives() == 2 && getShape().getChildCount() == 8) {
            for (int i = 0; i < 4; i++)
                getShape().removeChild(getShape().getChildCount() - 1);
        } else if(getPlayerLives() == 1) {
            setShape(getPApplet().createShape());
            getGameObjectsMarkedForRemoval().add(this);
        }
    }
}
