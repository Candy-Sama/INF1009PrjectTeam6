package com.team6.arcadesim.managers;

import com.team6.arcadesim.input.InputState;

public class InputManager {
    private InputState currentState;

    public void poll() {
        // Poll the current input state from the input system
        // This is a placeholder; actual implementation depends on the input library used
    }

    public InputState getState() {
        return currentState;
    }

    public boolean isKeyDown(int keyCode) {
        return currentState.isKeyDown(keyCode);
    }

    public boolean isKeyPressed(int keyCode) {
        return currentState.isKeyPressed(keyCode);
    }

    public boolean isKeyReleased(int keyCode) {
        return currentState.isKeyReleased(keyCode);
    }
    
}
