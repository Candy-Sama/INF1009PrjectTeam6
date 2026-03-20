package com.team6.arcadesim.sandbox.controllers;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.components.RadiusComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.managers.EntityManager;
import com.team6.arcadesim.sandbox.BodyType;
import com.team6.arcadesim.sandbox.config.SandboxConfig;
import com.team6.arcadesim.sandbox.events.SandboxAudioEvent;
import com.team6.arcadesim.sandbox.factory.CelestialEntityFactory;

public class SimulationController extends InputAdapter {

    private final AbstractGameMaster gameMaster;
    private final EntityManager entityManager;
    private final CelestialEntityFactory celestialEntityFactory;
    private final Consumer<Entity> onEntitySelected;
    private final Runnable onClearSelection;
    private final Supplier<Boolean> isPointerOverUi;
    private final Supplier<SpawnRequest> spawnRequestSupplier;

    private boolean panning;
    private int lastPanX;
    private int lastPanY;

    public SimulationController(
        AbstractGameMaster gameMaster,
        EntityManager entityManager,
        CelestialEntityFactory celestialEntityFactory,
        Consumer<Entity> onEntitySelected,
        Runnable onClearSelection,
        Supplier<Boolean> isPointerOverUi,
        Supplier<SpawnRequest> spawnRequestSupplier
    ) {
        this.gameMaster = gameMaster;
        this.entityManager = entityManager;
        this.celestialEntityFactory = celestialEntityFactory;
        this.onEntitySelected = onEntitySelected;
        this.onClearSelection = onClearSelection;
        this.isPointerOverUi = isPointerOverUi;
        this.spawnRequestSupplier = spawnRequestSupplier;
        this.panning = false;
        this.lastPanX = 0;
        this.lastPanY = 0;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE) {
            panning = true;
            lastPanX = screenX;
            lastPanY = screenY;
            return true;
        }

        if (button != Input.Buttons.LEFT) {
            return false;
        }

        if (isPointerOverUi.get()) {
            return false;
        }

        Vector2 worldPosition = gameMaster.getViewportManager().screenToWorld(screenX, screenY);
        Entity clickedEntity = findNearestEntityInsideRadius(worldPosition.x, worldPosition.y);
        if (clickedEntity != null) {
            onEntitySelected.accept(clickedEntity);
            return true;
        }

        if (countActiveEntities() >= SandboxConfig.MAX_ACTIVE_BODIES) {
            System.out.println("Spawn blocked");
            onClearSelection.run();
            return true;
        }

        SpawnRequest spawnRequest = spawnRequestSupplier.get();
        if (spawnRequest == null) {
            return false;
        }

        if (overlapsExistingEntity(worldPosition.x, worldPosition.y, spawnRequest.radius)) {
            System.out.println("Spawn blocked");
            onClearSelection.run();
            return true;
        }

        Entity spawnedEntity = celestialEntityFactory.createBody(
            spawnRequest.bodyType,
            worldPosition.x,
            worldPosition.y,
            spawnRequest.radius,
            spawnRequest.mass,
            spawnRequest.velocityX,
            spawnRequest.velocityY
        );
        entityManager.addEntity(spawnedEntity);
        if (gameMaster.getEventBus() != null) {
            gameMaster.getEventBus().publish(
                new SandboxAudioEvent(SandboxAudioEvent.Type.ENTITY_SPAWNED, spawnedEntity, null)
            );
        }
        onEntitySelected.accept(spawnedEntity);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.RIGHT || button == Input.Buttons.MIDDLE) {
            panning = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!panning) {
            return false;
        }

        OrthographicCamera camera = gameMaster.getViewportManager().getCamera();
        float dx = screenX - lastPanX;
        float dy = screenY - lastPanY;
        camera.translate(-dx * camera.zoom, dy * camera.zoom);
        camera.update();
        lastPanX = screenX;
        lastPanY = screenY;
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (isPointerOverUi.get()) {
            return false;
        }
        OrthographicCamera camera = gameMaster.getViewportManager().getCamera();
        float newZoom = camera.zoom + (amountY * SandboxConfig.CAMERA_ZOOM_STEP);
        camera.zoom = Math.max(SandboxConfig.CAMERA_MIN_ZOOM, Math.min(SandboxConfig.CAMERA_MAX_ZOOM, newZoom));
        camera.update();
        return true;
    }

    private Entity findNearestEntityInsideRadius(float worldX, float worldY) {
        Entity nearest = null;
        float nearestDistance = Float.MAX_VALUE;

        for (Entity entity : entityManager.getAllEntities()) {
            if (entity == null || !entity.isActive()) {
                continue;
            }
            if (!entity.hasComponent(TransformComponent.class) || !entity.hasComponent(RadiusComponent.class)) {
                continue;
            }

            Vector2 position = entity.getComponent(TransformComponent.class).getPosition();
            float radius = entity.getComponent(RadiusComponent.class).getRadius();
            float distance = Vector2.dst(worldX, worldY, position.x, position.y);

            if (distance <= radius && distance < nearestDistance) {
                nearest = entity;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    private boolean overlapsExistingEntity(float worldX, float worldY, float newRadius) {
        for (Entity entity : entityManager.getAllEntities()) {
            if (entity == null || !entity.isActive()) {
                continue;
            }
            if (!entity.hasComponent(TransformComponent.class) || !entity.hasComponent(RadiusComponent.class)) {
                continue;
            }

            Vector2 position = entity.getComponent(TransformComponent.class).getPosition();
            float existingRadius = entity.getComponent(RadiusComponent.class).getRadius();
            float distance = Vector2.dst(worldX, worldY, position.x, position.y);
            if (distance <= existingRadius + newRadius) {
                return true;
            }
        }
        return false;
    }

    private int countActiveEntities() {
        int activeCount = 0;
        for (Entity entity : entityManager.getAllEntities()) {
            if (entity != null && entity.isActive()) {
                activeCount++;
            }
        }
        return activeCount;
    }

    public static class SpawnRequest {
        private final BodyType bodyType;
        private final float mass;
        private final float radius;
        private final float velocityX;
        private final float velocityY;

        public SpawnRequest(BodyType bodyType, float mass, float radius, float velocityX, float velocityY) {
            this.bodyType = bodyType;
            this.mass = mass;
            this.radius = radius;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
        }
    }
}
