package com.team6.arcadesim.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Rectangle;

public class ViewportManager {

    // --- LibGDX Internal Components ---
    private OrthographicCamera camera;
    private Viewport viewport;

    // --- State Variables from UML ---
    private int baseResolutionWidth;
    private int baseResolutionHeight;
    private int screenWidth;
    private int screenHeight;
    
    // Reusable vector to prevent garbage collection on every mouse click
    private Vector2 tempVector = new Vector2();

    public ViewportManager() {
        // Default Arcade Resolution (Example: 800x600 or 1920x1080)
        // You can change this via setVirtualResolution
        this.baseResolutionWidth = 1280;
        this.baseResolutionHeight = 720;

        // Initialize Camera (The "Eye")
        camera = new OrthographicCamera();
        
        // Initialize Viewport (The "Lens" that handles aspect ratio)
        // FitViewport maintains aspect ratio by adding black bars (Letterboxing)
        viewport = new FitViewport(baseResolutionWidth, baseResolutionHeight, camera);
        
        // Center the camera by default
        camera.position.set(baseResolutionWidth / 2f, baseResolutionHeight / 2f, 0);
        camera.update();
    }

    // --- Configuration ---

    public void setVirtualResolution(int w, int h) {
        this.baseResolutionWidth = w;
        this.baseResolutionHeight = h;
        // Re-create viewport with new logical size
        viewport.setWorldSize(w, h);
        viewport.update(screenWidth, screenHeight, true);
    }

    /**
     * Called by AbstractGameMaster.resize() whenever the window changes size.
     */
    public void resize(int screenW, int screenH) {
        this.screenWidth = screenW;
        this.screenHeight = screenH;
        
        // Tell LibGDX to update the viewport calculations
        // The 'true' parameter centers the camera
        viewport.update(screenW, screenH, true);
    }

    public void apply() {
        viewport.apply();
        // Set the projection matrix for the RenderManager's batch if needed
        // (Usually handled in the RenderManager by calling batch.setProjectionMatrix(camera.combined))
    }
    
    // --- Camera Control ---

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

    // --- Coordinate Conversion (Crucial for Input) ---

    /**
     * Converts Mouse Coordinates (Pixels) -> Game World Coordinates
     * Usage: Checking if the mouse clicked a button or enemy.
     */
    public Vector2 screenToWorld(Vector2 screenCoords) {
        // unproject: Screen -> World
        tempVector.set(screenCoords);
        viewport.unproject(tempVector);
        return new Vector2(tempVector.x, tempVector.y); // Return copy or use temp if careful
    }
    
    public Vector2 screenToWorld(float x, float y) {
        tempVector.set(x, y);
        viewport.unproject(tempVector);
        return new Vector2(tempVector.x, tempVector.y);
    }

    /**
     * Converts Game World Coordinates -> Screen Pixels
     * Usage: Positioning a floating UI label above a player's head.
     */
    public Vector2 worldToScreen(Vector2 worldCoords) {
        // project: World -> Screen
        tempVector.set(worldCoords);
        viewport.project(tempVector);
        return new Vector2(tempVector.x, tempVector.y);
    }

    // --- Getters ---

    public Rectangle getViewBounds() {
        // Returns the rectangle of the world currently visible
        float w = camera.viewportWidth * camera.zoom;
        float h = camera.viewportHeight * camera.zoom;
        float x = camera.position.x - (w / 2);
        float y = camera.position.y - (h / 2);
        return new Rectangle(x, y, w, h);
    }

    public int getScale() {
        // Approximate scale factor (Screen Pixels per World Unit)
        return (int) (screenWidth / (float) baseResolutionWidth);
    }
}