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

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */

/**
 * The central mediator for the Abstract Engine.
 * It owns the managers and coordinates the game loop.
 */

public abstract class AbstractGameMaster extends Game {

    //Variables
    private boolean isRunning;
    private float deltaTime;
    private long lastFrameTime;

    //Managers
    protected SceneManager sceneManager;
    protected EntityManager entityManager;
    protected InputManager inputManager;
    protected MovementManager movementManager;
    protected CollisionManager collisionManager;
    protected RenderManager renderManager;
    protected SoundManager soundManager;
    protected ViewportManager viewportManager;

    @Override
    public void create() {
        // Initialize the Must-Have Managers
        entityManager = new EntityManager();
        inputManager = new InputManager();
        sceneManager = new SceneManager();
        viewportManager = new ViewportManager();
        soundManager = new SoundManager();
        // soundManager = new SoundManager();

        movementManager = new MovementManager(entityManager);
        collisionManager = new CollisionManager(entityManager);
        renderManager = new RenderManager(entityManager);

        // Specific game initialization (Tetris/Space Invaders logic)
        init();
    }
    
    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        deltaTime = dt;
        lastFrameTime = System.currentTimeMillis();

        // 1. Input Phase - Raw hardware polling
        inputManager.poll();

        // 2. Logic/Simulation Phase
        // GameMaster coordinates here (e.g. converting input to velocity)
        update(dt); 

        // 3. Movement & Physics Phase
        movementManager.update(dt, null, null);
        collisionManager.update(dt);

        // 4. Render Phase
        viewportManager.apply();
        if (sceneManager.getCurrentScene() != null) {
            sceneManager.getCurrentScene().render(dt);
        }
    }

    public void run() {
    
    }

    public void startup() {
    
    }

    public void shutdown() {
    
    }

    public abstract void init();
    public abstract void update(float dt);
}