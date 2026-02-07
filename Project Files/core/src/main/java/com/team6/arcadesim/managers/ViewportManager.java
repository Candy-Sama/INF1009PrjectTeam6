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
    }

    // Operations from UML

    public void setVirtualResolution(int w, int h) {
        this.baseResolutionWidth = w;
        this.baseResolutionHeight = h;
        viewport.setWorldSize(w, h);
    }

    /** * Resizes the internal viewport when the window size changes.
     * This ensures the simulation scales correctly. 
     */
    public void resize(int screenW, int screenH) {
        viewport.update(screenW, screenH, true);
    }

    public void apply() {
        viewport.apply(); // Standard libGDX call to set the GL viewport
    }

    public Vector2 screenToWorld(Vector2 screenCoords) {
        // Converts mouse clicks to in-game coordinates
        return viewport.unproject(screenCoords); 
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}