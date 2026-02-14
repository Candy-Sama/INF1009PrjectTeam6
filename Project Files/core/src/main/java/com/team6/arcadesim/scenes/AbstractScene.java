package com.team6.arcadesim.scenes;

import com.badlogic.gdx.utils.Disposable;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.managers.EntityManager; // Import this!

public abstract class AbstractScene implements Disposable {

    private String sceneName;
    protected AbstractGameMaster gameMaster;
    
    protected EntityManager entityManager;

    public AbstractScene(AbstractGameMaster gameMaster, String sceneName) {
        this.gameMaster = gameMaster;
        this.sceneName = sceneName;
        
        // Every Scene starts with an empty list of entities
        this.entityManager = new EntityManager();
    }

    // --- Standard Hooks ---
    public abstract void onEnter();
    public void onPause(){}
    public void onResume(){}
    public abstract void onExit();
    public abstract void update(float dt);
    public abstract void render(float dt);

    public String getName() { return sceneName; }
    
    // Allow the Scene logic to access its own entities
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void dispose() {
        // Optional: clear entities on dispose
        entityManager.removeAll();
    }
}