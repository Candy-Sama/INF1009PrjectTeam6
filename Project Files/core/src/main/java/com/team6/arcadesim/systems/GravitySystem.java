package com.team6.arcadesim.systems;

import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.managers.EntityManager;

public class GravitySystem implements EngineSystem {

    private final int priority;

    public GravitySystem() {
        this(100);
    }

    public GravitySystem(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager) {
        gameMaster.getGravityManager().update(dt, entityManager.getAllEntities());
    }
}
