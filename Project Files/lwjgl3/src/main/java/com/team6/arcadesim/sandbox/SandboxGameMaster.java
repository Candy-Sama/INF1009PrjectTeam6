package com.team6.arcadesim.sandbox;

import com.badlogic.gdx.utils.ScreenUtils;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.sandbox.scenes.MainMenuScene;
import com.team6.arcadesim.sandbox.scenes.SandboxPauseScene;
import com.team6.arcadesim.sandbox.scenes.SandboxScene;

public class SandboxGameMaster extends AbstractGameMaster {

    @Override
    public void init() {
        sceneManager.registerScene("main_menu", () -> new MainMenuScene(this));
        sceneManager.registerScene("sandbox", () -> new SandboxScene(this));
        sceneManager.registerScene("sandbox_pause", () -> new SandboxPauseScene(this));
        sceneManager.changeScene("main_menu");
    }

    @Override
    public void update(float dt) {
        // Phase 1: no global game-level update logic yet.
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.08f, 0.09f, 0.12f, 1f);
        super.render();
    }
}
