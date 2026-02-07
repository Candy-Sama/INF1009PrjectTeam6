package com.team6.arcadesim.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.team6.arcadesim.input.InputState;

public class InputManager {
    private final InputState currentState;

    public InputManager(){
        this.currentState = new InputState();
    }

    /**
     * Polls libGDX hardware state and updates our internal InputState
     */
    public void poll() {
        currentState.clearJustPressed();

        // Example: Checking common keys. 
        // In a more robust engine, you might iterate through all possible keys.
        checkKey(Input.Keys.UP);
        checkKey(Input.Keys.DOWN);
        checkKey(Input.Keys.LEFT);
        checkKey(Input.Keys.RIGHT);
        checkKey(Input.Keys.SPACE);
    }

    private void checkKey(int keyCode) {
        currentState.setKeyPressed(keyCode, Gdx.input.isKeyPressed(keyCode));
    }

    public InputState getState() {
        return currentState; //
    }
    
}
