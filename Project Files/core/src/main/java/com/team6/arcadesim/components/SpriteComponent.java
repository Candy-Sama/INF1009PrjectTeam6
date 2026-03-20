package com.team6.arcadesim.components;

import com.team6.arcadesim.ecs.Component;

public class SpriteComponent implements Component {
    private String spriteId;
    private float width;
    private float height;
    private boolean flipX;
    private boolean flipY;

    public SpriteComponent(String spriteId, float width, float height) {
        this.spriteId = spriteId;
        this.width = width;
        this.height = height;
        this.flipX = false;
        this.flipY = false;
    }

    public String getSpriteId() {
        return spriteId;
    }

<<<<<<< HEAD
    public void setSpriteId(String spriteId) {
        this.spriteId = spriteId;
    }
=======
    // --- Operations from UML ---
    public String getTexturePath() { return texturePath; }

    public void setTexture(Texture t) { this.texture = t; }
    public Texture getTexture() { return texture; }
>>>>>>> main

    public void setSize(float w, float h) {
        this.width = w;
        this.height = h;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setFlip(boolean x, boolean y) {
        this.flipX = x;
        this.flipY = y;
    }

    public boolean isFlipX() {
        return flipX;
    }

    public boolean isFlipY() {
        return flipY;
    }
}
