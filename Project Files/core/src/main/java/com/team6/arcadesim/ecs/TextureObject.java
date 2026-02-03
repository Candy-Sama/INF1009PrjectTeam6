package com.team6.arcadesim.ecs;

import com.badlogic.gdx.graphics.Texture;

public abstract class TextureObject extends Entity {
    private Texture texture;
    private float width, height;
    private boolean isPlayer;

    public TextureObject(int id, String name, boolean isActive, boolean isPlayer, Texture texture, float width, float height) {
        super(id, name);
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.setActive(isActive);
        this.setPlayer(isPlayer);
    }

    public Texture getTexture() {
        return texture;
    }
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float getWidth() {
        return width;
    }
    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }
    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isPlayer() {
        return isPlayer;
    }
    public void setPlayer(boolean player) {
        isPlayer = player;
    }
}