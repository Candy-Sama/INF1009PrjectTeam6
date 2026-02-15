package com.team6.arcadesim.Demoscene;

import com.badlogic.gdx.utils.ScreenUtils;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.Demoscene.Scenes.DemoGravity;

public class GameMaster extends AbstractGameMaster {

    @Override
    public void init() {
        // Start the demo scene
        sceneManager.setScene(new DemoGravity(this));
    }

    @Override
    public void update(float dt) {
        // Global logic (if any) goes here. 
        // We do NOT need to call sceneManager.update(dt) here manually, 
        // because AbstractGameMaster.render() does it automatically!
    }

    @Override
    public void render() {
        // Clear the screen (Blue-ish background)
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        
        // Call the parent to run the engine loop (Update -> Render Stack)
        super.render();
    }
}