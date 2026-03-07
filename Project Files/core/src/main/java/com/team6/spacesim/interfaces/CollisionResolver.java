package com.team6.spacesim.interfaces;

import com.team6.spacesim.ecs.Entity;

public interface CollisionResolver {

    void resolve(Entity a, Entity b);
}