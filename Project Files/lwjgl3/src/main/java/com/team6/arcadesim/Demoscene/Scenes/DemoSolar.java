package com.team6.arcadesim.Demoscene.Scenes;

import java.util.function.Consumer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.Managers.SimulationController;
import com.team6.arcadesim.components.CompositeShapeComponent;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.RadiusComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.scenes.AbstractPlayableScene;
import com.team6.arcadesim.systems.CollisionSystem;
import com.team6.arcadesim.systems.GravitySystem;
import com.team6.arcadesim.systems.MovementSystem;
import com.team6.arcadesim.systems.SystemPipeline;

public class DemoSolar extends AbstractPlayableScene {

    private SimulationController simulationController;
    private Entity selectedEntity;

    public DemoSolar(AbstractGameMaster gameMaster) {
        super(gameMaster, "SandboxCheckpoint");
    }

    public class TestEntity extends Entity {
        public TestEntity() { super(); }
    }

    private class EntitySelectionHandler implements Consumer<Entity> {
        @Override
        public void accept(Entity entity) {
            selectedEntity = entity;
            System.out.println("Selected entity: " + entity.getId());
        }
    }

    private class DefaultSpawnValuesProvider implements SimulationController.SpawnValuesProvider {
        @Override
        public float getMass() {
            return 10f;
        }

        @Override
        public float getRadius() {
            return 10f;
        }

        @Override
        public float getSpeedX() {
            return 120f;
        }

        @Override
        public float getSpeedY() {
            return 0f;
        }

        @Override
        public String getType() {
            return "planet";
        }
    }

    @Override
    protected void configureSystems(SystemPipeline pipeline) {
        pipeline.addSystem(new GravitySystem());
        pipeline.addSystem(new MovementSystem());
        pipeline.addSystem(new CollisionSystem());
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

        // 3. Setup the SimulationController to handle user input for spawning new planets and selecting entities
        simulationController = new SimulationController(
            gameMaster,
            this.getEntityManager(),
            new EntitySelectionHandler(),
            new DefaultSpawnValuesProvider()
        );

        gameMaster.getInputManager().addInputProcessor(simulationController);
    }

    @Override
    protected void processLevelLogic(float dt) {
        // Test Pause Overlay
        if (gameMaster.getInputManager().isKeyJustPressed(Input.Keys.P)) {
            System.out.println("Requesting Pause Overlay...");
            gameMaster.getSceneManager().pushScene("pause");
            return;
        }

        // Press Space to spawn a random planet!
        if (gameMaster.getInputManager().isKeyJustPressed(Input.Keys.SPACE)) {
            float randomX = (float) (Math.random() * 400) + 200;
            float randomY = (float) (Math.random() * 200) + 400;
            // Give it a random horizontal speed so it orbits uniquely
            float randomSpeedX = (float) (Math.random() * 100) + 100; 
            spawnPlanet(randomX, randomY, randomSpeedX, 0f, Color.GREEN);
            System.out.println("Spawned a new planet!");
        }

        // On leaving the scene boundary, delete the entity and print a message
        if (entityManager.getAllEntities().size() > 0) {
            for (Entity entity : entityManager.getAllEntities()) {
                TransformComponent transform = entity.getComponent(TransformComponent.class);
                if (transform != null) {
                    float x = transform.getPosition().x;
                    float y = transform.getPosition().y;
                    if (x < -100 || x > 1380 || y < -100 || y > 820) { // Allow some buffer before deletion
                        System.out.println("Entity " + entity.getId() + " left the scene and was removed.");
                        entityManager.removeEntity(entity);
                    }
                }
            }
        }
    }

    @Override
    public void render(float dt) {
        // Our RenderManager automatically handles the CompositeShapeComponents!
        gameMaster.getRenderManager().render(
            dt,
            this.getEntityManager().getAllEntities(),
            gameMaster.getViewportManager().getCamera()
        );
    }

    @Override
    public void onExit() {
        System.out.println("--- EXITING GRAVITY CHECKPOINT ---");
        if (simulationController != null) {
            gameMaster.getInputManager().removeInputProcessor(simulationController);
        }
        selectedEntity = null;
        this.getEntityManager().removeAll();
    }

    // --- Helper Factory Methods ---

    private void spawnSun() {
        Entity sun = new TestEntity();
        sun.addComponent(new TransformComponent(640, 360)); // Center of screen
        sun.addComponent(new MassComponent(1000f));         // Massive!
        sun.addComponent(new RadiusComponent(40f));
        
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
        planet.addComponent(new RadiusComponent(10f));
        
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
