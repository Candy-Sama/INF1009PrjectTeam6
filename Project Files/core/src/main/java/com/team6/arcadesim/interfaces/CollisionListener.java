package com.team6.arcadesim.interfaces;

import com.team6.arcadesim.ecs.Entity;

public interface CollisionListener {
    void onCollisionStart(Entity a, Entity b);
    
    void onCollisionEnd(Entity a, Entity b);
}