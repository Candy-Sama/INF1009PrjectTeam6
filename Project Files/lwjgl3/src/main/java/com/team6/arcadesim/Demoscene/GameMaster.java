package com.team6.arcadesim.Demoscene;

import com.badlogic.gdx.utils.ScreenUtils;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.Scenes.DemoScene;

public class GameMaster extends AbstractGameMaster {

    @Override
    public void init() {
        sceneManager.setScene(new DemoScene(this));
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        super.render();
    }
}