package com.team6.arcadesim.components;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.ecs.Component;

public class MovementComponent implements Component {

    private Vector2 velocity;
    private Vector2 acceleration;
    
    // Optional: Max speed to prevent infinite acceleration glitches
    private float maxSpeed = 500f;

    public MovementComponent(float vx, float vy) {
        this.velocity = new Vector2(vx, vy);
        this.acceleration = new Vector2(0, 0);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(float x, float y) {
        this.velocity.set(x, y);
    }
    
    public void setVelocity(Vector2 v) {
        this.velocity.set(v);
    }

    public Vector2 getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(float x, float y) {
        this.acceleration.set(x, y);
    }
}