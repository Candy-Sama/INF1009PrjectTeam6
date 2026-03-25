package com.team6.arcadesim.sandbox.config;

public class SandboxRuntimeSettings {

    private boolean showVelocityVectors;
    private boolean useMergeCollision;

    public SandboxRuntimeSettings() {
        this.showVelocityVectors = false;
        this.useMergeCollision = false;
    }

    public boolean isShowVelocityVectors() {
        return showVelocityVectors;
    }

    public void setShowVelocityVectors(boolean showVelocityVectors) {
        this.showVelocityVectors = showVelocityVectors;
    }

    public void toggleShowVelocityVectors() {
        this.showVelocityVectors = !this.showVelocityVectors;
    }

    public boolean isUseMergeCollision() {
        return useMergeCollision;
    }

    public void setUseMergeCollision(boolean useMergeCollision) {
        this.useMergeCollision = useMergeCollision;
    }

    public void toggleUseMergeCollision() {
        this.useMergeCollision = !this.useMergeCollision;
    }
}
