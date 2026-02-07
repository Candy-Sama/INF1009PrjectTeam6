package com.team6.arcadesim.components;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.ecs.Component;

public class MovementComponent implements Component {
    private Vector2 velocity; 
    private Vector2 acceleration;

    public MovementComponent() {
        this.velocity = new Vector2(0, 0);
        this.acceleration = new Vector2(0, 0);
    }

    public void setVelocity(Vector2 velocity) { 
        this.velocity.set(velocity); 
    }

    public Vector2 getVelocity() { 
        return velocity; 
    }

    public void setAcceleration(Vector2 acceleration) { 
        this.acceleration.set(acceleration); 
    }

    public Vector2 getAcceleration() { 
        return acceleration; 
    }

    public void applyVelocity(Vector2 v) {
        this.velocity.add(v);
    }
}
