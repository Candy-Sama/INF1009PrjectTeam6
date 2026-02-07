package com.team6.arcadesim.components;
import com.team6.arcadesim.ecs.Component;

public class CollisionComponent implements Component {
    private float width;
    private float height;
    private boolean solid;
    private boolean isTrigger;

    public CollisionComponent(float width, float height, boolean solid, boolean isTrigger) {
        this.width = width;
        this.height = height;
        this.solid = solid;
        this.isTrigger = isTrigger;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    public boolean isTrigger() {
        return isTrigger;
    }

    public void setTrigger(boolean trigger) {
        isTrigger = trigger;
    }
}
