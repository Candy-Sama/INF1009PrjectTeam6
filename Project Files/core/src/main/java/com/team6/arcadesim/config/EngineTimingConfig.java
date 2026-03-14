package com.team6.arcadesim.config;

public class EngineTimingConfig {

    public static final float DEFAULT_FIXED_TIME_STEP = 1f / 60f;
    public static final float DEFAULT_MAX_FRAME_TIME = 0.1f;
    public static final int DEFAULT_MAX_SIMULATION_STEPS = 5;

    private float fixedTimeStep;
    private float maxFrameTime;
    private int maxSimulationSteps;

    public EngineTimingConfig() {
        this(DEFAULT_FIXED_TIME_STEP, DEFAULT_MAX_FRAME_TIME, DEFAULT_MAX_SIMULATION_STEPS);
    }

    public EngineTimingConfig(float fixedTimeStep, float maxFrameTime, int maxSimulationSteps) {
        setFixedTimeStep(fixedTimeStep);
        setMaxFrameTime(maxFrameTime);
        setMaxSimulationSteps(maxSimulationSteps);
    }

    public float getFixedTimeStep() {
        return fixedTimeStep;
    }

    public void setFixedTimeStep(float fixedTimeStep) {
        this.fixedTimeStep = fixedTimeStep <= 0f ? DEFAULT_FIXED_TIME_STEP : fixedTimeStep;
    }

    public float getMaxFrameTime() {
        return maxFrameTime;
    }

    public void setMaxFrameTime(float maxFrameTime) {
        this.maxFrameTime = maxFrameTime <= 0f ? DEFAULT_MAX_FRAME_TIME : maxFrameTime;
    }

    public int getMaxSimulationSteps() {
        return maxSimulationSteps;
    }

    public void setMaxSimulationSteps(int maxSimulationSteps) {
        this.maxSimulationSteps = maxSimulationSteps <= 0 ? DEFAULT_MAX_SIMULATION_STEPS : maxSimulationSteps;
    }
}
