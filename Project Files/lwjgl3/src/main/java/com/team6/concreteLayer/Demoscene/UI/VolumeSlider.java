package com.team6.arcadesim.Demoscene.UI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class VolumeSlider {
    private float x, y, width, height;
    private String label;
    private float value; // 0.0 to 1.0
    
    public VolumeSlider(float x, float y, float width, float height, String label, float initialValue) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.value = Math.max(0f, Math.min(1f, initialValue));
    }
    
    public boolean isMouseOver(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width && 
               mouseY >= y - height && mouseY <= y + height;
    }
    
    public void updateValue(float mouseX) {
        float relativeX = mouseX - x;
        value = Math.max(0f, Math.min(1f, relativeX / width));
    }
    
    public float getValue() {
        return value;
    }
    
    public void draw(ShapeRenderer sr, BitmapFont font, SpriteBatch batch) {
        // Draw slider track
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.DARK_GRAY);
        sr.rect(x, y, width, height);
        
        // Draw filled portion
        sr.setColor(Color.GREEN);
        sr.rect(x, y, width * value, height);
        sr.end();
        
        // Draw slider handle
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(Color.WHITE);
        float handleX = x + width * value;
        sr.circle(handleX, y + height / 2, height);
        sr.end();
        
        // Draw label and value
        batch.begin();
        font.draw(batch, label, x - 10, y + height + 25);
        font.draw(batch, String.format("%.0f%%", value * 100), x + width + 10, y + height + 5);
        batch.end();
    }
}
