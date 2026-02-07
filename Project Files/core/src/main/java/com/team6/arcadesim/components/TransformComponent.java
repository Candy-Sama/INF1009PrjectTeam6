package com.team6.arcadesim.components;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.ecs.Component;

public class TransformComponent implements Component {
    private Vector2 position;
    private Vector2 rotation;

    public void setPosition(Vector2 p) {

    }

    public Vector2 getPosition() {
        return position;
    }

    public void setRotation(Vector2 r) {

    }

    public Vector2 getRotation() {
        return rotation;
    }
}
