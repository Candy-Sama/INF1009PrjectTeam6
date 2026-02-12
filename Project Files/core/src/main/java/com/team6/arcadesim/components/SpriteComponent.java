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

    public SpriteComponent(Texture texture, float width, float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }

    // --- Operations from UML ---

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