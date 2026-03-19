package com.team6.arcadesim.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.team6.arcadesim.managers.SceneManager;

public class MainMenuScene {

    private Stage stage;
    private SceneManager sceneManager;
    private Skin skin;

    public MainMenuScene(SceneManager sceneManager, Skin skin) {
        this.sceneManager = sceneManager;
        this.skin = skin;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        TextButton startBtn = new TextButton("Start", skin);
        startBtn.setPosition(300, 300);

        startBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.push(new SandboxScene(sceneManager, skin));
            }
        });

        TextButton exitBtn = new TextButton("Exit", skin);
        exitBtn.setPosition(300, 200);

        exitBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                sceneManager.push(new CreditScene(sceneManager, skin));
            }
        });

        stage.addActor(startBtn);
        stage.addActor(exitBtn);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
}