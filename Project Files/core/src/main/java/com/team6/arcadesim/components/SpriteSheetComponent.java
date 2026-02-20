package com.team6.arcadesim.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.team6.arcadesim.ecs.Component;

public class SpriteSheetComponent implements Component {

    private TextureRegion region; // Better than Texture, supports sprite sheets
    private float width;
    private float height;
    private boolean flipX;
    private boolean flipY;

    // Default constructor
    public SpriteSheetComponent(Texture texture) {
        this(texture, texture.getWidth(), texture.getHeight());
    }

    public SpriteSheetComponent(Texture texture, float width, float height) {
        this.region = new TextureRegion(texture);
        this.width = width;
        this.height = height;
        this.flipX = false;
        this.flipY = false;
    }

    public TextureRegion getRegion() { return region; }
    
    public void setTexture(Texture texture) {
        this.region.setRegion(texture);
    }

    public float getWidth() { return width; }
    public void setWidth(float width) { this.width = width; }

    public float getHeight() { return height; }
    public void setHeight(float height) { this.height = height; }

    public boolean isFlipX() { return flipX; }
    public void setFlipX(boolean flipX) { this.flipX = flipX; }

    public boolean isFlipY() { return flipY; }
    public void setFlipY(boolean flipY) { this.flipY = flipY; }
}
