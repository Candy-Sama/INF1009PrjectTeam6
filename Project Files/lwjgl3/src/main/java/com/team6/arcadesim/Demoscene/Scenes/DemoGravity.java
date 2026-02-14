//package com.team6.arcadesim.Demoscene.Scenes;
//import com.badlogic.gdx.Game;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.math.MathUtils;
//import com.badlogic.gdx.math.Vector2;
//import com.team6.arcadesim.components.CollisionComponent;
//import com.team6.arcadesim.components.MovementComponent;
//import com.team6.arcadesim.components.SpriteComponent;
//import com.team6.arcadesim.components.TransformComponent;
//import com.team6.arcadesim.ecs.Entity;
//import com.team6.arcadesim.interfaces.CollisionResolver;
//import com.team6.arcadesim.interfaces.CollisionListener;
//import com.team6.arcadesim.scenes.AbstractScene;
//import com.team6.arcadesim.Demoscene.GameMaster;
//import com.team6.arcadesim.Demoscene.CollisionListener.DemoCollisionListener;
//import com.team6.arcadesim.Demoscene.Managers.RubberCollision;
//import com.team6.arcadesim.AbstractGameMaster;
//import com.team6.arcadesim.ecs.AudioClip;
//
//public class DemoGravity extends AbstractScene {
//    private Texture blockTexture;
//    private Texture floorTexture;
//    private GameMaster gameMaster;
//    private CollisionResolver RubberCollision;
//
//    public DemoGravity(AbstractGameMaster gameMaster) {
//        super(gameMaster, "DemoGravity");
//    }
//
//    public void onEnter() {
//        // Create textures
//        blockTexture = createColorTexture(32, 32, 0xFF0000FF);
//        floorTexture = createColorTexture(800, 32, 0x00FF00FF);
//
//        // Load sound effect
//        if (Gdx.files.internal("rubberBlockBounce.mp3").exists()) {
//            AudioClip rubberBounceClip = new AudioClip(Gdx.audio.newSound(Gdx.files.internal("rubberBlockBounce.mp3")));
//            gameMaster.getSoundManager().preload("rubber_bounce", rubberBounceClip);
//        }
//
//        createFloor();
//
//        gameMaster.getCollisionManager().setResolver(RubberCollision);
//
//        DemoCollisionListener myListener = new DemoCollisionListener(
//            (String soundId) -> {
//                gameMaster.getSoundManager().playSFX(soundId);
//            }
//        );
//
//        gameMaster.getCollisionManager().addCollisionListener(myListener);
//    }
//
//    @Override
//    public void update(float dt) {
//        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//            float spawnX = gameMaster.getInputManager().getMouseX();
//            float spawnY = gameMaster.getInputManager().getMouseY();
//
//            Vector2 worldPos = gameMaster.getViewportManager().screenToWorld(new Vector2(spawnX, spawnY));
//
//            createFallingBlock(worldPos.x, worldPos.y);
//        }
//    }
//
//    @Override
//    public void onExit() {
//        blockTexture.dispose();
//        floorTexture.dispose();
//        gameMaster.getEntityManager().removeAll();
//    }
//
//    @Override
//    public void render(float dt) {
//        // No additional rendering needed; entities are drawn by RenderManager
//    }
//
//    public void createFallingBlock(float x, float y) {
//        Entity block = gameMaster.getEntityManager().createEntity();
//        block.addComponent(new TransformComponent(x, y));
//        block.addComponent(new SpriteComponent(blockTexture, x, y));
//
//        MovementComponent movement = new MovementComponent();
//        movement.setAcceleration(0, -980f);
//        movement.setVelocity(MathUtils.random(-100, 100), 0);
//        block.addComponent(movement);
//        block.addComponent(new CollisionComponent(32, 32, true, false));
//
//        gameMaster.getEntityManager().addEntity(block);
//        System.out.println("Created falling block:" + block.getId()+ " at (" + x + ", " + y + ")");
//    };
//
//    private void createFloor() {
//        Entity floor = gameMaster.getEntityManager().createEntity();
//        // Positioned at bottom center
//        floor.addComponent(new TransformComponent(0, 0));
//        floor.addComponent(new SpriteComponent(floorTexture, 1280, 50));
//        floor.addComponent(new CollisionComponent(1280, 50, true, false));
//        // No MovementComponent (Static)
//    }
//
//    private Texture createColorTexture(int width, int height, int color) {
//        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
//        pixmap.setColor(color);
//        pixmap.fill();
//        Texture t = new Texture(pixmap);
//        pixmap.dispose();
//        return t;
//    }
//}
