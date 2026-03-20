package com.team6.arcadesim.sandbox.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.scenes.AbstractScene;
import com.team6.arcadesim.sandbox.config.SandboxConfig;
import com.team6.arcadesim.sandbox.simulation.MergeCollisionResolver;
import com.team6.arcadesim.sandbox.simulation.MutualDestructionResolver;

public class SandboxPauseScene extends AbstractScene {

    private static final int VIRTUAL_WIDTH = 1280;
    private static final int VIRTUAL_HEIGHT = 720;

    private Stage uiStage;
    private Skin skin;
    private BitmapFont font;
    private Texture overlayTexture;
    private Texture panelTexture;
    private Texture buttonUpTexture;
    private Texture buttonOverTexture;
    private Texture buttonDownTexture;
    private Texture sliderTrackTexture;
    private Texture sliderFillTexture;
    private Texture sliderKnobTexture;
    private Slider masterSlider;
    private Slider musicSlider;
    private Slider sfxSlider;
    private Label masterValueLabel;
    private Label musicValueLabel;
    private Label sfxValueLabel;
    private TextButton vectorModeButton;
    private TextButton collisionModeButton;
    private MutualDestructionResolver mutualDestructionResolver;
    private MergeCollisionResolver mergeCollisionResolver;
    private boolean firstFrame;
    private boolean resumeRequested;
    private boolean mainMenuRequested;

