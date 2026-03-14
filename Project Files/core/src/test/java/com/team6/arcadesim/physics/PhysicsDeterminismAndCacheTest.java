package com.team6.arcadesim.physics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.managers.GravityManager;
import com.team6.arcadesim.managers.MovementManager;

public class PhysicsDeterminismAndCacheTest {

    @Test
    void predictorMatchesRuntimeForShortHorizon() {
        GravityConfig gravityConfig = new GravityConfig(5000f, 2500f);
        float fixedDt = 1f / 60f;
        int steps = 180;

        List<Entity> runtimeEntities = new ArrayList<>();
        runtimeEntities.add(createBody(200f, 200f, 0f, 40f, 150f, true));
        runtimeEntities.add(createBody(600f, 360f, -20f, 0f, 200f, true));
        runtimeEntities.add(createBody(400f, 360f, 0f, 0f, 1000f, false));

        List<NBodyState> predictorInitialStates = toNBodyStates(runtimeEntities);

        GravityManager gravityManager = new GravityManager(gravityConfig);
        MovementManager movementManager = new MovementManager();
        for (int i = 0; i < steps; i++) {
            gravityManager.update(fixedDt, runtimeEntities);
            movementManager.update(fixedDt, runtimeEntities);
        }

        NBodyTrajectoryPredictor predictor = new NBodyTrajectoryPredictor();
        List<NBodyState> predictedFinalStates = predictor.simulateFinalStates(
            predictorInitialStates,
            gravityConfig,
            steps,
            fixedDt
        );

        List<NBodyState> runtimeFinalStates = toNBodyStates(runtimeEntities);
        DeterminismValidator.assertStatesWithinTolerance(runtimeFinalStates, predictedFinalStates, 0.01f, true);
    }

    @Test
    void predictionCacheRecomputesOnlyWhenDirtyAndRespectsLimits() {
        GravityConfig gravityConfig = new GravityConfig();
        TrajectoryPredictionConfig predictionConfig = new TrajectoryPredictionConfig();
        predictionConfig.setPredictionSteps(40);
        predictionConfig.setSampleInterval(1);
        predictionConfig.setMaxBodies(1);
        predictionConfig.setMaxPathPoints(5);
        predictionConfig.setFixedTimeStep(1f / 60f);

        List<NBodyState> bodies = new ArrayList<>();
        bodies.add(new NBodyState(1, new Vector2(0f, 0f), new Vector2(1f, 0f), 10f, true));
        bodies.add(new NBodyState(2, new Vector2(100f, 0f), new Vector2(0f, 0f), 500f, false));

        TrajectoryPredictionCache cache = new TrajectoryPredictionCache(new NBodyTrajectoryPredictor());

        Map<Integer, List<Vector2>> first = cache.getOrRecompute(bodies, gravityConfig, predictionConfig);
        Map<Integer, List<Vector2>> second = cache.getOrRecompute(bodies, gravityConfig, predictionConfig);

        assertEquals(1, cache.getRecomputeCount());
        assertEquals(first.keySet(), second.keySet());
        assertEquals(1, first.size());
        assertTrue(first.containsKey(1));
        assertTrue(first.get(1).size() <= 5);

        cache.markDirty();
        cache.getOrRecompute(bodies, gravityConfig, predictionConfig);
        assertEquals(2, cache.getRecomputeCount());
    }

    private Entity createBody(float x, float y, float vx, float vy, float mass, boolean movable) {
        Entity entity = new Entity();
        entity.addComponent(new TransformComponent(x, y));
        entity.addComponent(new MassComponent(mass));
        if (movable) {
            entity.addComponent(new MovementComponent(vx, vy));
        }
        return entity;
    }

    private List<NBodyState> toNBodyStates(List<Entity> entities) {
        Map<Integer, NBodyState> states = new LinkedHashMap<>();
        for (Entity entity : entities) {
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            MassComponent mass = entity.getComponent(MassComponent.class);
            MovementComponent movement = entity.getComponent(MovementComponent.class);

            Vector2 velocity = (movement == null) ? new Vector2() : new Vector2(movement.getVelocity());
            boolean movable = movement != null;
            states.put(
                entity.getId(),
                new NBodyState(entity.getId(), new Vector2(transform.getPosition()), velocity, mass.getMass(), movable)
            );
        }
        return new ArrayList<>(states.values());
    }
}
