package com.team6.arcadesim.Demoscene.Scenes;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.Demoscene.GameMaster;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.SpriteComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.AudioClip;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionListener;
import com.team6.arcadesim.scenes.AbstractScene;

public class DemoScene extends AbstractScene {

    private static final String SCENE_NAME = "DemoScene";
    private GameMaster gameMaster;
    private com.team6.arcadesim.Demoscene.Managers.CubeCollision cubeCollision;
    private Vector2 sceneResolution = new Vector2(1280, 720);

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

        // Setup scene size
        gameMaster.getViewportManager().setVirtualResolution((int) sceneResolution.x, (int) sceneResolution.y);

        // Initialize scene resources, entities, etc.
        System.out.println("Entering " + SCENE_NAME);

        // Load boop sound effect with error handling
        try {
            com.badlogic.gdx.audio.Sound boopSound = com.badlogic.gdx.Gdx.audio.newSound(
                com.badlogic.gdx.Gdx.files.internal("boop.mp3")
            );
            AudioClip boopClip = new AudioClip(boopSound);
            gameMaster.getSoundManager().preload("boop", boopClip);
            System.out.println("Boop sound loaded successfully!");
        } catch (Exception e) {
            System.err.println("Failed to load boop sound: " + e.getMessage());
            e.printStackTrace();
        }

        com.badlogic.gdx.audio.Music demoMusic = com.badlogic.gdx.Gdx.audio.newMusic(
            com.badlogic.gdx.Gdx.files.internal("ArcadeMusic.mp3")
        );

        // Set up collision resolver (handles physics only)
        cubeCollision = new com.team6.arcadesim.Demoscene.Managers.CubeCollision(sceneResolution.x, sceneResolution.y);
        gameMaster.getCollisionManager().setResolver(cubeCollision);

        // Add collision listener for sound effects (separation of concerns)
        gameMaster.getCollisionManager().addCollisionListener(new CollisionListener() {
            @Override
            public void onCollisionStart(Entity a, Entity b) {
                // Play boop sound when collision starts
                gameMaster.getSoundManager().playSFX("boop");
            }

            @Override
            public void onCollisionEnd(Entity a, Entity b) {
                // Optional: handle collision end if needed
            }
        });
        // Play background music
        AudioClip musicClip = new AudioClip(demoMusic);
        gameMaster.getSoundManager().preload("demoMusic", musicClip);
        gameMaster.getSoundManager().playMusic("demoMusic", true);

        // Random placement of entities
        for (int i = 0; i < 10; i++) {
            Entity testObject = new TestEntity();
            
            // random positions
            float randomX = (float) Math.random() * sceneResolution.x;
            float randomY = (float) Math.random() * sceneResolution.y;
            
            testObject.addComponent(new TransformComponent(randomX, randomY));

            // random speed so they move in different directions
            float speedX = (float) (Math.random() * 200 - 100);
            float speedY = (float) (Math.random() * 200 - 100);

            testObject.addComponent(new MovementComponent(speedX, speedY));
            testObject.addComponent(new SpriteComponent("pixel_square.png", 32, 32));
            testObject.addComponent(new CollisionComponent(32,32, true, false));
                        
            
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

        // Wall bouncing handled by CubeCollision
        for (Entity entity : entities) {
            cubeCollision.checkWallBounce(entity);
        }

        // Entity-to-entity collision
        gameMaster.getCollisionManager().update(deltaTime, entities);
        

        //Check for P key to pause
        if (gameMaster.getInputManager().isKeyJustPressed(com.badlogic.gdx.Input.Keys.P)) {
           
            // Push the pause scene on top of the demo scene
            gameMaster.getSceneManager().pushScene(new PauseScene(gameMaster));
        }
    }

    @Override
    public void render(float deltaTime) {
        // Get the entities again for the renderer
        List<Entity> entities = gameMaster.getEntityManager().getAllEntities();
        
        // Tell the RenderManager to draw them with the camera
        gameMaster.getRenderManager().render(deltaTime, entities, gameMaster.getViewportManager().getCamera());
    }

}
