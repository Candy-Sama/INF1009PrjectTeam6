package com.team6.arcadesim.managers;

import com.badlogic.gdx.Gdx;
import com.team6.arcadesim.input.InputState;
import com.team6.arcadesim.interfaces.InputHandler;

public class InputManager {

    private InputState inputState;
    private InputHandler inputHandler;

    public InputManager() {
        this.inputState = new InputState();
        this.inputHandler = new InputHandler(inputState);
        Gdx.input.setInputProcessor(inputHandler);
    }

    public void update() {
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
    
    public int getMouseX() {
        return Gdx.input.getX();
    }

    public int getMouseY() {
        return Gdx.input.getY();
    }
}