package com.team6.spacesim.interfaces;

import com.team6.spacesim.ecs.Entity;

public interface CollisionListener {
    void onCollisionStart(Entity a, Entity b);
    
    void onCollisionEnd(Entity a, Entity b);
}