package com.team6.arcadesim.interfaces;
import com.badlogic.gdx.InputProcessor;
import com.team6.arcadesim.input.InputState;

public class InputHandler implements InputProcessor {

    private InputState inputState;

    public InputHandler(InputState inputState) {
        this.inputState = inputState;
    }

    @Override
    public boolean keyDown(int keycode) {
        inputState.setKeyPressed(keycode);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        inputState.setKeyReleased(keycode);
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false; // Not needed for arcade games usually
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        inputState.setMouseButtonPressed(button);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        inputState.setMouseButtonReleased(button);
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        inputState.setMouseButtonReleased(button);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Implement if you need drag controls
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Track mouse position here if needed
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}