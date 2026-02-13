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
        if (currentScene != null) {
            currentScene.onExit();
        }

        while (!sceneStack.isEmpty()) {
            sceneStack.pop().onExit();
        }

        currentScene = newScene;

        if (currentScene != null) {
            currentScene.onEnter();
        }
    }

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

    public void pushScene(AbstractScene overlayScene){
        if(currentScene != null){
            sceneStack.push(currentScene);
            currentScene.onPause();
        }
        currentScene = overlayScene;
        currentScene.onEnter();
    }

    public void popScene(){
        if(currentScene != null){
            currentScene.onExit();
        }
        if(!sceneStack.isEmpty()){
            currentScene = sceneStack.pop();
            currentScene.onResume();
        } 
    }

}