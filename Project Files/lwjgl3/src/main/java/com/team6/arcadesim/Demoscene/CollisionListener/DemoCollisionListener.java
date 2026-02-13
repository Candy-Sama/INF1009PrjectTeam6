package com.team6.arcadesim.Demoscene.CollisionListener;
import com.team6.arcadesim.interfaces.CollisionListener;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.components.MovementComponent;
import java.util.function.Consumer; // Standard Java Interface

public class DemoCollisionListener implements CollisionListener {

    // We don't hold the Manager anymore.
    // We just hold an "action" that we can trigger.
    private Consumer<String> onCollisionSoundEffect;

    // Constructor: "Give me a function that takes a String ID"
    public DemoCollisionListener(Consumer<String> onCollisionSoundEffect) {
        this.onCollisionSoundEffect = onCollisionSoundEffect;
    }

    @Override
    public void onCollisionStart(Entity a, Entity b) {
        // Logic: Only play if something is moving fast
        if (isFastMoving(a) || isFastMoving(b)) {
            // Trigger the action. We don't know WHO plays it, we just ask for it.
            onCollisionSoundEffect.accept("rubber_bounce");
        }
    }

    @Override
    public void onCollisionEnd(Entity a, Entity b) {
        // No action needed on collision end for this demo
    }

    private boolean isFastMoving(Entity e) {
        if (!e.hasComponent(MovementComponent.class)) return false;
        MovementComponent mc = e.getComponent(MovementComponent.class);
        return Math.abs(mc.getVelocity().y) > 50;
    }
}