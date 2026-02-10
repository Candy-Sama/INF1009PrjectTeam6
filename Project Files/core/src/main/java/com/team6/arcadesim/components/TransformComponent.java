package com.team6.arcadesim.components;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.ecs.Component;

public class TransformComponent implements Component {

    private Vector2 position;
    private float rotation; // In degrees

    public TransformComponent(float x, float y) {   
        this.position = new Vector2(x, y);
        this.rotation = 0f;
    }

    // Default constructor
    public TransformComponent() {
        this(0, 0);
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }
    
    public void setPosition(Vector2 pos) {
        this.position.set(pos);
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
}