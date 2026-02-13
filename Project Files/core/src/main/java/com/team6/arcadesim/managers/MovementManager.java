package com.team6.arcadesim.managers;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;

public class MovementManager {

    /**
     * The Main Physics Loop.
     * Called by AbstractGameMaster every frame.
     */
    public void update(float dt, List<Entity> entities) {
        for (Entity entity : entities) {
            if (!entity.hasComponent(MovementComponent.class) || 
                !entity.hasComponent(TransformComponent.class)) {
                continue;
            }

            MovementComponent mc = entity.getComponent(MovementComponent.class);
            TransformComponent tc = entity.getComponent(TransformComponent.class);

            float newX = tc.getPosition().x + (mc.getVelocity().x * dt);
            float newY = tc.getPosition().y + (mc.getVelocity().y * dt);

            tc.setPosition(newX, newY);
        }
    }

    /**
     * External control method (e.g., called by Input logic).
     * Sets the velocity immediately.
     */
    public void moveEntity(Entity e, Vector2 direction) {
        if (e.hasComponent(MovementComponent.class)) {
            MovementComponent mc = e.getComponent(MovementComponent.class);
            mc.setVelocity(direction);
        }
    }
    
    public void stopEntity(Entity e) {
        if (e.hasComponent(MovementComponent.class)) {
            e.getComponent(MovementComponent.class).setVelocity(0, 0);
        }
    }
}