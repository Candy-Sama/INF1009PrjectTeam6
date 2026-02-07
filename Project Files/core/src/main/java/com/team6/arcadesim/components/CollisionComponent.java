package com.team6.arcadesim.components;

import com.team6.arcadesim.ecs.Component;

public class CollisionComponent implements Component {
    private float width;
    private float height;
    private boolean solid;
    private boolean isTrigger;

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {

    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {

    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {

    }

    public boolean isTrigger() {
        return isTrigger;
    }

    public void setTrigger(boolean trigger) {

    }
}
