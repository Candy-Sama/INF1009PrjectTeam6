package com.team6.arcadesim.components;

// Use the Rectangle class by Libgdx math library in this file
import com.badlogic.gdx.math.Rectangle;
import com.team6.arcadesim.ecs.Component;

// CollisionComponent stores size,offset info and provide rectangle which will be used by CollisionManager for collision detection
public class CollisionComponent extends Component {

    private float width;
    private float height;
    private float offsetX;
    private float offsetY;

    private final Rectangle bounds = new Rectangle();
    public CollisionComponent(float width, float height, float offsetX, float offsetY) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    // Returns collision bounds as Rectangle
    public Rectangle getBounds() {
         // check: if component is not attached to an entity
        if (owner == null) {
            return bounds;
        }

        // update Rectangle's pos and size
        // Entity.java needa have owner.getXpos & owner.getYpos
        bounds.set(
            owner.getXPos() + offsetX,
            owner.getYPos() + offsetY,
            width,
            height
        );

        return bounds;
    }

    // make CollisionComponent no-op
    @Override
    public void update (float dt) { 
    }
}
