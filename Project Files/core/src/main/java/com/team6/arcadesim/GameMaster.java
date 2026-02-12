package com.team6.arcadesim;

import com.team6.arcadesim.scenes.DemoScene;
/**
 * Concrete implementation of AbstractGameMaster.
 * This is a placeholder class; actual game logic should be implemented here.
 */
public class GameMaster extends AbstractGameMaster {

    @Override
    public void init() {
        // Initialize the game here
        // Example: Load initial scene, set up entities, etc.
        sceneManager.setScene(new DemoScene(this));
    }

    @Override
    public void update(float dt) {
        // Game-specific update logic
        // Example: Process input and convert to entity actions
    }
}
