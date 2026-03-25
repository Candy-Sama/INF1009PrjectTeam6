package com.team6.concreteLayer.sandbox.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.team6.concreteLayer.sandbox.BodyType;
import com.team6.concreteLayer.sandbox.config.SandboxConfig;

public class SandboxControlPanel {

    private static final float PANEL_WIDTH = 300f;
    private static final float PANEL_PADDING = 12f;
    private static final float PANEL_CONTENT_WIDTH = PANEL_WIDTH - (PANEL_PADDING * 2f);
    private static final float ROW_GAP = 6f;
    private static final float SMALL_LABEL_WIDTH = 14f;
    private static final float HALF_FIELD_WIDTH = (PANEL_CONTENT_WIDTH - (SMALL_LABEL_WIDTH * 2f) - ROW_GAP) / 2f;
    private static final float HALF_BUTTON_WIDTH = (PANEL_CONTENT_WIDTH - ROW_GAP) / 2f;

    private final Table rootTable;
    private final TextButton starButton;
    private final TextButton planetButton;
    private final ButtonGroup<TextButton> bodyTypeGroup;
    private final TextField radiusField;
    private final TextField massField;
    private final TextField velocityXField;
    private final TextField velocityYField;
    private final TextField positionXField;
    private final TextField positionYField;
    private final TextButton startSimulationButton;
    private final TextButton clearBoardButton;
    private final TextButton speed1xButton;
    private final TextButton speed2xButton;
    private final TextButton speed3xButton;

    public SandboxControlPanel(Skin skin) {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().right();
        rootTable.pad(12f);

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("panel-bg"));
        panel.setTouchable(Touchable.enabled);
        panel.pad(PANEL_PADDING);
        panel.defaults().padBottom(6f).left();

        rootTable.add(panel).width(PANEL_WIDTH).growY();

        Label headingLabel = new Label("Sandbox Control Panel", skin, "heading");
        panel.add(headingLabel).padBottom(10f);
        panel.row();

        panel.add(new Label("Body Type", skin));
        panel.row();

        Table bodyTypeRow = new Table();
        starButton = new TextButton("Star", skin);
        planetButton = new TextButton("Planet", skin);
        bodyTypeGroup = new ButtonGroup<>(starButton, planetButton);
        bodyTypeGroup.setMinCheckCount(1);
        bodyTypeGroup.setMaxCheckCount(1);
        starButton.setChecked(true);
        bodyTypeRow.add(starButton).width(HALF_BUTTON_WIDTH).height(36f).padRight(ROW_GAP);
        bodyTypeRow.add(planetButton).width(HALF_BUTTON_WIDTH).height(36f);
        panel.add(bodyTypeRow).padBottom(10f);
        panel.row();

        panel.add(new Label("Radius", skin));
        panel.row();
        radiusField = createNumericField(String.valueOf((int) SandboxConfig.DEFAULT_RADIUS), skin);
        panel.add(radiusField).width(PANEL_CONTENT_WIDTH).height(34f);
        panel.row();
        panel.add(createHintLabel("Radius controls size and collision range.", skin)).width(PANEL_CONTENT_WIDTH).padTop(-2f);
        panel.row();

        panel.add(new Label("Mass", skin));
        panel.row();
        massField = createNumericField(String.valueOf((int) SandboxConfig.DEFAULT_MASS), skin);
        panel.add(massField).width(PANEL_CONTENT_WIDTH).height(34f);
        panel.row();
        panel.add(createHintLabel("Higher mass creates stronger gravity pull.", skin)).width(PANEL_CONTENT_WIDTH).padTop(-2f);
        panel.row();

        panel.add(new Label("Initial Velocity Vector", skin));
        panel.row();
        Table velocityRow = new Table();
        velocityXField = createNumericField(String.valueOf((int) SandboxConfig.DEFAULT_VELOCITY), skin);
        velocityYField = createNumericField(String.valueOf((int) SandboxConfig.DEFAULT_VELOCITY), skin);
        velocityRow.add(new Label("X", skin)).width(SMALL_LABEL_WIDTH);
        velocityRow.add(velocityXField).width(HALF_FIELD_WIDTH).height(34f).padRight(ROW_GAP);
        velocityRow.add(new Label("Y", skin)).width(SMALL_LABEL_WIDTH);
        velocityRow.add(velocityYField).width(HALF_FIELD_WIDTH).height(34f);
        panel.add(velocityRow);
        panel.row();
        panel.add(createHintLabel("Velocity X controls left/right motion.", skin)).width(PANEL_CONTENT_WIDTH).padTop(-2f);
        panel.row();
        panel.add(createHintLabel("Velocity Y controls up/down motion.", skin)).width(PANEL_CONTENT_WIDTH).padTop(-2f);
        panel.row();

