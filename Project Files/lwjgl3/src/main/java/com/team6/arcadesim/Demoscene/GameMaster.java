package com.team6.arcadesim.Demoscene;

import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.Scenes.DemoScene;
import com.team6.arcadesim.Demoscene.Scenes.PauseScene;
import com.team6.arcadesim.Demoscene.Scenes.DemoSolar;

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
        // Clear the screen
        com.badlogic.gdx.utils.ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        
        // Let AbstractGameMaster handle the rest (camera setup, rendering, etc.)
        super.render();
    }
}
