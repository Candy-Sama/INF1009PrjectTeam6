package com.team6.engineLayer.physics;

public class GravityConfig {

    public static final float DEFAULT_GRAVITY_CONSTANT = 5000f;
    public static final float DEFAULT_MIN_DISTANCE_SQ = 2500f;

    private float gravityConstant;
    private float minDistanceSq;

    public GravityConfig() {
        this(DEFAULT_GRAVITY_CONSTANT, DEFAULT_MIN_DISTANCE_SQ);
    }

    public GravityConfig(float gravityConstant, float minDistanceSq) {
        setGravityConstant(gravityConstant);
        setMinDistanceSq(minDistanceSq);
    }

    public float getGravityConstant() {
        return gravityConstant;
    }

    public void setGravityConstant(float gravityConstant) {
        this.gravityConstant = gravityConstant <= 0f ? DEFAULT_GRAVITY_CONSTANT : gravityConstant;
    }

    public float getMinDistanceSq() {
        return minDistanceSq;
    }

    public void setMinDistanceSq(float minDistanceSq) {
        this.minDistanceSq = minDistanceSq <= 0f ? DEFAULT_MIN_DISTANCE_SQ : minDistanceSq;
    }
}
