package com.team6.arcadesim.components;

import com.team6.arcadesim.ecs.Component;

public class MassComponent implements Component {
    private float mass;

    public MassComponent(float mass) {
        this.mass = mass;
    }

    public float getMass() { return mass; }
    public void setMass(float mass) { this.mass = mass; }
}