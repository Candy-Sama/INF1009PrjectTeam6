package com.team6.arcadesim.managers;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.PhysicsComponent;
import com.team6.arcadesim.ecs.Entity;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class CollisionManager {
    public void update(List<Entity> entities) {
        for (int i = 0; i < entities.length; i++) {
            // Double for loop to ensure every entity is checked with every other entity
            for (int j = i + 1; j < entities.length; j++) {
                Entity entityA = entities[i];
                Entity entityB = entities[j];

                if (checkCollision(entityA, entityB)) {
                    resolveCollision(entityA, entityB);
                }
            }
        }
    }

    public boolean checkCollision(Entity a, Entity b) {
        CollisionComponent colA = a.getComponent(CollisionComponent.class);
        CollisionComponent colB = b.getComponent(CollisionComponent.class);
        PhysicsComponent phyA = a.getComponent(PhysicsComponent.class);
        PhysicsComponent phyB = b.getComponent(PhysicsComponent.class);

        if (colA == null || colB == null || phyA == null || phyB == null) {
            return false; // Not collidable
        }

        Rectangle recA = new Rectangle(phyA.x, phyA.y, colA.width, colA.height);
        Rectangle recB = new Rectangle(phyB.x, phyB.y, colB.width, colB.height);

        return Intersector.overlaps(recA, recB); // Collision
    }

    public void resolveCollision(Entity a, Entity b) {
        PhysicsComponent phyA = a.getComponent(PhysicsComponent.class);
        PhysicsComponent phyB = B.getComponent(PhysicsComponent.class);

        // sample logic to resolve collision
        if (phyA.getVeloxityX() < phyB.getVelocityX()) {
            phyA.setVelocityX(phyA.getVelocityX - 2);
        } else {
            phyA.setVelocityX(phyA.getVelocityX + 2);
        }

    }

}
