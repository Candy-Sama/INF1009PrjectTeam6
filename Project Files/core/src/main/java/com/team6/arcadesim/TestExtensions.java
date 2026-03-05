package com.team6.arcadesim;

// Test imports to verify extensions are working
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.physics.bullet.Bullet;

public class TestExtensions {
    // If this file compiles without errors, the extensions are working
    public void test() {
        System.out.println("Freetype: " + FreeTypeFontGenerator.class.getName());
        System.out.println("Bullet: " + Bullet.class.getName());
    }
}
