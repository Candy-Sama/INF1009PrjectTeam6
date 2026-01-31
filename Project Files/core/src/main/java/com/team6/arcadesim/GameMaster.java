package com.team6.arcadesim;

import com.badlogic.gdx.Game;
import com.team6.arcadesim.scenes.SceneManager;
import com.team6.arcadesim.scenes.MenuScene;
import com.team6.arcadesim.scenes.Scene;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class GameMaster extends Game {
    SceneManager sceneManager;

    @Override
    public void create() {
        sceneManager = new SceneManager();
        sceneManager.setScene(new MenuScene());
    }
    
    @Override
    public void render() {
        float delta = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        sceneManager.update(delta);
    }
    
    @Override
    public void dispose() {
        sceneManager.dispose();
    }
}