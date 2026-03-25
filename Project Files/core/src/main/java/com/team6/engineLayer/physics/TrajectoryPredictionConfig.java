package com.team6.arcadesim.physics;

import com.team6.arcadesim.config.EngineTimingConfig;

public class TrajectoryPredictionConfig {

    public static final int DEFAULT_PREDICTION_STEPS = 300;
    public static final int DEFAULT_SAMPLE_INTERVAL = 1;
    public static final int DEFAULT_MAX_BODIES = 500;
    public static final int DEFAULT_MAX_PATH_POINTS = 600;

    private int predictionSteps;
    private int sampleInterval;
    private int maxBodies;
    private int maxPathPoints;
    private float fixedTimeStep;

    public TrajectoryPredictionConfig() {
        this(
            DEFAULT_PREDICTION_STEPS,
            DEFAULT_SAMPLE_INTERVAL,
            DEFAULT_MAX_BODIES,
            DEFAULT_MAX_PATH_POINTS,
            EngineTimingConfig.DEFAULT_FIXED_TIME_STEP
        );
    }

    public TrajectoryPredictionConfig(int predictionSteps, int sampleInterval, int maxBodies, int maxPathPoints, float fixedTimeStep) {
        setPredictionSteps(predictionSteps);
        setSampleInterval(sampleInterval);
        setMaxBodies(maxBodies);
        setMaxPathPoints(maxPathPoints);
        setFixedTimeStep(fixedTimeStep);
    }

    public int getPredictionSteps() {
        return predictionSteps;
    }

    public void setPredictionSteps(int predictionSteps) {
        this.predictionSteps = predictionSteps <= 0 ? DEFAULT_PREDICTION_STEPS : predictionSteps;
    }

    public int getSampleInterval() {
        return sampleInterval;
    }

    public void setSampleInterval(int sampleInterval) {
        this.sampleInterval = sampleInterval <= 0 ? DEFAULT_SAMPLE_INTERVAL : sampleInterval;
    }

    public int getMaxBodies() {
        return maxBodies;
    }

    public void setMaxBodies(int maxBodies) {
        this.maxBodies = maxBodies <= 0 ? DEFAULT_MAX_BODIES : maxBodies;
    }

    public int getMaxPathPoints() {
        return maxPathPoints;
    }

    public void setMaxPathPoints(int maxPathPoints) {
        this.maxPathPoints = maxPathPoints <= 0 ? DEFAULT_MAX_PATH_POINTS : maxPathPoints;
    }

    public float getFixedTimeStep() {
        return fixedTimeStep;
    }

    public void setFixedTimeStep(float fixedTimeStep) {
        this.fixedTimeStep = fixedTimeStep <= 0f ? EngineTimingConfig.DEFAULT_FIXED_TIME_STEP : fixedTimeStep;
    }

    public void useEngineTiming(EngineTimingConfig engineTimingConfig) {
        if (engineTimingConfig == null) {
            return;
        }
        setFixedTimeStep(engineTimingConfig.getFixedTimeStep());
    }
}