        panel.add(new Label("Initial Position Vector", skin));
        panel.row();
        Table positionRow = new Table();
        positionXField = createNumericField("640", skin);
        positionYField = createNumericField("360", skin);
        positionRow.add(new Label("X", skin)).width(SMALL_LABEL_WIDTH);
        positionRow.add(positionXField).width(HALF_FIELD_WIDTH).height(34f).padRight(ROW_GAP);
        positionRow.add(new Label("Y", skin)).width(SMALL_LABEL_WIDTH);
        positionRow.add(positionYField).width(HALF_FIELD_WIDTH).height(34f);
        panel.add(positionRow);
        panel.row();
        panel.add(createHintLabel("Position X sets spawn point horizontally.", skin)).width(PANEL_CONTENT_WIDTH).padTop(-2f);
        panel.row();
        panel.add(createHintLabel("Position Y sets spawn point vertically.", skin)).width(PANEL_CONTENT_WIDTH).padTop(-2f).padBottom(10f);
        panel.row();

        Table actionsRow = new Table();
        startSimulationButton = new TextButton("Start Simulation", skin);
        clearBoardButton = new TextButton("Clear Board", skin);
        actionsRow.add(startSimulationButton).width(HALF_BUTTON_WIDTH).height(42f).padRight(ROW_GAP);
        actionsRow.add(clearBoardButton).width(HALF_BUTTON_WIDTH).height(42f);
        panel.add(actionsRow).padTop(4f);
        panel.row();

        panel.add(new Label("Simulation Speed", skin)).padTop(2f);
        panel.row();
        Table speedRow = new Table();
        speed1xButton = new TextButton("1x", skin);
        speed2xButton = new TextButton("2x", skin);
        speed3xButton = new TextButton("3x", skin);
        float speedButtonWidth = (PANEL_CONTENT_WIDTH - (ROW_GAP * 2f)) / 3f;
        speedRow.add(speed1xButton).width(speedButtonWidth).height(34f).padRight(ROW_GAP);
        speedRow.add(speed2xButton).width(speedButtonWidth).height(34f).padRight(ROW_GAP);
        speedRow.add(speed3xButton).width(speedButtonWidth).height(34f);
        panel.add(speedRow).width(PANEL_CONTENT_WIDTH);
        panel.row();
        panel.add(createHintLabel("Affects physics update speed while simulation is running.", skin))
            .width(PANEL_CONTENT_WIDTH).padTop(-2f).padBottom(6f);
        panel.row();

        panel.add().growY();
    }

    public Table getRootTable() {
        return rootTable;
    }

    public TextButton getStarButton() {
        return starButton;
    }

    public TextButton getPlanetButton() {
        return planetButton;
    }

    public BodyType getSelectedBodyType() {
        if (starButton.isChecked()) {
            return BodyType.STAR;
        }
        return BodyType.PLANET;
    }

    public float getRadiusValue() {
        return parseOrDefault(radiusField.getText(), SandboxConfig.DEFAULT_RADIUS);
    }

    public float getMassValue() {
        return parseOrDefault(massField.getText(), SandboxConfig.DEFAULT_MASS);
    }

    public float getVelocityXValue() {
        return parseOrDefault(velocityXField.getText(), SandboxConfig.DEFAULT_VELOCITY);
    }

    public float getVelocityYValue() {
        return parseOrDefault(velocityYField.getText(), SandboxConfig.DEFAULT_VELOCITY);
    }

    public float getPositionXValue() {
        return parseOrDefault(positionXField.getText(), 640f);
    }

    public float getPositionYValue() {
        return parseOrDefault(positionYField.getText(), 360f);
    }

    public TextField getRadiusField() {
        return radiusField;
    }

    public TextField getMassField() {
        return massField;
    }

    public TextField getVelocityXField() {
        return velocityXField;
    }

    public TextField getVelocityYField() {
        return velocityYField;
    }

    public TextField getPositionXField() {
        return positionXField;
    }

    public TextField getPositionYField() {
        return positionYField;
    }

    public TextButton getStartSimulationButton() {
        return startSimulationButton;
    }

    public TextButton getClearBoardButton() {
        return clearBoardButton;
    }

    public TextButton getSpeed1xButton() {
        return speed1xButton;
    }

    public TextButton getSpeed2xButton() {
        return speed2xButton;
    }

    public TextButton getSpeed3xButton() {
        return speed3xButton;
    }

    public void setSimulationRunning(boolean running) {
        startSimulationButton.setText(running ? "Pause Simulation" : "Start Simulation");
    }

    public void setSelectedBodyType(BodyType type) {
        if (type == BodyType.STAR) {
            starButton.setChecked(true);
        } else {
            planetButton.setChecked(true);
        }
    }

    public void setSimulationSpeed(float speedMultiplier) {
        speed1xButton.setChecked(Math.abs(speedMultiplier - 1f) < 0.001f);
        speed2xButton.setChecked(Math.abs(speedMultiplier - 2f) < 0.001f);
        speed3xButton.setChecked(Math.abs(speedMultiplier - 3f) < 0.001f);
    }

    private TextField createNumericField(String initialValue, Skin skin) {
        TextField field = new TextField(initialValue, skin);
        field.setAlignment(Align.center);
        return field;
    }

    private float parseOrDefault(String text, float fallback) {
        if (text == null) {
            return fallback;
        }
        try {
            return Float.parseFloat(text.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private Label createHintLabel(String text, Skin skin) {
        Label hintLabel = new Label(text, skin);
        hintLabel.setWrap(true);
        hintLabel.setFontScale(0.9f);
        hintLabel.setColor(0.72f, 0.76f, 0.84f, 1f);
        return hintLabel;
    }
}
