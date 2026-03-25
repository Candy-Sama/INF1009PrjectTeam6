package com.team6.concreteLayer.sandbox;

import com.badlogic.gdx.utils.ScreenUtils;
import com.team6.concreteLayer.sandbox.config.SandboxRuntimeSettings;
import com.team6.concreteLayer.sandbox.scenes.CreditsScene;
import com.team6.concreteLayer.sandbox.scenes.MainMenuScene;
import com.team6.concreteLayer.sandbox.scenes.SandboxPauseScene;
import com.team6.concreteLayer.sandbox.scenes.SandboxScene;
import com.team6.engineLayer.AbstractGameMaster;

public class SandboxGameMaster extends AbstractGameMaster {

    private final SandboxRuntimeSettings runtimeSettings = new SandboxRuntimeSettings();

    @Override
    public void init() {
        sceneManager.registerScene("main_menu", () -> new MainMenuScene(this));
        sceneManager.registerScene("credits", () -> new CreditsScene(this));
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

    public SandboxRuntimeSettings getRuntimeSettings() {
        return runtimeSettings;
    }
}
