package com.team6.arcadesim.scenes;

import com.team6.arcadesim.GameMaster;

public class DemoScene extends AbstractScene {

    private static final String SCENE_NAME = "DemoScene";
    private GameMaster gameMaster;

    public DemoScene(GameMaster gameMaster) {
        super(gameMaster, SCENE_NAME);
        this.gameMaster = gameMaster;
    }

    @Override
    public void onEnter() {
        // Initialize scene resources, entities, etc.
        System.out.println("Entering " + SCENE_NAME);
    }

    @Override
    public void onExit() {
        // Cleanup scene resources, entities, etc.
        System.out.println("Exiting " + SCENE_NAME);
    }

    @Override
    public void dispose() {
        // Dispose of any resources specific to this scene      
        System.out.println("Disposing " + SCENE_NAME);
    }

    @Override
    public void update(float deltaTime) {
        // Update scene logic
    }   

    @Override
    public void render(float deltaTime) {
        // Render scene
    }

}
