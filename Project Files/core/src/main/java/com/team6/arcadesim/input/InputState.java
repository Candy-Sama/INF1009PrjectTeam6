package com.team6.arcadesim.input;

import java.util.Set;

public class InputState {
    private Set<Integer> keysPressed;
    private Set<Integer> keysReleased;
    private boolean mousePressed;

    public boolean isKeyDown(int keyCode) {
        return false;
    }

    public boolean isKeyPressed(int keyCode) {
        return false;
    }

    public boolean isKeyReleased(int keyCode) {
        return false;
    }

    public boolean isKeyReleasedKeyCode(int keyCode, boolean b) {
        return false;
    }
}
