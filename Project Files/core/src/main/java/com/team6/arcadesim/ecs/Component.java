package com.team6.arcadesim.ecs;

abstract public class Component {
    protected Entity owner;

    public abstract void update(float deltaTime);

    public Entity getOwner() {
        return this.owner;
    }

    public void setOwner(Entity e) {
        this.owner = e;
    }
}
