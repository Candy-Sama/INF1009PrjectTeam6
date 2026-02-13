package com.team6.arcadesim.Demoscene;

import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.Scenes.DemoScene;
import com.team6.arcadesim.managers.CollisionManager;
import com.team6.arcadesim.managers.EntityManager;
import com.team6.arcadesim.managers.MovementManager;
import com.team6.arcadesim.managers.RenderManager;

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
        if (sceneManager.getCurrentScene() != null) {
            sceneManager.getCurrentScene().update(dt); // Tells DemoScene to run Movement & Collision
        }
    }

    public EntityManager getEntityManager() { 
        return entityManager; 
    }

    public CollisionManager getCollisionManager() {
        return collisionManager;
    }

    public MovementManager getMovementManager() {
        return movementManager;
    }

    public RenderManager getRenderManager() { 
        return renderManager; 
    }

    public com.team6.arcadesim.managers.ViewportManager getViewportManager() {
        return viewportManager;
    }

    public com.team6.arcadesim.managers.SoundManager getSoundManager() {
        return soundManager;
    }

    @Override
    public void render() {
        // Clear the screen
        com.badlogic.gdx.utils.ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        
        // Let AbstractGameMaster handle the rest (camera setup, rendering, etc.)
        super.render();
    }

}
