package com.team6.concreteLayer.sandbox.simulation;

import com.team6.concreteLayer.sandbox.events.SandboxAudioEvent;
import com.team6.engineLayer.ecs.Entity;
import com.team6.engineLayer.events.EventBus;
import com.team6.engineLayer.interfaces.CollisionResolver;

public class MutualDestructionResolver implements CollisionResolver {

    private final EventBus eventBus;

    public MutualDestructionResolver(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void resolve(Entity a, Entity b) {
        if (a == null || b == null) {
            return;
        }
        a.setActive(false);
        b.setActive(false);

        if (eventBus != null) {
            eventBus.publish(new SandboxAudioEvent(SandboxAudioEvent.Type.MUTUAL_DESTRUCTION, a, b));
        }
    }
}
