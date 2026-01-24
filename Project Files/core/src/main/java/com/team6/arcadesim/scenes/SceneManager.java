package com.team6.arcadesim.scenes;

public class SceneManager {
    private Scene currentScene;
    
    public void setScene(Scene newScene) {
        if (currentScene != null) {
            currentScene.dispose();
        }
        currentScene = newScene;
        currentScene.create();
    }
    
    public void update(float delta) {
        if (currentScene != null) {
            currentScene.update(delta);
        }
    }
    
    public Scene getCurrentScene() {
        return currentScene;
    }
    
    public void dispose() {
        if (currentScene != null) {
            currentScene.dispose();
        }
    }
}
