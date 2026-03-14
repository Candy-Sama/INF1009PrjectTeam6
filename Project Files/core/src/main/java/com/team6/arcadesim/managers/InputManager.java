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
    
    public int getMouseX() {
        return Gdx.input.getX();
    }

    public int getMouseY() {
        return Gdx.input.getY();
    }
}
