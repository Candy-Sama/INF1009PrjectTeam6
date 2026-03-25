package com.team6.engineLayer.events;

import com.team6.engineLayer.ecs.Entity;

public class CollisionEvent implements EngineEvent {

    public enum Type {
        START,
        STAY,
        END
    }

    private final Type type;
    private final Entity entityA;
    private final Entity entityB;

    public CollisionEvent(Type type, Entity entityA, Entity entityB) {
        this.type = type;
        this.entityA = entityA;
        this.entityB = entityB;
    }

    public Type getType() {
        return type;
    }

    public Entity getEntityA() {
        return entityA;
    }

    public Entity getEntityB() {
        return entityB;
    }
}
