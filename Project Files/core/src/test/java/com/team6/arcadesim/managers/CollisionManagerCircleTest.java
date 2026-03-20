package com.team6.arcadesim.managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.RadiusComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;

public class CollisionManagerCircleTest {

    @Test
    void circleNarrowphaseDetectsEdgeTouchingBodies() {
        CollisionManager collisionManager = new CollisionManager();
        AtomicInteger resolveCount = new AtomicInteger(0);
        collisionManager.setResolver((a, b) -> resolveCount.incrementAndGet());

        Entity a = createCircleEntity(0f, 0f, 5f);
        Entity b = createCircleEntity(10f, 0f, 5f);

        collisionManager.update(1f / 60f, Arrays.asList(a, b));

        assertEquals(1, resolveCount.get());
    }

    private Entity createCircleEntity(float x, float y, float radius) {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent(x, y));
        entity.addComponent(new RadiusComponent(radius));
        entity.addComponent(new CollisionComponent(radius * 2f, radius * 2f, true, false));
        return entity;
    }
}
