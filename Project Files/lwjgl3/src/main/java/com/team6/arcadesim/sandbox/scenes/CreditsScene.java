package com.team6.arcadesim.sandbox.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

public class CreditsScene extends AbstractScene {

    private static final int VIRTUAL_WIDTH = 1280;
    private static final int VIRTUAL_HEIGHT = 720;

    private Stage uiStage;
    private Skin skin;
    private BitmapFont font;
    private Texture backgroundTexture;
    private Texture buttonUpTexture;
    private Texture buttonOverTexture;
    private Texture buttonDownTexture;

    public CreditsScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "CreditsScene");
    }

    @Override
    public void onEnter() {
        gameMaster.getViewportManager().setVirtualResolution(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        registerSceneInputProcessorFirst(uiStage);
        createSkin();
        buildLayout();
    }

    @Override
    public void onExit() {
        unregisterSceneInputProcessor(uiStage);
    }

    @Override
    public void update(float dt) {
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
        if (skin != null) {
            skin.dispose();
            skin = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
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
        root.setBackground(skin.getDrawable("bg-black"));
        root.pad(28f);
        uiStage.addActor(root);

        Label title = new Label("ArcadeSim Gravity Sandbox", skin, "title");
        Label team = new Label("Created by Team 6", skin);
        Label team1 = new Label("1. Chanel", skin);
        Label team2 = new Label("2. Cheston", skin);
        Label team3 = new Label("3. Jolyn", skin);
        Label team4 = new Label("4. Kong Sheng", skin);
        Label team5 = new Label("5. Mustaquim", skin);

        TextButton shutdownButton = new TextButton("Shut Down Simulator", skin);
        shutdownButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.exit();
            }
        });

        root.add(title).padBottom(20f).center();
        root.row();
        root.add(team).padBottom(10f).center();
        root.row();
        root.add(team1).padBottom(20f).center();
        root.row();
        root.add(team2).padBottom(20f).center();
        root.row();
        root.add(team3).padBottom(20f).center();
        root.row();
        root.add(team4).padBottom(20f).center();
        root.row();
        root.add(team5).padBottom(20f).center();
        root.row();
        root.add(shutdownButton).width(360f).height(68f).bottom().center().expandY();
    }

    private void createSkin() {
        skin = new Skin();
        font = new BitmapFont();

        backgroundTexture = loadTextureOrFallback("credits_bg.png", 0f, 0f, 0f, 1f);
        buttonUpTexture = createSolidTexture(1, 1, 0.24f, 0.24f, 0.24f, 1f);
        buttonOverTexture = createSolidTexture(1, 1, 0.35f, 0.35f, 0.35f, 1f);
        buttonDownTexture = createSolidTexture(1, 1, 0.17f, 0.17f, 0.17f, 1f);

        skin.add("default-font", font);
        skin.add("bg-black", new TextureRegionDrawable(new TextureRegion(backgroundTexture)), Drawable.class);

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
        titleLabelStyle.fontColor = new Color(0.93f, 0.93f, 0.93f, 1f);
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

    private Texture loadTextureOrFallback(String path, float r, float g, float b, float a) {
        try {
            if (Gdx.files.internal(path).exists()) {
                return new Texture(Gdx.files.internal(path));
            }
        } catch (Exception ex) {
            System.err.println("Failed to load texture: " + path + " (" + ex.getMessage() + ")");
        }
        return createSolidTexture(1, 1, r, g, b, a);
    }
}
