package com.team6.arcadesim.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class CreditScene {

    private Stage stage;
    private SceneManager sceneManager;
    private Skin skin;

    public CreditScene(SceneManager sceneManager, Skin skin) {
        this.sceneManager = sceneManager;
        this.skin = skin;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Label label = new Label("Credits", skin);
        label.setPosition(300, 300);

        TextButton quitBtn = new TextButton("Quit Game", skin);
        quitBtn.setPosition(300, 200);

        quitBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        stage.addActor(label);
        stage.addActor(quitBtn);
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }
}