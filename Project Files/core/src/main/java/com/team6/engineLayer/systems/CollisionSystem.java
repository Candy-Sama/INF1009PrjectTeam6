package com.team6.arcadesim.systems;

import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.managers.EntityManager;

public class CollisionSystem implements EngineSystem {

    private final int priority;

    public CollisionSystem() {
        this(300);
    }

    public CollisionSystem(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager) {
        gameMaster.getCollisionManager().update(dt, entityManager.getAllEntities());
    }
}
