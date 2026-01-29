package com.team6.arcadesim.managers;
import com.team6.arcadesim.components.PhysicsComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;

public class MovementManager {
    public void update(Entity[] entities, float deltaTime) {
        for (Entity entity : entities) {
            PhysicsComponent phyC = entity.getComponent(PhysicsComponent.class);
            TransformComponent tranC = entity.getComponent(TransformComponent.class);

            if (phyC != null && tranC != null) {
                tranC.setX(tranC.getX() + phyC.getVelocityX() * deltaTime);
                tranC.setY(tranC.getY() + phyC.getVelocityY() * deltaTime);
            }
        }
    }

}
