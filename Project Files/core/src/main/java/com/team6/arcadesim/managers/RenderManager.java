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
        this.batch = new SpriteBatch();
    }

    public void render(float dt, List<Entity> entities, OrthographicCamera camera) {
        if (camera != null) {
            batch.setProjectionMatrix(camera.combined);
        }
        
        batch.begin();

        for (Entity entity : entities) {
            if (!entity.hasComponent(TransformComponent.class) || 
                !entity.hasComponent(SpriteComponent.class)) {
                continue;
            }

            TransformComponent tc = entity.getComponent(TransformComponent.class);
            SpriteComponent sc = entity.getComponent(SpriteComponent.class);
            Texture texture = sc.getTexture();

            if (texture == null) continue;

            float centerX = tc.getPosition().x;
            float centerY = tc.getPosition().y;
            
            float width = sc.getWidth();
            float height = sc.getHeight();
            float x = centerX - width / 2;
            float y = centerY - height / 2;
            float originX = width / 2;
            float originY = height / 2;
            
            float rotation = tc.getRotation();

            int srcWidth = texture.getWidth();
            int srcHeight = texture.getHeight();

            batch.draw(
                texture,
                x, y,
                originX, originY,
                width, height,
                1, 1,
                rotation,
                0, 0,
                srcWidth, srcHeight,
                sc.isFlipX(),
                sc.isFlipY()
            );
        }

        batch.end();
    }
    
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