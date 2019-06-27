package game.play;

import game.GameObject;
import processing.core.PVector;

public abstract class Collidable extends GameObject {
    public Collidable(float x, float y) {
        super(x, y);
    }

    public PVector[] getChildShapesPositions() {
        PVector[] childShapesPositions = new PVector[getShape().getChildCount()];
        for(int i = 0; i < childShapesPositions.length; i++) {
            childShapesPositions[i] = new PVector(getPos().x + getShape().getChild(i).getParams()[0],
                    getPos().y + getShape().getChild(i).getParams()[1]);
        }
        return childShapesPositions;
    }

    protected boolean hasCollidedWith(Collidable collidable) {
        boolean collided = false;
        for(PVector childPos : getChildShapesPositions())
            for(PVector collidableChildPos : collidable.getChildShapesPositions())
                if(childPos.equals(collidableChildPos))
                    collided = true;
        return collided;
    }

}
