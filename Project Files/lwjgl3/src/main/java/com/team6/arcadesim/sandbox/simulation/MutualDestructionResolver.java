package com.team6.arcadesim.sandbox.simulation;

import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionResolver;

public class MutualDestructionResolver implements CollisionResolver {

    @Override
    public void resolve(Entity a, Entity b) {
        if (a == null || b == null) {
            return;
        }
        a.setActive(false);
        b.setActive(false);
    }
}
