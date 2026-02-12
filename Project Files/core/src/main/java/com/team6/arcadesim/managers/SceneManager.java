package com.team6.arcadesim.managers;

import java.util.HashMap;
import java.util.Map;

import com.team6.arcadesim.scenes.AbstractScene;

public class SceneManager {

    private AbstractScene currentScene;
    // Optional: Keep a cache of scenes if you want to switch back and forth
    private Map<String, AbstractScene> sceneMap;

    public SceneManager() {
        this.sceneMap = new HashMap<>();
    }

    /**
     * Registers a scene so it can be loaded by string name later.
     */
    public void addScene(AbstractScene scene) {
        sceneMap.put(scene.getName(), scene);
    }

    /**
     * Switches to a new scene immediately.
     */
    public void setScene(AbstractScene newScene) {
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
        AbstractScene scene = sceneMap.get(sceneName);
        if (scene != null) {
            setScene(scene);
        } else {
            System.err.println("Error: Scene not found - " + sceneName);
        }
    }

    public AbstractScene getCurrentScene() {
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
        for (AbstractScene scene : sceneMap.values()) {
            scene.dispose();
        }
        sceneMap.clear();
    }
}