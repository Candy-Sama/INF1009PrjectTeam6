package com.team6.arcadesim.sandbox.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.team6.arcadesim.logging.EngineLogger;
import com.team6.arcadesim.logging.NoOpEngineLogger;
import com.team6.arcadesim.sandbox.config.SandboxConfig;
import com.team6.arcadesim.sandbox.config.SandboxConfig.SpriteCell;

public class CelestialSpriteRegistry implements Disposable {

    private Texture spritesheet;
    private final List<TextureRegion> starRegions;
    private final List<TextureRegion> planetRegions;
    private final EngineLogger logger;

    public CelestialSpriteRegistry() {
        this(new NoOpEngineLogger());
    }

    public CelestialSpriteRegistry(EngineLogger logger) {
        this.spritesheet = null;
        this.starRegions = new ArrayList<>();
        this.planetRegions = new ArrayList<>();
        this.logger = (logger == null) ? new NoOpEngineLogger() : logger;
    }

    public void load() {
        clearPools();
        disposeSpritesheetOnly();

        try {
            if (!Gdx.files.internal(SandboxConfig.SPRITESHEET_PATH).exists()) {
                logger.warn("Spritesheet not found: " + SandboxConfig.SPRITESHEET_PATH);
                return;
            }

            spritesheet = new Texture(Gdx.files.internal(SandboxConfig.SPRITESHEET_PATH));
            int cellSize = SandboxConfig.SPRITE_CELL_SIZE;
            int totalRows = spritesheet.getHeight() / cellSize;
            int totalCols = spritesheet.getWidth() / cellSize;

            buildPool("STAR", SandboxConfig.STAR_SPRITE_CELLS, starRegions, totalRows, totalCols, cellSize);
            buildPool("PLANET", SandboxConfig.PLANET_SPRITE_CELLS, planetRegions, totalRows, totalCols, cellSize);
        } catch (Exception ex) {
            logger.error("Failed loading spritesheet registry: " + ex.getMessage());
            clearPools();
            disposeSpritesheetOnly();
        }
    }

    public List<TextureRegion> getStarRegions() {
        return Collections.unmodifiableList(starRegions);
    }

    public List<TextureRegion> getPlanetRegions() {
        return Collections.unmodifiableList(planetRegions);
    }

    @Override
    public void dispose() {
        clearPools();
        disposeSpritesheetOnly();
    }

    private void buildPool(
        String poolName,
        List<SpriteCell> cells,
        List<TextureRegion> outPool,
        int totalRows,
        int totalCols,
        int cellSize
    ) {
        for (SpriteCell cell : cells) {
            if (cell == null) {
                continue;
            }
            int row = cell.getRow();
            int col = cell.getCol();
            if (row < 0 || col < 0 || row >= totalRows || col >= totalCols) {
                logger.warn(
                    "Skipping invalid " + poolName + " sprite cell (" + row + "," + col + ") " +
                    "for sheet " + totalCols + "x" + totalRows + " cells."
                );
                continue;
            }

            // TextureRegion pixel slicing here is top-left row order for spritesheet usage.
            int x = col * cellSize;
            int y = row * cellSize;
            outPool.add(new TextureRegion(spritesheet, x, y, cellSize, cellSize));
        }
    }

    private void clearPools() {
        starRegions.clear();
        planetRegions.clear();
    }

    private void disposeSpritesheetOnly() {
        if (spritesheet != null) {
            spritesheet.dispose();
            spritesheet = null;
        }
    }
}
