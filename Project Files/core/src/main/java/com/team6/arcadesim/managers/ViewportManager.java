package com.team6.arcadesim.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ViewportManager {
    private OrthographicCamera camera;
    private int baseResolutionWidth, baseResolutionHeight, scale, screenWidth, screenHeight;

    public void setVirtualResolution(int width, int height) {
        this.baseResolutionWidth = width;
        this.baseResolutionHeight = height;
        updateViewport();
    }

    public void onResize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        updateViewport();
    }

    public void setCameraPosition(float x, float y) {
        camera.position.set(x, y, 0);
    }

    public Vector2 worldToScreen(Vector2 p) {
        // Convert world coordinates to screen coordinates
        float screenX = (p.x - camera.position.x) * scale + screenWidth / 2;
        float screenY = (p.y - camera.position.y) * scale + screenHeight / 2;
        return new Vector2(screenX, screenY);
    }

    public Vector2 screenToWorld(Vector2 p) {
        // Convert screen coordinates to world coordinates
        float worldX = (p.x - screenWidth / 2) / scale + camera.position.x;
        float worldY = (p.y - screenHeight / 2) / scale + camera.position.y;
        return new Vector2(worldX, worldY);
    }

    public Rectangle getViewBounds() {
        float halfWidth = (screenWidth / scale) / 2f;
        float halfHeight = (screenHeight / scale) / 2f;
        return new Rectangle(camera.position.x - halfWidth, camera.position.y - halfHeight,
                        camera.position.x + halfWidth, camera.position.y + halfHeight);
    }  

    public int getScale() {
        return scale;
    }

    public void apply() {
        // Apply the viewport transformation to the rendering context
        // This is a placeholder; actual implementation depends on the rendering library used
    }

    private void updateViewport() {
        if (baseResolutionWidth == 0 || baseResolutionHeight == 0 || screenWidth == 0 || screenHeight == 0) {
            scale = 1;
            return;
        }
        int scaleX = screenWidth / baseResolutionWidth;
        int scaleY = screenHeight / baseResolutionHeight;
        scale = Math.min(scaleX, scaleY);
        if (scale < 1) scale = 1; // Prevent downscaling below 1

        if (camera == null) {
            camera = new OrthographicCamera();
        }
        camera.setToOrtho(false, screenWidth / scale, screenHeight / scale);
        camera.update();
    }
}
