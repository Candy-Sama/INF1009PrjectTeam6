package com.team6.arcadesim.scenes;

import com.badlogic.gdx.utils.Disposable;
import com.team6.arcadesim.AbstractGameMaster;

public abstract class AbstractScene implements Disposable {

    private String sceneName;
    
    // We keep a reference to the Master so the Scene can access Managers
    protected AbstractGameMaster gameMaster;

    public AbstractScene(AbstractGameMaster gameMaster, String sceneName) {
        this.gameMaster = gameMaster;
        this.sceneName = sceneName;
    }

    // --- Lifecycle Hooks ---

    /**
     * Called when the SceneManager switches TO this scene.
     * Use this to load assets, create entities, and setup the camera.
     */
    public abstract void onEnter();

    /**
     * Called when the SceneManager switches AWAY from this scene.
     * Use this to clear entities, stop music, or save state.
     */
    public abstract void onExit();

    /**
     * Called every frame by the GameMaster.
     * Use this for scene-specific logic (e.g., spawning waves, checking win condition).
     */
    public abstract void update(float dt);

    /**
     * Called every frame after the physics/logic phase.
     * Use this to draw backgrounds and UI.
     * (Entities are drawn automatically by the RenderManager).
     */
    public abstract void render(float dt);

    // --- Getters ---
    public String getName() {
        return sceneName;
    }

    @Override
    public void dispose() {
        // Default dispose - override if you have scene-specific textures/sounds
    }
}