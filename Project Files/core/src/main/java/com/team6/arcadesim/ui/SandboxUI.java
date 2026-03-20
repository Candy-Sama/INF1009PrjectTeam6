package com.team6.arcadesim.ui;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SandboxUI implements Disposable {

    private static final float PANEL_WIDTH = 320f;
    private static final float DEFAULT_MASS = 10f;
    private static final float DEFAULT_RADIUS = 12f;
    private static final float DEFAULT_SPEED = 0f;

    private final Stage stage;
    private final Skin skin;

    private final SelectBox<String> entitySelector;
    private final TextField massField;
    private final TextField radiusField;
    private final TextField speedXField;
    private final TextField speedYField;
    private final TextButton removeButton;
    private final TextButton resetButton;
    private final TextButton startPauseButton;
    private final TextButton timeScaleButton;
    private final TextButton returnButton;
    private final Label speedValueLabel;
    private final Label accelValueLabel;
    private final Label nearestStarValueLabel;
    private final Label selectedTypeValueLabel;

    private Runnable onRemovePressed;
    private Runnable onResetPressed;
    private Runnable onStartPausePressed;
    private Runnable onTimeScalePressed;
    private Runnable onReturnPressed;
    private Consumer<String> onEntityTypeChanged;
    private BiConsumer<Float, Float> onVelocityChanged;

    private boolean suppressVelocityCallbacks;

    public SandboxUI() {
        this.skin = buildSkin();
        this.stage = new Stage(new ScreenViewport());

        this.entitySelector = new SelectBox<>(skin);
        this.massField = new TextField(Float.toString(DEFAULT_MASS), skin);
        this.radiusField = new TextField(Float.toString(DEFAULT_RADIUS), skin);
        this.speedXField = new TextField(Float.toString(DEFAULT_SPEED), skin);
        this.speedYField = new TextField(Float.toString(DEFAULT_SPEED), skin);
        this.removeButton = new TextButton("Remove", skin);
        this.resetButton = new TextButton("Reset", skin);
        this.startPauseButton = new TextButton("Start", skin);
        this.timeScaleButton = new TextButton("Speed x1", skin);
        this.returnButton = new TextButton("Return", skin);
        this.speedValueLabel = new Label("-", skin);
        this.accelValueLabel = new Label("-", skin);
        this.nearestStarValueLabel = new Label("-", skin);
        this.selectedTypeValueLabel = new Label("None", skin);

        configureFields();
        buildLayout();
        bindEvents();
    }

    private void configureFields() {
        TextField.TextFieldFilter numberFilter = (field, c) -> {
            if (Character.isDigit(c)) {
                return true;
            }
            if (c == '.' && !field.getText().contains(".")) {
                return true;
            }
            return c == '-' && field.getCursorPosition() == 0 && !field.getText().contains("-");
        };

        massField.setTextFieldFilter(numberFilter);
        radiusField.setTextFieldFilter(numberFilter);
        speedXField.setTextFieldFilter(numberFilter);
        speedYField.setTextFieldFilter(numberFilter);
    }

    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.top().right().pad(20f);

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("panel-bg"));
        panel.pad(18f);
        panel.defaults().width(PANEL_WIDTH - 36f).padBottom(10f);

        Label title = new Label("Sandbox Controls", skin);
        title.setAlignment(Align.center);
        panel.add(title).padBottom(16f).row();

        panel.add(new Label("Entity Type", skin)).left().row();
        entitySelector.setItems("Star", "Planet");
        panel.add(entitySelector).row();

        panel.add(new Label("Mass", skin)).left().row();
        panel.add(massField).row();

        panel.add(new Label("Radius", skin)).left().row();
        panel.add(radiusField).row();

        panel.add(new Label("Speed X", skin)).left().row();
        panel.add(speedXField).row();

        panel.add(new Label("Speed Y", skin)).left().row();
        panel.add(speedYField).row();

        Table row = new Table();
        row.defaults().expandX().fillX().padRight(8f);
        row.add(removeButton);
        row.add(resetButton).padRight(0f);
        panel.add(row).padTop(8f).row();

        panel.add(startPauseButton).padTop(4f).row();
        panel.add(timeScaleButton).padTop(4f).row();
        panel.add(returnButton).padTop(4f).padBottom(0f).row();

        panel.add(new Label("Learning HUD", skin)).left().padTop(12f).row();

        Table hudTable = new Table();
        hudTable.defaults().padBottom(6f);

        addHudRow(hudTable, "Selected", selectedTypeValueLabel);
        addHudRow(hudTable, "Speed |v|", speedValueLabel);
        addHudRow(hudTable, "Accel |a|", accelValueLabel);
        addHudRow(hudTable, "Nearest Star", nearestStarValueLabel);

        panel.add(hudTable).row();

        root.add(panel).width(PANEL_WIDTH).top().right();
        stage.addActor(root);
    }

    private void bindEvents() {
        entitySelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (onEntityTypeChanged != null) {
                    onEntityTypeChanged.accept(entitySelector.getSelected());
                }
            }
        });

        removeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (onRemovePressed != null) {
                    onRemovePressed.run();
                }
            }
        });

        resetButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (onResetPressed != null) {
                    onResetPressed.run();
                }
            }
        });

        startPauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (onStartPausePressed != null) {
                    onStartPausePressed.run();
                }
            }
        });

        timeScaleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (onTimeScalePressed != null) {
                    onTimeScalePressed.run();
                }
            }
        });

        returnButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (onReturnPressed != null) {
                    onReturnPressed.run();
                }
            }
        });

        speedXField.setTextFieldListener((textField, c) -> notifyVelocityChanged());
        speedYField.setTextFieldListener((textField, c) -> notifyVelocityChanged());
    }

    private Skin buildSkin() {
        Skin uiSkin = new Skin();
        BitmapFont font = new BitmapFont();
        uiSkin.add("default-font", font);

        uiSkin.add("button-up", solid(0.22f, 0.24f, 0.30f, 1f), Drawable.class);
        uiSkin.add("button-over", solid(0.30f, 0.33f, 0.40f, 1f), Drawable.class);
        uiSkin.add("button-down", solid(0.16f, 0.18f, 0.24f, 1f), Drawable.class);
        uiSkin.add("field-bg", solid(0.14f, 0.15f, 0.19f, 1f), Drawable.class);
        uiSkin.add("field-focus", solid(0.20f, 0.22f, 0.28f, 1f), Drawable.class);
        uiSkin.add("panel-bg", solid(0.07f, 0.08f, 0.11f, 0.94f), Drawable.class);
        uiSkin.add("select-bg", solid(0.14f, 0.15f, 0.19f, 1f), Drawable.class);
        uiSkin.add("select-open", solid(0.18f, 0.20f, 0.26f, 1f), Drawable.class);
        uiSkin.add("list-select", solid(0.30f, 0.34f, 0.45f, 1f), Drawable.class);
        uiSkin.add("list-bg", solid(0.12f, 0.13f, 0.17f, 0.98f), Drawable.class);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        uiSkin.add("default", labelStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = uiSkin.getDrawable("button-up");
        buttonStyle.over = uiSkin.getDrawable("button-over");
        buttonStyle.down = uiSkin.getDrawable("button-down");
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.WHITE;
        uiSkin.add("default", buttonStyle);

        TextField.TextFieldStyle fieldStyle = new TextField.TextFieldStyle();
        fieldStyle.font = font;
        fieldStyle.fontColor = Color.WHITE;
        fieldStyle.background = uiSkin.getDrawable("field-bg");
        fieldStyle.focusedBackground = uiSkin.getDrawable("field-focus");
        fieldStyle.cursor = uiSkin.getDrawable("button-over");
        uiSkin.add("default", fieldStyle);

        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = font;
        listStyle.fontColorUnselected = new Color(0.88f, 0.88f, 0.88f, 1f);
        listStyle.fontColorSelected = Color.WHITE;
        listStyle.selection = uiSkin.getDrawable("list-select");
        listStyle.background = uiSkin.getDrawable("list-bg");
        uiSkin.add("default", listStyle);

        ScrollPane.ScrollPaneStyle scrollPaneStyle = new ScrollPane.ScrollPaneStyle();
        scrollPaneStyle.background = uiSkin.getDrawable("list-bg");
        uiSkin.add("default", scrollPaneStyle);

        SelectBox.SelectBoxStyle selectStyle = new SelectBox.SelectBoxStyle();
        selectStyle.font = font;
        selectStyle.fontColor = Color.WHITE;
        selectStyle.background = uiSkin.getDrawable("select-bg");
        selectStyle.backgroundOpen = uiSkin.getDrawable("select-open");
        selectStyle.listStyle = listStyle;
        selectStyle.scrollStyle = scrollPaneStyle;
        uiSkin.add("default", selectStyle);

        return uiSkin;
    }

    private TextureRegionDrawable solid(float r, float g, float b, float a) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(r, g, b, a));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }

    private void notifyVelocityChanged() {
        if (suppressVelocityCallbacks || onVelocityChanged == null) {
            return;
        }
        onVelocityChanged.accept(getSpeedX(), getSpeedY());
    }

    private void addHudRow(Table table, String title, Label valueLabel) {
        Label titleLabel = new Label(title, skin);
        titleLabel.setAlignment(Align.left);
        valueLabel.setAlignment(Align.right);

        table.add(titleLabel).left().expandX().fillX();
        table.add(valueLabel).right().minWidth(110f);
        table.row();
    }

    private float parse(String raw, float fallback) {
        if (raw == null) {
            return fallback;
        }
        String value = raw.trim();
        if (value.isEmpty() || value.equals("-") || value.equals(".") || value.equals("-.")) {
            return fallback;
        }
        try {
            float parsed = Float.parseFloat(value);
            return Float.isFinite(parsed) ? parsed : fallback;
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    public void update(float dt) {
        stage.act(dt);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public String getEntityType() {
        return entitySelector.getSelected();
    }

    public float getMass() {
        return parse(massField.getText(), DEFAULT_MASS);
    }

    public float getRadius() {
        return parse(radiusField.getText(), DEFAULT_RADIUS);
    }

    public float getSpeedX() {
        return parse(speedXField.getText(), DEFAULT_SPEED);
    }

    public float getSpeedY() {
        return parse(speedYField.getText(), DEFAULT_SPEED);
    }

    public void populateSelectedEntity(String entityType, float mass, float radius, float speedX, float speedY) {
        suppressVelocityCallbacks = true;
        entitySelector.setSelected(entityType == null ? "Planet" : entityType);
        massField.setText(Float.toString(mass));
        radiusField.setText(Float.toString(radius));
        speedXField.setText(Float.toString(speedX));
        speedYField.setText(Float.toString(speedY));
        suppressVelocityCallbacks = false;
    }

    public void setSimulationRunning(boolean running) {
        startPauseButton.setText(running ? "Pause" : "Start");
    }

    public void setTimeScale(float scale) {
        if (scale == (int) scale) {
            timeScaleButton.setText("Speed x" + (int) scale);
        } else {
            timeScaleButton.setText(String.format("Speed x%.1f", scale));
        }
    }

    public void setEducationalStats(String selectedType, float speed, float acceleration, float nearestStarDistance) {
        selectedTypeValueLabel.setText(selectedType == null ? "None" : selectedType);
        speedValueLabel.setText(String.format("%.2f u/s", speed));
        accelValueLabel.setText(String.format("%.2f u/s^2", acceleration));

        if (nearestStarDistance < 0f) {
            nearestStarValueLabel.setText("N/A");
        } else {
            nearestStarValueLabel.setText(String.format("%.2f u", nearestStarDistance));
        }
    }

    public void setOnRemovePressed(Runnable onRemovePressed) {
        this.onRemovePressed = onRemovePressed;
    }

    public void setOnResetPressed(Runnable onResetPressed) {
        this.onResetPressed = onResetPressed;
    }

    public void setOnStartPausePressed(Runnable onStartPausePressed) {
        this.onStartPausePressed = onStartPausePressed;
    }

    public void setOnTimeScalePressed(Runnable onTimeScalePressed) {
        this.onTimeScalePressed = onTimeScalePressed;
    }

    public void setOnReturnPressed(Runnable onReturnPressed) {
        this.onReturnPressed = onReturnPressed;
    }

    public void setOnEntityTypeChanged(Consumer<String> onEntityTypeChanged) {
        this.onEntityTypeChanged = onEntityTypeChanged;
    }

    public void setOnVelocityChanged(BiConsumer<Float, Float> onVelocityChanged) {
        this.onVelocityChanged = onVelocityChanged;
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
