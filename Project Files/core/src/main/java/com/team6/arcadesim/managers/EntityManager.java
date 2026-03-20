package com.team6.arcadesim.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.team6.arcadesim.ecs.Component;
import com.team6.arcadesim.ecs.Entity;

public class EntityManager {

    private final List<Entity> entities;
    private final List<Entity> pendingAdditions;
    private final List<Entity> pendingRemovals;
    private boolean updateInProgress;

    public EntityManager() {
        this.entities = new ArrayList<>();
        this.pendingAdditions = new ArrayList<>();
        this.pendingRemovals = new ArrayList<>();
        this.updateInProgress = false;
    }

    public void addEntity(Entity e) {
        if (updateInProgress) {
            pendingAdditions.add(e);
        } else {
            entities.add(e);
        }
    }

    public void removeEntity(Entity e) {
        if (updateInProgress) {
            pendingRemovals.add(e);
        } else {
            entities.remove(e);
        }
    }

    public Entity createEntity() {
        Entity e = new Entity();
        addEntity(e);
        return e;
    }
    
    public void removeAll() {
        if (updateInProgress) {
            pendingRemovals.addAll(entities);
            pendingAdditions.clear();
        } else {
            entities.clear();
        }
    }

    public void beginUpdate() {
        updateInProgress = true;
    }

    public void endUpdate() {
        updateInProgress = false;
        flushPendingChanges();
    }

    public List<Entity> getAllEntities() {
        return Collections.unmodifiableList(entities);
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
        pendingAdditions.clear();
        pendingRemovals.clear();
        updateInProgress = false;
    }

    private void flushPendingChanges() {
        if (!pendingRemovals.isEmpty()) {
            entities.removeAll(pendingRemovals);
            pendingRemovals.clear();
        }
        if (!pendingAdditions.isEmpty()) {
            entities.addAll(pendingAdditions);
            pendingAdditions.clear();
        }
    }
}
