package com.team6.arcadesim.sandbox.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.CompositeShapeComponent;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.RadiusComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.scenes.AbstractPlayableScene;
import com.team6.arcadesim.sandbox.BodyType;
import com.team6.arcadesim.sandbox.audio.SandboxAudioService;
import com.team6.arcadesim.sandbox.config.SandboxConfig;
import com.team6.arcadesim.sandbox.controllers.SimulationController;
import com.team6.arcadesim.sandbox.events.SandboxAudioEvent;
import com.team6.arcadesim.sandbox.factory.CelestialEntityFactory;
import com.team6.arcadesim.sandbox.render.TrajectoryRenderer;
import com.team6.arcadesim.sandbox.simulation.MutualDestructionResolver;
import com.team6.arcadesim.sandbox.simulation.SandboxTrajectoryService;
import com.team6.arcadesim.sandbox.ui.SandboxControlPanel;
import com.team6.arcadesim.sandbox.ui.SandboxSkinFactory;
import com.team6.arcadesim.systems.CollisionSystem;
import com.team6.arcadesim.systems.GravitySystem;
import com.team6.arcadesim.systems.MovementSystem;
import com.team6.arcadesim.systems.SystemPipeline;

public class SandboxScene extends AbstractPlayableScene {

    private static final int VIRTUAL_WIDTH = 1280;
    private static final int VIRTUAL_HEIGHT = 720;

    private Stage uiStage;
    private Skin uiSkin;
    private SandboxControlPanel controlPanel;
    private final CelestialEntityFactory celestialEntityFactory;
    private final MutualDestructionResolver mutualDestructionResolver;
    private final SandboxTrajectoryService trajectoryService;
    private final TrajectoryRenderer trajectoryRenderer;
    private SandboxAudioService audioService;
    private SimulationController simulationController;
    private SandboxMode currentMode;
    private boolean toggleSimulationRequested;
    private boolean clearBoardRequested;
    private Entity selectedEntity;
    private boolean syncingUiFromSelection;

