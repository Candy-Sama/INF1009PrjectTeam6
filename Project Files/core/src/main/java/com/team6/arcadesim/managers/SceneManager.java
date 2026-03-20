package com.team6.arcadesim.managers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.team6.arcadesim.events.EventBus;
import com.team6.arcadesim.events.SceneLifecycleEvent;
import com.team6.arcadesim.logging.EngineLogger;
import com.team6.arcadesim.logging.NoOpEngineLogger;
import com.team6.arcadesim.scenes.AbstractScene;

public class SceneManager {

<<<<<<< HEAD
    // The stack handles which scene is currently active, allowing for overlays and popups.
    private final Deque<AbstractScene> sceneStack;

    // Route Name -> Scene Factory.
    private final Map<String, Supplier<AbstractScene>> sceneRegistry;
    private EngineLogger logger;
    private EventBus eventBus;
=======
    private AbstractScene currentScene;
    private Map<String, AbstractScene> sceneMap;
    private Stack<AbstractScene> sceneStack = new Stack<>();
>>>>>>> main

    public SceneManager() {
        this.sceneStack = new ArrayDeque<>();
        this.sceneRegistry = new HashMap<>();
        this.logger = new NoOpEngineLogger();
        this.eventBus = null;
    }

<<<<<<< HEAD
    public void setLogger(EngineLogger logger) {
        this.logger = (logger == null) ? new NoOpEngineLogger() : logger;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void registerScene(String routeName, Supplier<AbstractScene> sceneFactory) {
        sceneRegistry.put(routeName, sceneFactory);
    }

    public void changeScene(String routeName) {
        Supplier<AbstractScene> factory = sceneRegistry.get(routeName);
        if (factory == null) {
            logger.error("SceneManager: No scene registered for route '" + routeName + "'.");
            return;
        }

        String previousSceneName = sceneStack.isEmpty() ? null : sceneStack.peek().getName();

        while (!sceneStack.isEmpty()) {
            AbstractScene oldScene = sceneStack.pop();
            oldScene.onExit();
            oldScene.dispose();
        }

        AbstractScene newScene = factory.get();
        sceneStack.push(newScene);
        newScene.onEnter();
        publishSceneEvent(SceneLifecycleEvent.Type.CHANGED, previousSceneName, newScene.getName());
    }

    public void pushScene(String routeName)  {
        Supplier<AbstractScene> factory = sceneRegistry.get(routeName);
        if (factory == null) {
            logger.error("SceneManager: No scene registered for route '" + routeName + "'.");
            return;
        }

        AbstractScene previousTop = sceneStack.peek();
        if (previousTop != null) {
            previousTop.onPause();
        }

        AbstractScene overlayScene = factory.get();
        sceneStack.push(overlayScene);
        overlayScene.onEnter();
        publishSceneEvent(SceneLifecycleEvent.Type.PUSHED, previousTop == null ? null : previousTop.getName(), overlayScene.getName());
    }

    public void popScene() {
        if (sceneStack.size() > 1) {
            AbstractScene topScene = sceneStack.pop();
            topScene.onExit();
            topScene.dispose();
            AbstractScene resumedScene = sceneStack.peek();
            if (resumedScene != null) {
                resumedScene.onResume();
            }
            publishSceneEvent(SceneLifecycleEvent.Type.POPPED, topScene.getName(), resumedScene == null ? null : resumedScene.getName());
        } else {
            logger.warn("SceneManager: Cannot pop the last scene.");
        }
=======
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
>>>>>>> main
    }

    public void update(float dt) {
        if (!sceneStack.isEmpty()) {
            AbstractScene currentScene = sceneStack.peek();
            currentScene.getEntityManager().beginUpdate();
            try {
                currentScene.update(dt);
            } finally {
                currentScene.getEntityManager().endUpdate();
            }
        }
    }

<<<<<<< HEAD
    public void render(float dt) {
        for (java.util.Iterator<AbstractScene> it = sceneStack.descendingIterator(); it.hasNext();) {
            it.next().render(dt);
        }
    }
    
    public void dispose() {
        while (!sceneStack.isEmpty()) {
            AbstractScene scene = sceneStack.pop();
            scene.onExit();
            scene.dispose();
        }
    }

    private void publishSceneEvent(SceneLifecycleEvent.Type type, String fromScene, String toScene) {
        if (eventBus != null) {
            eventBus.publish(new SceneLifecycleEvent(type, fromScene, toScene));
        }
    }
}
=======
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
>>>>>>> main
