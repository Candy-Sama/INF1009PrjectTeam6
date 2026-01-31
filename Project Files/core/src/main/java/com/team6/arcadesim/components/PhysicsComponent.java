package com.team6.arcadesim.components;

import com.team6.arcadesim.ecs.Component;

public class PhysicsComponent extends Component {

    private float velocityX;
    private float velocityY;

    public PhysicsComponent() {
        this.velocityX = 0.0f;
        this.velocityY = 0.0f;
    }

    public PhysicsComponent(float velocityX, float velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    @Override // It will not have any changes
    public void update(float deltaTime) {
    }

    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }
}
