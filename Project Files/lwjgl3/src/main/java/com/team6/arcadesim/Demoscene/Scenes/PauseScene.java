package com.team6.arcadesim.Demoscene.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.team6.arcadesim.AbstractGameMaster; // Correct import
import com.team6.arcadesim.scenes.AbstractScene;

public class PauseScene extends AbstractScene {

    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch spriteBatch;
    private boolean firstFrame = true;

    public PauseScene(AbstractGameMaster gameMaster) {
        super(gameMaster, "PauseScene");
    }

    @Override
    public void onEnter() {
        System.out.println("Game Paused.");
        
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);
        spriteBatch = new SpriteBatch();

        gameMaster.getSoundManager().pauseMusic();
        firstFrame = true;
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
        // Note: Using camera viewport size ensures it covers the game world correctly
        float w = gameMaster.getViewportManager().getCamera().viewportWidth;
        float h = gameMaster.getViewportManager().getCamera().viewportHeight;
        shapeRenderer.rect(0, 0, w, h);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Draw Text
        spriteBatch.setProjectionMatrix(gameMaster.getViewportManager().getCamera().combined);
        spriteBatch.begin();
        
        String pauseText = "Game Paused\nPress P to Resume";
        GlyphLayout layout = new GlyphLayout(font, pauseText);
        
        // Center relative to camera
        float x = (w - layout.width) / 2;
        float y = (h + layout.height) / 2;

        font.draw(spriteBatch, pauseText, x, y);
        spriteBatch.end();
    }

    @Override
    public void onExit() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (spriteBatch != null) spriteBatch.dispose();

        gameMaster.getSoundManager().resumeMusic();
    }
}