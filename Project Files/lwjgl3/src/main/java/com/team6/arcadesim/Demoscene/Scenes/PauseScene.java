package com.team6.arcadesim.Demoscene.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.team6.arcadesim.Demoscene.GameMaster;
import com.team6.arcadesim.scenes.AbstractScene;


public class PauseScene extends AbstractScene {

    private GameMaster gameMaster;

    //For drawing pause menu
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private SpriteBatch spriteBatch;

    private boolean firstFrame = true;

    // Constructor
    public PauseScene(GameMaster gameMaster) {
        super(gameMaster, "PauseScene");
        this.gameMaster = gameMaster;
    }

    @Override
    public void onEnter() {
        System.out.println("Game Paused. Press P to Resume.");

        //Initialize renderers and font
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2f); // Make the font larger
        spriteBatch = new SpriteBatch();

        //Pause the music
        gameMaster.getSoundManager().pauseMusic();
    }
    @Override
    public void onExit() {
        System.out.println("Exiting Pause Scene.");

        // Dispose of renderers and font
        if (shapeRenderer != null) {shapeRenderer.dispose();}
        if (font != null) {font.dispose();}
        if (spriteBatch != null) {spriteBatch.dispose();}

        //Resume the music
        gameMaster.getSoundManager().resumeMusic();
    }

    @Override
    public void update(float dt) {

        //Skip first frame to avoid instant unpausing
        if (firstFrame) {
            firstFrame = false;
            return;
        }
        
        //Check to see if player presses P.
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.P)) {
            //remove pause scene and return to demo scene
            gameMaster.getSceneManager().popScene();
        }


    }

    @Override
    public void render(float dt) {

        //To set tranparency
        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(
            com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA, 
            com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA
        );

        //Draw semi-transparent overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0, 0, 0.7f)); // Black wth 70% opacity
        shapeRenderer.rect(0, 0, com.badlogic.gdx.Gdx.graphics.getWidth(), com.badlogic.gdx.Gdx.graphics.getHeight());
        shapeRenderer.end();

        //Turn off transparency for text
        com.badlogic.gdx.Gdx.gl.glDisable(com.badlogic.gdx.graphics.GL20.GL_BLEND);


        spriteBatch.begin();
        String pauseText = "Game Paused\nPress P to Resume";

        //get Screensize
        float screenWidth = com.badlogic.gdx.Gdx.graphics.getWidth();
        float screenHeight = com.badlogic.gdx.Gdx.graphics.getHeight();

        //Measure text 
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, pauseText);

        //Calculate position to center text
        float x = (screenWidth - layout.width) / 2;
        float y = (screenHeight + layout.height) / 2;

        font.draw(spriteBatch, pauseText, x, y);
        

        spriteBatch.end();
    }
}