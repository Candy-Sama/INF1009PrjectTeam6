package com.team6.arcadesim.sandbox.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
import com.team6.arcadesim.sandbox.simulation.CelestialSpriteRegistry;
import com.team6.arcadesim.sandbox.simulation.MergeCollisionResolver;
import com.team6.arcadesim.sandbox.simulation.MutualDestructionResolver;
import com.team6.arcadesim.sandbox.simulation.SandboxTrajectoryService;
import com.team6.arcadesim.sandbox.ui.SandboxControlPanel;
import com.team6.arcadesim.sandbox.ui.SandboxEducationalHud;
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
    private SandboxEducationalHud educationalHud;
    private Table legendTable;
    private CelestialEntityFactory celestialEntityFactory;
    private final MutualDestructionResolver mutualDestructionResolver;
    private final MergeCollisionResolver mergeCollisionResolver;
    private final SandboxTrajectoryService trajectoryService;
    private final TrajectoryRenderer trajectoryRenderer;
    private final ShapeRenderer vectorShapeRenderer;
    private final SpriteBatch backgroundBatch;
    private CelestialSpriteRegistry spriteRegistry;
    private Texture backgroundTexture;
    private SandboxAudioService audioService;
    private SimulationController simulationController;
    private SandboxMode currentMode;
    private boolean toggleSimulationRequested;
    private boolean clearBoardRequested;
    private Entity selectedEntity;
    private boolean syncingUiFromSelection;
    private boolean scenePaused;

    public SandboxScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "SandboxScene", false);
        this.celestialEntityFactory = null;
        this.educationalHud = null;
        this.mutualDestructionResolver = new MutualDestructionResolver(gameMaster.getEventBus());
        this.mergeCollisionResolver = new MergeCollisionResolver(gameMaster.getEventBus());
        this.trajectoryService = new SandboxTrajectoryService();
        this.trajectoryRenderer = new TrajectoryRenderer();
        this.vectorShapeRenderer = new ShapeRenderer();
        this.backgroundBatch = new SpriteBatch();
        this.spriteRegistry = null;
        this.backgroundTexture = null;
        this.audioService = null;
        this.currentMode = SandboxMode.BLUEPRINT;
        this.toggleSimulationRequested = false;
        this.clearBoardRequested = false;
        this.selectedEntity = null;
        this.syncingUiFromSelection = false;
        this.scenePaused = false;
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
        loadBackgroundAsset();
        initializeSpriteRegistry();
        celestialEntityFactory = new CelestialEntityFactory(
            spriteRegistry.getStarRegions(),
            spriteRegistry.getPlanetRegions()
        );

        uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = SandboxSkinFactory.createSkin();
        controlPanel = new SandboxControlPanel(uiSkin);
        educationalHud = new SandboxEducationalHud(uiSkin);
        uiStage.addActor(educationalHud.getRootTable());
        uiStage.addActor(controlPanel.getRootTable());
        buildLegendOverlay();
        wireControlPanelEvents();
        bindLiveEditListeners();
        educationalHud.setNoSelection();
        setMode(SandboxMode.BLUEPRINT);
        scenePaused = false;

        audioService = new SandboxAudioService(gameMaster);
        if (gameMaster.getEventBus() != null) {
            gameMaster.getEventBus().subscribe(SandboxAudioEvent.class, audioService);
        }
        audioService.playSandboxBgm();

        applyCollisionResolver();
        registerSceneInputProcessorFirst(uiStage);
        simulationController = new SimulationController(
            gameMaster,
            getEntityManager(),
            celestialEntityFactory,
            entity -> {
                selectedEntity = entity;
                bindUiToSelectedEntity();
                updateEducationalHud();
                trajectoryService.markDirty();
            },
            () -> {
                selectedEntity = null;
                updateEducationalHud();
            },
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
        scenePaused = false;
    }

    @Override
    public void onPause() {
        scenePaused = true;
    }

    @Override
    public void onResume() {
        scenePaused = false;
        applyCollisionResolver();
    }

    @Override
    protected void processLevelLogic(float dt) {
        // Global pause behavior:
        // - ESC always pauses, even while editing text fields.
        // - P pauses only when not actively typing in a TextField.
        if (uiStage != null) {
            Actor keyboardFocus = uiStage.getKeyboardFocus();
            boolean editingText = keyboardFocus instanceof TextField;

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                uiStage.setKeyboardFocus(null);
                gameMaster.getSceneManager().pushScene("sandbox_pause");
                return;
            }

            if (editingText) {
                // Quick way to exit textbox without touching UI internals.
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    uiStage.setKeyboardFocus(null);
                } else if (gameMaster.getInputManager().isMouseButtonJustPressed(Input.Buttons.LEFT) && !isPointerOverUi()) {
                    uiStage.setKeyboardFocus(null);
                }
            } else if (gameMaster.getInputManager().isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                gameMaster.getSceneManager().pushScene("sandbox_pause");
                return;
            }
        } else if (gameMaster.getInputManager().isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
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
        updateEducationalHud();

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
        renderTiledBackground();

        gameMaster.getRenderManager().render(
            dt,
            getEntityManager().getAllEntities(),
            gameMaster.getViewportManager().getCamera()
        );

        if (isSimulationRunning()
            && !scenePaused
            && currentMode == SandboxMode.SIMULATION
            && SandboxConfig.showVelocityVectors) {
            renderVelocityVectors();
        }

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
        vectorShapeRenderer.dispose();
        backgroundBatch.dispose();
        if (spriteRegistry != null) {
            spriteRegistry.dispose();
            spriteRegistry = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        if (audioService != null) {
            audioService.dispose();
            audioService = null;
        }
        if (uiStage != null) {
            uiStage.dispose();
            uiStage = null;
        }
        legendTable = null;
        if (uiSkin != null) {
            uiSkin.dispose();
            uiSkin = null;
        }
        controlPanel = null;
        educationalHud = null;
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

    private void buildLegendOverlay() {
        legendTable = new Table();
        legendTable.setFillParent(true);
        legendTable.bottom().left();
        legendTable.pad(12f);
        legendTable.setTouchable(Touchable.disabled);

        Label legendLabel = new Label(
            "Legend: P / ESC to pause  |  Click an entity to select it, then edit values from the side panel.",
            uiSkin
        );
        legendLabel.setWrap(true);

        legendTable.add(legendLabel).width(760f).left();
        uiStage.addActor(legendTable);
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
        if (educationalHud != null) {
            educationalHud.setNoSelection();
        }
        trajectoryService.clear();
    }

    private void loadBackgroundAsset() {
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        try {
            if (Gdx.files.internal("sandbox_bg.png").exists()) {
                backgroundTexture = new Texture(Gdx.files.internal("sandbox_bg.png"));
                backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            }
        } catch (Exception ex) {
            System.err.println("Failed to load sandbox_bg.png (" + ex.getMessage() + ")");
        }
    }

    private void initializeSpriteRegistry() {
        if (spriteRegistry != null) {
            spriteRegistry.dispose();
        }
        spriteRegistry = new CelestialSpriteRegistry();
        spriteRegistry.load();
    }

    private void renderTiledBackground() {
        if (backgroundTexture == null) {
            return;
        }

        com.badlogic.gdx.graphics.OrthographicCamera camera = gameMaster.getViewportManager().getCamera();
        float worldWidth = camera.viewportWidth * camera.zoom;
        float worldHeight = camera.viewportHeight * camera.zoom;
        float left = camera.position.x - (worldWidth / 2f);
        float bottom = camera.position.y - (worldHeight / 2f);
        float tileScale = 512f;

        backgroundBatch.setProjectionMatrix(camera.combined);
        backgroundBatch.begin();
        backgroundBatch.draw(
            backgroundTexture,
            left,
            bottom,
            worldWidth,
            worldHeight,
            left / tileScale,
            bottom / tileScale,
            (left + worldWidth) / tileScale,
            (bottom + worldHeight) / tileScale
        );
        backgroundBatch.end();
    }

    private void applyCollisionResolver() {
        if (SandboxConfig.useMergeCollision) {
            gameMaster.getCollisionManager().setResolver(mergeCollisionResolver);
        } else {
            gameMaster.getCollisionManager().setResolver(mutualDestructionResolver);
        }
    }

    private void renderVelocityVectors() {
        vectorShapeRenderer.setProjectionMatrix(gameMaster.getViewportManager().getCamera().combined);
        vectorShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        vectorShapeRenderer.setColor(Color.GREEN);

        for (Entity entity : getEntityManager().getAllEntities()) {
            if (entity == null || !entity.isActive()) {
                continue;
            }
            if (!entity.hasComponent(TransformComponent.class) || !entity.hasComponent(MovementComponent.class)) {
                continue;
            }

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            MovementComponent movement = entity.getComponent(MovementComponent.class);
            float startX = transform.getPosition().x;
            float startY = transform.getPosition().y;
            float endX = startX + (movement.getVelocity().x * SandboxConfig.VELOCITY_VECTOR_SCALE);
            float endY = startY + (movement.getVelocity().y * SandboxConfig.VELOCITY_VECTOR_SCALE);
            vectorShapeRenderer.line(startX, startY, endX, endY);
        }

        vectorShapeRenderer.end();
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

    private void updateEducationalHud() {
        if (educationalHud == null) {
            return;
        }
        if (selectedEntity == null || !selectedEntity.isActive()) {
            educationalHud.setNoSelection();
            return;
        }
        if (!selectedEntity.hasComponent(TransformComponent.class)
            || !selectedEntity.hasComponent(MassComponent.class)
            || !selectedEntity.hasComponent(RadiusComponent.class)) {
            educationalHud.setNoSelection();
            return;
        }

        TransformComponent selectedTransform = selectedEntity.getComponent(TransformComponent.class);
        MassComponent selectedMass = selectedEntity.getComponent(MassComponent.class);
        RadiusComponent selectedRadius = selectedEntity.getComponent(RadiusComponent.class);
        MovementComponent selectedMovement = selectedEntity.getComponent(MovementComponent.class);

        boolean selectedIsStar = (selectedMovement == null);
        float speed = selectedIsStar ? 0f : selectedMovement.getVelocity().len();

        float gravityConstant = gameMaster.getGravityManager().getGravityConfig().getGravityConstant();
        float minDistanceSq = gameMaster.getGravityManager().getGravityConfig().getMinDistanceSq();

        float nearestStarDistance = -1f;
        float nearestStarMass = -1f;
        float totalAccX = 0f;
        float totalAccY = 0f;

        for (Entity entity : getEntityManager().getAllEntities()) {
            if (entity == null || entity == selectedEntity || !entity.isActive()) {
                continue;
            }
            if (!entity.hasComponent(TransformComponent.class) || !entity.hasComponent(MassComponent.class)) {
                continue;
            }

            TransformComponent otherTransform = entity.getComponent(TransformComponent.class);
            MassComponent otherMass = entity.getComponent(MassComponent.class);

            float dx = otherTransform.getPosition().x - selectedTransform.getPosition().x;
            float dy = otherTransform.getPosition().y - selectedTransform.getPosition().y;
            float rawDistanceSq = (dx * dx) + (dy * dy);
            float clampedDistanceSq = Math.max(rawDistanceSq, minDistanceSq);
            float clampedDistance = (float) Math.sqrt(clampedDistanceSq);

            if (clampedDistance <= 0f) {
                continue;
            }

            float force = gravityConstant * otherMass.getMass() / clampedDistanceSq;
            totalAccX += force * (dx / clampedDistance);
            totalAccY += force * (dy / clampedDistance);

            boolean otherIsStar = !entity.hasComponent(MovementComponent.class);
            if (otherIsStar) {
                float rawDistance = (float) Math.sqrt(rawDistanceSq);
                if (nearestStarDistance < 0f || rawDistance < nearestStarDistance) {
                    nearestStarDistance = rawDistance;
                    nearestStarMass = otherMass.getMass();
                }
            }
        }

        float accelerationMagnitude = (float) Math.sqrt((totalAccX * totalAccX) + (totalAccY * totalAccY));
        String orbitType = classifyOrbitType(
            selectedIsStar,
            speed,
            nearestStarDistance,
            nearestStarMass,
            gravityConstant
        );

        educationalHud.setStats(
            selectedIsStar ? "Star" : "Planet",
            selectedMass.getMass(),
            selectedRadius.getRadius(),
            speed,
            accelerationMagnitude,
            nearestStarDistance,
            orbitType
        );
    }

    private String classifyOrbitType(
        boolean selectedIsStar,
        float speed,
        float nearestStarDistance,
        float nearestStarMass,
        float gravityConstant
    ) {
        if (selectedIsStar) {
            return "Anchor";
        }
        if (nearestStarDistance <= 0f || nearestStarMass <= 0f) {
            return "Free Drift";
        }

        float circularSpeed = (float) Math.sqrt((gravityConstant * nearestStarMass) / nearestStarDistance);
        float escapeSpeed = (float) (Math.sqrt(2f) * circularSpeed);

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

    private interface FloatConsumer {
        void accept(float value);
    }
}
