package com.team6.arcadesim.interfaces;

import com.team6.arcadesim.ecs.Entity;

public interface CollisionResolver {

    void resolve(Entity a, Entity b);
}