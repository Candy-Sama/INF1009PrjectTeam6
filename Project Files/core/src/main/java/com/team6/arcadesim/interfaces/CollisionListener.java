package com.team6.arcadesim.interfaces;

import com.team6.arcadesim.ecs.Entity;

public interface CollisionListener {
    /**
     * Called when two entities start overlapping.
     * @param a The first entity (usually the mover)
     * @param b The second entity (usually the obstacle)
     */
    void onCollisionStart(Entity a, Entity b);
    
    void onCollisionEnd(Entity a, Entity b);
}