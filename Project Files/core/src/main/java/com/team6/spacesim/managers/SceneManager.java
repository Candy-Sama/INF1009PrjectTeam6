package com.team6.spacesim.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

import com.team6.spacesim.scenes.AbstractScene;

public class SceneManager {

    // The stack handles which scene is currently active, allowing for overlays and popups
    private Stack<AbstractScene> sceneStack;

    // The Register acts as a factory (Route Name -> Scene Supplier) for creating scenes on demand
    private Map<String, Supplier<AbstractScene>> sceneRegistry;

    public SceneManager() {
        sceneStack = new Stack<>();
        sceneRegistry = new HashMap<>();
    }

    // Initialises the blueprint for a new scene without instantiating it immediately
    // e.g., registry.put("MAIN_MENU", () -> new MainMenuScene(gameMaster));
    public void registerScene(String routeName, Supplier<AbstractScene> sceneFactory) {
        sceneRegistry.put(routeName, sceneFactory);
    }

    // Swaps from old scene to new scene, this destroys the old scene to free memory 
    // and creates the new scene on demand using the factory
    public void changeScene(String routeName) {
        Supplier<AbstractScene> factory = sceneRegistry.get(routeName);
        if (factory != null) {
            // Clean up old scene and stack
            if (!sceneStack.isEmpty()) {
                AbstractScene oldScene = sceneStack.pop();
                oldScene.onExit();
            }

            // Build new scene on demand and push it to the stack
            AbstractScene newScene = factory.get();
            sceneStack.push(newScene);
            newScene.onEnter();
            System.out.println("SceneManager: Routed to scene '" + routeName + "'.");
         } else {
            System.err.println("SceneManager: No scene registered for route '" + routeName + "'!");
        }
    }

    // Push a new scene on top of the current top scene
    public void pushScene(String routeName)  {
        Supplier<AbstractScene> factory = sceneRegistry.get(routeName);
        if (factory != null) {
            AbstractScene overlayScene = factory.get();
            sceneStack.push(overlayScene);
            overlayScene.onEnter();
        }
    }

    // Pop and destroy the top scene off the stack
    public void popScene() {
        if (sceneStack.size() > 1) {
            AbstractScene topScene = sceneStack.pop();
            topScene.onExit();
        } else {
            System.err.println("SceneManager: Cannot pop the last scene!");
        }
    }

    // Standard game loop
    public void update(float dt) {
        if (!sceneStack.isEmpty()) {
            // Only update the top scene of the stack, allowing for overlays to render but not update
            sceneStack.peek().update(dt);
        }
    }

    public void render(float dt) {
        // Renders from bottom to top of the stack, allowing for overlays to render on top of the main scene
        for (AbstractScene scene : sceneStack) {
            scene.render(dt);
        }
    }
    
    public void dispose() {
        for (AbstractScene s : sceneStack) s.dispose();
    }
}