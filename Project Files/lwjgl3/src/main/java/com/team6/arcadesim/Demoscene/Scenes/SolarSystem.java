package com.team6.arcadesim.Demoscene.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.team6.arcadesim.scenes.AbstractPlayableScene;

public class SolarSystem extends AbstractPlayableScene {

    public SolarSystem() {
        super(null, "SolarSystem");
    }

    @Override
    public void onEnter() {
        System.out.println("Entering Solar System Scene...");
        // Setup a 1280x720 camera view
        gameMaster.getViewportManager().setVirtualResolution(1280, 720);

        
    }

    @Override
    public void onExit() {
        System.out.println("Exiting Solar System Scene...");
    }

    @Override
    public void render(float dt) {
        // No rendering logic for this demo scene
    }

    @Override
    protected void processLevelLogic(float dt) {
                // Cap the delta time to prevent physics explosions if the window is dragged
        if (dt > 0.05f) dt = 0.05f;

        // Test Pause Overlay
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            System.out.println("Requesting Pause Overlay...");
            gameMaster.getSceneManager().pushScene("pause");
            return;
        }
    }


    // Create sun

    // Create planets with different orbits and speeds

    // Check for player input to spawn new planets or pause/reset the scene
    
    
}
