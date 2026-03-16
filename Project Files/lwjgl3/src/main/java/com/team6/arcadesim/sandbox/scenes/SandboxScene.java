package com.team6.arcadesim.sandbox.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.scenes.AbstractPlayableScene;
import com.team6.arcadesim.sandbox.ui.SandboxControlPanel;
import com.team6.arcadesim.sandbox.ui.SandboxSkinFactory;
import com.team6.arcadesim.systems.SystemPipeline;

public class SandboxScene extends AbstractPlayableScene {

    private static final int VIRTUAL_WIDTH = 1280;
    private static final int VIRTUAL_HEIGHT = 720;

    private Stage uiStage;
    private Skin uiSkin;
    private SandboxControlPanel controlPanel;

    public SandboxScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "SandboxScene", false);
    }

    @Override
    protected void configureSystems(SystemPipeline pipeline) {
        // Phase 1 placeholder: systems will be configured in later phases.
    }

    @Override
    public void onEnter() {
        gameMaster.getViewportManager().setVirtualResolution(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        uiSkin = SandboxSkinFactory.createSkin();
        controlPanel = new SandboxControlPanel(uiSkin);
        uiStage.addActor(controlPanel.getRootTable());

        registerSceneInputProcessorFirst(uiStage);
    }

    @Override
    public void onExit() {
        getEntityManager().removeAll();
        unregisterSceneInputProcessor(uiStage);
    }

    @Override
    protected void processLevelLogic(float dt) {
        if (uiStage != null) {
            uiStage.act(dt);
        }
    }

    @Override
    public void render(float dt) {
        if (uiStage == null) {
            return;
        }

        uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        uiStage.getViewport().apply();
        uiStage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (uiStage != null) {
            uiStage.dispose();
            uiStage = null;
        }
        if (uiSkin != null) {
            uiSkin.dispose();
            uiSkin = null;
        }
        controlPanel = null;
    }
}
