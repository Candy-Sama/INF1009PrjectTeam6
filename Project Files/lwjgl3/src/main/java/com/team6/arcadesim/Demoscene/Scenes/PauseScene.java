package com.team6.arcadesim.Demoscene.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.UI.VolumeSlider;
import com.team6.arcadesim.scenes.AbstractScene;

public class PauseScene extends AbstractScene {

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch spriteBatch;
    private boolean firstFrame = true;
    
    // Volume slider properties
    private static final float SLIDER_WIDTH = 200f;
    private static final float SLIDER_HEIGHT = 10f;
    private static final float SLIDER_SPACING = 60f;
    private VolumeSlider masterSlider;
    private VolumeSlider musicSlider;
    private VolumeSlider sfxSlider;
    private int draggingSlider = -1; // -1: none, 0: master, 1: music, 2: sfx

    public PauseScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "PauseScene");
    }

    @Override
    public void onEnter() {
        System.out.println("Game Paused.");
        
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        spriteBatch = new SpriteBatch();

        gameMaster.getSoundManager().pauseMusic();
        firstFrame = true;
        
        // Initialize sliders with current volume values
        float centerX = gameMaster.getViewportManager().getCamera().viewportWidth / 2 - SLIDER_WIDTH / 2;
        float startY = gameMaster.getViewportManager().getCamera().viewportHeight / 2 + 80;
        
        masterSlider = new VolumeSlider(centerX, startY, SLIDER_WIDTH, SLIDER_HEIGHT, 
                                       "Master Volume", gameMaster.getSoundManager().getMasterVolume());
        musicSlider = new VolumeSlider(centerX, startY - SLIDER_SPACING, SLIDER_WIDTH, SLIDER_HEIGHT,
                                      "Music Volume", gameMaster.getSoundManager().getMusicVolume());
        sfxSlider = new VolumeSlider(centerX, startY - SLIDER_SPACING * 2, SLIDER_WIDTH, SLIDER_HEIGHT,
                                    "SFX Volume", gameMaster.getSoundManager().getSFXVolume());
    }

    @Override
    public void update(float dt) {
        if (firstFrame) {
            firstFrame = false;
            return;
        }
        
        // Use Global Input Manager
        if (gameMaster.getInputManager().isKeyJustPressed(Input.Keys.P)) {
            gameMaster.getSceneManager().popScene();
            return;
        }
        
        // Handle mouse input for sliders
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            gameMaster.getViewportManager().getCamera().unproject(touchPos);
            
            if (masterSlider.isMouseOver(touchPos.x, touchPos.y)) {
                draggingSlider = 0;
            } else if (musicSlider.isMouseOver(touchPos.x, touchPos.y)) {
                draggingSlider = 1;
            } else if (sfxSlider.isMouseOver(touchPos.x, touchPos.y)) {
                draggingSlider = 2;
            }
        }
        
        // Update slider values while dragging
        if (Gdx.input.isTouched() && draggingSlider != -1) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            gameMaster.getViewportManager().getCamera().unproject(touchPos);
            
            switch (draggingSlider) {
                case 0:
                    masterSlider.updateValue(touchPos.x);
                    gameMaster.getSoundManager().setMasterVolume(masterSlider.getValue());
                    break;
                case 1:
                    musicSlider.updateValue(touchPos.x);
                    gameMaster.getSoundManager().setMusicVolume(musicSlider.getValue());
                    break;
                case 2:
                    sfxSlider.updateValue(touchPos.x);
                    gameMaster.getSoundManager().setSFXVolume(sfxSlider.getValue());
                    // Play a test sound effect when adjusting SFX volume
                    if (Math.random() < 0.05) { // Play occasionally during drag
                        gameMaster.getSoundManager().playSFX("boop");
                    }
                    break;
            }
        }
        
        // Stop dragging when mouse released
        if (!Gdx.input.isTouched()) {
            draggingSlider = -1;
        }
    }

    @Override
    public void render(float dt) {
        // Transparency Setup
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw Overlay (Black 70%)
        shapeRenderer.setProjectionMatrix(gameMaster.getViewportManager().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.7f));
        
        // Fill the whole camera view
        float w = gameMaster.getViewportManager().getCamera().viewportWidth;
        float h = gameMaster.getViewportManager().getCamera().viewportHeight;
        shapeRenderer.rect(0, 0, w, h);
        shapeRenderer.end();

        // Draw Text
        spriteBatch.setProjectionMatrix(gameMaster.getViewportManager().getCamera().combined);
        spriteBatch.begin();
        
        String pauseText = "Game Paused";
        GlyphLayout layout = new GlyphLayout(font, pauseText);
        
        // Center pause text at top
        float pauseX = (w - layout.width) / 2;
        float pauseY = h - 40;

        font.getData().setScale(2f);
        font.draw(spriteBatch, pauseText, pauseX, pauseY);
        font.getData().setScale(1.5f);
        spriteBatch.end();
        
        // Draw volume sliders
        shapeRenderer.setProjectionMatrix(gameMaster.getViewportManager().getCamera().combined);
        spriteBatch.setProjectionMatrix(gameMaster.getViewportManager().getCamera().combined);
        
        masterSlider.draw(shapeRenderer, font, spriteBatch);
        musicSlider.draw(shapeRenderer, font, spriteBatch);
        sfxSlider.draw(shapeRenderer, font, spriteBatch);
        
        // Draw instructions at bottom
        spriteBatch.begin();
        String instructions = "Press P to Resume";
        GlyphLayout instrLayout = new GlyphLayout(font, instructions);
        float instrX = (w - instrLayout.width) / 2;
        font.draw(spriteBatch, instructions, instrX, 40);
        spriteBatch.end();

        // Disable blending after renderingop
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void onExit() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (spriteBatch != null) spriteBatch.dispose();

        gameMaster.getSoundManager().resumeMusic();
    }
}