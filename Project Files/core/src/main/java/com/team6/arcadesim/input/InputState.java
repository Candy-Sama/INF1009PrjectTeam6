package com.team6.arcadesim.input;

import java.util.HashSet;
import java.util.Set;

public class InputState {

    // HashSet is O(1) for lookups - very fast
    private Set<Integer> keysPressed;
    private Set<Integer> keysJustPressed;
    private Set<Integer> mouseButtonsPressed;

    public InputState() {
        this.keysPressed = new HashSet<>();
        this.keysJustPressed = new HashSet<>();
        this.mouseButtonsPressed = new HashSet<>();
    }

    // --- Write Methods (Called by InputHandler) ---

    public void setKeyPressed(int keycode) {
        keysPressed.add(keycode);
        keysJustPressed.add(keycode); // Mark as "Just Pressed" for this frame
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

    // --- Read Methods (Called by InputManager) ---

    public boolean isKeyDown(int keycode) {
        return keysPressed.contains(keycode);
    }

    public boolean isKeyJustPressed(int keycode) {
        return keysJustPressed.contains(keycode);
    }

    public boolean isMouseButtonDown(int button) {
        return mouseButtonsPressed.contains(button);
    }

    // --- Frame Cleanup ---

    public void reset() {
        // Clear the "Just Pressed" set so they don't trigger again next frame
        keysJustPressed.clear();
    }
}