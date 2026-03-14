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
        
        gameMaster.getViewportManager().setVirtualResolution((int) sceneResolution.x, (int) sceneResolution.y);

        blockTexture = createColorTexture(32, 32, 0xFF0000FF);
        floorTexture = createColorTexture((int) sceneResolution.x, 50, 0x00FF00FF);


        try {
            if (Gdx.files.internal("rubberBlockBounce.mp3").exists()) {
                AudioClip rubberBounceClip = new AudioClip(Gdx.audio.newSound(Gdx.files.internal("rubberBlockBounce.mp3")));
                gameMaster.getSoundManager().preload("rubber_bounce", rubberBounceClip);
            }
        } catch (Exception e) {
            System.err.println("Failed to load rubber bounce sound: " + e.getMessage());
        }

        createFloor();
        
        System.out.println("=== Entities after onEnter ===");
        for (Entity e : this.getEntityManager().getAllEntities()) {
            if (e.hasComponent(TransformComponent.class) && e.hasComponent(CollisionComponent.class)) {
                TransformComponent tc = e.getComponent(TransformComponent.class);
                CollisionComponent cc = e.getComponent(CollisionComponent.class);
                System.out.println(e.getClass().getSimpleName() + ": pos=(" + tc.getPosition().x + "," + tc.getPosition().y + ") size=(" + cc.getWidth() + "," + cc.getHeight() + ")");
            }
        }

        rubberCollision = new RubberCollision();
        gameMaster.getCollisionManager().setResolver(rubberCollision);

        DemoCollisionListener myListener = new DemoCollisionListener(
            (String soundId) -> {
                gameMaster.getSoundManager().playSFX(soundId);
            }
        );
        gameMaster.getCollisionManager().addCollisionListener(myListener);
    }

    @Override
    public void update(float dt) {
        if (dt > 0.05f) dt = 0.05f;
        
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Vector2 worldPos = gameMaster.getViewportManager().screenToWorld(mouseX, mouseY);
            
            createFallingBlock(worldPos.x, worldPos.y);
            System.out.println("=== Created block at mouse cursor: x=" + worldPos.x + ", y=" + worldPos.y + " ===");
        }
        
        for (Entity entity : this.getEntityManager().getAllEntities()) {
            if (entity.hasComponent(MovementComponent.class) && entity.hasComponent(TransformComponent.class)) {
                MovementComponent mc = entity.getComponent(MovementComponent.class);
                TransformComponent tc = entity.getComponent(TransformComponent.class);
                
                float newVelY = mc.getVelocity().y + (GRAVITY * dt);
                float maxFallSpeed = 300f;
                if (newVelY < -maxFallSpeed) {
                    newVelY = -maxFallSpeed;
                }
                
                mc.setVelocity(mc.getVelocity().x, newVelY);
            }
        }

        gameMaster.getMovementManager().update(dt, this.getEntityManager().getAllEntities());
        gameMaster.getCollisionManager().update(dt, this.getEntityManager().getAllEntities());
        
        for (Entity entity : this.getEntityManager().getAllEntities()) {
            if (entity instanceof FallingBlock) {
                TransformComponent tc = entity.getComponent(TransformComponent.class);
                MovementComponent mc = entity.getComponent(MovementComponent.class);
                CollisionComponent cc = entity.getComponent(CollisionComponent.class);
                
                float blockBottom = tc.getPosition().y - cc.getHeight() / 2;
                float floorTop = 50;
                
                if (blockBottom < floorTop && mc.getVelocity().y < 0) {
                    System.out.println("FLOOR BOUNCE! Block bottom=" + blockBottom + ", floor top=" + floorTop);
                    tc.setPosition(tc.getPosition().x, floorTop + cc.getHeight() / 2);
                    mc.setVelocity(mc.getVelocity().x, -mc.getVelocity().y * 0.7f);
                }
            }
        }
    }

    @Override
    public void render(float dt) {
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
        
        this.getEntityManager().removeAll();
        gameMaster.getCollisionManager().reset();
    }

    public void createFallingBlock(float x, float y) {
        Entity block = new FallingBlock();
        block.addComponent(new TransformComponent(x, y));
        block.addComponent(new SpriteComponent(blockTexture, 32, 32));
        MovementComponent movement = new MovementComponent();
        movement.setVelocity(0, 0);
        block.addComponent(movement);
        block.addComponent(new CollisionComponent(32, 32, true, false));
        this.getEntityManager().addEntity(block);
        System.out.println("Created falling block: " + block.getId() + " at (" + x + ", " + y + ")");
    }

    private void createFloor() {
        Entity floor = new FloorEntity();
        float floorY = 25;
        float floorX = sceneResolution.x / 2;
        floor.addComponent(new TransformComponent(floorX, floorY));
        floor.addComponent(new SpriteComponent(floorTexture, sceneResolution.x, 50));
        floor.addComponent(new CollisionComponent((int) sceneResolution.x, 50, true, false));
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

