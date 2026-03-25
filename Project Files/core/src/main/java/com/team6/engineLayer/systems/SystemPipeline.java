package com.team6.engineLayer.systems;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.team6.engineLayer.AbstractGameMaster;
import com.team6.engineLayer.managers.EntityManager;

public class SystemPipeline {

    private final List<EngineSystem> systems;

    public SystemPipeline() {
        this.systems = new ArrayList<>();
    }

    public void addSystem(EngineSystem system) {
        systems.add(system);
        systems.sort(Comparator.comparingInt(EngineSystem::getPriority));
    }

    public void removeSystem(EngineSystem system) {
        systems.remove(system);
    }

    public void clearSystems() {
        systems.clear();
    }

    public List<EngineSystem> getSystems() {
        return new ArrayList<>(systems);
    }

    public void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager) {
        for (EngineSystem system : systems) {
            system.update(dt, gameMaster, entityManager);
        }
    }
}
