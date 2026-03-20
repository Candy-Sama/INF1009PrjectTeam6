package com.team6.arcadesim.components;
import com.team6.arcadesim.ecs.Component;

public class CollisionComponent implements Component {

    private float width;
    private float height;
    private boolean isSolid; // If true, physics resolution stops overlap
    private boolean isTrigger; // If true, only notifies listeners (doesn't push back)

    public CollisionComponent(float width, float height, boolean isSolid, boolean isTrigger) {
        this.width = width;
        this.height = height;
        this.isSolid = isSolid;
        this.isTrigger = isTrigger;
    }
    
    // Default 32x32 box
    public CollisionComponent() {
        this(32, 32, true, false);
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isSolid() { return isSolid; }
    public boolean isTrigger() { return isTrigger; }
}