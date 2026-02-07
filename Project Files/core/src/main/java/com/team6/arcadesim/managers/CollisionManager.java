package com.team6.arcadesim.managers;

import java.util.List;

import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionResolver;

public class CollisionManager {
    private EntityManager entityManager;
    private CollisionResolver resolver;
    private boolean isActive;

    public void resolveCollision(List<Entity> entities) {

    }

    public void setResolver(CollisionResolver resolver) {
        this.resolver = resolver; // Strategy Pattern: Injecting the logic
    }

    public void update(float dt) {
        // Filter entities that have both a position and a hitbox
        List<Entity> collidables = entityManager.getEntitiesFor(CollisionComponent.class, TransformComponent.class);

        for (int i = 0; i < collidables.size(); i++) {
            for (int j = i + 1; j < collidables.size(); j++) {
                Entity a = collidables.get(i);
                Entity b = collidables.get(j);

                if (checkCollision(a, b)) {
                    if (resolver != null) {
                        resolver.resolveCollision(a, b); // Delegate the reaction to the resolver
                    }
                }
            }
        }
    }

    public boolean isColliding(Entity a, Entity b) {
        return checkCollision(a, b);
    }

    public void removeCollisionsInvolvingEntity(Entity e) {

    }

    private boolean checkCollision(Entity a, Entity b) {
        TransformComponent tA = a.getComponent(TransformComponent.class);
        CollisionComponent cA = a.getComponent(CollisionComponent.class);
        TransformComponent tB = b.getComponent(TransformComponent.class);
        CollisionComponent cB = b.getComponent(CollisionComponent.class);

        // Standard AABB Collision Math [cite: 232, 236]
        return tA.getPosition().x < tB.getPosition().x + cB.getWidth() &&
               tA.getPosition().x + cA.getWidth() > tB.getPosition().x &&
               tA.getPosition().y < tB.getPosition().y + cB.getHeight() &&
               tA.getPosition().y + cA.getHeight() > tB.getPosition().y;
    }
}