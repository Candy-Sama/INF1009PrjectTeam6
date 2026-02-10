package com.team6.arcadesim.managers;

import com.team6.arcadesim.scenes.Scene;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private Scene currentScene;
    // Optional: Keep a cache of scenes if you want to switch back and forth
    private Map<String, Scene> sceneMap;

    public SceneManager() {
        this.sceneMap = new HashMap<>();
    }

    /**
     * Registers a scene so it can be loaded by string name later.
     */
    public void addScene(Scene scene) {
        sceneMap.put(scene.getName(), scene);
    }

    /**
     * Switches to a new scene immediately.
     */
    public void setScene(Scene newScene) {
        // 1. Cleanup the old scene
        if (currentScene != null) {
            currentScene.onExit();
        }

        // 2. Switch reference
        currentScene = newScene;

        // 3. Setup the new scene
        if (currentScene != null) {
            currentScene.onEnter();
        }
    }

    /**
     * Looks up a scene by name and switches to it.
     */
    public void loadScene(String sceneName) {
        Scene scene = sceneMap.get(sceneName);
        if (scene != null) {
            setScene(scene);
        } else {
            System.err.println("Error: Scene not found - " + sceneName);
        }
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    // Called by AbstractGameMaster
    public void update(float dt) {
        if (currentScene != null) {
            currentScene.update(dt);
        }
    }

    public void dispose() {
        // Dispose all cached scenes
        for (Scene scene : sceneMap.values()) {
            scene.dispose();
        }
        sceneMap.clear();
    }
}