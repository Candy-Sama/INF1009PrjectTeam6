package com.team6.engineLayer.managers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.team6.engineLayer.events.EventBus;
import com.team6.engineLayer.events.SceneLifecycleEvent;
import com.team6.engineLayer.logging.EngineLogger;
import com.team6.engineLayer.logging.NoOpEngineLogger;
import com.team6.engineLayer.scenes.AbstractScene;

public class SceneManager {

    // The stack handles which scene is currently active, allowing for overlays and popups.
    private final Deque<AbstractScene> sceneStack;

    // Route Name -> Scene Factory.
    private final Map<String, Supplier<AbstractScene>> sceneRegistry;
    private EngineLogger logger;
    private EventBus eventBus;

    public SceneManager() {
        this.sceneStack = new ArrayDeque<>();
        this.sceneRegistry = new HashMap<>();
        this.logger = new NoOpEngineLogger();
        this.eventBus = null;
    }

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
            oldScene.detachSceneInputProcessors();
            oldScene.onExit();
            oldScene.dispose();
        }

        AbstractScene newScene = factory.get();
        sceneStack.push(newScene);
        newScene.onEnter();
        newScene.attachSceneInputProcessors();
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
            previousTop.detachSceneInputProcessors();
        }

        AbstractScene overlayScene = factory.get();
        sceneStack.push(overlayScene);
        overlayScene.onEnter();
        overlayScene.attachSceneInputProcessors();
        publishSceneEvent(SceneLifecycleEvent.Type.PUSHED, previousTop == null ? null : previousTop.getName(), overlayScene.getName());
    }

    public void popScene() {
        if (sceneStack.size() > 1) {
            AbstractScene topScene = sceneStack.pop();
            topScene.detachSceneInputProcessors();
            topScene.onExit();
            topScene.dispose();
            AbstractScene resumedScene = sceneStack.peek();
            if (resumedScene != null) {
                resumedScene.onResume();
                resumedScene.attachSceneInputProcessors();
            }
            publishSceneEvent(SceneLifecycleEvent.Type.POPPED, topScene.getName(), resumedScene == null ? null : resumedScene.getName());
        } else {
            logger.warn("SceneManager: Cannot pop the last scene.");
        }
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

    public void render(float dt) {
        for (java.util.Iterator<AbstractScene> it = sceneStack.descendingIterator(); it.hasNext();) {
            it.next().render(dt);
        }
    }
    
    public void dispose() {
        while (!sceneStack.isEmpty()) {
            AbstractScene scene = sceneStack.pop();
            scene.detachSceneInputProcessors();
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
