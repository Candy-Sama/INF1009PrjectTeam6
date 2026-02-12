package com.team6.arcadesim.components;

import com.badlogic.gdx.graphics.Texture;
import com.team6.arcadesim.ecs.Component;

public class SpriteComponent implements Component {
    // Attributes from UML
    private Texture texture;
    private float width;
    private float height;
    private boolean flipX = false;
    private boolean flipY = false;
    private String texturePath;

    public SpriteComponent(Texture texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    public SpriteComponent(String texturePath, float width, float height) {
        this.texturePath = texturePath;
        this.width = width;
        this.height = height;

        // Create a 1x1 white pixel texture programmatically
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        pixmap.fill();
        this.texture = new com.badlogic.gdx.graphics.Texture(pixmap);
        pixmap.dispose();
    }

    // --- Operations from UML ---
    public String getTexturePath() { return texturePath; }

    public void setTexture(Texture t) { this.texture = t; }
    public Texture getTexture() { return texture; }

    public void setSize(float w, float h) {
        this.width = w;
        this.height = h;
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    // Logic for flipping (useful for Tetris rotations or Space Invaders)
    public void setFlip(boolean x, boolean y) {
        this.flipX = x;
        this.flipY = y;
    }

    public boolean isFlipX() { return flipX; }
    public boolean isFlipY() { return flipY; }
}