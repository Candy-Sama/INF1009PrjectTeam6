package com.team6.arcadesim.managers;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;

public class RenderManager {
    private SpriteBatch batch;

    public RenderManager() {
        this.batch = new SpriteBatch();
    }

    /**
     * Now the Manager doesn't know WHERE the entities come from. 
     * It just draws what it is given.
     */
    public void render(List<Entity> entitiesToDraw) {
        batch.begin();
        for (Entity e : entitiesToDraw) {
            // Filter: Can only draw things with a position
            if (e.hasComponent(TransformComponent.class)) {
                TransformComponent tc = e.getComponent(TransformComponent.class);
                // Placeholder drawing logic
                // batch.draw(texture, tc.getPosition().x, tc.getPosition().y);
            }
        }
        batch.end();
    }

    public void dispose() {
        batch.dispose();
    }
}