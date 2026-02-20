package com.team6.arcadesim.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.team6.arcadesim.scenes.AbstractScene;

public class SceneManager {

    private AbstractScene currentScene;
    private Map<String, AbstractScene> sceneMap;
    // Java's Stack iterates bottom-up (FIFO) for for-each loops
    private Stack<AbstractScene> sceneStack = new Stack<>();

    public SceneManager() {
        this.sceneMap = new HashMap<>();
    }

    public void addScene(AbstractScene scene) { sceneMap.put(scene.getName(), scene); }
    public void loadScene(String name) { if(sceneMap.containsKey(name)) setScene(sceneMap.get(name)); }
    
    public void setScene(AbstractScene newScene) {
        if (currentScene != null) currentScene.onExit();
        while (!sceneStack.isEmpty()) sceneStack.pop().onExit();
        currentScene = newScene;
        if (currentScene != null) currentScene.onEnter();
    }
    
    public AbstractScene getCurrentScene() { return currentScene; }

    public void update(float dt) {
        if (currentScene != null) {
            currentScene.update(dt);
        }
    }

    public void render(float dt) {
        for (AbstractScene scene : sceneStack) {
            scene.render(dt);
        }
        
        if (currentScene != null) {
            currentScene.render(dt);
        }
    }

    public void pushScene(AbstractScene overlayScene) {
        if (currentScene != null) {
            sceneStack.push(currentScene);
            currentScene.onPause();
        }
        currentScene = overlayScene;
        currentScene.onEnter();
    }

    public void popScene() {
        if (currentScene != null) {
            currentScene.onExit();
        }
        
        if (!sceneStack.isEmpty()) {
            currentScene = sceneStack.pop();
            currentScene.onResume();
        } else {
            System.err.println("SceneManager: Cannot pop the last scene!");
        }
    }
    
    public void dispose() {
        for (AbstractScene s : sceneMap.values()) s.dispose();
    }
}