package com.team6.arcadesim.managers;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;

public class RenderManager {
    private SpriteBatch batch;
    private EntityManager entityManager;

    public RenderManager(EntityManager em) {
        this.entityManager = em;
        this.batch = new SpriteBatch();
    }

    public void render(float dt) {
        List<Entity> allEntities = entityManager.getAllEntities();

        batch.begin();
        for (Entity e : allEntities) {
            // Filter: Can only draw things with a position
            if (e.hasComponent(TransformComponent.class)) {
                TransformComponent tc = e.getComponent(TransformComponent.class);
                // Placeholder drawing logic (You will add sprites in Part 2)
            }
        }
        batch.end();
    }

    public void dispose() {
        batch.dispose();
    }
}