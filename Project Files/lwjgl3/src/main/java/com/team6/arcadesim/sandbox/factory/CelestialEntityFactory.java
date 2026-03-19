package com.team6.arcadesim.sandbox.factory;

import com.badlogic.gdx.graphics.Color;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.CompositeShapeComponent;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.RadiusComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.sandbox.BodyType;
import com.team6.arcadesim.sandbox.config.SandboxConfig;

public class CelestialEntityFactory {

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

        Entity star = createBaseBody(x, y, resolvedRadius, resolvedMass, Color.YELLOW);
        return star;
    }

    public Entity createPlanet(float x, float y, float radius, float mass, float velocityX, float velocityY) {
        float resolvedRadius = SandboxConfig.clampRadius(radius);
        float resolvedMass = SandboxConfig.clampMass(mass);
        float resolvedVelocityX = SandboxConfig.clampVelocity(velocityX);
        float resolvedVelocityY = SandboxConfig.clampVelocity(velocityY);

        Entity planet = createBaseBody(x, y, resolvedRadius, resolvedMass, new Color(0.22f, 0.55f, 0.95f, 1f));
        planet.addComponent(new MovementComponent(resolvedVelocityX, resolvedVelocityY));
        return planet;
    }

    private Entity createBaseBody(float x, float y, float radius, float mass, Color color) {
        Entity body = new Entity();
        body.addComponent(new TransformComponent(x, y));
        body.addComponent(new MassComponent(mass));
        body.addComponent(new RadiusComponent(radius));
        body.addComponent(new CollisionComponent(radius * 2f, radius * 2f, true, false));

        CompositeShapeComponent shape = new CompositeShapeComponent();
        shape.addShape(CompositeShapeComponent.SubShape.createCircle(0f, 0f, radius, color, true));
        body.addComponent(shape);
        return body;
    }

}
