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

    public Entity createEntity() {
        Entity e = new Entity() {};
        entities.add(e);
        return e;
    }
    
    public void removeAll() {
        entities.clear();
    }

    public List<Entity> getAllEntities() {
        return entities;
    }

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