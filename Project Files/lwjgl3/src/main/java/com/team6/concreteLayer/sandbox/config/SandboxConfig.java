package com.team6.concreteLayer.sandbox.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SandboxConfig {

    private SandboxConfig() {
    }

    public static final float DEFAULT_RADIUS = 20f;
    public static final float MIN_RADIUS = 2f;
    public static final float MAX_RADIUS = 120f;

    public static final float DEFAULT_MASS = 150f;
    public static final float MIN_MASS = 1f;
    public static final float MAX_MASS = 50_000f;

    public static final float DEFAULT_VELOCITY = 0f;
    public static final float MIN_VELOCITY = -2_000f;
    public static final float MAX_VELOCITY = 2_000f;

    public static final int MAX_ACTIVE_BODIES = 500;

    public static final int PREDICTION_STEPS = 1000;
    public static final int PREDICTION_SAMPLE_INTERVAL = 8;
    public static final int MAX_PREDICTION_BODIES = 500;

    public static final float CAMERA_MIN_ZOOM = 0.25f;
    public static final float CAMERA_MAX_ZOOM = 4.0f;
    public static final float CAMERA_ZOOM_STEP = 0.10f;
    public static final float VELOCITY_VECTOR_SCALE = 0.5f;
    public static final String SPRITESHEET_PATH = "sprite/spritesheet.png";
    public static final int SPRITE_CELL_SIZE = 64;
    public static final List<SpriteCell> STAR_SPRITE_CELLS = Collections.unmodifiableList(Arrays.asList(
        new SpriteCell(0, 0),
        new SpriteCell(0, 1),
        new SpriteCell(0, 2),
        new SpriteCell(0, 3)
    ));
    public static final List<SpriteCell> PLANET_SPRITE_CELLS = Collections.unmodifiableList(Arrays.asList(
        new SpriteCell(1, 0), 
        new SpriteCell(1, 1), 
        new SpriteCell(1, 2), 
        new SpriteCell(1, 3)

    ));

    public static final class SpriteCell {
        private final int row;
        private final int col;

        public SpriteCell(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    public static float clampRadius(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return DEFAULT_RADIUS;
        }
        return clamp(value, MIN_RADIUS, MAX_RADIUS);
    }

    public static float clampMass(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return DEFAULT_MASS;
        }
        return clamp(value, MIN_MASS, MAX_MASS);
    }

    public static float clampVelocity(float value) {
        if (Float.isNaN(value) || Float.isInfinite(value)) {
            return DEFAULT_VELOCITY;
        }
        return clamp(value, MIN_VELOCITY, MAX_VELOCITY);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
