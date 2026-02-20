package com.team6.arcadesim.input;

import java.util.HashSet;
import java.util.Set;

public class InputState {

    private Set<Integer> keysPressed;
    private Set<Integer> keysJustPressed;
    private Set<Integer> mouseButtonsPressed;

    public InputState() {
        this.keysPressed = new HashSet<>();
        this.keysJustPressed = new HashSet<>();
        this.mouseButtonsPressed = new HashSet<>();
    }

    public void setKeyPressed(int keycode) {
        keysPressed.add(keycode);
        keysJustPressed.add(keycode);
    }

    public void setKeyReleased(int keycode) {
        keysPressed.remove(keycode);
        keysJustPressed.remove(keycode);
    }

    public void setMouseButtonPressed(int button) {
        mouseButtonsPressed.add(button);
    }

    public void setMouseButtonReleased(int button) {
        mouseButtonsPressed.remove(button);
    }

    public boolean isKeyDown(int keycode) {
        return keysPressed.contains(keycode);
    }

    public boolean isKeyJustPressed(int keycode) {
        return keysJustPressed.contains(keycode);
    }

    public boolean isMouseButtonDown(int button) {
        return mouseButtonsPressed.contains(button);
    }

    public void reset() {
        keysJustPressed.clear();
    }
}