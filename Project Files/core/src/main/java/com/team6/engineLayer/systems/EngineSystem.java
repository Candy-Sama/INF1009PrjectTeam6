package com.team6.engineLayer.systems;

import com.team6.engineLayer.AbstractGameMaster;
import com.team6.engineLayer.managers.EntityManager;

public interface EngineSystem {
    default int getPriority() {
        return 100;
    }

    void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager);
}
