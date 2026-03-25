package com.team6.arcadesim.physics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public class TrajectoryPredictionCache {

    private final NBodyTrajectoryPredictor predictor;
    private boolean dirty;
    private int recomputeCount;
    private int lastSignature;
    private Map<Integer, List<Vector2>> cachedPaths;

    public TrajectoryPredictionCache() {
        this(new NBodyTrajectoryPredictor());
    }

    public TrajectoryPredictionCache(NBodyTrajectoryPredictor predictor) {
        this.predictor = predictor;
        this.dirty = true;
        this.recomputeCount = 0;
        this.lastSignature = 0;
        this.cachedPaths = new LinkedHashMap<>();
    }

    public void markDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public int getRecomputeCount() {
        return recomputeCount;
    }

    public Map<Integer, List<Vector2>> getOrRecompute(List<NBodyState> bodies, GravityConfig gravityConfig, TrajectoryPredictionConfig predictionConfig) {
        int signature = computeSignature(bodies, gravityConfig, predictionConfig);
        if (dirty || signature != lastSignature) {
            cachedPaths = predictor.predictPaths(bodies, gravityConfig, predictionConfig);
            lastSignature = signature;
            dirty = false;
            recomputeCount++;
        }
        return deepCopy(cachedPaths);
    }

    private int computeSignature(List<NBodyState> bodies, GravityConfig gravityConfig, TrajectoryPredictionConfig predictionConfig) {
        int result = 17;
        result = 31 * result + Float.floatToIntBits(gravityConfig.getGravityConstant());
        result = 31 * result + Float.floatToIntBits(gravityConfig.getMinDistanceSq());
        result = 31 * result + predictionConfig.getPredictionSteps();
        result = 31 * result + predictionConfig.getSampleInterval();
        result = 31 * result + predictionConfig.getMaxBodies();
        result = 31 * result + predictionConfig.getMaxPathPoints();
        result = 31 * result + Float.floatToIntBits(predictionConfig.getFixedTimeStep());

        if (bodies != null) {
            int limit = Math.min(predictionConfig.getMaxBodies(), bodies.size());
            result = 31 * result + limit;
            for (int i = 0; i < limit; i++) {
                NBodyState b = bodies.get(i);
                result = 31 * result + b.getId();
                result = 31 * result + Float.floatToIntBits(b.getPosition().x);
                result = 31 * result + Float.floatToIntBits(b.getPosition().y);
                result = 31 * result + Float.floatToIntBits(b.getVelocity().x);
                result = 31 * result + Float.floatToIntBits(b.getVelocity().y);
                result = 31 * result + Float.floatToIntBits(b.getMass());
                result = 31 * result + (b.isMovable() ? 1 : 0);
            }
        }

        return result;
    }

    private Map<Integer, List<Vector2>> deepCopy(Map<Integer, List<Vector2>> source) {
        Map<Integer, List<Vector2>> copy = new LinkedHashMap<>();
        for (Map.Entry<Integer, List<Vector2>> entry : source.entrySet()) {
            List<Vector2> points = new ArrayList<>(entry.getValue().size());
            for (Vector2 point : entry.getValue()) {
                points.add(new Vector2(point));
            }
            copy.put(entry.getKey(), points);
        }
        return copy;
    }
}