    public SandboxPauseScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "SandboxPauseScene");
    }

    @Override
    public void onEnter() {
        gameMaster.getViewportManager().setVirtualResolution(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        firstFrame = true;
        resumeRequested = false;
        mainMenuRequested = false;

        uiStage = new Stage(new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT));
        registerSceneInputProcessorFirst(uiStage);

        mutualDestructionResolver = new MutualDestructionResolver(gameMaster.getEventBus());
        mergeCollisionResolver = new MergeCollisionResolver(gameMaster.getEventBus());

        createSkin();
        buildLayout();
        applyCollisionResolverFromConfig();
        gameMaster.getSoundManager().pauseMusic();
    }

    @Override
    public void onExit() {
        unregisterSceneInputProcessor(uiStage);
        gameMaster.getSoundManager().resumeMusic();
    }

    @Override
    public void update(float dt) {
        if (firstFrame) {
            firstFrame = false;
            uiStage.act(dt);
            return;
        }

        if (gameMaster.getInputManager().isKeyJustPressed(Input.Keys.P)) {
            resumeRequested = true;
        }

        if (resumeRequested) {
            resumeRequested = false;
            gameMaster.getSceneManager().popScene();
            return;
        }

        if (mainMenuRequested) {
            mainMenuRequested = false;
            gameMaster.getSceneManager().changeScene("main_menu");
            return;
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
        if (overlayTexture != null) {
            overlayTexture.dispose();
            overlayTexture = null;
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
        if (sliderTrackTexture != null) {
            sliderTrackTexture.dispose();
            sliderTrackTexture = null;
        }
        if (sliderFillTexture != null) {
            sliderFillTexture.dispose();
            sliderFillTexture = null;
        }
        if (sliderKnobTexture != null) {
            sliderKnobTexture.dispose();
            sliderKnobTexture = null;
        }
    }

    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(skin.getDrawable("overlay-bg"));
        uiStage.addActor(root);

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("panel-bg"));
        panel.pad(22f);
        root.add(panel).width(680f).pad(40f);

        Label title = new Label("Paused", skin, "title");
        panel.add(title).colspan(3).padBottom(20f).center();
        panel.row();

        masterSlider = new Slider(0f, 1f, 0.01f, false, skin);
        masterSlider.setValue(gameMaster.getSoundManager().getMasterVolume());
        masterValueLabel = new Label(toPercent(masterSlider.getValue()), skin);
        addSliderRow(panel, "Master Volume", masterSlider, masterValueLabel);

        musicSlider = new Slider(0f, 1f, 0.01f, false, skin);
        musicSlider.setValue(gameMaster.getSoundManager().getMusicVolume());
        musicValueLabel = new Label(toPercent(musicSlider.getValue()), skin);
        addSliderRow(panel, "Music Volume", musicSlider, musicValueLabel);

        sfxSlider = new Slider(0f, 1f, 0.01f, false, skin);
        sfxSlider.setValue(gameMaster.getSoundManager().getSFXVolume());
        sfxValueLabel = new Label(toPercent(sfxSlider.getValue()), skin);
        addSliderRow(panel, "SFX Volume", sfxSlider, sfxValueLabel);

        wireSliderListeners();

        vectorModeButton = new TextButton("", skin);
        refreshVectorModeButtonText();
        vectorModeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SandboxConfig.showVelocityVectors = !SandboxConfig.showVelocityVectors;
                refreshVectorModeButtonText();
            }
        });
        panel.row();
        panel.add(vectorModeButton).colspan(3).width(380f).height(52f).padTop(8f).center();

        collisionModeButton = new TextButton("", skin);
        refreshCollisionModeButtonText();
        collisionModeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SandboxConfig.useMergeCollision = !SandboxConfig.useMergeCollision;
                refreshCollisionModeButtonText();
                applyCollisionResolverFromConfig();
            }
        });
        panel.row();
        panel.add(collisionModeButton).colspan(3).width(380f).height(52f).padTop(16f).center();

        TextButton resumeButton = new TextButton("Resume (P)", skin);
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                resumeRequested = true;
            }
        });

        TextButton mainMenuButton = new TextButton("Main Menu", skin);
        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                mainMenuRequested = true;
            }
        });

        panel.row();
        panel.add(resumeButton).colspan(3).width(240f).height(56f).padTop(20f).center();
        panel.row();
        panel.add(mainMenuButton).colspan(3).width(240f).height(56f).padTop(10f).center();
    }

    private void addSliderRow(Table panel, String labelText, Slider slider, Label valueLabel) {
        Label label = new Label(labelText, skin);
        panel.add(label).left().width(170f).padBottom(14f);
        panel.add(slider).growX().height(24f).padLeft(12f).padRight(12f).padBottom(14f);
        panel.add(valueLabel).right().width(60f).padBottom(14f);
        panel.row();
    }

    private void wireSliderListeners() {
        masterSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = masterSlider.getValue();
                gameMaster.getSoundManager().setMasterVolume(value);
                masterValueLabel.setText(toPercent(value));
            }
        });

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = musicSlider.getValue();
                gameMaster.getSoundManager().setMusicVolume(value);
                musicValueLabel.setText(toPercent(value));
            }
        });

        sfxSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float value = sfxSlider.getValue();
                gameMaster.getSoundManager().setSFXVolume(value);
                sfxValueLabel.setText(toPercent(value));
            }
        });
    }

    private String toPercent(float value) {
        return Math.round(value * 100f) + "%";
    }

    private void refreshVectorModeButtonText() {
        if (vectorModeButton == null) {
            return;
        }
        if (SandboxConfig.showVelocityVectors) {
            vectorModeButton.setText("Velocity Vectors: ON");
        } else {
            vectorModeButton.setText("Velocity Vectors: OFF");
        }
    }

    private void refreshCollisionModeButtonText() {
        if (collisionModeButton == null) {
            return;
        }
        if (SandboxConfig.useMergeCollision) {
            collisionModeButton.setText("Collision Mode: Merge");
        } else {
            collisionModeButton.setText("Collision Mode: Mutual Destruction");
        }
    }

    private void applyCollisionResolverFromConfig() {
        if (SandboxConfig.useMergeCollision) {
            gameMaster.getCollisionManager().setResolver(mergeCollisionResolver);
        } else {
            gameMaster.getCollisionManager().setResolver(mutualDestructionResolver);
        }
    }

    private void createSkin() {
        skin = new Skin();
        font = new BitmapFont();

        overlayTexture = createSolidTexture(1, 1, 0f, 0f, 0f, 0.65f);
        panelTexture = createSolidTexture(1, 1, 0.12f, 0.14f, 0.18f, 0.95f);
        buttonUpTexture = createSolidTexture(1, 1, 0.18f, 0.28f, 0.45f, 1f);
        buttonOverTexture = createSolidTexture(1, 1, 0.22f, 0.36f, 0.56f, 1f);
        buttonDownTexture = createSolidTexture(1, 1, 0.14f, 0.22f, 0.36f, 1f);
        sliderTrackTexture = createSolidTexture(1, 1, 0.18f, 0.20f, 0.24f, 1f);
        sliderFillTexture = createSolidTexture(1, 1, 0.24f, 0.67f, 0.39f, 1f);
        sliderKnobTexture = createSolidTexture(1, 1, 0.94f, 0.95f, 0.98f, 1f);

        skin.add("default-font", font);
        skin.add("overlay-bg", new TextureRegionDrawable(new TextureRegion(overlayTexture)), Drawable.class);
        skin.add("panel-bg", new TextureRegionDrawable(new TextureRegion(panelTexture)), Drawable.class);
        skin.add("slider-bg", new TextureRegionDrawable(new TextureRegion(sliderTrackTexture)), Drawable.class);
        skin.add("slider-fill", new TextureRegionDrawable(new TextureRegion(sliderFillTexture)), Drawable.class);
        skin.add("slider-knob", new TextureRegionDrawable(new TextureRegion(sliderKnobTexture)), Drawable.class);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonUpTexture));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(buttonOverTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonDownTexture));
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        skin.add("default", buttonStyle);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = font;
        titleStyle.fontColor = new Color(0.95f, 0.93f, 0.82f, 1f);
        skin.add("title", titleStyle);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin.getDrawable("slider-bg");
        sliderStyle.knobBefore = skin.getDrawable("slider-fill");
        sliderStyle.knob = skin.getDrawable("slider-knob");
        skin.add("default-horizontal", sliderStyle);
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
