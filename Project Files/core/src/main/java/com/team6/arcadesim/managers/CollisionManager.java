package com.team6.arcadesim.managers;

import java.util.List;

import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionResolver;

public class CollisionManager {
    private CollisionResolver resolver;
    private boolean isActive;

    public CollisionManager() {

    }

    public void resolveCollision(List<Entity> entities) {

    }

    public void setResolver(CollisionResolver resolver) {

    }

    public void update(float dt) {

    }

    public boolean isColliding(Entity a, Entity b) {
        return false;
    }

    public void removeCollisionsInvolvingEntity(Entity e) {

    }
}
