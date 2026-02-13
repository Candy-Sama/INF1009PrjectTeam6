package com.team6.arcadesim.Demoscene.Scenes;

import com.team6.arcadesim.Demoscene.GameMaster;
import com.team6.arcadesim.scenes.AbstractScene;


public class PauseScene extends AbstractScene {

    private GameMaster gameMaster;

    // Constructor
    public PauseScene(GameMaster gameMaster) {
        super(gameMaster, "PauseScene");
    }

    @Override
    public void onEnter() {
        System.out.println("Game Paused. Press P to Resume.");
    }
    @Override
    public void onExit() {
        System.out.println("Exiting Pause Scene.");
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