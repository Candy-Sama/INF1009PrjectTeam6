package com.team6.arcadesim.Demoscene.Scenes;

import com.badlogic.gdx.utils.ScreenUtils;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.Controller.SimulationController;
import com.team6.arcadesim.Demoscene.CollisionListener.DestructionListener;
import com.team6.arcadesim.Demoscene.UI.SandboxUI;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.scenes.AbstractPlayableScene;
import com.team6.arcadesim.systems.CollisionSystem;
import com.team6.arcadesim.systems.GravitySystem;
import com.team6.arcadesim.systems.MovementSystem;
import com.team6.arcadesim.systems.SystemPipeline;

public class SandboxScene extends AbstractPlayableScene {

    private SandboxUI sandboxUI;
    private SimulationController simulationController;
    private DestructionListener destructionListener;

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
        simulationController = new SimulationController(this, gameMaster);

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
    public void update(float dt) {
        // Update the UI stage (animations, layout, etc.)
        sandboxUI.update(dt);

        // Call parent's update which runs systemPipeline
        // Only process physics if simulation is running
        if (isSimulating) {
            super.update(dt);
        } else {
            // Even when paused, still need to call processLevelLogic to handle inputs
            processLevelLogic(dt);
        }
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
        sandboxUI.render(gameMaster.getViewportManager().getCamera());
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
            simulationController.removeSelectedEntity();
        });

        // Reset button
        sandboxUI.setOnResetPressed(() -> {
            // Clear all entities and reset the scene
            this.getEntityManager().removeAll();
            gameMaster.getCollisionManager().reset();
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
        sandboxUI.setOnEntityTypeChanged((entityType) -> {
            simulationController.setEntityType(entityType);
        });

        // Velocity/properties changed
        sandboxUI.setOnVelocityChanged(() -> {
            // Update controller with new UI values
            simulationController.updateEntityProperties(
                sandboxUI.getSelectedMass(),
                sandboxUI.getSelectedRadius(),
                sandboxUI.getSelectedSpeedX(),
                sandboxUI.getSelectedSpeedY()
            );
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

    /**
     * Return the current selected entity ID (if any) for controller to use
     */
    public Entity getCurrentSelectedEntity() {
        // This would need to be tracked by the SimulationController
        return simulationController.getSelectedEntity();
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
