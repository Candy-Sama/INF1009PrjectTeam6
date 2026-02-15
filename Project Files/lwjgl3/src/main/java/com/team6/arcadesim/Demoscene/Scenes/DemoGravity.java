package com.team6.arcadesim.Demoscene.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.CollisionListener.DemoCollisionListener;
import com.team6.arcadesim.Demoscene.Managers.RubberCollision;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.SpriteComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.AudioClip;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionResolver;
import com.team6.arcadesim.scenes.AbstractScene;

public class DemoGravity extends AbstractScene {

    private static final String SCENE_NAME = "DemoGravity";
    
    private Texture blockTexture;
    private Texture floorTexture;
    private CollisionResolver rubberCollision;
    private Vector2 sceneResolution = new Vector2(1280, 720);
    
    // Gravity constant
    private static final float GRAVITY = -980f;

    // Concrete entity classes
    public class FallingBlock extends Entity {
        public FallingBlock() { super(); }
    }

    public class FloorEntity extends Entity {
        public FloorEntity() { super(); }
    }

    public DemoGravity(AbstractGameMaster gameMaster) {
        super(gameMaster, SCENE_NAME);
    }

    @Override
    public void onEnter() {
        System.out.println("Entering " + SCENE_NAME);
        
        // Setup camera resolution
        gameMaster.getViewportManager().setVirtualResolution((int) sceneResolution.x, (int) sceneResolution.y);

        // Create textures programmatically
        blockTexture = createColorTexture(32, 32, 0xFF0000FF);  // Red blocks
        floorTexture = createColorTexture((int) sceneResolution.x, 50, 0x00FF00FF);  // Green floor

        // Load sound effect
        try {
            if (Gdx.files.internal("rubberBlockBounce.mp3").exists()) {
                AudioClip rubberBounceClip = new AudioClip(Gdx.audio.newSound(Gdx.files.internal("rubberBlockBounce.mp3")));
                gameMaster.getSoundManager().preload("rubber_bounce", rubberBounceClip);
            }
        } catch (Exception e) {
            System.err.println("Failed to load rubber bounce sound: " + e.getMessage());
        }

        // Create floor
        createFloor();
        
        // Debug: Print all entities
        System.out.println("=== Entities after onEnter ===");
        for (Entity e : this.getEntityManager().getAllEntities()) {
            if (e.hasComponent(TransformComponent.class) && e.hasComponent(CollisionComponent.class)) {
                TransformComponent tc = e.getComponent(TransformComponent.class);
                CollisionComponent cc = e.getComponent(CollisionComponent.class);
                System.out.println(e.getClass().getSimpleName() + ": pos=(" + tc.getPosition().x + "," + tc.getPosition().y + ") size=(" + cc.getWidth() + "," + cc.getHeight() + ")");
            }
        }

        // Set up collision resolver ( instantiate RubberCollision!)
        rubberCollision = new RubberCollision();
        gameMaster.getCollisionManager().setResolver(rubberCollision);

        // Add collision listener for sound effects
        DemoCollisionListener myListener = new DemoCollisionListener(
            (String soundId) -> {
                gameMaster.getSoundManager().playSFX(soundId);
            }
        );
        gameMaster.getCollisionManager().addCollisionListener(myListener);
    }

    @Override
    public void update(float dt) {
        // Cap dt to prevent physics explosions on lag spikes
        if (dt > 0.05f) dt = 0.05f;
        
        // Spawn falling block on SPACE press - spawn at top center for testing
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // Spawn at top center, above the floor
            float spawnX = sceneResolution.x / 2; // Center X
            float spawnY = sceneResolution.y - 50; // Near top of screen (720 - 50 = 670)
            
            createFallingBlock(spawnX, spawnY);
            System.out.println("=== Created block at x=" + spawnX + ", y=" + spawnY + " ===");
        }
        
        // Debug: print all entity positions every frame
        // Only occasionally to avoid spam
        if ((int)(System.currentTimeMillis() / 1000) % 2 == 0) {
            for (Entity entity : this.getEntityManager().getAllEntities()) {
                if (entity.hasComponent(TransformComponent.class) && entity.hasComponent(MovementComponent.class)) {
                    TransformComponent tc = entity.getComponent(TransformComponent.class);
                    MovementComponent mc = entity.getComponent(MovementComponent.class);
                    System.out.println(entity.getClass().getSimpleName() + " pos=(" + tc.getPosition().x + "," + tc.getPosition().y + ") vel=(" + mc.getVelocity().x + "," + mc.getVelocity().y + ")");
                }
            }
        }
        
