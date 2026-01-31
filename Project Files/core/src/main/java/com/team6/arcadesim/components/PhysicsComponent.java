package com.team6.arcadesim.components;

public class PhysicsComponent {

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
