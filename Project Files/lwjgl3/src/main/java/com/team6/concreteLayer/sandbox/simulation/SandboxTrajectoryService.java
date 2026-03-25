package com.team6.arcadesim.sandbox.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.config.EngineTimingConfig;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.sandbox.config.SandboxConfig;
import com.team6.arcadesim.physics.GravityConfig;

public class SandboxTrajectoryService {

    private final SandboxNBodyAdapter adapter;
    private Map<Integer, List<Vector2>> latestMovablePaths;
    private Map<Integer, List<Vector2>> readOnlyMovablePaths;
    private boolean dirty;

    public SandboxTrajectoryService() {
        this(new SandboxNBodyAdapter());
    }

    public SandboxTrajectoryService(SandboxNBodyAdapter adapter) {
        this.adapter = adapter;
        this.latestMovablePaths = new LinkedHashMap<>();
        this.readOnlyMovablePaths = Collections.emptyMap();
        this.dirty = true;
    }

    public void markDirty() {
        dirty = true;
    }

    public void clear() {
        latestMovablePaths.clear();
        readOnlyMovablePaths = Collections.emptyMap();
        dirty = true;
    }

    public void updatePredictions(
        List<Entity> entities,
        EngineTimingConfig engineTimingConfig,
        GravityConfig gravityConfig
    ) {
        if (!dirty) {
            return;
        }

        float fixedDt = (engineTimingConfig == null)
            ? EngineTimingConfig.DEFAULT_FIXED_TIME_STEP
            : engineTimingConfig.getFixedTimeStep();

        GravityConfig resolvedGravityConfig = (gravityConfig == null) ? new GravityConfig() : gravityConfig;
        List<NBodyState> sourceStates = adapter.toNBodyStates(entities);
        List<NBodyState> states = copyLimitedStates(sourceStates, SandboxConfig.MAX_PREDICTION_BODIES);
        Map<Integer, List<Vector2>> allPaths = runEulerPrediction(states, fixedDt, resolvedGravityConfig);

        latestMovablePaths = filterMovablePaths(allPaths, states);
        readOnlyMovablePaths = buildReadOnlyView(latestMovablePaths);
        dirty = false;
    }

    public Map<Integer, List<Vector2>> getPredictedPaths() {
        return readOnlyMovablePaths;
    }

    private Map<Integer, List<Vector2>> runEulerPrediction(
        List<NBodyState> states,
        float fixedDt,
        GravityConfig gravityConfig
    ) {
        Map<Integer, List<Vector2>> paths = new LinkedHashMap<>();
        if (states.isEmpty()) {
            return paths;
        }

        for (NBodyState state : states) {
            if (state.isMovable()) {
                List<Vector2> points = new ArrayList<>();
                points.add(new Vector2(state.getPosition()));
                paths.put(state.getId(), points);
            }
        }

        for (int step = 0; step < SandboxConfig.PREDICTION_STEPS; step++) {
            Vector2[] accelerations = computeAccelerations(states, gravityConfig);

            for (int i = 0; i < states.size(); i++) {
                NBodyState state = states.get(i);
                if (!state.isMovable()) {
                    continue;
                }

                Vector2 acceleration = accelerations[i];
                state.getVelocity().x += acceleration.x * fixedDt;
                state.getVelocity().y += acceleration.y * fixedDt;
                state.getPosition().x += state.getVelocity().x * fixedDt;
                state.getPosition().y += state.getVelocity().y * fixedDt;
            }

            if ((step + 1) % SandboxConfig.PREDICTION_SAMPLE_INTERVAL == 0) {
                for (NBodyState state : states) {
                    if (!state.isMovable()) {
                        continue;
                    }
                    paths.get(state.getId()).add(new Vector2(state.getPosition()));
                }
            }
        }

        return paths;
    }

    private Vector2[] computeAccelerations(List<NBodyState> states, GravityConfig gravityConfig) {
        Vector2[] accelerations = new Vector2[states.size()];
        float gravitationalConstant = gravityConfig.getGravityConstant();
        float minDistanceSq = gravityConfig.getMinDistanceSq();

        for (int i = 0; i < states.size(); i++) {
            NBodyState a = states.get(i);
            float totalAccX = 0f;
            float totalAccY = 0f;

            for (int j = 0; j < states.size(); j++) {
                if (i == j) {
                    continue;
                }

                NBodyState b = states.get(j);

                float dx = b.getPosition().x - a.getPosition().x;
                float dy = b.getPosition().y - a.getPosition().y;
                float distanceSq = dx * dx + dy * dy;
                if (distanceSq < minDistanceSq) {
                    distanceSq = minDistanceSq;
                }

                float distance = (float) Math.sqrt(distanceSq);

                // F = G * (m1 * m2) / r^2, and a = F / m1 = G * m2 / r^2.
                float accelerationMagnitude = gravitationalConstant * b.getMass() / distanceSq;
                totalAccX += accelerationMagnitude * (dx / distance);
                totalAccY += accelerationMagnitude * (dy / distance);
            }

            accelerations[i] = new Vector2(totalAccX, totalAccY);
        }

        return accelerations;
    }

    private Map<Integer, List<Vector2>> filterMovablePaths(
        Map<Integer, List<Vector2>> allPaths,
        List<NBodyState> states
    ) {
        Map<Integer, List<Vector2>> movablePaths = new LinkedHashMap<>();
        if (allPaths == null || allPaths.isEmpty() || states == null || states.isEmpty()) {
            return movablePaths;
        }

        for (NBodyState state : states) {
            if (!state.isMovable()) {
                continue;
            }
            List<Vector2> path = allPaths.get(state.getId());
            if (path != null) {
                movablePaths.put(state.getId(), path);
            }
        }

        return movablePaths;
    }

    private List<NBodyState> copyLimitedStates(List<NBodyState> states, int maxBodies) {
        List<NBodyState> copies = new ArrayList<>();
        if (states == null || states.isEmpty() || maxBodies <= 0) {
            return copies;
        }

        int limit = Math.min(states.size(), maxBodies);
        for (int i = 0; i < limit; i++) {
            copies.add(states.get(i).copy());
        }
        return copies;
    }

    private Map<Integer, List<Vector2>> buildReadOnlyView(Map<Integer, List<Vector2>> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, List<Vector2>> view = new LinkedHashMap<>(source.size());
        for (Map.Entry<Integer, List<Vector2>> entry : source.entrySet()) {
            List<Vector2> path = entry.getValue();
            view.put(entry.getKey(), (path == null) ? Collections.emptyList() : Collections.unmodifiableList(path));
        }
        return Collections.unmodifiableMap(view);
    }
}
