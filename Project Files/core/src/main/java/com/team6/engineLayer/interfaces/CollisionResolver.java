package com.team6.engineLayer.interfaces;

import com.team6.engineLayer.ecs.Entity;

public interface CollisionResolver {

    void resolve(Entity a, Entity b);
}