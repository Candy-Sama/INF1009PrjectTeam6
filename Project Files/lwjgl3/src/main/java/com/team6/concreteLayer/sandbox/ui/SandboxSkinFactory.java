package com.team6.arcadesim.sandbox.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class SandboxSkinFactory {

    private SandboxSkinFactory() {
    }

    public static Skin createSkin() {
        Skin skin = new Skin();
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);

        Texture panelTexture = createTexture(0.09f, 0.11f, 0.14f, 0.95f);
        Texture buttonUpTexture = createTexture(0.18f, 0.29f, 0.43f, 1f);
        Texture buttonOverTexture = createTexture(0.23f, 0.37f, 0.54f, 1f);
        Texture buttonDownTexture = createTexture(0.14f, 0.22f, 0.33f, 1f);
        Texture fieldTexture = createTexture(0.15f, 0.18f, 0.22f, 1f);
        Texture fieldFocusedTexture = createTexture(0.22f, 0.28f, 0.34f, 1f);
        Texture cursorTexture = createTexture(0.95f, 0.95f, 0.95f, 1f);
        Texture selectionTexture = createTexture(0.30f, 0.45f, 0.65f, 0.75f);

        skin.add("panel-bg", new TextureRegionDrawable(new TextureRegion(panelTexture)), Drawable.class);
        skin.add("button-up", new TextureRegionDrawable(new TextureRegion(buttonUpTexture)), Drawable.class);
        skin.add("button-over", new TextureRegionDrawable(new TextureRegion(buttonOverTexture)), Drawable.class);
        skin.add("button-down", new TextureRegionDrawable(new TextureRegion(buttonDownTexture)), Drawable.class);
        skin.add("field-bg", new TextureRegionDrawable(new TextureRegion(fieldTexture)), Drawable.class);
        skin.add("field-focused-bg", new TextureRegionDrawable(new TextureRegion(fieldFocusedTexture)), Drawable.class);
        skin.add("field-cursor", new TextureRegionDrawable(new TextureRegion(cursorTexture)), Drawable.class);
        skin.add("field-selection", new TextureRegionDrawable(new TextureRegion(selectionTexture)), Drawable.class);

        Label.LabelStyle defaultLabelStyle = new Label.LabelStyle();
        defaultLabelStyle.font = font;
        defaultLabelStyle.fontColor = new Color(0.95f, 0.95f, 0.95f, 1f);
        skin.add("default", defaultLabelStyle);

        Label.LabelStyle headingStyle = new Label.LabelStyle();
        headingStyle.font = font;
        headingStyle.fontColor = new Color(0.98f, 0.90f, 0.66f, 1f);
        skin.add("heading", headingStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable("button-up");
        buttonStyle.over = skin.getDrawable("button-over");
        buttonStyle.down = skin.getDrawable("button-down");
        buttonStyle.checked = skin.getDrawable("button-over");
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        skin.add("default", buttonStyle);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = new Color(0.97f, 0.97f, 0.97f, 1f);
        textFieldStyle.messageFont = font;
        textFieldStyle.messageFontColor = new Color(0.72f, 0.74f, 0.77f, 1f);
        textFieldStyle.background = skin.getDrawable("field-bg");
        textFieldStyle.focusedBackground = skin.getDrawable("field-focused-bg");
        textFieldStyle.cursor = skin.getDrawable("field-cursor");
        textFieldStyle.selection = skin.getDrawable("field-selection");
        skin.add("default", textFieldStyle);

        return skin;
    }

    private static Texture createTexture(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(r, g, b, a);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
