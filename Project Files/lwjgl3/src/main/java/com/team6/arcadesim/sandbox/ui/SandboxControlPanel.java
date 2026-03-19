package com.team6.arcadesim.sandbox.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Align;
import com.team6.arcadesim.sandbox.BodyType;
import com.team6.arcadesim.sandbox.config.SandboxConfig;

public class SandboxControlPanel {

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

    public SandboxControlPanel(Skin skin) {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().right();
        rootTable.pad(16f);

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("panel-bg"));
        panel.setTouchable(Touchable.enabled);
        panel.pad(18f);
        panel.defaults().padBottom(10f).left();

        rootTable.add(panel).width(280f).growY();

        Label headingLabel = new Label("Sandbox Control Panel", skin, "heading");
        panel.add(headingLabel).padBottom(16f);
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
        bodyTypeRow.add(starButton).width(155f).height(42f).padRight(8f);
        bodyTypeRow.add(planetButton).width(155f).height(42f);
        panel.add(bodyTypeRow).padBottom(14f);
        panel.row();

        panel.add(new Label("Radius", skin));
        panel.row();
        radiusField = createNumericField(String.valueOf((int) SandboxConfig.DEFAULT_RADIUS), skin);
        panel.add(radiusField).width(320f).height(40f);
        panel.row();

        panel.add(new Label("Mass", skin));
        panel.row();
        massField = createNumericField(String.valueOf((int) SandboxConfig.DEFAULT_MASS), skin);
        panel.add(massField).width(320f).height(40f);
        panel.row();

        panel.add(new Label("Initial Velocity Vector", skin));
        panel.row();
        Table velocityRow = new Table();
        velocityXField = createNumericField(String.valueOf((int) SandboxConfig.DEFAULT_VELOCITY), skin);
        velocityYField = createNumericField(String.valueOf((int) SandboxConfig.DEFAULT_VELOCITY), skin);
        velocityRow.add(new Label("X", skin)).width(16f);
        velocityRow.add(velocityXField).width(132f).height(40f).padRight(8f);
        velocityRow.add(new Label("Y", skin)).width(16f);
        velocityRow.add(velocityYField).width(132f).height(40f);
        panel.add(velocityRow);
        panel.row();

        panel.add(new Label("Initial Position Vector", skin));
        panel.row();
        Table positionRow = new Table();
        positionXField = createNumericField("640", skin);
        positionYField = createNumericField("360", skin);
        positionRow.add(new Label("X", skin)).width(16f);
        positionRow.add(positionXField).width(132f).height(40f).padRight(8f);
        positionRow.add(new Label("Y", skin)).width(16f);
        positionRow.add(positionYField).width(132f).height(40f);
        panel.add(positionRow).padBottom(20f);
        panel.row();

        Table actionsRow = new Table();
        startSimulationButton = new TextButton("Start Simulation", skin);
        clearBoardButton = new TextButton("Clear Board", skin);
        actionsRow.add(startSimulationButton).width(155f).height(50f).padRight(10f);
        actionsRow.add(clearBoardButton).width(155f).height(50f);
        panel.add(actionsRow).padTop(4f);
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
}