        // Apply gravity to all entities with MovementComponent
        // Since MovementManager doesn't apply acceleration, we do it here
        for (Entity entity : this.getEntityManager().getAllEntities()) {
            if (entity.hasComponent(MovementComponent.class) && entity.hasComponent(TransformComponent.class)) {
                MovementComponent mc = entity.getComponent(MovementComponent.class);
                TransformComponent tc = entity.getComponent(TransformComponent.class);
                
                // Apply gravity (acceleration)
                float newVelY = mc.getVelocity().y + (GRAVITY * dt);
                
                // Cap maximum fall speed to prevent tunneling through floor
                float maxFallSpeed = 300f; // pixels per second
                if (newVelY < -maxFallSpeed) {
                    newVelY = -maxFallSpeed;
                }
                
                mc.setVelocity(mc.getVelocity().x, newVelY);
            }
        }

        // Update movement using global MovementManager
        gameMaster.getMovementManager().update(dt, this.getEntityManager().getAllEntities());
        
        // Update collision using global CollisionManager
        gameMaster.getCollisionManager().update(dt, this.getEntityManager().getAllEntities());
        
        // Simple floor collision check - if block goes below floor, bounce it
        for (Entity entity : this.getEntityManager().getAllEntities()) {
            if (entity instanceof FallingBlock) {
                TransformComponent tc = entity.getComponent(TransformComponent.class);
                MovementComponent mc = entity.getComponent(MovementComponent.class);
                CollisionComponent cc = entity.getComponent(CollisionComponent.class);
                
                // Floor is at Y=25 with height 50, so floor top is at Y=50
                // Block bottom = position.y - height/2
                float blockBottom = tc.getPosition().y - cc.getHeight() / 2;
                float floorTop = 50; // floor center at 25, height 50, so top is at 50
                
                if (blockBottom < floorTop && mc.getVelocity().y < 0) {
                    // Collision detected! Bounce!
                    System.out.println("FLOOR BOUNCE! Block bottom=" + blockBottom + ", floor top=" + floorTop);
                    
                    // Move block to sit on top of floor
                    tc.setPosition(tc.getPosition().x, floorTop + cc.getHeight() / 2);
                    
                    // Reverse velocity with bounce factor
                    mc.setVelocity(mc.getVelocity().x, -mc.getVelocity().y * 0.7f);
                }
            }
        }
    }

    @Override
    public void render(float dt) {
        // Render entities using global RenderManager
        gameMaster.getRenderManager().render(
            dt,
            this.getEntityManager().getAllEntities(),
            gameMaster.getViewportManager().getCamera()
        );
    }

    @Override
    public void onExit() {
        System.out.println("Exiting " + SCENE_NAME);
        
        if (blockTexture != null) blockTexture.dispose();
        if (floorTexture != null) floorTexture.dispose();
        
        // Clear local entities
        this.getEntityManager().removeAll();
        
        // Reset collision manager
        gameMaster.getCollisionManager().reset();
    }

    public void createFallingBlock(float x, float y) {
        Entity block = new FallingBlock();
        
        // Add TransformComponent (position)
        block.addComponent(new TransformComponent(x, y));
        
        // Add SpriteComponent (width, height) - NOT x, y
        block.addComponent(new SpriteComponent(blockTexture, 32, 32));
        
        // Add MovementComponent - NO horizontal velocity, just fall straight down
        MovementComponent movement = new MovementComponent();
        movement.setVelocity(0, 0); // Fall straight down
        block.addComponent(movement);
        
        // Add CollisionComponent (width, height, isSolid, isTrigger)
        block.addComponent(new CollisionComponent(32, 32, true, false));

        // Add to LOCAL entity manager (this scene's entities)
        this.getEntityManager().addEntity(block);
        System.out.println("Created falling block: " + block.getId() + " at (" + x + ", " + y + ")");
    }

    private void createFloor() {
        Entity floor = new FloorEntity();
        
        // Position at bottom center of screen
        // In libGDX default coords, Y=0 is at the BOTTOM
        float floorY = 25; // Half of height (50/2) = 25, so floor spans Y=0 to Y=50 from bottom
        float floorX = sceneResolution.x / 2;
        
        floor.addComponent(new TransformComponent(floorX, floorY));
        
        // SpriteComponent: width, height (NOT position!)
        floor.addComponent(new SpriteComponent(floorTexture, sceneResolution.x, 50));
        
        // CollisionComponent: solid, not a trigger
        floor.addComponent(new CollisionComponent((int) sceneResolution.x, 50, true, false));
        
        // No MovementComponent (static object)
        
        // Add to LOCAL entity manager
        this.getEntityManager().addEntity(floor);
        System.out.println("Created floor at (" + floorX + ", " + floorY + ")");
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

