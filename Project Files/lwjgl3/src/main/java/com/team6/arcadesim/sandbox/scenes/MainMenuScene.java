package com.team6.arcadesim.sandbox.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.scenes.AbstractScene;

public class MainMenuScene extends AbstractScene {

    private static final int VIRTUAL_WIDTH = 1280;
    private static final int VIRTUAL_HEIGHT = 720;

    private Stage uiStage;
    private Skin skin;
    private BitmapFont font;
    private Texture panelTexture;
    private Texture buttonUpTexture;
    private Texture buttonOverTexture;
    private Texture buttonDownTexture;
    private boolean startRequested;
    private boolean exitRequested;

    public MainMenuScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "MainMenuScene");
    }

    @Override
    public void onEnter() {
        gameMaster.getViewportManager().setVirtualResolution(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        startRequested = false;
        exitRequested = false;

        uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        registerSceneInputProcessorFirst(uiStage);

        createSkin();
        buildLayout();
    }

    @Override
    public void onExit() {
        getEntityManager().removeAll();
        unregisterSceneInputProcessor(uiStage);
    }

    @Override
    public void update(float dt) {
        if (startRequested) {
            startRequested = false;
            gameMaster.getSceneManager().changeScene("sandbox");
            return;
        }
        if (exitRequested) {
            exitRequested = false;
            Gdx.app.exit();
        }
        uiStage.act(dt);
    }

    @Override
    public void render(float dt) {
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
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        if (panelTexture != null) {
            panelTexture.dispose();
            panelTexture = null;
        }
        if (buttonUpTexture != null) {
            buttonUpTexture.dispose();
            buttonUpTexture = null;
        }
        if (buttonOverTexture != null) {
            buttonOverTexture.dispose();
            buttonOverTexture = null;
        }
        if (buttonDownTexture != null) {
            buttonDownTexture.dispose();
            buttonDownTexture = null;
        }
    }

    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(skin.getDrawable("panel-bg"));
        root.pad(36f);
        uiStage.addActor(root);

        Label title = new Label("ArcadeSim Gravity Sandbox", skin, "title");
        TextButton startButton = new TextButton("Start Sandbox", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                startRequested = true;
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                exitRequested = true;
            }
        });

        root.add(title).padBottom(36f).center();
        root.row();
        root.add(startButton).width(320f).height(70f).padBottom(16f);
        root.row();
        root.add(exitButton).width(320f).height(70f);
    }

    private void createSkin() {
        skin = new Skin();
        font = new BitmapFont();

        panelTexture = createSolidTexture(1, 1, 0.10f, 0.12f, 0.16f, 0.92f);
        buttonUpTexture = createSolidTexture(1, 1, 0.18f, 0.28f, 0.45f, 1f);
        buttonOverTexture = createSolidTexture(1, 1, 0.22f, 0.36f, 0.56f, 1f);
        buttonDownTexture = createSolidTexture(1, 1, 0.14f, 0.22f, 0.36f, 1f);

        skin.add("default-font", font);
        skin.add("panel-bg", new TextureRegionDrawable(new TextureRegion(panelTexture)), Drawable.class);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonUpTexture));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(buttonOverTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        skin.add("default", buttonStyle);

        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle();
        defaultLabelStyle.font = font;
        defaultLabelStyle.fontColor = Color.WHITE;
        skin.add("default", defaultLabelStyle);

        Label.LabelStyle titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = font;
        titleLabelStyle.fontColor = new Color(0.95f, 0.93f, 0.82f, 1f);
        skin.add("title", titleLabelStyle);
    }

    private Texture createSolidTexture(int width, int height, float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
