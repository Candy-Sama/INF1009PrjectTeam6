package com.team6.arcadesim.sandbox.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;

public class SandboxControlPanel {

    private final Table rootTable;
    private final TextButton starButton;
    private final TextButton planetButton;
    private final TextField radiusField;
    private final TextField massField;
    private final TextField accelerationXField;
    private final TextField accelerationYField;
    private final TextField positionXField;
    private final TextField positionYField;
    private final TextButton startSimulationButton;

    public SandboxControlPanel(Skin skin) {
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().right();
        rootTable.pad(16f);

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("panel-bg"));
        panel.pad(18f);
        panel.defaults().padBottom(10f).left();

        rootTable.add(panel).width(360f).growY();

        Label headingLabel = new Label("Sandbox Control Panel", skin, "heading");
        panel.add(headingLabel).padBottom(16f);
        panel.row();

        panel.add(new Label("Body Type", skin));
        panel.row();

        Table bodyTypeRow = new Table();
        starButton = new TextButton("Star", skin);
        planetButton = new TextButton("Planet", skin);
        bodyTypeRow.add(starButton).width(155f).height(42f).padRight(8f);
        bodyTypeRow.add(planetButton).width(155f).height(42f);
        panel.add(bodyTypeRow).padBottom(14f);
        panel.row();

        panel.add(new Label("Radius", skin));
        panel.row();
        radiusField = createNumericField("20", skin);
        panel.add(radiusField).width(320f).height(40f);
        panel.row();

        panel.add(new Label("Mass", skin));
        panel.row();
        massField = createNumericField("150", skin);
        panel.add(massField).width(320f).height(40f);
        panel.row();

        panel.add(new Label("Initial Acceleration Vector", skin));
        panel.row();
        Table accelerationRow = new Table();
        accelerationXField = createNumericField("0", skin);
        accelerationYField = createNumericField("0", skin);
        accelerationRow.add(new Label("X", skin)).width(16f);
        accelerationRow.add(accelerationXField).width(132f).height(40f).padRight(8f);
        accelerationRow.add(new Label("Y", skin)).width(16f);
        accelerationRow.add(accelerationYField).width(132f).height(40f);
        panel.add(accelerationRow);
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

        startSimulationButton = new TextButton("Start Simulation", skin);
        panel.add(startSimulationButton).width(320f).height(50f).padTop(4f);
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

    public TextField getRadiusField() {
        return radiusField;
    }

    public TextField getMassField() {
        return massField;
    }

    public TextField getAccelerationXField() {
        return accelerationXField;
    }

    public TextField getAccelerationYField() {
        return accelerationYField;
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

    private TextField createNumericField(String initialValue, Skin skin) {
        TextField field = new TextField(initialValue, skin);
        field.setAlignment(Align.center);
        return field;
    }
}
