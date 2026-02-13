package com.team6.arcadesim.managers;

import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.team6.arcadesim.components.SpriteComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;


public class RenderManager implements Disposable {

    private SpriteBatch batch;

    public RenderManager() {
        // The heavy GPU object. Created once, reused forever.
        this.batch = new SpriteBatch();
    }

    /**
     * The Main Render Loop.
     * Called by scenes with the camera passed from AbstractGameMaster via GameMaster.
     */
    public void render(float dt, List<Entity> entities, OrthographicCamera camera) {
        // Ensure camera is set right before rendering
        if (camera != null) {
            batch.setProjectionMatrix(camera.combined);
        }
        
        batch.begin();

        for (Entity entity : entities) {
            // 1. Check: Entity must have Position (Transform) AND Looks (Sprite)
            if (!entity.hasComponent(TransformComponent.class) || 
                !entity.hasComponent(SpriteComponent.class)) {
                continue;
            }

            // 2. GetData
            TransformComponent tc = entity.getComponent(TransformComponent.class);
            SpriteComponent sc = entity.getComponent(SpriteComponent.class);
            Texture texture = sc.getTexture();

            // Safety: Don't crash if texture isn't loaded yet
            if (texture == null) continue;

            // 3. Prepare Draw Variables
            float centerX = tc.getPosition().x;
            float centerY = tc.getPosition().y;
            
            float width = sc.getWidth();
            float height = sc.getHeight();
            
            // Calculate top-left corner from center position
            float x = centerX - width / 2;
            float y = centerY - height / 2;
            
            // Origin for rotation (center of sprite)
            float originX = width / 2;
            float originY = height / 2;
            
            float rotation = tc.getRotation();

            // Source dimensions (Full image size)
            int srcWidth = texture.getWidth();
            int srcHeight = texture.getHeight();

            // 4. Draw Call (The Complex One for Texture)
            // This signature supports Rotation AND Flipping
            // Transform position represents the CENTER of the sprite
            batch.draw(
                texture,
                x, y,                  // Position on screen (bottom-left corner)
                originX, originY,      // Rotation origin (center)
                width, height,         // Size to draw
                1, 1,                  // Scale (1 = 100%)
                rotation,              // Rotation in degrees
                0, 0,                  // Source X, Source Y (Start of image)
                srcWidth, srcHeight,   // Source Width, Source Height (Full image)
                sc.isFlipX(),          // Flip Horizontal?
                sc.isFlipY()           // Flip Vertical?
            );
        }

        batch.end();
    }
    
    // Allows Scene to use the batch for UI/Backgrounds
    public SpriteBatch getBatch() {
        return batch;
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
    }
}