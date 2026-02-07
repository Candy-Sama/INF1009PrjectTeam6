package com.team6.arcadesim.components;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.ecs.Component;

public class TransformComponent implements Component {
    private Vector2 position;
    private float rotation;

    public TransformComponent(float x, float y) {
        this.position = new Vector2(x, y);
        this.rotation = 0;
    }

    public void setPosition(Vector2 position) { 
        this.position.set(position); 
    }

    public Vector2 getPosition() { 
        return position; 
    }

    public void setRotation(float rotation) {
         this.rotation = rotation; 
    }

    public float getRotation() { 
        return rotation; 
    }
}
