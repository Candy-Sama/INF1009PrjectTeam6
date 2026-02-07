package com.team6.arcadesim.interfaces;

import com.team6.arcadesim.ecs.Entity;

public interface CollisionResolver {
    /**
     * Decodes the logic of what happens when two entities touch.
     * This allows the engine to handle different game rules without code changes.
     */
    void resolve(Entity a, Entity b);
}