package com.team6.engineLayer.interfaces;

import com.team6.engineLayer.ecs.Entity;

public interface CollisionListener {
    void onCollisionStart(Entity a, Entity b);
    default void onCollisionStay(Entity a, Entity b) {}
    
    void onCollisionEnd(Entity a, Entity b);
}
