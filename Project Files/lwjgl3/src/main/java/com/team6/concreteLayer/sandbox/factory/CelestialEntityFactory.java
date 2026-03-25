package com.team6.arcadesim.sandbox.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.CompositeShapeComponent;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.RadiusComponent;
import com.team6.arcadesim.components.SpriteComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.sandbox.BodyType;
import com.team6.arcadesim.sandbox.config.SandboxConfig;

public class CelestialEntityFactory {

    private final List<TextureRegion> starRegions;
    private final List<TextureRegion> planetRegions;

    public CelestialEntityFactory() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public CelestialEntityFactory(List<TextureRegion> starRegions, List<TextureRegion> planetRegions) {
        this.starRegions = (starRegions == null) ? Collections.emptyList() : new ArrayList<>(starRegions);
        this.planetRegions = (planetRegions == null) ? Collections.emptyList() : new ArrayList<>(planetRegions);
    }

    public Entity createBody(
        BodyType bodyType,
        float x,
        float y,
        float radius,
        float mass,
        float velocityX,
        float velocityY
    ) {
        BodyType resolvedType = (bodyType == null) ? BodyType.PLANET : bodyType;
        if (resolvedType == BodyType.STAR) {
            return createStar(x, y, radius, mass);
        }
        return createPlanet(x, y, radius, mass, velocityX, velocityY);
    }

    public Entity createStar(float x, float y, float radius, float mass) {
        float resolvedRadius = SandboxConfig.clampRadius(radius);
        float resolvedMass = SandboxConfig.clampMass(mass);
        TextureRegion region = randomRegion(starRegions);

        Entity star = createBaseBody(x, y, resolvedRadius, resolvedMass, region, true);
        return star;
    }

    public Entity createPlanet(float x, float y, float radius, float mass, float velocityX, float velocityY) {
        float resolvedRadius = SandboxConfig.clampRadius(radius);
        float resolvedMass = SandboxConfig.clampMass(mass);
        float resolvedVelocityX = SandboxConfig.clampVelocity(velocityX);
        float resolvedVelocityY = SandboxConfig.clampVelocity(velocityY);
        TextureRegion region = randomRegion(planetRegions);

        Entity planet = createBaseBody(x, y, resolvedRadius, resolvedMass, region, false);
        planet.addComponent(new MovementComponent(resolvedVelocityX, resolvedVelocityY));
        return planet;
    }

    private Entity createBaseBody(float x, float y, float radius, float mass, TextureRegion region, boolean isStar) {
        Entity body = new Entity();
        body.addComponent(new TransformComponent(x, y));
        body.addComponent(new MassComponent(mass));
        body.addComponent(new RadiusComponent(radius));
        body.addComponent(new CollisionComponent(radius * 2f, radius * 2f, true, false));

        if (region != null) {
            body.addComponent(new SpriteComponent(region));
        } else {
            Color fallbackColor = isStar ? Color.YELLOW : new Color(0.22f, 0.55f, 0.95f, 1f);
            CompositeShapeComponent shape = new CompositeShapeComponent();
            shape.addShape(CompositeShapeComponent.SubShape.createCircle(0f, 0f, radius, fallbackColor, true));
            body.addComponent(shape);
        }
        return body;
    }

    private TextureRegion randomRegion(List<TextureRegion> regions) {
        if (regions == null || regions.isEmpty()) {
            return null;
        }
        return regions.get(MathUtils.random(regions.size() - 1));
    }
}
