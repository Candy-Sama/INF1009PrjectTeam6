package com.team6.arcadesim;

import com.team6.arcadesim.managers.CollisionManager;
import com.team6.arcadesim.managers.EntityManager;
import com.team6.arcadesim.managers.MovementManager;
import com.team6.arcadesim.managers.RenderManager;
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

    @Override
    public void render() {
        com.badlogic.gdx.utils.ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        float dt = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        
        this.update(dt); 
        
        if (sceneManager.getCurrentScene() != null) {
            sceneManager.getCurrentScene().render(dt);
        }
    }

}
