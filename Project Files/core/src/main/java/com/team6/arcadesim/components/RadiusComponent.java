package com.team6.arcadesim.components;

import com.team6.arcadesim.ecs.Component;

public class RadiusComponent implements Component {
    private float radius;

    public RadiusComponent(float radius) {
        this.radius = radius;
    }

    public float getRadius() { return radius; }
    public void setRadius(float radius) { this.radius = radius; }
}
