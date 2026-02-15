package com.team6.arcadesim.Demoscene.Managers;

import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionResolver;

public class RubberCollision implements CollisionResolver{
    public void resolve(Entity mover, Entity obstacle) {
        System.out.println("RubberCollision.resolve() called!");
        
        if (mover.hasComponent(MovementComponent.class)) {
            MovementComponent mc = mover.getComponent(MovementComponent.class);
            TransformComponent tc = mover.getComponent(TransformComponent.class);

            System.out.println("Before bounce: velY=" + mc.getVelocity().y);
            
            if (mc.getVelocity().y < 0) {
                mc.setVelocity(mc.getVelocity().x, -mc.getVelocity().y * 0.7f);
                tc.getPosition().y += 1; // Slightly adjust position to avoid sticking
                
                System.out.println("After bounce: velY=" + mc.getVelocity().y);
            }
        }
    }
}
