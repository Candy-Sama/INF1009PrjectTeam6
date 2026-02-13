package com.team6.arcadesim.Demoscene.Managers;
import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionResolver;

public class CubeCollision implements CollisionResolver {

    private float worldWidth = 800;
    private float worldHeight = 600;

    public CubeCollision() {
    }

    public CubeCollision(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    @Override
    public void resolve(Entity a, Entity b) {
        // Custom collision resolution logic for cubes
        // For example, stop movement or bounce off each other

        //Code to get the movement components and adjust positions/speeds accordingly
        MovementComponent ma = a.getComponent(MovementComponent.class);
        MovementComponent mb = b.getComponent(MovementComponent.class);

        if (ma != null && mb != null) {
            // Example: simple bounce effect by reversing velocities
            Vector2 tempVelocity = ma.getVelocity().cpy();

            ma.setVelocity(mb.getVelocity());
            mb.setVelocity(tempVelocity);
        }
    }

    /**
     * Handles wall bouncing for a single entity
     */
    public void checkWallBounce(Entity entity) {
        TransformComponent tc = entity.getComponent(TransformComponent.class);
        MovementComponent mc = entity.getComponent(MovementComponent.class);
        CollisionComponent cc = entity.getComponent(CollisionComponent.class);
        
        if (tc != null && mc != null && cc != null) {
            float x = tc.getPosition().x;
            float y = tc.getPosition().y;
            float width = cc.getWidth();
            float height = cc.getHeight();
            
            // Check left and right walls
            if (x <= 0) {
                tc.setPosition(0, y);
                mc.setVelocity(Math.abs(mc.getVelocity().x), mc.getVelocity().y);
            } else if (x + width >= worldWidth) {
                tc.setPosition(worldWidth - width, y);
                mc.setVelocity(-Math.abs(mc.getVelocity().x), mc.getVelocity().y);
            }
            
            // Check top and bottom walls
            if (y <= 0) {
                tc.setPosition(x, 0);
                mc.setVelocity(mc.getVelocity().x, Math.abs(mc.getVelocity().y));
            } else if (y + height >= worldHeight) {
                tc.setPosition(x, worldHeight - height);
                mc.setVelocity(mc.getVelocity().x, -Math.abs(mc.getVelocity().y));
            }
        }
    }
}
