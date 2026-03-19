package com.team6.arcadesim.sandbox.render;

import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class TrajectoryRenderer {

    private final ShapeRenderer shapeRenderer;
    private final Color pathColor;

    public TrajectoryRenderer() {
        this.shapeRenderer = new ShapeRenderer();
        this.pathColor = new Color(0.55f, 0.82f, 1f, 0.9f);
    }

    public void render(Map<Integer, List<Vector2>> predictedPaths, OrthographicCamera camera) {
        if (predictedPaths == null || predictedPaths.isEmpty() || camera == null) {
            return;
        }

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(pathColor);

        for (List<Vector2> path : predictedPaths.values()) {
            if (path == null || path.size() < 2) {
                continue;
            }

            for (int i = 1; i < path.size(); i++) {
                Vector2 p0 = path.get(i - 1);
                Vector2 p1 = path.get(i);
                shapeRenderer.line(p0.x, p0.y, p1.x, p1.y);
            }
        }

        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
