package com.team6.arcadesim.scenes;

import java.util.List;

import com.team6.arcadesim.GameMaster;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.SpriteComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;

public class DemoScene extends AbstractScene {

    private static final String SCENE_NAME = "DemoScene";
    private GameMaster gameMaster;

    public DemoScene(GameMaster gameMaster) {
        super(gameMaster, SCENE_NAME);
        this.gameMaster = gameMaster;
    }

    public class TestEntity extends Entity {
        public TestEntity() {
            super(); 
        }
    }

    @Override
    public void onEnter() {
        // Initialize scene resources, entities, etc.
        System.out.println("Entering " + SCENE_NAME);

        // // Naive way: Random Placement
        for (int i = 0; i < 100; i++) {
            Entity testObject = new TestEntity();
            
            // random positions
            float randomX = (float) Math.random() * 800;
            float randomY = (float) Math.random() * 600;
            
            testObject.addComponent(new TransformComponent(randomX, randomY));

            // random speed so they move in different directions
            float speedX = (float) (Math.random() * 200 - 100);
            float speedY = (float) (Math.random() * 200 - 100);

            testObject.addComponent(new MovementComponent(speedX, speedY));
            testObject.addComponent(new SpriteComponent("pixel_square.png", 32, 32));
            
            // The manager handles the list
            gameMaster.getEntityManager().addEntity(testObject);
        }

    }

    @Override
    public void onExit() {
        // Cleanup scene resources, entities, etc.
        System.out.println("Exiting " + SCENE_NAME);
    }

    @Override
    public void dispose() {
        // Dispose of any resources specific to this scene      
        System.out.println("Disposing " + SCENE_NAME);

        for (Entity e : gameMaster.getEntityManager().getAllEntities()) {
        SpriteComponent sc = e.getComponent(SpriteComponent.class);
        if (sc != null && sc.getTexture() != null) {
            sc.getTexture().dispose(); // Frees the GPU memory
        }
    }
    }

    @Override
    public void update(float deltaTime) {
        // Get the list of entities from the Entity Manager
        List<Entity> entities = gameMaster.getEntityManager().getAllEntities();

        // movement
        gameMaster.getMovementManager().update(deltaTime, entities);

        // collision
        gameMaster.getCollisionManager().update(deltaTime, entities);
    }

    @Override
    public void render(float deltaTime) {
        // Get the entities again for the renderer
        List<Entity> entities = gameMaster.getEntityManager().getAllEntities();
        
        // Tell the RenderManager (or IOManager) to draw them
        gameMaster.getRenderManager().render(entities, deltaTime);
    }

}
