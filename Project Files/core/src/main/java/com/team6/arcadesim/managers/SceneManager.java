package com.team6.arcadesim.managers;

import com.team6.arcadesim.scenes.Scene;

public class SceneManager {
    private Scene currentScene;

    public void setScene(Scene scene) {
        this.currentScene = scene;
    }
    public Scene getCurrentScene() {
        return currentScene;
    }

    public void update(float dt) {
    }

}
