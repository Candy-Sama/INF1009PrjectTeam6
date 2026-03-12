package com.team6.arcadesim.managers;

import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.team6.arcadesim.components.CompositeShapeComponent;
import com.team6.arcadesim.components.SpriteComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;


public class RenderManager implements Disposable {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;  // For drawing shapes

    public RenderManager() {
        this.batch = new SpriteBatch();
        this.shapeRenderer = new ShapeRenderer();  
    }

    public void render(float dt, List<Entity> entities, OrthographicCamera camera) {
        // Ensure camera is set right before rendering
        if (camera != null) {
            batch.setProjectionMatrix(camera.combined);
            shapeRenderer.setProjectionMatrix(camera.combined); 
        }
        
        batch.begin();

        for (Entity entity : entities) {
            // 1. Check: Entity must have Position (Transform) AND Looks (Sprite)
            if (!entity.hasComponent(TransformComponent.class) || 
                !entity.hasComponent(SpriteComponent.class)) {
                continue;
            }

            TransformComponent tc = entity.getComponent(TransformComponent.class);
            SpriteComponent sc = entity.getComponent(SpriteComponent.class);
            Texture texture = sc.getTexture();

            // Safety: Don't crash if texture isn't loaded yet
            if (texture == null) continue;

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

        batch.end();  // MUST end batch before starting shapes!
        
        // TWO passes: filled shapes first, then outlined shapes
        
        // PASS 1: Draw all FILLED shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        
        for (Entity entity : entities) {
            // Skip if entity doesn't have Transform or CompositeShape
            if (!entity.hasComponent(TransformComponent.class) || 
                !entity.hasComponent(CompositeShapeComponent.class)) {
                continue;
            }
            
            TransformComponent tc = entity.getComponent(TransformComponent.class);
            CompositeShapeComponent csc = entity.getComponent(CompositeShapeComponent.class);
            
            float entityX = tc.getPosition().x;
            float entityY = tc.getPosition().y;
            
            // Loop through each shape in the component
            for (CompositeShapeComponent.SubShape shape : csc.getShapes()) {
                if (!shape.isFilled()) continue;  // Skip outline shapes in this pass
                
                shapeRenderer.setColor(shape.getColor());
                renderShape(shape, entityX, entityY);
            }
        }
        
        shapeRenderer.end();
        
        // PASS 2: Draw all OUTLINE shapes
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        
        for (Entity entity : entities) {
            if (!entity.hasComponent(TransformComponent.class) || 
                !entity.hasComponent(CompositeShapeComponent.class)) {
                continue;
            }
            
            TransformComponent tc = entity.getComponent(TransformComponent.class);
            CompositeShapeComponent csc = entity.getComponent(CompositeShapeComponent.class);
            
            float entityX = tc.getPosition().x;
            float entityY = tc.getPosition().y;
            
            for (CompositeShapeComponent.SubShape shape : csc.getShapes()) {
                if (shape.isFilled()) continue;  // Skip filled shapes in this pass
                
                shapeRenderer.setColor(shape.getColor());
                renderShape(shape, entityX, entityY);
            }
        }
        
        shapeRenderer.end();
    }
    

    private void renderShape(CompositeShapeComponent.SubShape shape, float entityX, float entityY) {
        // Calculate absolute position: entity position + shape's offset
        float absoluteX = entityX + shape.getOffsetX();
        float absoluteY = entityY + shape.getOffsetY();
        
        float[] dim = shape.getDimensions();
        
        // Use switch to handle each shape type differently
        switch (shape.getType()) {
            case RECTANGLE:
                // dim[0] = width, dim[1] = height
                // Draw rectangle centered at absoluteX, absoluteY
                shapeRenderer.rect(
                    absoluteX - dim[0] / 2,  // Left edge
                    absoluteY - dim[1] / 2,  // Bottom edge
                    dim[0],                   // Width
                    dim[1]                    // Height
                );
                break;
                
            case CIRCLE:
                // dim[0] = radius
                shapeRenderer.circle(absoluteX, absoluteY, dim[0]);
                break;
                
            case LINE:
                // dim[0,1] = start point, dim[2,3] = end point (all relative)
                shapeRenderer.line(
                    entityX + dim[0],  // Start X (absolute)
                    entityY + dim[1],  // Start Y (absolute)
                    entityX + dim[2],  // End X (absolute)
                    entityY + dim[3]   // End Y (absolute)
                );
                break;
                
            case TRIANGLE:
                // dim[0,1] = point1, dim[2,3] = point2, dim[4,5] = point3
                shapeRenderer.triangle(
                    entityX + dim[0], entityY + dim[1],  // Point 1
                    entityX + dim[2], entityY + dim[3],  // Point 2
                    entityX + dim[4], entityY + dim[5]   // Point 3
                );
                break;
                
            case POLYGON:
                // dim = array of vertices [x1,y1, x2,y2, x3,y3, ...]
                // Need to convert to absolute coordinates
                float[] absoluteVertices = new float[dim.length];
                for (int i = 0; i < dim.length; i += 2) {
                    absoluteVertices[i] = entityX + dim[i];      // X coordinate
                    absoluteVertices[i + 1] = entityY + dim[i + 1];  // Y coordinate
                }
                shapeRenderer.polygon(absoluteVertices);
                break;
        }
    }
    
    // Allows Scene to use the batch for UI/Backgrounds
    public SpriteBatch getBatch() {
        return batch;
    }
    
    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();  // NEW: Clean up ShapeRenderer too!
        }
    }
}