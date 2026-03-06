package com.team6.arcadesim.managers;

import java.util.ArrayList;
import java.util.List;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;

public class GravityManager {
    // The gravitational constant (in m^3 kg^-1 s^-2)
    private static final double G = 5000.0; // Adjusted to fit the scale of the simulation
    
    // Minimum distance to prevent division by zero and excessive forces
    // If Entities perfectly overlap, we can set a small minimum distance to avoid infinite forces
    private static final double MIN_DISTANCE_SQ = 2500; // Adjust as needed for the scale of the simulation

    public void update(float dt, List<Entity> entities) {
        List<Entity> massiveBodies = new ArrayList<>();
        for (Entity e : entities) {
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
                double distanceSq = dx * dx + dy * dy;

                if (distanceSq < MIN_DISTANCE_SQ) {
                    distanceSq = MIN_DISTANCE_SQ; // Prevent excessive forces
                }

                float distance = (float) Math.sqrt(distanceSq);

                // Calculate the gravitational force magnitude
                float force = (float) (G * massB.getMass() / distanceSq);

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
