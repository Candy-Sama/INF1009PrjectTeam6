/* Mus Code */
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

    public Entity getEntityById(int id) {
        for (Entity entity : entityList) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        return null;
    }

    public Entity getEntityByName(String name) {
        for (Entity entity : entityList) {
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return null;
    }

    // Removes an entity by its ID
    public void removeEntity(int id) {

        //use the lambda to remove entity with matching ID
        entityList.removeIf(entity -> entity.getId() == id);
    }

    public void dispose() {
        entityList.clear();
    }



}
