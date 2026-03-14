package com.team6.arcadesim.Demoscene.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.components.CompositeShapeComponent;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.scenes.AbstractPlayableScene;

public class DemoSolar extends AbstractPlayableScene {

    public DemoSolar(AbstractGameMaster gameMaster) {
        super(gameMaster, "SandboxCheckpoint");
    }

    public class TestEntity extends Entity {
        public TestEntity() { super(); }
    }

    @Override
    public void onEnter() {
        System.out.println("--- ENTERING GRAVITY CHECKPOINT ---");
        
        // Setup a 1280x720 camera view
        gameMaster.getViewportManager().setVirtualResolution(1280, 720);

        // 1. Spawn the stationary Sun
        spawnSun();

        // 2. Spawn the first orbiting Planet
        spawnPlanet(640, 500, 189f, 0f, Color.CYAN);
    }

    @Override
    protected void processLevelLogic(float dt) {
        
        // Cap the delta time to prevent physics explosions if the window is dragged
        if (dt > 0.05f) dt = 0.05f;

        // Test Pause Overlay
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            System.out.println("Requesting Pause Overlay...");
            gameMaster.getSceneManager().pushScene("pause");
            return;
        }

        // Press Space to spawn a random planet!
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            float randomX = (float) (Math.random() * 400) + 200;
            float randomY = (float) (Math.random() * 200) + 400;
            // Give it a random horizontal speed so it orbits uniquely
            float randomSpeedX = (float) (Math.random() * 100) + 100; 
            spawnPlanet(randomX, randomY, randomSpeedX, 0f, Color.GREEN);
            System.out.println("Spawned a new planet!");
        }
    }

    @Override
    public void render(float dt) {
        // Our RenderManager automatically handles the CompositeShapeComponents
        gameMaster.getRenderManager().render(
            dt,
            this.getEntityManager().getAllEntities(),
            gameMaster.getViewportManager().getCamera()
        );
    }

    @Override
    public void onExit() {
        System.out.println("--- EXITING GRAVITY CHECKPOINT ---");
        this.getEntityManager().removeAll();
    }

    // --- Helper Factory Methods ---

    private void spawnSun() {
        Entity sun = new TestEntity();
        sun.addComponent(new TransformComponent(640, 360)); // Center of screen
        sun.addComponent(new MassComponent(1000f));         // Massive!
        
        // It has no MovementComponent, so the GravityManager won't move it.
        // It acts as an immovable anchor.

        // Add the Shape
        CompositeShapeComponent shapeComp = new CompositeShapeComponent();
        shapeComp.addShape(
            CompositeShapeComponent.SubShape.createCircle(0, 0, 40f, Color.YELLOW, true)
        );
        sun.addComponent(shapeComp);

        this.getEntityManager().addEntity(sun);
    }

    private void spawnPlanet(float startX, float startY, float velX, float velY, Color color) {
        Entity planet = new TestEntity();
        planet.addComponent(new TransformComponent(startX, startY));
        planet.addComponent(new MassComponent(10f)); // Small mass
        
        // Give it velocity so it falls AROUND the sun instead of INTO it
        MovementComponent movement = new MovementComponent();
        movement.setVelocity(velX, velY); 
        planet.addComponent(movement);

        // Add the Shape
        CompositeShapeComponent shapeComp = new CompositeShapeComponent();
        shapeComp.addShape(
            CompositeShapeComponent.SubShape.createCircle(0, 0, 10f, color, true)
        );
        planet.addComponent(shapeComp);

        this.getEntityManager().addEntity(planet);
    }
}