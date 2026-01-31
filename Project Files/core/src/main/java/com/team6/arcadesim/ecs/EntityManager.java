package com.team6.arcadesim.ecs;

import com.team6.arcadesim.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class EntityManager {

    private List<Entity> entityList;

    public EntityManager() {
        entityList = new ArrayList<>();
    }

    public void addEntity(Entity entity) {
        entityList.add(entity);
    }

    public void update(float deltaTime) {
        for (Entity entity : entityList) {
            entity.update(deltaTime);
        }
    }

    public List<Entity> getEntities() {
        return entityList;
    }

    public void removeEntity(int id) {
        entityList.remove();
    }

}
