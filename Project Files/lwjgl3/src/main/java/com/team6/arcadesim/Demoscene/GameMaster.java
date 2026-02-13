package com.team6.arcadesim.Demoscene;

import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.Scenes.DemoScene;
import com.team6.arcadesim.managers.CollisionManager;
import com.team6.arcadesim.managers.EntityManager;
import com.team6.arcadesim.managers.InputManager;
import com.team6.arcadesim.managers.MovementManager;
import com.team6.arcadesim.managers.RenderManager;
import com.team6.arcadesim.managers.SceneManager;
import com.team6.arcadesim.managers.SoundManager;
import com.team6.arcadesim.managers.ViewportManager;

public class GameMaster extends AbstractGameMaster {

    @Override
    public void init() {
        sceneManager.setScene(new DemoScene(this));
    }

    @Override
    public void update(float dt) {
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

    public ViewportManager getViewportManager() {
        return viewportManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    @Override
    public void render() {
        // Clear the screen
        com.badlogic.gdx.utils.ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        
        // Let AbstractGameMaster handle the rest (camera setup, rendering, etc.)
        super.render();
    }

}
