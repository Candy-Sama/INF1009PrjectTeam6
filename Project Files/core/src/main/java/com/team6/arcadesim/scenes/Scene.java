package com.team6.arcadesim.scenes;

/*
    The Abstract class for all Scenes in the game.
    Each scene represents a different state or screen in the game.
    - Mus
 */
public abstract class Scene {
    protected String sceneName;

    public Scene(String sceneName) {
        this.sceneName = sceneName;
    }

    public abstract void onEnter();
    public abstract void onExit();
    public abstract void update(float dt);
    
    public void render(float dt) {
        // Default implementation - can be overridden
    }
    
    public String getName() {
        return sceneName;
    }
}
