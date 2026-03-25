package com.team6.engineLayer;

import com.badlogic.gdx.ApplicationListener;
import com.team6.engineLayer.config.EngineTimingConfig;
import com.team6.engineLayer.events.EventBus;
import com.team6.engineLayer.logging.ConsoleEngineLogger;
import com.team6.engineLayer.logging.EngineLogger;
import com.team6.engineLayer.managers.CollisionManager;
import com.team6.engineLayer.managers.GravityManager;
import com.team6.engineLayer.managers.InputManager;
import com.team6.engineLayer.managers.MovementManager;
import com.team6.engineLayer.managers.RenderManager;
import com.team6.engineLayer.managers.SceneManager;
import com.team6.engineLayer.managers.SoundManager;
import com.team6.engineLayer.managers.ViewportManager;
import com.team6.engineLayer.physics.GravityConfig;

public abstract class AbstractGameMaster implements ApplicationListener {

    private boolean isRunning;
    private long lastFrameTime;
    private float accumulator;

    protected SceneManager sceneManager;
    protected ViewportManager viewportManager;
    protected InputManager inputManager;
    protected MovementManager movementManager;
    protected CollisionManager collisionManager;
    protected RenderManager renderManager;
    protected SoundManager soundManager;
    protected GravityManager gravityManager;
    protected EngineTimingConfig engineTimingConfig;
    protected EventBus eventBus;
    protected EngineLogger logger;

    public abstract void init();
    public abstract void update(float dt);

    @Override
    public void create() {
        logger = new ConsoleEngineLogger();
        eventBus = new EventBus();

        viewportManager = new ViewportManager();
        inputManager = new InputManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager();
        renderManager = new RenderManager();
        soundManager = new SoundManager();
        sceneManager = new SceneManager();
        engineTimingConfig = new EngineTimingConfig();
        gravityManager = new GravityManager(new GravityConfig());

        sceneManager.setLogger(logger);
        sceneManager.setEventBus(eventBus);
        collisionManager.setLogger(logger);
        collisionManager.setEventBus(eventBus);
        soundManager.setLogger(logger);

        isRunning = true;
        lastFrameTime = System.nanoTime();
        accumulator = 0f;

        init();
    }

    @Override
    public void render() {
        if (!isRunning) return;

        long time = System.nanoTime(); // Get current time in nanoseconds
        float deltaTime = (time - lastFrameTime) / 1_000_000_000.0f; // Convert to seconds
        lastFrameTime = time;
        if (deltaTime > engineTimingConfig.getMaxFrameTime()) deltaTime = engineTimingConfig.getMaxFrameTime(); // Cap deltaTime to avoid spiral of death -> https://www.gafferongames.com/post/fix_your_timestep/

        accumulator += deltaTime; // Accumulate elapsed time
        update(deltaTime); // Allow game-specific logic to run every frame (e.g. input handling, UI updates)

        float fixedTimeStep = engineTimingConfig.getFixedTimeStep();
        int maxSimulationSteps = engineTimingConfig.getMaxSimulationSteps();
        int simulationSteps = 0;
        boolean transientInputsCleared = false; // Track if transient inputs have been cleared during this update cycle
        while (accumulator >= fixedTimeStep && simulationSteps < maxSimulationSteps) {
            sceneManager.update(fixedTimeStep); // Update the current scene with a fixed time step for consistent physics and game logic
            accumulator -= fixedTimeStep;
            simulationSteps++;

            if (!transientInputsCleared) { // Clear transient inputs after the first simulation step to ensure they are processed for the current frame but not carried over to the next frame
                inputManager.clearTransientInputs();
                transientInputsCleared = true;
            }
        }

        if (simulationSteps == maxSimulationSteps) { // If we've hit the maximum number of simulation steps, we may be falling behind. Log a warning and reset the accumulator to avoid spiraling out of control.
            accumulator = 0f;
            logger.warn("Simulation fell behind; dropping accumulated time to recover.");
        }

        if (!transientInputsCleared) {
            inputManager.clearTransientInputs();
        }

        viewportManager.apply();
        sceneManager.render(deltaTime); // Render the current scene, passing the actual deltaTime for smooth rendering (e.g. for animations) even if the update logic runs at a fixed time step.
    }
    
    @Override public void resize(int width, int height) { if (viewportManager != null) viewportManager.resize(width, height); }
    @Override public void pause() { isRunning = false; }
    @Override public void resume() { isRunning = true; lastFrameTime = System.nanoTime(); accumulator = 0f; }
    @Override public void dispose() {
        if (renderManager != null) renderManager.dispose();
        if (sceneManager != null) sceneManager.dispose();
        if (soundManager != null) soundManager.dispose();
    }
    
    public SceneManager getSceneManager() { return sceneManager; }
    public ViewportManager getViewportManager() { return viewportManager; }
    public InputManager getInputManager() { return inputManager; }
    public MovementManager getMovementManager() { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public RenderManager getRenderManager() { return renderManager; }
    public SoundManager getSoundManager() { return soundManager; }
    public GravityManager getGravityManager() { return gravityManager; }
    public EngineTimingConfig getEngineTimingConfig() { return engineTimingConfig; }
    public void setEngineTimingConfig(EngineTimingConfig engineTimingConfig) {
        this.engineTimingConfig = (engineTimingConfig == null) ? new EngineTimingConfig() : engineTimingConfig;
    }
    public EventBus getEventBus() { return eventBus; }
    public EngineLogger getLogger() { return logger; }
}
