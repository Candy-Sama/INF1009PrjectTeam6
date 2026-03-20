package com.team6.arcadesim.Demoscene.CollisionListener;

import java.util.function.Consumer;

import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionListener;

/**
 * DestructionListener handles collision audio in the Sandbox Scene.
 * Detects high-velocity collisions and triggers destruction/impact sounds.
 */
public class DestructionListener implements CollisionListener {

    // Callback to trigger sound effects
    private final Consumer<String> onCollisionSoundEffect;

    // Minimum speed threshold to play destruction sound
    private static final float DESTRUCTION_SPEED_THRESHOLD = 50f;

    public DestructionListener(Consumer<String> onCollisionSoundEffect) {
        this.onCollisionSoundEffect = onCollisionSoundEffect;
    }

    @Override
    public void onCollisionStart(Entity a, Entity b) {
        // Only play destruction sound if one of the entities is moving fast
        if (isFastMoving(a) || isFastMoving(b)) {
            // Play destruction/collision sound
            onCollisionSoundEffect.accept("destruction");
        }
    }

    @Override
    public void onCollisionStay(Entity a, Entity b) {
        // No action needed while colliding
    }

    @Override
    public void onCollisionEnd(Entity a, Entity b) {
        // No action needed when collision ends
    }

    /**
     * Check if an entity is moving fast enough to trigger destruction sound
     * @param entity The entity to check
     * @return true if entity's speed exceeds threshold
     */
    private boolean isFastMoving(Entity entity) {
        if (entity == null) {
            return false;
        }

        MovementComponent movement = entity.getComponent(MovementComponent.class);
        if (movement == null) {
            return false;
        }

        // Calculate speed magnitude
        float speed = movement.getVelocity().len();

        return speed > DESTRUCTION_SPEED_THRESHOLD;
    }
}
