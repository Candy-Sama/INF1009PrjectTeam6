package com.team6.spacesim.components;

import com.team6.spacesim.ecs.Component;

public class MassComponent implements Component {
    private float mass;

    public MassComponent(float mass) {
        this.mass = mass;
    }

    public float getMass() { return mass; }
    public void setMass(float mass) { this.mass = mass; }
}