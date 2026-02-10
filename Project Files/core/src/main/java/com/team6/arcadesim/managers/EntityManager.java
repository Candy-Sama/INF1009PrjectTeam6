package com.team6.arcadesim.managers;

import java.util.ArrayList;
import java.util.List;

import com.team6.arcadesim.ecs.Component;
import com.team6.arcadesim.ecs.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class EntityManager {

    // Thread-safe list to prevent crashes when removing entities during the game loop
    private List<Entity> entities;

    public EntityManager() {
        this.entities = new CopyOnWriteArrayList<>();
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }
    
    public void removeAll() {
        entities.clear();
    }

    public List<Entity> getAllEntities() {
        return entities;
    }

    /**
     * The "Star Player" method for ECS performance.
     * Returns only entities that have ALL the specified component types.
     * Usage: getEntitiesFor(MovementComponent.class, TransformComponent.class)
     */
    @SafeVarargs
    public final List<Entity> getEntitiesFor(Class<? extends Component>... componentTypes) {
        List<Entity> result = new ArrayList<>();
        
        for (Entity e : entities) {
            boolean hasAll = true;
            for (Class<? extends Component> type : componentTypes) {
                if (!e.hasComponent(type)) {
                    hasAll = false;
                    break;
                }
            }
            if (hasAll) {
                result.add(e);
            }
        }
        return result;
    }

    public Entity getEntityById(int id) {
        for (Entity e : entities) {
            if (e.getId() == id) return e;
        }
        return null;
    }
    
    public void dispose() {
        entities.clear();
    }
}