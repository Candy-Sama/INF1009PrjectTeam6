package com.team6.arcadesim.scenes;

import com.badlogic.gdx.utils.Disposable;
import com.team6.arcadesim.AbstractGameMaster;

public abstract class AbstractScene implements Disposable {

    private String sceneName;
    
    protected AbstractGameMaster gameMaster;

    public AbstractScene(AbstractGameMaster gameMaster, String sceneName) {
        this.gameMaster = gameMaster;
        this.sceneName = sceneName;
    }

    public abstract void onEnter();

    public void onPause(){}
    public void onResume(){}

    public abstract void onExit();

    public abstract void update(float dt);

    public abstract void render(float dt);

    public String getName() {
        return sceneName;
    }

    @Override
    public void dispose() {
    }
}