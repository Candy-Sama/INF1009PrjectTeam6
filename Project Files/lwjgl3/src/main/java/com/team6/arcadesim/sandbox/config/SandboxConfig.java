package com.team6.arcadesim.sandbox.config;

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

    public static final int PREDICTION_STEPS = 500;
    public static final int PREDICTION_SAMPLE_INTERVAL = 8;
    public static final int MAX_PREDICTION_BODIES = 500;

    public static final float CAMERA_MIN_ZOOM = 0.25f;
    public static final float CAMERA_MAX_ZOOM = 4.0f;
    public static final float CAMERA_ZOOM_STEP = 0.10f;

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
