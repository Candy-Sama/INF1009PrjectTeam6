package com.team6.concreteLayer.sandbox.simulation;

import com.team6.concreteLayer.sandbox.events.SandboxAudioEvent;
import com.team6.engineLayer.components.CollisionComponent;
import com.team6.engineLayer.components.CompositeShapeComponent;
import com.team6.engineLayer.components.MassComponent;
import com.team6.engineLayer.components.MovementComponent;
import com.team6.engineLayer.components.RadiusComponent;
import com.team6.engineLayer.ecs.Entity;
import com.team6.engineLayer.events.EventBus;
import com.team6.engineLayer.interfaces.CollisionResolver;

public class MergeCollisionResolver implements CollisionResolver {

    private final EventBus eventBus;

    public MergeCollisionResolver(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void resolve(Entity a, Entity b) {
        if (!isMergeable(a) || !isMergeable(b)) {
            return;
        }

        MassComponent massA = a.getComponent(MassComponent.class);
        MassComponent massB = b.getComponent(MassComponent.class);
        RadiusComponent radiusA = a.getComponent(RadiusComponent.class);
        RadiusComponent radiusB = b.getComponent(RadiusComponent.class);

        Entity survivor = massA.getMass() >= massB.getMass() ? a : b;
        Entity absorbed = (survivor == a) ? b : a;

        float mA = Math.max(0f, massA.getMass());
        float mB = Math.max(0f, massB.getMass());
        float totalMass = mA + mB;
        if (totalMass <= 0f) {
            return;
        }

        float vAx = getVelocityX(a);
        float vAy = getVelocityY(a);
        float vBx = getVelocityX(b);
        float vBy = getVelocityY(b);

        float finalVx = ((mA * vAx) + (mB * vBx)) / totalMass;
        float finalVy = ((mA * vAy) + (mB * vBy)) / totalMass;

        float rA = Math.max(0f, radiusA.getRadius());
        float rB = Math.max(0f, radiusB.getRadius());
        float finalRadius = (float) Math.sqrt((rA * rA) + (rB * rB));

        absorbed.setActive(false);
        applyMergedState(survivor, totalMass, finalRadius, finalVx, finalVy);

        if (eventBus != null) {
            eventBus.publish(new SandboxAudioEvent(SandboxAudioEvent.Type.MERGE, survivor, absorbed));
        }
    }

    private void applyMergedState(Entity survivor, float mass, float radius, float velocityX, float velocityY) {
        survivor.getComponent(MassComponent.class).setMass(mass);
        survivor.getComponent(RadiusComponent.class).setRadius(radius);
        survivor.addComponent(new CollisionComponent(radius * 2f, radius * 2f, true, false));

        MovementComponent movement = survivor.getComponent(MovementComponent.class);
        if (movement != null) {
            movement.setVelocity(velocityX, velocityY);
        }

        if (survivor.hasComponent(CompositeShapeComponent.class)) {
            CompositeShapeComponent shape = survivor.getComponent(CompositeShapeComponent.class);
            for (CompositeShapeComponent.SubShape subShape : shape.getShapes()) {
                if (subShape.getType() == CompositeShapeComponent.SubShape.ShapeType.CIRCLE) {
                    subShape.getDimensions()[0] = radius;
                }
            }
        }
    }

    private float getVelocityX(Entity entity) {
        if (!entity.hasComponent(MovementComponent.class)) {
            return 0f;
        }
        return entity.getComponent(MovementComponent.class).getVelocity().x;
    }

    private float getVelocityY(Entity entity) {
        if (!entity.hasComponent(MovementComponent.class)) {
            return 0f;
        }
        return entity.getComponent(MovementComponent.class).getVelocity().y;
    }

    private boolean isMergeable(Entity entity) {
        return entity != null
            && entity.isActive()
            && entity.hasComponent(MassComponent.class)
            && entity.hasComponent(RadiusComponent.class)
            && entity.hasComponent(CollisionComponent.class);
    }
}