    public SandboxScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "SandboxScene", false);
        this.celestialEntityFactory = new CelestialEntityFactory();
        this.mutualDestructionResolver = new MutualDestructionResolver(gameMaster.getEventBus());
        this.trajectoryService = new SandboxTrajectoryService();
        this.trajectoryRenderer = new TrajectoryRenderer();
        this.audioService = null;
        this.currentMode = SandboxMode.BLUEPRINT;
        this.toggleSimulationRequested = false;
        this.clearBoardRequested = false;
        this.selectedEntity = null;
        this.syncingUiFromSelection = false;
    }

    @Override
    protected void configureSystems(SystemPipeline pipeline) {
        pipeline.addSystem(new GravitySystem());
        pipeline.addSystem(new MovementSystem());
        pipeline.addSystem(new CollisionSystem());
    }

    @Override
    public void onEnter() {
        gameMaster.getViewportManager().setVirtualResolution(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = SandboxSkinFactory.createSkin();
        controlPanel = new SandboxControlPanel(uiSkin);
        uiStage.addActor(controlPanel.getRootTable());
        wireControlPanelEvents();
        bindLiveEditListeners();
        setMode(SandboxMode.BLUEPRINT);

        audioService = new SandboxAudioService(gameMaster);
        if (gameMaster.getEventBus() != null) {
            gameMaster.getEventBus().subscribe(SandboxAudioEvent.class, audioService);
        }
        audioService.playSandboxBgm();

        gameMaster.getCollisionManager().setResolver(mutualDestructionResolver);
        registerSceneInputProcessorFirst(uiStage);
        simulationController = new SimulationController(
            gameMaster,
            getEntityManager(),
            celestialEntityFactory,
            entity -> {
                selectedEntity = entity;
                bindUiToSelectedEntity();
                trajectoryService.markDirty();
            },
            () -> selectedEntity = null,
            this::isPointerOverUi,
            this::getSpawnRequestFromUi
        );
        registerSceneInputProcessor(simulationController);
    }

    @Override
    public void onExit() {
        getEntityManager().removeAll();
        trajectoryService.clear();
        selectedEntity = null;
        if (audioService != null) {
            audioService.stopSandboxBgm();
            if (gameMaster.getEventBus() != null) {
                gameMaster.getEventBus().unsubscribe(SandboxAudioEvent.class, audioService);
            }
        }
        gameMaster.getCollisionManager().setResolver(null);
        unregisterSceneInputProcessor(simulationController);
        unregisterSceneInputProcessor(uiStage);
    }

    @Override
    protected void processLevelLogic(float dt) {
        if (gameMaster.getInputManager().isKeyJustPressed(Input.Keys.P)) {
            gameMaster.getSceneManager().pushScene("sandbox_pause");
            return;
        }

        if (uiStage != null) {
            uiStage.act(dt);
        }

        if (toggleSimulationRequested) {
            toggleSimulationRequested = false;
            toggleSimulationMode();
        }

        if (clearBoardRequested) {
            clearBoardRequested = false;
            clearBoard();
        }

        pruneInactiveEntities();

        if (currentMode == SandboxMode.BLUEPRINT) {
            trajectoryService.updatePredictions(
                getEntityManager().getAllEntities(),
                gameMaster.getEngineTimingConfig(),
                gameMaster.getGravityManager().getGravityConfig()
            );
        }
    }

    @Override
    protected boolean shouldRunSystems() {
        return currentMode == SandboxMode.SIMULATION;
    }

    @Override
    public void render(float dt) {
        gameMaster.getRenderManager().render(
            dt,
            getEntityManager().getAllEntities(),
            gameMaster.getViewportManager().getCamera()
        );

        if (currentMode == SandboxMode.BLUEPRINT) {
            trajectoryRenderer.render(
                trajectoryService.getPredictedPaths(),
                gameMaster.getViewportManager().getCamera()
            );
        }

        if (uiStage == null) {
            return;
        }

        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        uiStage.getViewport().apply();
        uiStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        trajectoryRenderer.dispose();
        if (audioService != null) {
            audioService.dispose();
            audioService = null;
        }
        if (uiStage != null) {
            uiStage.dispose();
            uiStage = null;
        }
        if (uiSkin != null) {
            uiSkin.dispose();
            uiSkin = null;
        }
        controlPanel = null;
        simulationController = null;
        selectedEntity = null;
    }

    private void wireControlPanelEvents() {
        controlPanel.getStartSimulationButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                toggleSimulationRequested = true;
            }
        });
        controlPanel.getClearBoardButton().addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                clearBoardRequested = true;
            }
        });
    }

    private void bindLiveEditListeners() {
        bindField(controlPanel.getMassField(), value -> {
            if (selectedEntity == null || !selectedEntity.hasComponent(MassComponent.class)) {
                return;
            }
            selectedEntity.getComponent(MassComponent.class).setMass(SandboxConfig.clampMass(value));
            trajectoryService.markDirty();
        });

        bindField(controlPanel.getRadiusField(), value -> {
            if (selectedEntity == null || !selectedEntity.hasComponent(RadiusComponent.class)) {
                return;
            }
            float clampedRadius = SandboxConfig.clampRadius(value);
            selectedEntity.getComponent(RadiusComponent.class).setRadius(clampedRadius);

            if (selectedEntity.hasComponent(CollisionComponent.class)) {
                selectedEntity.addComponent(new CollisionComponent(clampedRadius * 2f, clampedRadius * 2f, true, false));
            }

            if (selectedEntity.hasComponent(CompositeShapeComponent.class)) {
                CompositeShapeComponent shape = selectedEntity.getComponent(CompositeShapeComponent.class);
                for (CompositeShapeComponent.SubShape subShape : shape.getShapes()) {
                    if (subShape.getType() == CompositeShapeComponent.SubShape.ShapeType.CIRCLE) {
                        subShape.getDimensions()[0] = clampedRadius;
                    }
                }
            }

            trajectoryService.markDirty();
        });

        bindField(controlPanel.getVelocityXField(), value -> {
            if (selectedEntity == null || !selectedEntity.hasComponent(MovementComponent.class)) {
                return;
            }
            MovementComponent movement = selectedEntity.getComponent(MovementComponent.class);
            movement.setVelocity(SandboxConfig.clampVelocity(value), movement.getVelocity().y);
            trajectoryService.markDirty();
        });

        bindField(controlPanel.getVelocityYField(), value -> {
            if (selectedEntity == null || !selectedEntity.hasComponent(MovementComponent.class)) {
                return;
            }
            MovementComponent movement = selectedEntity.getComponent(MovementComponent.class);
            movement.setVelocity(movement.getVelocity().x, SandboxConfig.clampVelocity(value));
            trajectoryService.markDirty();
        });

        bindField(controlPanel.getPositionXField(), value -> {
            if (selectedEntity == null || !selectedEntity.hasComponent(TransformComponent.class)) {
                return;
            }
            TransformComponent transform = selectedEntity.getComponent(TransformComponent.class);
            transform.setPosition(value, transform.getPosition().y);
            trajectoryService.markDirty();
        });

        bindField(controlPanel.getPositionYField(), value -> {
            if (selectedEntity == null || !selectedEntity.hasComponent(TransformComponent.class)) {
                return;
            }
            TransformComponent transform = selectedEntity.getComponent(TransformComponent.class);
            transform.setPosition(transform.getPosition().x, value);
            trajectoryService.markDirty();
        });
    }

    private void bindField(TextField field, FloatConsumer onValidValueChanged) {
        field.setTextFieldListener((textField, c) -> {
            if (syncingUiFromSelection || selectedEntity == null) {
                return;
            }
            Float parsed = tryParseFloat(textField.getText());
            if (parsed == null) {
                return;
            }
            onValidValueChanged.accept(parsed);
        });
    }

    private void toggleSimulationMode() {
        if (currentMode == SandboxMode.BLUEPRINT) {
            setMode(SandboxMode.SIMULATION);
        } else {
            setMode(SandboxMode.BLUEPRINT);
        }
    }

    private void setMode(SandboxMode mode) {
        currentMode = mode;
        boolean running = (mode == SandboxMode.SIMULATION);
        setSimulationRunning(running);
        if (mode == SandboxMode.BLUEPRINT) {
            trajectoryService.markDirty();
        }
        if (controlPanel != null) {
            controlPanel.setSimulationRunning(running);
        }
    }

    private void bindUiToSelectedEntity() {
        if (controlPanel == null || selectedEntity == null || !selectedEntity.isActive()) {
            return;
        }
        if (!selectedEntity.hasComponent(TransformComponent.class)
            || !selectedEntity.hasComponent(MassComponent.class)
            || !selectedEntity.hasComponent(RadiusComponent.class)) {
            return;
        }

        TransformComponent transform = selectedEntity.getComponent(TransformComponent.class);
        MassComponent mass = selectedEntity.getComponent(MassComponent.class);
        RadiusComponent radius = selectedEntity.getComponent(RadiusComponent.class);
        MovementComponent movement = selectedEntity.getComponent(MovementComponent.class);

        syncingUiFromSelection = true;
        try {
            controlPanel.setSelectedBodyType(movement == null ? BodyType.STAR : BodyType.PLANET);
            controlPanel.getMassField().setText(formatFloat(mass.getMass()));
            controlPanel.getRadiusField().setText(formatFloat(radius.getRadius()));
            controlPanel.getPositionXField().setText(formatFloat(transform.getPosition().x));
            controlPanel.getPositionYField().setText(formatFloat(transform.getPosition().y));
            if (movement != null) {
                controlPanel.getVelocityXField().setText(formatFloat(movement.getVelocity().x));
                controlPanel.getVelocityYField().setText(formatFloat(movement.getVelocity().y));
            } else {
                controlPanel.getVelocityXField().setText("0");
                controlPanel.getVelocityYField().setText("0");
            }
        } finally {
            syncingUiFromSelection = false;
        }
    }

    private String formatFloat(float value) {
        if (Math.abs(value - Math.round(value)) < 0.0001f) {
            return Integer.toString(Math.round(value));
        }
        return Float.toString(value);
    }

    private Float tryParseFloat(String text) {
        if (text == null) {
            return null;
        }
        try {
            return Float.parseFloat(text.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void clearBoard() {
        getEntityManager().removeAll();
        selectedEntity = null;
        trajectoryService.clear();
    }

    private void pruneInactiveEntities() {
        for (Entity entity : getEntityManager().getAllEntities()) {
            if (entity != null && !entity.isActive()) {
                if (entity == selectedEntity) {
                    selectedEntity = null;
                }
                getEntityManager().removeEntity(entity);
            }
        }
    }

    private boolean isPointerOverUi() {
        if (uiStage == null) {
            return false;
        }
        int screenX = gameMaster.getInputManager().getMouseX();
        int screenY = gameMaster.getInputManager().getMouseY();
        Vector2 stageCoords = uiStage.screenToStageCoordinates(new Vector2(screenX, screenY));
        Actor hit = uiStage.hit(stageCoords.x, stageCoords.y, true);
        return hit != null;
    }

    private SimulationController.SpawnRequest getSpawnRequestFromUi() {
        if (controlPanel == null) {
            return null;
        }
        BodyType bodyType = controlPanel.getSelectedBodyType();
        float mass = SandboxConfig.clampMass(controlPanel.getMassValue());
        float radius = SandboxConfig.clampRadius(controlPanel.getRadiusValue());
        float velocityX = SandboxConfig.clampVelocity(controlPanel.getVelocityXValue());
        float velocityY = SandboxConfig.clampVelocity(controlPanel.getVelocityYValue());
        return new SimulationController.SpawnRequest(bodyType, mass, radius, velocityX, velocityY);
    }

    private interface FloatConsumer {
        void accept(float value);
    }
}
