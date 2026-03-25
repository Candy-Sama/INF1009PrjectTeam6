package com.team6.arcadesim.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.InputMultiplexer;
import com.team6.arcadesim.input.InputState;
import com.team6.arcadesim.interfaces.InputHandler;

public class InputManager {

    private final InputState inputState;
    private final InputHandler inputHandler;
    private final InputMultiplexer inputMultiplexer;

    public InputManager() {
        this.inputState = new InputState();
        this.inputHandler = new InputHandler(inputState);
        this.inputMultiplexer = new InputMultiplexer();
        this.inputMultiplexer.addProcessor(inputHandler);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    public void addInputProcessor(InputProcessor processor) {
        if (processor != null) {
            inputMultiplexer.addProcessor(processor);
        }
    }

    /**
     * Registers an input processor at highest priority.
     * Use this for UI stages so they can consume events before world logic sees them.
     */
    public void addInputProcessorFirst(InputProcessor processor) {
        if (processor != null) {
            inputMultiplexer.addProcessor(0, processor);
        }
    }

    public void removeInputProcessor(InputProcessor processor) {
        if (processor != null) {
            inputMultiplexer.removeProcessor(processor);
        }
    }

    public void clearTransientInputs() {
        inputState.reset();
    }

    public boolean isKeyDown(int keycode) {
        return inputState.isKeyDown(keycode);
    }

    public boolean isKeyJustPressed(int keycode) {
        return inputState.isKeyJustPressed(keycode);
    }

    public boolean isMouseButtonDown(int button) {
        return inputState.isMouseButtonDown(button);
    }

    public boolean isMouseButtonJustPressed(int button) {
        return inputState.isMouseButtonJustPressed(button);
    }

    public boolean consumeMouseButtonJustPressed(int button) {
        return inputState.consumeMouseButtonJustPressed(button);
    }

    /**
     * Convenience guard for scene logic: only react to click if UI is not currently targeted.
     */
    public boolean consumeWorldClick(int button, boolean pointerOverUi) {
        if (pointerOverUi) {
            consumeMouseButtonJustPressed(button);
            return false;
        }
        return consumeMouseButtonJustPressed(button);
    }
    
    public int getMouseX() {
        return Gdx.input.getX();
    }

    public int getMouseY() {
        return Gdx.input.getY();
    }
}
