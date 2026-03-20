package com.team6.arcadesim.input;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class InputStateConsumptionTest {

    @Test
    void consumedMouseClickIsOnlyHandledOnce() {
        InputState inputState = new InputState();
        int leftButton = 0;

        inputState.setMouseButtonPressed(leftButton);

        assertTrue(inputState.consumeMouseButtonJustPressed(leftButton));
        assertFalse(inputState.consumeMouseButtonJustPressed(leftButton));
        assertTrue(inputState.isMouseButtonDown(leftButton));
    }
}
