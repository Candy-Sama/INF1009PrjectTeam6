package com.team6.arcadesim.input;

import java.util.HashSet;
import java.util.Set;

public class InputState {
    // Stores currently held keys and mouse buttons
    private final Set<Integer> keysPressed = new HashSet<>();
    private final Set<Integer> mousePressed = new HashSet<>();
    
    // Tracking keys that were JUST pressed this frame
    private final Set<Integer> keysJustPressed = new HashSet<>();

    public void setKeyPressed(int keyCode, boolean isPressed) {
        if (isPressed) {
            if (!keysPressed.contains(keyCode)) {
                keysJustPressed.add(keyCode); // Mark as just pressed
            }
            keysPressed.add(keyCode);
        } else {
            keysPressed.remove(keyCode);
        }
    }

    public boolean isKeyDown(int keyCode) {
        return keysPressed.contains(keyCode); // Constant check
    }
    
    public boolean isKeyJustPressed(int keyCode) {
        // This only returns true if it's in our 'justPressed' list for this frame
        return keysJustPressed.contains(keyCode);
    }

    public void clearJustPressed() {
        keysJustPressed.clear();
    }
}
