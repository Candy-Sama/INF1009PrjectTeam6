package com.team6.arcadesim.sandbox.simulation;

import com.badlogic.gdx.math.Vector2;

public class NBodyState {

    private final int id;
    private final Vector2 position;
    private final Vector2 velocity;
    private float mass;
    private boolean movable;

    public NBodyState(int id, Vector2 position, Vector2 velocity, float mass, boolean movable) {
        this.id = id;
        this.position = new Vector2(position);
        this.velocity = new Vector2(velocity);
        this.mass = mass;
        this.movable = movable;
    }

    public int getId() {
        return id;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public NBodyState copy() {
        return new NBodyState(id, position, velocity, mass, movable);
    }
}
