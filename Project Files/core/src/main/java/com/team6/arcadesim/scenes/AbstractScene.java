package com.team6.arcadesim.scenes;

import com.badlogic.gdx.utils.Disposable;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.managers.EntityManager;

public abstract class AbstractScene implements Disposable {

    private String sceneName;
    protected AbstractGameMaster gameMaster;
    
    protected EntityManager entityManager;

    public AbstractScene(AbstractGameMaster gameMaster, String sceneName) {
        this.gameMaster = gameMaster;
        this.sceneName = sceneName;
        this.entityManager = new EntityManager();
    }

    public abstract void onEnter();
    public void onPause(){}
    public void onResume(){}
    public abstract void onExit();
    public abstract void update(float dt);
    public abstract void render(float dt);

    public String getName() { return sceneName; }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void dispose() {
        entityManager.removeAll();
    }
}