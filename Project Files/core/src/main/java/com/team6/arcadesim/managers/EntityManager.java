package com.team6.arcadesim.managers;

import java.util.ArrayList;
import java.util.List;

import com.team6.arcadesim.ecs.Component;
import com.team6.arcadesim.ecs.Entity;

public class EntityManager {

    private List<Entity> entities;

    public EntityManager() {
        this.entities = new ArrayList<>();
    }



    public void addEntity(Entity e) {
        if (e != null) {
            entities.add(e);
        }
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    /**
     * Filters the master list for entities that possess specific components.
     * This allows managers to ignore irrelevant entities.
     *
     */
    public List<Entity> getEntitiesFor(Class<? extends Component>... types) {
        List<Entity> results = new ArrayList<>();
        
        for (Entity e : entities) {
            boolean hasAll = true;
            for (Class<? extends Component> type : types) {
                if (!e.hasComponent(type)) {
                    hasAll = false;
                    break;
                }
            }
            if (hasAll) {
                results.add(e);
            }
        }
        return results;
    }


    public Entity getEntityById(int id) {
        for (Entity e : entities) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    public List<Entity> getAllEntities() {
        return new ArrayList<>(entities);
    }
}