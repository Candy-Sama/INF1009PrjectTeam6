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

    // Constructor
    public PauseScene(GameMaster gameMaster) {
        super(gameMaster, "PauseScene");
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
    }
    @Override
    public void onExit() {
        System.out.println("Exiting Pause Scene.");

        // Dispose of renderers and font
        if (shapeRenderer != null) {shapeRenderer.dispose();}
        if (font != null) {font.dispose();}
        if (spriteBatch != null) {spriteBatch.dispose();}
    }

    @Override
    public void update(float dt) {
        //Check to see if player presses P.
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.P)) {
            //remove pause scene and return to demo scene
            gameMaster.getSceneManager().popScene();
        }


    }

    @Override
    public void render(float dt) {
    }
}