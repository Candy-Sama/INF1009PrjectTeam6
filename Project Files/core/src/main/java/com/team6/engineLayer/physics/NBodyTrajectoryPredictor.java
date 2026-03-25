package com.team6.arcadesim.physics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public class NBodyTrajectoryPredictor {

    public Map<Integer, List<Vector2>> predictPaths(List<NBodyState> bodies, GravityConfig gravityConfig, TrajectoryPredictionConfig predictionConfig) {
        Map<Integer, List<Vector2>> paths = new LinkedHashMap<>();
        List<NBodyState> states = copyAndLimitBodies(bodies, predictionConfig.getMaxBodies());
        if (states.isEmpty()) {
            return paths;
        }

        for (NBodyState state : states) {
            List<Vector2> points = new ArrayList<>();
            points.add(new Vector2(state.getPosition()));
            paths.put(state.getId(), points);
        }

        for (int step = 0; step < predictionConfig.getPredictionSteps(); step++) {
            stepSimulation(states, gravityConfig, predictionConfig.getFixedTimeStep());

            if ((step + 1) % predictionConfig.getSampleInterval() == 0) {
                for (NBodyState state : states) {
                    List<Vector2> points = paths.get(state.getId());
                    if (points.size() < predictionConfig.getMaxPathPoints()) {
                        points.add(new Vector2(state.getPosition()));
                    }
                }
            }
        }

        return paths;
    }

    public List<NBodyState> simulateFinalStates(List<NBodyState> bodies, GravityConfig gravityConfig, int steps, float fixedDt) {
        List<NBodyState> states = copyAndLimitBodies(bodies, bodies == null ? 0 : bodies.size());
        for (int i = 0; i < steps; i++) {
            stepSimulation(states, gravityConfig, fixedDt);
        }
        return states;
    }

    public void stepSimulation(List<NBodyState> states, GravityConfig gravityConfig, float fixedDt) {
        if (states == null || states.isEmpty()) {
            return;
        }

        Vector2[] accelerations = new Vector2[states.size()];
        for (int i = 0; i < states.size(); i++) {
            accelerations[i] = computeAcceleration(states.get(i), states, gravityConfig);
        }

        // Keep runtime order exactly: gravity -> velocity -> position.
        for (int i = 0; i < states.size(); i++) {
            NBodyState current = states.get(i);
            if (!current.isMovable()) {
                continue;
            }

            Vector2 acceleration = accelerations[i];
            current.getVelocity().x += acceleration.x * fixedDt;
            current.getVelocity().y += acceleration.y * fixedDt;
            current.getPosition().x += current.getVelocity().x * fixedDt;
            current.getPosition().y += current.getVelocity().y * fixedDt;
        }
    }

    private Vector2 computeAcceleration(NBodyState current, List<NBodyState> states, GravityConfig gravityConfig) {
        float totalAccX = 0f;
        float totalAccY = 0f;

        for (NBodyState other : states) {
            if (other.getId() == current.getId()) {
                continue;
            }

            float dx = other.getPosition().x - current.getPosition().x;
            float dy = other.getPosition().y - current.getPosition().y;
            float distanceSq = dx * dx + dy * dy;
            if (distanceSq < gravityConfig.getMinDistanceSq()) {
                distanceSq = gravityConfig.getMinDistanceSq();
            }

            float distance = (float) Math.sqrt(distanceSq);
            float force = gravityConfig.getGravityConstant() * other.getMass() / distanceSq;

            totalAccX += force * (dx / distance);
            totalAccY += force * (dy / distance);
        }

        return new Vector2(totalAccX, totalAccY);
    }

    private List<NBodyState> copyAndLimitBodies(List<NBodyState> bodies, int maxBodies) {
        List<NBodyState> copies = new ArrayList<>();
        if (bodies == null || bodies.isEmpty() || maxBodies <= 0) {
            return copies;
        }

        int limit = Math.min(maxBodies, bodies.size());
        for (int i = 0; i < limit; i++) {
            copies.add(bodies.get(i).copy());
        }
        return copies;
    }
}
