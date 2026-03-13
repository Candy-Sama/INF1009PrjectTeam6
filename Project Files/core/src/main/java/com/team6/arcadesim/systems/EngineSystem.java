package com.team6.arcadesim.systems;

import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.managers.EntityManager;

public interface EngineSystem {
    default int getPriority() {
        return 100;
    }

    void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager);
}
