package com.team6.arcadesim.managers;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;

public class MovementManager {

    public void update(float dt, List<Entity> entities) {
        for (Entity e : entities) {
            if (e.hasComponent(TransformComponent.class) && e.hasComponent(MovementComponent.class)) {
                TransformComponent tc = e.getComponent(TransformComponent.class);
                MovementComponent mc = e.getComponent(MovementComponent.class);

                // Update velocity based on acceleration
                mc.getVelocity().add(mc.getAcceleration().x * dt, mc.getAcceleration().y * dt);

                // Update position based on velocity
                tc.getPosition().add(mc.getVelocity().x * dt, mc.getVelocity().y * dt);
            }
        }
    }

    public void moveEntity(Entity e, Vector2 direction) {
        if (e.hasComponent(MovementComponent.class)) {
            MovementComponent mc = e.getComponent(MovementComponent.class);
            mc.getVelocity().set(direction);
        }
    }
}
