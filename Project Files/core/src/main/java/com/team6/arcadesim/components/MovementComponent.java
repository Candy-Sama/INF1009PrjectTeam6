package com.team6.arcadesim.components;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.ecs.Component;

public class MovementComponent implements Component {
    private Vector2 velocity;
    private Vector2 acceleration;

    public void setVelocity(Vector2 v) {

    }

    public void applyVelocity(Vector2 v) {

    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setAcceleration(Vector2 a) {

    }

    public Vector2 getAcceleration() {
        return acceleration;
    }
}
