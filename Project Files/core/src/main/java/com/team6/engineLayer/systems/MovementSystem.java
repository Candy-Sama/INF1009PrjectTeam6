package com.team6.arcadesim.systems;

import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.managers.EntityManager;

public class MovementSystem implements EngineSystem {

    private final int priority;

    public MovementSystem() {
        this(200);
    }

    public MovementSystem(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager) {
        gameMaster.getMovementManager().update(dt, entityManager.getAllEntities());
    }
}
