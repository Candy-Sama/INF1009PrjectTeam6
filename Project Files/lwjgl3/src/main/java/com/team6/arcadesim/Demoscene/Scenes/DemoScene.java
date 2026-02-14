package com.team6.arcadesim.Demoscene.Scenes;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.AbstractGameMaster;
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
    private com.team6.arcadesim.Demoscene.Managers.CubeCollision cubeCollision;
    private Vector2 sceneResolution = new Vector2(1280, 720);
    private Texture coinTex;

    public DemoScene(AbstractGameMaster gameMaster) {
        super(gameMaster, SCENE_NAME);
    }

    public class TestEntity extends Entity {
        public TestEntity() { super(); }
    }

    @Override
    public void onEnter() {
        System.out.println("Entering " + SCENE_NAME);
        
        // 1. Setup Camera
        gameMaster.getViewportManager().setVirtualResolution((int) sceneResolution.x, (int) sceneResolution.y);

        // 2. Load Resources
        try {
            Sound boopSound = Gdx.audio.newSound(Gdx.files.internal("boop.mp3"));
            gameMaster.getSoundManager().preload("boop", new AudioClip(boopSound));
        } catch (Exception e) {
            System.err.println("Failed to load boop sound: " + e.getMessage());
        }

        Music demoMusic = Gdx.audio.newMusic(Gdx.files.internal("ArcadeMusic.mp3"));
        gameMaster.getSoundManager().preload("demoMusic", new AudioClip(demoMusic));
        gameMaster.getSoundManager().playMusic("demoMusic", true);

        // 3. Setup Physics Tools (Collision)
        cubeCollision = new com.team6.arcadesim.Demoscene.Managers.CubeCollision(sceneResolution.x, sceneResolution.y);
        // We configure the GLOBAL collision manager to use OUR logic
        gameMaster.getCollisionManager().setResolver(cubeCollision);

        gameMaster.getCollisionManager().addCollisionListener(new CollisionListener() {
            @Override
            public void onCollisionStart(Entity a, Entity b) {
                gameMaster.getSoundManager().playSFX("boop");
            }
            @Override
            public void onCollisionEnd(Entity a, Entity b) { }
        });

        coinTex = new Texture(Gdx.files.internal("coin.png"));

        // 4. Create Entities (Using LOCAL EntityManager)
        for (int i = 0; i < 10; i++) {
            Entity testObject = new TestEntity();
            
            float randomX = (float) Math.random() * sceneResolution.x;
            float randomY = (float) Math.random() * sceneResolution.y;
            testObject.addComponent(new TransformComponent(randomX, randomY));

            float speedX = (float) (Math.random() * 200 - 100);
            float speedY = (float) (Math.random() * 200 - 100);
            testObject.addComponent(new MovementComponent(speedX, speedY));
            
            testObject.addComponent(new SpriteComponent(coinTex, 32, 32));
            testObject.addComponent(new CollisionComponent(32, 32, true, false));
                        
            // CRITICAL: Add to THIS scene's manager, not the global one
            this.getEntityManager().addEntity(testObject);
        }
    }

    @Override
    public void update(float deltaTime) {
        // 1. Input Check
        if (gameMaster.getInputManager().isKeyJustPressed(Input.Keys.P)) {
            gameMaster.getSceneManager().pushScene(new PauseScene(gameMaster));
            return; // Stop processing this frame so we don't move entities while switching
        }

        // 2. Physics Update
        // We pass OUR local entities to the global tools
        List<Entity> myEntities = this.getEntityManager().getAllEntities();

        gameMaster.getMovementManager().update(deltaTime, myEntities);
        gameMaster.getCollisionManager().update(deltaTime, myEntities);

        // 3. Custom Logic (Wall Bounce)
        for (Entity entity : myEntities) {
            cubeCollision.checkWallBounce(entity);
        }
    }

    @Override
    public void render(float deltaTime) {
        // 4. Render Update
        List<Entity> myEntities = this.getEntityManager().getAllEntities();
        
        gameMaster.getRenderManager().render(
            deltaTime, 
            myEntities, 
            gameMaster.getViewportManager().getCamera()
        );
    }

    @Override
    public void onExit() {
        System.out.println("Exiting " + SCENE_NAME);
        
        // Clear LOCAL entities
        this.getEntityManager().removeAll();
        
        // Reset GLOBAL tools so they don't hold references to our dead entities
        // (Assuming you added reset() to CollisionManager as discussed)
        gameMaster.getCollisionManager().reset(); 

        if (coinTex != null) coinTex.dispose();
    }
}