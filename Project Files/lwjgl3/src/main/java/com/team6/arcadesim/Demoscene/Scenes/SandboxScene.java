package com.team6.arcadesim.Demoscene.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.CollisionListener.DestructionListener;
import com.team6.arcadesim.Demoscene.Managers.SimulationController;
import com.team6.arcadesim.components.CompositeShapeComponent;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.RadiusComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.scenes.AbstractPlayableScene;
import com.team6.arcadesim.systems.CollisionSystem;
import com.team6.arcadesim.systems.GravitySystem;
import com.team6.arcadesim.systems.MovementSystem;
import com.team6.arcadesim.systems.SystemPipeline;
import com.team6.arcadesim.ui.SandboxUI;

public class SandboxScene extends AbstractPlayableScene {

    private SandboxUI sandboxUI;
    private SimulationController simulationController;
    private DestructionListener destructionListener;
    private Entity selectedEntity;

    private boolean isSimulating = false;

    public SandboxScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "SandboxScene");
    }

    @Override
    protected void configureSystems(SystemPipeline pipeline) {
        // Add physics systems in order
        pipeline.addSystem(new GravitySystem());
        pipeline.addSystem(new MovementSystem());
        pipeline.addSystem(new CollisionSystem());
    }

    @Override
    public void onEnter() {
        System.out.println("--- ENTERING SANDBOX SCENE ---");

        // Setup viewport resolution
        gameMaster.getViewportManager().setVirtualResolution(1280, 720);

        // Initialize UI
        sandboxUI = new SandboxUI();
        sandboxUI.resize(1280, 720);

        // Initialize Controller
        simulationController = new SimulationController(
            gameMaster,
            this.getEntityManager(),
            this::handleEntitySelected,
            new SimulationController.SpawnValuesProvider() {
                @Override
                public float getMass() {
                    return sandboxUI.getMass();
                }

                @Override
                public float getRadius() {
                    return sandboxUI.getRadius();
                }

                @Override
                public float getSpeedX() {
                    return sandboxUI.getSpeedX();
                }

                @Override
                public float getSpeedY() {
                    return sandboxUI.getSpeedY();
                }

                @Override
                public String getType() {
                    return sandboxUI.getEntityType();
                }
            }
        );

        // Initialize Destruction Listener for collision audio
        destructionListener = new DestructionListener(
            soundId -> gameMaster.getSoundManager().playSFX(soundId)
        );

        // Register input in order: UI first, then Controller
        // This prevents click bleeding - UI stage captures mouse events first
        gameMaster.getInputManager().addInputProcessor(sandboxUI.getStage());
        gameMaster.getInputManager().addInputProcessor(simulationController);

        // Register collision listener
        gameMaster.getCollisionManager().addCollisionListener(destructionListener);

        // Wire UI callbacks to scene/controller logic
        wireUICallbacks();
    }

    @Override
    protected void processLevelLogic(float dt) {
        // Let the simulation controller handle input and entity creation
        // (simulationController is registered as an InputProcessor)

        // If UI says to pause/play, toggle isSimulating
        // (This is handled through UI callbacks)
    }

    @Override
    public void render(float dt) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Render all entities (handled by RenderManager for CompositeShapeComponents)
        gameMaster.getRenderManager().render(
            dt,
            this.getEntityManager().getAllEntities(),
            gameMaster.getViewportManager().getCamera()
        );

        // Render UI on top
        sandboxUI.update(dt);
    }

    @Override
    public void onExit() {
        System.out.println("--- EXITING SANDBOX SCENE ---");

        // Unregister input
        gameMaster.getInputManager().removeInputProcessor(sandboxUI.getStage());
        gameMaster.getInputManager().removeInputProcessor(simulationController);

        // Unregister collision listener
        gameMaster.getCollisionManager().removeCollisionListener(destructionListener);

        // Cleanup
        this.getEntityManager().removeAll();
        gameMaster.getCollisionManager().reset();
        selectedEntity = null;
        if (sandboxUI != null) {
            sandboxUI.dispose();
        }
    }

    /**
     * Wires all SandboxUI callbacks to scene/controller logic
     */
    private void wireUICallbacks() {
        // Remove button
        sandboxUI.setOnRemovePressed(() -> {
            if (selectedEntity != null) {
                this.getEntityManager().removeEntity(selectedEntity);
                selectedEntity = null;
                onEntityDeselected();
            }
        });

        // Reset button
        sandboxUI.setOnResetPressed(() -> {
            // Clear all entities and reset the scene
            this.getEntityManager().removeAll();
            gameMaster.getCollisionManager().reset();
            selectedEntity = null;
            onEntityDeselected();
            setSimulating(false);
            sandboxUI.setSimulationRunning(false);
        });

        // Start/Pause toggle button
        sandboxUI.setOnStartPausePressed(() -> {
            toggleSimulation();
        });

        // Return button
        sandboxUI.setOnReturnPressed(() -> {
            // Go back to main menu or previous scene
            gameMaster.getSceneManager().popScene();
        });

        // Entity type changed (Star/Planet selector)
        sandboxUI.setOnEntityTypeChanged(entityType -> { });

        // Velocity/properties changed
        sandboxUI.setOnVelocityChanged((speedX, speedY) -> {
            if (selectedEntity != null && selectedEntity.hasComponent(MovementComponent.class)) {
                selectedEntity.getComponent(MovementComponent.class).setVelocity(speedX, speedY);
            }
        });
    }

    /**
     * Toggle the simulation running state
     */
    public void toggleSimulation() {
        setSimulating(!isSimulating);
    }

    /**
     * Set simulation running state and update UI
     */
    public void setSimulating(boolean running) {
        isSimulating = running;
        sandboxUI.setSimulationRunning(running);
    }

    /**
     * Called when a new entity is selected/created
     * Updates UI to show the entity's properties
     */
    public void onEntitySelected(String entityType, float mass, float radius, float speedX, float speedY) {
        sandboxUI.populateSelectedEntity(entityType, mass, radius, speedX, speedY);
    }

    /**
     * Called when an entity is deselected
     */
    public void onEntityDeselected() {
        sandboxUI.populateSelectedEntity("", 0, 0, 0, 0);
    }

    private void handleEntitySelected(Entity entity) {
        selectedEntity = entity;

        String entityType = "Planet";
        if (entity.hasComponent(CompositeShapeComponent.class)) {
            CompositeShapeComponent shape = entity.getComponent(CompositeShapeComponent.class);
            if (!shape.getShapes().isEmpty() && Color.YELLOW.equals(shape.getShapes().get(0).getColor())) {
                entityType = "Star";
            }
        }

        float mass = entity.hasComponent(MassComponent.class)
            ? entity.getComponent(MassComponent.class).getMass()
            : 0f;
        float radius = entity.hasComponent(RadiusComponent.class)
            ? entity.getComponent(RadiusComponent.class).getRadius()
            : 0f;
        float speedX = 0f;
        float speedY = 0f;
        if (entity.hasComponent(MovementComponent.class)) {
            MovementComponent movement = entity.getComponent(MovementComponent.class);
            speedX = movement.getVelocity().x;
            speedY = movement.getVelocity().y;
        }

        onEntitySelected(entityType, mass, radius, speedX, speedY);
    }

    /**
     * Return the current selected entity ID (if any) for controller to use
     */
    public Entity getCurrentSelectedEntity() {
        return selectedEntity;
    }

    public boolean isSimulating() {
        return isSimulating;
    }

    public SandboxUI getSandboxUI() {
        return sandboxUI;
    }

    public SimulationController getSimulationController() {
        return simulationController;
    }
}
