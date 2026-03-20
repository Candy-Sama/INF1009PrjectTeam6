package com.team6.arcadesim.Demoscene.Scenes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.GameMaster;
import com.team6.arcadesim.Demoscene.CollisionListener.DemoCollisionListener;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.SpriteComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.AudioClip;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionResolver;
import com.team6.arcadesim.scenes.AbstractScene;

public class DemoGravity extends AbstractScene {
<<<<<<< HEAD

    private static final String SCENE_NAME = "DemoGravity";
    private static final String BLOCK_SPRITE_ID = "gravity.block";
    private static final String FLOOR_SPRITE_ID = "gravity.floor";
    
=======
>>>>>>> main
    private Texture blockTexture;
    private Texture floorTexture;
    private GameMaster gameMaster;
    private CollisionResolver RubberCollision;

    public DemoGravity(AbstractGameMaster gameMaster) {
        super(gameMaster, "DemoGravity");
    }

    public void onEnter() {
        // Create textures
        blockTexture = createColorTexture(32, 32, 0xFF0000FF);
<<<<<<< HEAD
        floorTexture = createColorTexture((int) sceneResolution.x, 50, 0x00FF00FF);
        gameMaster.getRenderManager().registerTexture(BLOCK_SPRITE_ID, blockTexture);
        gameMaster.getRenderManager().registerTexture(FLOOR_SPRITE_ID, floorTexture);
=======
        floorTexture = createColorTexture(800, 32, 0x00FF00FF);
>>>>>>> main

        // Load sound effect
        if (Gdx.files.internal("rubberBlockBounce.mp3").exists()) {
            AudioClip rubberBounceClip = new AudioClip(Gdx.audio.newSound(Gdx.files.internal("rubberBlockBounce.mp3")));
            gameMaster.getSoundManager().preload("rubber_bounce", rubberBounceClip);
        }

        createFloor();

        gameMaster.getCollisionManager().setResolver(RubberCollision);

        DemoCollisionListener myListener = new DemoCollisionListener(
            (String soundId) -> {
                gameMaster.getSoundManager().playSFX(soundId);
            }
        );

        gameMaster.getCollisionManager().addCollisionListener(myListener);
    }

    @Override
    public void update(float dt) {
<<<<<<< HEAD
        if (dt > 0.05f) dt = 0.05f;
        
        if (gameMaster.getInputManager().isKeyJustPressed(Input.Keys.SPACE)) {
            float mouseX = gameMaster.getInputManager().getMouseX();
            float mouseY = gameMaster.getInputManager().getMouseY();
            Vector2 worldPos = gameMaster.getViewportManager().screenToWorld(mouseX, mouseY);
            
=======
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float spawnX = gameMaster.getInputManager().getMouseX();
            float spawnY = gameMaster.getInputManager().getMouseY();

            Vector2 worldPos = gameMaster.getViewportManager().screenToWorld(new Vector2(spawnX, spawnY));

>>>>>>> main
            createFallingBlock(worldPos.x, worldPos.y);
        }
    }

    @Override
    public void onExit() {
<<<<<<< HEAD
        System.out.println("Exiting " + SCENE_NAME);
        
        if (blockTexture != null) blockTexture.dispose();
        if (floorTexture != null) floorTexture.dispose();
        gameMaster.getRenderManager().unregisterTexture(BLOCK_SPRITE_ID);
        gameMaster.getRenderManager().unregisterTexture(FLOOR_SPRITE_ID);
        
        this.getEntityManager().removeAll();
        gameMaster.getCollisionManager().reset();
=======
        blockTexture.dispose();
        floorTexture.dispose();
        gameMaster.getEntityManager().removeAll();
    }

    @Override
    public void render(float dt) {
        // No additional rendering needed; entities are drawn by RenderManager
>>>>>>> main
    }

    public void createFallingBlock(float x, float y) {
        Entity block = gameMaster.getEntityManager().createEntity();
        block.addComponent(new TransformComponent(x, y));
<<<<<<< HEAD
        block.addComponent(new SpriteComponent(BLOCK_SPRITE_ID, 32, 32));
=======
        block.addComponent(new SpriteComponent(blockTexture, x, y));

>>>>>>> main
        MovementComponent movement = new MovementComponent();
        movement.setAcceleration(0, -980f);
        movement.setVelocity(MathUtils.random(-100, 100), 0);
        block.addComponent(movement);
        block.addComponent(new CollisionComponent(32, 32, true, false));

        gameMaster.getEntityManager().addEntity(block);
        System.out.println("Created falling block:" + block.getId()+ " at (" + x + ", " + y + ")");
    };

    private void createFloor() {
<<<<<<< HEAD
        Entity floor = new FloorEntity();
        float floorY = 25;
        float floorX = sceneResolution.x / 2;
        floor.addComponent(new TransformComponent(floorX, floorY));
        floor.addComponent(new SpriteComponent(FLOOR_SPRITE_ID, sceneResolution.x, 50));
        floor.addComponent(new CollisionComponent((int) sceneResolution.x, 50, true, false));
        this.getEntityManager().addEntity(floor);
        System.out.println("Created floor at (" + floorX + ", " + floorY + ")");
=======
        Entity floor = gameMaster.getEntityManager().createEntity();
        // Positioned at bottom center
        floor.addComponent(new TransformComponent(0, 0)); 
        floor.addComponent(new SpriteComponent(floorTexture, 1280, 50));
        floor.addComponent(new CollisionComponent(1280, 50, true, false));
        // No MovementComponent (Static)
>>>>>>> main
    }

    private Texture createColorTexture(int width, int height, int color) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture t = new Texture(pixmap);
        pixmap.dispose();
        return t;
    }
}
