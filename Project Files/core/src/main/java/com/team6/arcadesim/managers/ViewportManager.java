package com.team6.arcadesim.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewportManager {

    // --- LibGDX Internal Components ---
    private OrthographicCamera camera;
    private Viewport viewport;

    private int baseResolutionWidth;
    private int baseResolutionHeight;
    private int screenWidth;
    private int screenHeight;
    
    private Vector2 tempVector = new Vector2();

    public ViewportManager() {
        this.baseResolutionWidth = 1280;
        this.baseResolutionHeight = 720;

        camera = new OrthographicCamera();
        
        viewport = new FitViewport(baseResolutionWidth, baseResolutionHeight, camera);
        
        // Center the camera by default
        camera.position.set(baseResolutionWidth / 2f, baseResolutionHeight / 2f, 0);
        camera.update();
    }

    public void setVirtualResolution(int w, int h) {
        this.baseResolutionWidth = w;
        this.baseResolutionHeight = h;
        viewport.setWorldSize(w, h);
        viewport.update(screenWidth, screenHeight, true);
    }

    public void resize(int screenW, int screenH) {
        this.screenWidth = screenW;
        this.screenHeight = screenH;
        
        viewport.update(screenW, screenH, true);
    }

    public void apply() {
        viewport.apply();
        camera.update();
    }
    
    public void setCameraPosition(Vector2 position) {
        camera.position.set(position.x, position.y, 0);
        camera.update();
    }
    
    public void setCameraPosition(float x, float y) {
        camera.position.set(x, y, 0);
        camera.update();
    }
    
    public OrthographicCamera getCamera() {
        return camera;
    }

    public Vector2 screenToWorld(Vector2 screenCoords) {
        tempVector.set(screenCoords);
        viewport.unproject(tempVector);
        return new Vector2(tempVector.x, tempVector.y);
    }
    
    public Vector2 screenToWorld(float x, float y) {
        tempVector.set(x, y);
        viewport.unproject(tempVector);
        return new Vector2(tempVector.x, tempVector.y);
    }

    public Vector2 worldToScreen(Vector2 worldCoords) {
        tempVector.set(worldCoords);
        viewport.project(tempVector);
        return new Vector2(tempVector.x, tempVector.y);
    }

    public Rectangle getViewBounds() {
        float w = camera.viewportWidth * camera.zoom;
        float h = camera.viewportHeight * camera.zoom;
        float x = camera.position.x - (w / 2);
        float y = camera.position.y - (h / 2);
        return new Rectangle(x, y, w, h);
    }

    public int getScale() {
        return (int) (screenWidth / (float) baseResolutionWidth);
    }
}