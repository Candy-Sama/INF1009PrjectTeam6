package com.team6.arcadesim.Demoscene.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.CollisionListener.DestructionListener;
import com.team6.arcadesim.Demoscene.Managers.SimulationController;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.CompositeShapeComponent;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.RadiusComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.managers.EntityManager;
import com.team6.arcadesim.scenes.AbstractPlayableScene;
import com.team6.arcadesim.systems.EngineSystem;
import com.team6.arcadesim.systems.SystemPipeline;
import com.team6.arcadesim.ui.SandboxUI;

public class SandboxScene extends AbstractPlayableScene {

    private static final float G = 50f;

    private SandboxUI sandboxUI;
    private SimulationController simulationController;
    private DestructionListener destructionListener;
    private Entity selectedEntity;

    private boolean isSimulating = false;
    private float simulationTimeScale = 1f;

    public SandboxScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "SandboxScene");
    }

    @Override
    protected void configureSystems(SystemPipeline pipeline) {
        // Gate updates by isSimulating and apply time scale for educational controls.
        pipeline.addSystem(new EngineSystem() {
            @Override
            public int getPriority() {
                return 100;
            }

            @Override
            public void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager) {
                if (!isSimulating) {
                    return;
                }
                gameMaster.getGravityManager().update(dt * simulationTimeScale, entityManager.getAllEntities());
            }
        });

        pipeline.addSystem(new EngineSystem() {
            @Override
            public int getPriority() {
                return 200;
            }

            @Override
            public void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager) {
                if (!isSimulating) {
                    return;
                }
                gameMaster.getMovementManager().update(dt * simulationTimeScale, entityManager.getAllEntities());
            }
        });

        pipeline.addSystem(new EngineSystem() {
            @Override
            public int getPriority() {
                return 300;
            }

            @Override
            public void update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager) {
                if (!isSimulating) {
                    return;
                }
                gameMaster.getCollisionManager().update(dt * simulationTimeScale, entityManager.getAllEntities());
            }
        });
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

        setSimulating(false);
        setTimeScale(1f);
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
        updateEducationalHud();
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

        sandboxUI.setOnTimeScalePressed(this::toggleTimeScale);

        sandboxUI.setOnPresetLoadRequested(this::loadPresetScenario);

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

    public void setTimeScale(float scale) {
        simulationTimeScale = Math.max(0.1f, scale);
        sandboxUI.setTimeScale(simulationTimeScale);
    }

    public void toggleTimeScale() {
        if (simulationTimeScale < 2f) {
            setTimeScale(5f);
        } else {
            setTimeScale(1f);
        }
    }

    private void loadPresetScenario(String presetName) {
        this.getEntityManager().removeAll();
        gameMaster.getCollisionManager().reset();
        selectedEntity = null;
        onEntityDeselected();

        Entity focusEntity;
        if ("Binary Stars".equalsIgnoreCase(presetName)) {
            Entity starA = createPresetEntity(520f, 360f, 0f, 45f, 1200f, 28f, Color.YELLOW);
            Entity starB = createPresetEntity(760f, 360f, 0f, -45f, 1200f, 28f, Color.YELLOW);
            createPresetEntity(900f, 360f, 0f, 145f, 18f, 10f, Color.CYAN);
            this.getEntityManager().addEntity(starA);
            this.getEntityManager().addEntity(starB);
            focusEntity = starA;
        } else {
            Entity sun = createPresetEntity(640f, 360f, 0f, 0f, 1800f, 36f, Color.YELLOW);
            Entity earth = createPresetEntity(860f, 360f, 0f, 195f, 20f, 12f, Color.CYAN);
            this.getEntityManager().addEntity(sun);
            this.getEntityManager().addEntity(earth);
            focusEntity = earth;
        }

        setSimulating(true);
        handleEntitySelected(focusEntity);
    }

    private Entity createPresetEntity(float x, float y, float vx, float vy, float mass, float radius, Color color) {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent(x, y));
        entity.addComponent(new MassComponent(mass));

        MovementComponent movement = new MovementComponent();
        movement.setVelocity(vx, vy);
        entity.addComponent(movement);

        entity.addComponent(new RadiusComponent(radius));

        CompositeShapeComponent shape = new CompositeShapeComponent();
        shape.addShape(CompositeShapeComponent.SubShape.createCircle(0, 0, radius, color, true));
        entity.addComponent(shape);

        entity.addComponent(new CollisionComponent(radius * 2f, radius * 2f, true, false));
        return entity;
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

    private void updateEducationalHud() {
        if (selectedEntity == null) {
            sandboxUI.setEducationalStats("None", 0f, 0f, -1f, "N/A");
            return;
        }

        String selectedType = "Planet";
        if (selectedEntity.hasComponent(CompositeShapeComponent.class)) {
            CompositeShapeComponent shape = selectedEntity.getComponent(CompositeShapeComponent.class);
            if (!shape.getShapes().isEmpty() && Color.YELLOW.equals(shape.getShapes().get(0).getColor())) {
                selectedType = "Star";
            }
        }

        float speed = 0f;
        if (selectedEntity.hasComponent(MovementComponent.class)) {
            speed = selectedEntity.getComponent(MovementComponent.class).getVelocity().len();
        }

        float nearestStarDistance = -1f;
        float acceleration = 0f;
        float nearestStarMass = -1f;
        if (selectedEntity.hasComponent(TransformComponent.class)) {
            Vector2 selectedPos = selectedEntity.getComponent(TransformComponent.class).getPosition();

            for (Entity entity : this.getEntityManager().getAllEntities()) {
                if (entity == selectedEntity || !entity.hasComponent(TransformComponent.class)
                    || !entity.hasComponent(MassComponent.class)
                    || !entity.hasComponent(CompositeShapeComponent.class)) {
                    continue;
                }

                CompositeShapeComponent shape = entity.getComponent(CompositeShapeComponent.class);
                if (shape.getShapes().isEmpty() || !Color.YELLOW.equals(shape.getShapes().get(0).getColor())) {
                    continue;
                }

                Vector2 starPos = entity.getComponent(TransformComponent.class).getPosition();
                float distance = selectedPos.dst(starPos);

                if (nearestStarDistance < 0f || distance < nearestStarDistance) {
                    nearestStarDistance = distance;
                    float starMass = entity.getComponent(MassComponent.class).getMass();
                    nearestStarMass = starMass;
                    if (distance > 0.001f) {
                        acceleration = (G * starMass) / (distance * distance);
                    }
                }
            }
        }

        String orbitType = classifyOrbitType(selectedType, speed, nearestStarDistance, nearestStarMass);
        sandboxUI.setEducationalStats(selectedType, speed, acceleration, nearestStarDistance, orbitType);
    }

    private String classifyOrbitType(String selectedType, float speed, float nearestStarDistance, float nearestStarMass) {
        if ("Star".equals(selectedType)) {
            return "Anchor";
        }

        if (nearestStarDistance <= 0f || nearestStarMass <= 0f) {
            return "Free Drift";
        }

        float circularSpeed = (float) Math.sqrt((G * nearestStarMass) / nearestStarDistance);
        float escapeSpeed = (float) Math.sqrt(2f) * circularSpeed;

        if (speed < circularSpeed * 0.4f) {
            return "Falling In";
        }
        if (Math.abs(speed - circularSpeed) <= circularSpeed * 0.15f) {
            return "Near Circular";
        }
        if (speed < escapeSpeed) {
            return "Elliptical";
        }
        return "Escape";
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
