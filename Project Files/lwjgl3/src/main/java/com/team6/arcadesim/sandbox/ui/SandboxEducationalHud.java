package com.team6.arcadesim.sandbox.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class SandboxEducationalHud {

    private static final float PANEL_WIDTH = 290f;
    private static final float PANEL_PADDING = 16f;

    private final Table rootTable;
    private final Skin skin;
    private final Label selectedTypeValueLabel;
    private final Label massValueLabel;
    private final Label radiusValueLabel;
    private final Label speedValueLabel;
    private final Label accelValueLabel;
    private final Label nearestStarValueLabel;
    private final Label orbitTypeValueLabel;

    public SandboxEducationalHud(Skin skin) {
        this.skin = skin;
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().left();
        rootTable.pad(16f);

        Table panel = new Table();
        panel.setBackground(skin.getDrawable("panel-bg"));
        panel.pad(PANEL_PADDING);
        panel.defaults().padBottom(8f).left();
        rootTable.add(panel).width(PANEL_WIDTH).top().left();

        Label headingLabel = new Label("Statistics", skin, "heading");
        panel.add(headingLabel).padBottom(12f);
        panel.row();

        selectedTypeValueLabel = new Label("None", skin);
        massValueLabel = new Label("-", skin);
        radiusValueLabel = new Label("-", skin);
        speedValueLabel = new Label("-", skin);
        accelValueLabel = new Label("-", skin);
        nearestStarValueLabel = new Label("N/A", skin);
        orbitTypeValueLabel = new Label("N/A", skin);

        addHudRow(panel, "Selected", selectedTypeValueLabel);
        addHudRow(panel, "Mass", massValueLabel);
        addHudRow(panel, "Radius", radiusValueLabel);
        addHudRow(panel, "Speed |v|", speedValueLabel);
        addHudRow(panel, "Accel |a|", accelValueLabel);
        addHudRow(panel, "Nearest Star", nearestStarValueLabel);
        addHudRow(panel, "Orbit Type", orbitTypeValueLabel);
        panel.row();
        panel.add().growY();
    }

    public Table getRootTable() {
        return rootTable;
    }

    public void setNoSelection() {
        selectedTypeValueLabel.setText("None");
        massValueLabel.setText("-");
        radiusValueLabel.setText("-");
        speedValueLabel.setText("-");
        accelValueLabel.setText("-");
        nearestStarValueLabel.setText("N/A");
        orbitTypeValueLabel.setText("N/A");
    }

    public void setStats(
        String selectedType,
        float mass,
        float radius,
        float speed,
        float acceleration,
        float nearestStarDistance,
        String orbitType
    ) {
        selectedTypeValueLabel.setText(isBlank(selectedType) ? "Unknown" : selectedType);
        massValueLabel.setText(formatValue(mass));
        radiusValueLabel.setText(formatValue(radius));
        speedValueLabel.setText(formatValue(speed) + " u/s");
        accelValueLabel.setText(formatValue(acceleration) + " u/s^2");
        nearestStarValueLabel.setText(nearestStarDistance < 0f ? "N/A" : formatValue(nearestStarDistance) + " u");
        orbitTypeValueLabel.setText(isBlank(orbitType) ? "N/A" : orbitType);
    }

    private void addHudRow(Table panel, String title, Label valueLabel) {
        Table row = new Table();
        row.defaults().padBottom(4f);

        Label titleLabel = new Label(title, skin);
        titleLabel.setAlignment(Align.left);
        valueLabel.setAlignment(Align.right);

        row.add(titleLabel).left().expandX().fillX();
        row.add(valueLabel).right().width(120f);
        panel.add(row).width(PANEL_WIDTH - (PANEL_PADDING * 2f)).left();
        panel.row();
    }

    private String formatValue(float value) {
        if (!Float.isFinite(value)) {
            return "-";
        }
        return String.format("%.2f", value);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
