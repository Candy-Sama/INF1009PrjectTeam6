package com.team6.concreteLayer.Demoscene;

import com.badlogic.gdx.utils.ScreenUtils;
import com.team6.concreteLayer.Demoscene.Scenes.DemoScene;
import com.team6.concreteLayer.Demoscene.Scenes.DemoSolar;
import com.team6.concreteLayer.Demoscene.Scenes.PauseScene;
import com.team6.engineLayer.AbstractGameMaster;

public class GameMaster extends AbstractGameMaster {

    @Override
    public void init() {
        sceneManager.registerScene("pause", () -> new PauseScene(this));
        sceneManager.registerScene("demo", () -> new DemoScene(this));
        sceneManager.registerScene("solar", () -> new DemoSolar(this));

        sceneManager.changeScene("solar");
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
