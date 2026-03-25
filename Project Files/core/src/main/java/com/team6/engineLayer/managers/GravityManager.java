package com.team6.engineLayer.managers;

import java.util.ArrayList;
import java.util.List;

import com.team6.engineLayer.components.MassComponent;
import com.team6.engineLayer.components.MovementComponent;
import com.team6.engineLayer.components.TransformComponent;
import com.team6.engineLayer.ecs.Entity;
import com.team6.engineLayer.physics.GravityConfig;

public class GravityManager {

    private GravityConfig gravityConfig;

    public GravityManager() {
        this(new GravityConfig());
    }

    public GravityManager(GravityConfig gravityConfig) {
        setGravityConfig(gravityConfig);
    }

    public void setGravityConfig(GravityConfig gravityConfig) {
        this.gravityConfig = (gravityConfig == null) ? new GravityConfig() : gravityConfig;
    }

    public GravityConfig getGravityConfig() {
        return gravityConfig;
    }

    public void update(float dt, List<Entity> entities) {
        float gravityConstant = gravityConfig.getGravityConstant();
        float minDistanceSq = gravityConfig.getMinDistanceSq();

        List<Entity> massiveBodies = new ArrayList<>();
        for (Entity e : entities) {
            if (!e.isActive()) {
                continue;
            }
            if (e.hasComponent(MassComponent.class) && e.hasComponent(TransformComponent.class)) {
                massiveBodies.add(e);
            }
        }

        for (Entity a : massiveBodies) {
            MovementComponent movA = a.getComponent(MovementComponent.class);

            // if the Entity cannot move (e.g., a stationary planet), skip applying force to it
            // Forces are still applied to other Entities due to this Entity's mass, but it won't be affected by those forces
            if (movA == null) {
                continue;
            }

            TransformComponent transA = a.getComponent(TransformComponent.class);
            float totalAccX = 0;
            float totalAccY = 0;

            for (Entity b : massiveBodies) {
                if (a == b) continue; // Skip self
                TransformComponent transB = b.getComponent(TransformComponent.class);
                MassComponent massB = b.getComponent(MassComponent.class);

                // Calculate the vector from A to B
                float dx = transB.getPosition().x - transA.getPosition().x;
                float dy = transB.getPosition().y - transA.getPosition().y;
                float distanceSq = dx * dx + dy * dy;

                if (distanceSq < minDistanceSq) {
                    distanceSq = minDistanceSq; // Prevent excessive forces
                }

                float distance = (float) Math.sqrt(distanceSq);

                // Calculate the gravitational force magnitude
                float force = gravityConstant * massB.getMass() / distanceSq;

                // Calculate the acceleration components
                totalAccX += force * (dx / distance);
                totalAccY += force * (dy / distance);
            }

            // Update the velocity of Entity A based on the total acceleration
            movA.setVelocity(
                movA.getVelocity().x + totalAccX * dt, 
                movA.getVelocity().y + totalAccY * dt
            );
        }
    }
}
