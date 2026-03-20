package com.team6.arcadesim.physics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public final class DeterminismValidator {

    private DeterminismValidator() {
    }

    public static void assertStatesWithinTolerance(
        List<NBodyState> referenceStates,
        List<NBodyState> actualStates,
        float tolerance,
        boolean enabled
    ) {
        if (!enabled) {
            return;
        }
        if (referenceStates == null || actualStates == null) {
            throw new IllegalStateException("Determinism check failed: state lists must not be null.");
        }
        if (referenceStates.size() != actualStates.size()) {
            throw new IllegalStateException("Determinism check failed: state count mismatch.");
        }

        Map<Integer, NBodyState> actualById = new HashMap<>();
        for (NBodyState state : actualStates) {
            actualById.put(state.getId(), state);
        }

        for (NBodyState reference : referenceStates) {
            NBodyState actual = actualById.get(reference.getId());
            if (actual == null) {
                throw new IllegalStateException("Determinism check failed: missing state id " + reference.getId());
            }

            float positionDrift = deltaMagnitude(reference.getPosition(), actual.getPosition());
            float velocityDrift = deltaMagnitude(reference.getVelocity(), actual.getVelocity());
            if (positionDrift > tolerance || velocityDrift > tolerance) {
                throw new IllegalStateException(
                    "Determinism drift detected for id " + reference.getId()
                        + " (positionDrift=" + positionDrift
                        + ", velocityDrift=" + velocityDrift + ")."
                );
            }
        }
    }

    private static float deltaMagnitude(Vector2 a, Vector2 b) {
        float dx = a.x - b.x;
        float dy = a.y - b.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
