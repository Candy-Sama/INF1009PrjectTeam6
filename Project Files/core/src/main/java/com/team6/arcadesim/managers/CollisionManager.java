package com.team6.arcadesim.managers;

import java.util.List;

import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionResolver;

public class CollisionManager {
    private EntityManager entityManager;
    private CollisionResolver resolver;

    public CollisionManager(EntityManager em) {
        this.entityManager = em;
    }

    public void setResolver(CollisionResolver r) {
        this.resolver = r;
    }

    public void update(float dt) {
        List<Entity> allEntities = entityManager.getAllEntities();

        // 1. Double loop for collision detection
        for (int i = 0; i < allEntities.size(); i++) {
            Entity a = allEntities.get(i);
            
            // Filter A
            if (!isValid(a)) continue;

            for (int j = i + 1; j < allEntities.size(); j++) {
                Entity b = allEntities.get(j);

                // Filter B
                if (!isValid(b)) continue;

                if (checkCollision(a, b)) {
                    resolveCollision(a, b); //
                }
            }
        }
    }

    // Helper to check if entity has required components
    private boolean isValid(Entity e) {
        return e.hasComponent(TransformComponent.class) && e.hasComponent(CollisionComponent.class);
    }

    private boolean checkCollision(Entity a, Entity b) {
        TransformComponent tA = a.getComponent(TransformComponent.class);
        CollisionComponent cA = a.getComponent(CollisionComponent.class);
        TransformComponent tB = b.getComponent(TransformComponent.class);
        CollisionComponent cB = b.getComponent(CollisionComponent.class);

        return tA.getPosition().x < tB.getPosition().x + cB.getWidth() &&
               tA.getPosition().x + cA.getWidth() > tB.getPosition().x &&
               tA.getPosition().y < tB.getPosition().y + cB.getHeight() &&
               tA.getPosition().y + cA.getHeight() > tB.getPosition().y;
    }

    private void resolveCollision(Entity a, Entity b) {
        if (resolver != null) {
            resolver.resolveCollision(a, b);
        }
    }
}