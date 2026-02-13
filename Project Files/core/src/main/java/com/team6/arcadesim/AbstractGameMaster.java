package com.team6.arcadesim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.team6.arcadesim.managers.CollisionManager;
import com.team6.arcadesim.managers.EntityManager;
import com.team6.arcadesim.managers.InputManager;
import com.team6.arcadesim.managers.MovementManager;
import com.team6.arcadesim.managers.RenderManager;
import com.team6.arcadesim.managers.SceneManager;
import com.team6.arcadesim.managers.SoundManager;
import com.team6.arcadesim.managers.ViewportManager;
import com.team6.arcadesim.ecs.Entity;
import com.badlogic.gdx.ApplicationListener;

public abstract class AbstractGameMaster implements ApplicationListener {

    // --- State Variables ---
    private boolean isRunning;
    private float deltaTime;
    private long lastFrameTime;

    // --- The Subsystems (Protected for inheritance) ---
    protected SceneManager sceneManager;
    protected ViewportManager viewportManager;
    protected EntityManager entityManager;
    protected InputManager inputManager;
    protected MovementManager movementManager;
    protected CollisionManager collisionManager;
    protected RenderManager renderManager;
    protected SoundManager soundManager;

    // --- Abstract Hooks (For the specific game to implement) ---
    public abstract void init();
    public abstract void update(float dt);

    @Override
    public void create() {
        // 1. Initialize all Managers
        // Note: Order matters slightly (e.g., RenderManager needs to exist before we draw)
        viewportManager = new ViewportManager();
        entityManager = new EntityManager();
        inputManager = new InputManager();
        movementManager = new MovementManager();
        collisionManager = new CollisionManager();
        renderManager = new RenderManager();
        soundManager = new SoundManager();
        sceneManager = new SceneManager();

        // 2. Initial State
        isRunning = true;
        lastFrameTime = System.nanoTime();

        // 3. Call the abstract init() so the subclass can load assets/scenes
        init();
    }

    @Override
    public void render() {
        if (!isRunning) return;

        // --- 1. Time Management ---
        long time = System.nanoTime();
        deltaTime = (time - lastFrameTime) / 1_000_000_000.0f; // Convert ns to seconds
        lastFrameTime = time;

        // Cap deltaTime to prevent "spiraling" physics on slow frames (max 0.1s)
        if (deltaTime > 0.1f) deltaTime = 0.1f;

        // --- 2. Input Phase ---
        // Clears "JustPressed" flags from the previous frame
        inputManager.update(); 
        
        // --- 3. Game Logic Phase ---
        // a. Global game update (defined in subclass)
        update(deltaTime);
        
        // b. Active Scene update
        sceneManager.update(deltaTime);

        // --- 4. Physics & Collision Phase ---
        // Get all active entities once to save performance
        java.util.List<Entity> entities = entityManager.getAllEntities();

        movementManager.update(deltaTime, entities);
        collisionManager.update(deltaTime, entities);

        // --- 5. Render Phase ---
        // Apply viewport (camera) settings
        viewportManager.apply();
        
        // Render the current scene (which will call RenderManager internally)
        if (sceneManager.getCurrentScene() != null) {
            sceneManager.getCurrentScene().render(deltaTime);
        }
    }

    @Override
    public void resize(int width, int height) {
        if (viewportManager != null) {
            viewportManager.resize(width, height);
        }
    }

    @Override
    public void pause() {
        isRunning = false;
    }

    @Override
    public void resume() {
        isRunning = true;
        lastFrameTime = System.nanoTime(); // Reset time to prevent huge delta jump
    }

    @Override
    public void dispose() {
        // Clean up all heavy resources
        if (renderManager != null) renderManager.dispose();
        if (sceneManager != null) sceneManager.dispose();
        // SoundManager doesn't strictly need dispose if it just holds audio references, 
        // but if you added one, call it here.
    }
}