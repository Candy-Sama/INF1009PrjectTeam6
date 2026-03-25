package com.team6.concreteLayer.sandbox.simulation;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.team6.engineLayer.components.MassComponent;
import com.team6.engineLayer.components.MovementComponent;
import com.team6.engineLayer.components.TransformComponent;
import com.team6.engineLayer.ecs.Entity;

public class SandboxNBodyAdapter {

    public List<NBodyState> toNBodyStates(List<Entity> entities) {
        List<NBodyState> states = new ArrayList<>();
        if (entities == null || entities.isEmpty()) {
            return states;
        }

        for (Entity entity : entities) {
            if (entity == null || !entity.isActive()) {
                continue;
            }
            if (!entity.hasComponent(TransformComponent.class) || !entity.hasComponent(MassComponent.class)) {
                continue;
            }

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            MassComponent mass = entity.getComponent(MassComponent.class);
            MovementComponent movement = entity.getComponent(MovementComponent.class);

            Vector2 position = new Vector2(transform.getPosition());
            Vector2 velocity = (movement == null) ? new Vector2() : new Vector2(movement.getVelocity());
            boolean movable = movement != null;

            states.add(new NBodyState(entity.getId(), position, velocity, mass.getMass(), movable));
        }

        return states;
    }
}
