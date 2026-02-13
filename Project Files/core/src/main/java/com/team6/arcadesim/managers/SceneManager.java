package com.team6.arcadesim.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.team6.arcadesim.scenes.AbstractScene;

public class SceneManager {

    private AbstractScene currentScene;
    private Map<String, AbstractScene> sceneMap;
    private Stack<AbstractScene> sceneStack = new Stack<>();

    public SceneManager() {
        this.sceneMap = new HashMap<>();
    }

    public void addScene(AbstractScene scene) {
        sceneMap.put(scene.getName(), scene);
    }

    public void setScene(AbstractScene newScene) {
        // 1. Teardown the current scene
        if (currentScene != null) {
            currentScene.onExit();
        }

        // Clear the stack if any scenes are stacked
        while (!sceneStack.isEmpty()) {
            sceneStack.pop().onExit();
        }

        // 2. Switch the rerefence
        currentScene = newScene;

        // 3. Setup the new scene
        if (currentScene != null) {
            currentScene.onEnter();
        }
    }

    // Loads a scene by its registered name.
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

    public void update(float dt) {
        if (currentScene != null) {
            currentScene.update(dt);
        }
    }

    public void dispose() {
        for (AbstractScene scene : sceneMap.values()) {
            scene.dispose();
        }
        sceneMap.clear();
    }


    // --- Scene Stack Methods ---

    //To put a scene on top of the current scene (e.g., for pause menus)
    public void pushScene(AbstractScene overlayScene){
        if(currentScene != null){
            sceneStack.push(currentScene);
            currentScene.onPause();
        }
        currentScene = overlayScene; //Switch to the overlay scene
        currentScene.onEnter();
    }

    // To remove the top scene and return to the previous one
    public void popScene(){
        if(currentScene != null){
            currentScene.onExit();
        }
        if(!sceneStack.isEmpty()){
            currentScene = sceneStack.pop(); //get scene from stack
            currentScene.onResume();
        } 
    }

}