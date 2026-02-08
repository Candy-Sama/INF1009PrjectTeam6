package com.team6.arcadesim.managers;

import com.team6.arcadesim.scenes.Scene;

public class SceneManager {
    private Scene currentScene;

    public void setScene(Scene scene) {

        // Clean up the current scene by exiting it
        if (currentScene != null) {
            currentScene.onExit();
        }

        // Switch to the new scene
        this.currentScene = scene;

        // Initialize the new scene by entering it
        if (currentScene != null) {
            currentScene.onEnter();
        }
    }

    public void update(float dt) {

        // Update the current scene (include null check)
        if (currentScene != null) {
            currentScene.update(dt);
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public void dispose() {
        // Dispose of the current scene if it exists
        if (currentScene != null) {
            currentScene.onExit();
            currentScene = null;
        }
    }

}
