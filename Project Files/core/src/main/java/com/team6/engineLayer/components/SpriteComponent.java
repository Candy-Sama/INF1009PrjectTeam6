package com.team6.engineLayer.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.team6.engineLayer.ecs.Component;

public class SpriteComponent implements Component {
    private TextureRegion region;

    // Legacy sprite-id path remains for backward compatibility with older demo scenes.
    private String spriteId;
    private float width;
    private float height;
    private boolean flipX;
    private boolean flipY;

    public SpriteComponent(Texture texture) {
        this.region = new TextureRegion(texture);
        this.spriteId = null;
        this.width = this.region.getRegionWidth();
        this.height = this.region.getRegionHeight();
        this.flipX = false;
        this.flipY = false;
    }

    public SpriteComponent(TextureRegion region) {
        this.region = region;
        this.spriteId = null;
        this.width = (region == null) ? 0f : region.getRegionWidth();
        this.height = (region == null) ? 0f : region.getRegionHeight();
        this.flipX = false;
        this.flipY = false;
    }

    public SpriteComponent(String spriteId, float width, float height) {
        this.region = null;
        this.spriteId = spriteId;
        this.width = width;
        this.height = height;
        this.flipX = false;
        this.flipY = false;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public void setRegion(TextureRegion region) {
        this.region = region;
    }

    public String getSpriteId() {
        return spriteId;
    }

    public void setSpriteId(String spriteId) {
        this.spriteId = spriteId;
    }

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
