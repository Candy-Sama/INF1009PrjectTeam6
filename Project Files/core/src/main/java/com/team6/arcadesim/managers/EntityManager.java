package com.team6.arcadesim.managers;

import java.util.List;

import com.team6.arcadesim.ecs.Component;
import com.team6.arcadesim.ecs.Entity;

public class EntityManager {
    private List<Entity> entities;

    public void addEntity(Entity e) {

    }

    public void removeEntity(Entity e) {

    }

    @SafeVarargs
    public final <T extends Component> List<Entity> getEntitiesFor(Class<? extends Component>... componentTypes) {
        return null;
    }

    public List<Entity> getEntitiesByTag(int tag, Entity e) {
        return null;
    }
}
