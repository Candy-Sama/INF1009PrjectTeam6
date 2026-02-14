package com.team6.arcadesim;

import com.badlogic.gdx.ApplicationListener;
import com.team6.arcadesim.managers.CollisionManager;
import com.team6.arcadesim.managers.InputManager;
import com.team6.arcadesim.managers.MovementManager;
import com.team6.arcadesim.managers.RenderManager;
import com.team6.arcadesim.managers.SceneManager;
import com.team6.arcadesim.managers.SoundManager;
import com.team6.arcadesim.managers.ViewportManager;

public abstract class AbstractGameMaster implements ApplicationListener {

    private boolean isRunning;
    private float deltaTime;
    private long lastFrameTime;

    // --- The Subsystems (Stateless Tools) ---
    protected SceneManager sceneManager;
    protected ViewportManager viewportManager;
    protected InputManager inputManager;
    protected MovementManager movementManager;
    protected CollisionManager collisionManager;
    protected RenderManager renderManager;
    protected SoundManager soundManager;

    public abstract void init();
    public abstract void update(float dt);

    @Override
    public void create() {
        // 1. Initialize Tools
        viewportManager = new ViewportManager();
        inputManager = new InputManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager();
        renderManager = new RenderManager();
        soundManager = new SoundManager();
        sceneManager = new SceneManager();

        isRunning = true;
        lastFrameTime = System.nanoTime();

        init();
    }

    @Override
    public void render() {
        if (!isRunning) return;

        long time = System.nanoTime();
        deltaTime = (time - lastFrameTime) / 1_000_000_000.0f;
        lastFrameTime = time;
        if (deltaTime > 0.1f) deltaTime = 0.1f;

        // 1. Logic Phase
        update(deltaTime); // Global update
        sceneManager.update(deltaTime); // Updates ONLY the top scene
        inputManager.update();

        // 2. Render Phase
        viewportManager.apply();
        
        // Delegate rendering entirely to SceneManager
        // This allows it to draw the background scenes behind the pause menu
        sceneManager.render(deltaTime);
    }
    
    // ... (Keep resize, pause, resume, dispose as they were) ...
    @Override public void resize(int width, int height) { if (viewportManager != null) viewportManager.resize(width, height); }
    @Override public void pause() { isRunning = false; }
    @Override public void resume() { isRunning = true; lastFrameTime = System.nanoTime(); }
    @Override public void dispose() { if (renderManager != null) renderManager.dispose(); if (sceneManager != null) sceneManager.dispose(); }
    
    // --- Getters for Tools (So Scenes can borrow them) ---
    public MovementManager getMovementManager() { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public RenderManager getRenderManager() { return renderManager; }
    public SoundManager getSoundManager() { return soundManager; }
    public InputManager getInputManager() { return inputManager; }
    public ViewportManager getViewportManager() { return viewportManager; }
    public SceneManager getSceneManager() { return sceneManager; }
}