package com.team6.arcadesim.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewportManager {
    // Fields from your UML
    private OrthographicCamera camera;
    private Viewport viewport;
    private int baseResolutionWidth = 800;
    private int baseResolutionHeight = 600;

    public ViewportManager() {
        // Initialize the camera and a FitViewport to maintain aspect ratio [cite: 25]
        camera = new OrthographicCamera();
        viewport = new FitViewport(baseResolutionWidth, baseResolutionHeight, camera);
        camera.position.set(baseResolutionWidth / 2f, baseResolutionHeight / 2f, 0);
        camera.update();
    }


    public void resize(int w, int h) {
        // Update viewport with new virtual resolution to be called from AbstractGameMaster.resize()
        viewport.update (w, h, true);
        camera.update();
    }

    public void apply() {
        viewport.apply(); // Standard libGDX call to set the GL viewport size via the Viewport
        camera.update();
    }

    public Vector2 screenToWorld(Vector2 screenCoords) {
        // Converts mouse clicks to in-game coordinates
        return viewport.unproject(screenCoords); 
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}