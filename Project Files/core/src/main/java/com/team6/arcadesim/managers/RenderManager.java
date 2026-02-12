package com.team6.arcadesim.managers;

import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.team6.arcadesim.components.SpriteComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;

public class RenderManager {
    private SpriteBatch batch;

    public RenderManager() {
        this.batch = new SpriteBatch();
    }

    // Renders a list of entities to the screen
    public void render(List<Entity> entitiesToDraw) {
        batch.begin();
        for (Entity e : entitiesToDraw) {
            // Filter: Can only draw things with a position and a sprite
            if (e.hasComponent(TransformComponent.class) && e.hasComponent(SpriteComponent.class)) {
                drawEntity(e);
                // Placeholder drawing logic
                // batch.draw(texture, tc.getPosition().x, tc.getPosition().y);
            }
        }
        batch.end();
    }

    private void drawEntity(Entity e) {
        TransformComponent tc = e.getComponent(TransformComponent.class);
        SpriteComponent sc = e.getComponent(SpriteComponent.class);

        // Drawing using the data defined in your UML SpriteComponent
        if (sc.getTexture() != null) {
            batch.draw(
                sc.getTexture(),
                tc.getPosition().x,
                tc.getPosition().y,
                sc.getWidth(),
                sc.getHeight()
            );
        }
    }
    
    public void dispose() {
        if (batch != null) {
            batch.dispose(); // To clear up resources
        }
    }
}