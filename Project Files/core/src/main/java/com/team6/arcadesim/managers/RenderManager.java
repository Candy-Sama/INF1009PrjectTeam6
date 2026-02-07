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
        this.batch = new SpriteBatch(); // The tool that actually draws to the GPU
    }

    /**
     * Iterates through all entities with a Transform and draws them.
     */
    public void render(float dt) {
        // 1. Get entities that have a position
        List<Entity> renderables = entityManager.getEntitiesFor(TransformComponent.class);

        batch.begin();
        for (Entity e : renderables) {
            TransformComponent tc = e.getComponent(TransformComponent.class);
            
            // In Part 2, you'll add a 'SpriteComponent' to entities.
            // For now, we just prepare the logic.
            drawEntity(e, tc);
        }
        batch.end();
    }

    private void drawEntity(Entity e, TransformComponent tc) {
        // Placeholder for drawing logic
        // batch.draw(texture, tc.getPosition().x, tc.getPosition().y);
    }

    public void dispose() {
        batch.dispose(); // Important for memory management
    }
}