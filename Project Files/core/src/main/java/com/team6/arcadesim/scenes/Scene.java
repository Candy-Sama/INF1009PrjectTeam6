package com.team6.arcadesim.scenes;

public abstract class Scene {
    private String sceneName;

    public Scene(String sceneName) {
        this.sceneName = sceneName;
    }

    public void onEnter() {
        // Called when the scene becomes active
    }
    public void onExit() {
        // Called when the scene is no longer active
    }

    public abstract void update(float dt);
    public abstract void render(float dt);
}
