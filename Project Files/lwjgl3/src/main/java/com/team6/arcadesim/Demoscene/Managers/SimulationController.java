package com.team6.arcadesim.Demoscene.Managers;

import java.util.function.Consumer;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.team6.arcadesim.AbstractGameMaster;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.CompositeShapeComponent;
import com.team6.arcadesim.components.MassComponent;
import com.team6.arcadesim.components.MovementComponent;
import com.team6.arcadesim.components.RadiusComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.managers.EntityManager;

public class SimulationController extends InputAdapter{
    private final AbstractGameMaster gameMaster;
    private final EntityManager entityManager;
    // What does consumer do?
    // A Consumer is a functional interface in Java that represents an operation that takes a single input argument and returns no result. 
    // It is often used to define callback functions or to pass behavior as an argument to methods.
    private final Consumer<Entity> onEntitySelected;
    private final SpawnValuesProvider spawnValuesProvider;


    public SimulationController(AbstractGameMaster gameMaster, EntityManager entityManager, Consumer<Entity> onEntitySelected, SpawnValuesProvider spawnValuesProvider) {
        this.gameMaster = gameMaster;
        this.entityManager = entityManager;
        this.onEntitySelected = onEntitySelected; 
        this.spawnValuesProvider = spawnValuesProvider;
    } //to handle click decisions and spawn entities based on user input

    public interface SpawnValuesProvider { //to provide necessary values for spawning entities
    float getMass();
    float getRadius();
    float getSpeedX();
    float getSpeedY();
    String getType();
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) { //to handle touch/click input
        // If the left mouse button is not pressed, ignore the input
        if (button != Input.Buttons.LEFT) {
            return false;
        }

        // Convert screen coordinates to world coordinates
        Vector2 worldCoordinates = gameMaster.getViewportManager().screenToWorld(x, y);

        //Debug log for touch coordinates
        System.out.println("Click world position: (" + worldCoordinates.x + ", " + worldCoordinates.y + ")");

        // Find if there is an entity at the clicked position with the provided spawn values
        Entity hit = findEntityAt(worldCoordinates.x, worldCoordinates.y);
        if (hit != null) {
            onEntitySelected.accept(hit); // Notify the game master about the selected entity. How? By calling the provided Consumer with the selected entity as an argument.
        } else {
            //If not, spawn a new entity at the clicked position using the provided spawn values
            float mass = clampMin(spawnValuesProvider.getMass(), 0.1f);
            float radius = clampMin(spawnValuesProvider.getRadius(), 1f);
            float speedX = spawnValuesProvider.getSpeedX();
            float speedY = spawnValuesProvider.getSpeedY();
            String type = spawnValuesProvider.getType();

            Entity spawnEntity = createEntityAt(
                worldCoordinates.x, 
                worldCoordinates.y,
                mass, 
                radius, 
                speedX, 
                speedY, 
                type);
            

            entityManager.addEntity(spawnEntity);
            onEntitySelected.accept(spawnEntity); // Notify the game master about the newly spawned entity

        }
        return true;
    }

    private Entity findEntityAt(float worldX, float worldY) { //to find an entity at the given world coordinates
        Entity nearest = null;
        float nearestDistanceSq = Float.MAX_VALUE; // Initialize with maximum value to find the closest entity
        
        for (Entity entity : entityManager.getAllEntities()) {
            if (!entity.isActive()) {
                continue; // Skip inactive entities
            }

            if (!entity.hasComponent(TransformComponent.class)|| (!entity.hasComponent(RadiusComponent.class))){
                continue; // Skip entities that don't have the necessary components
            }

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            RadiusComponent radius = entity.getComponent(RadiusComponent.class);

            // Check if the distance from the entity's center to the click position is less than the entity's radius
            float dx = worldX - transform.getPosition().x;
            float dy = worldY - transform.getPosition().y;
            float distanceSquared = dx * dx + dy * dy;

            float radiusSquared = radius.getRadius() * radius.getRadius();

            if (distanceSquared <= radiusSquared) {
                // Keep the closest center among all circles that contain the click
                if (distanceSquared < nearestDistanceSq) {
                    nearestDistanceSq = distanceSquared;
                    nearest = entity;
                }
            }
        }
        return nearest;
    }


    private Entity createEntityAt( //Factory method to create a new entity with the specified properties at the given world coordinates
        float x,
        float y,
        float mass,
        float radius,
        float speedX,
        float speedY,
        String type) {

    Entity entity = new Entity();

    entity.addComponent(new TransformComponent(x, y));
    entity.addComponent(new MassComponent(mass));

    MovementComponent movement = new MovementComponent();
    movement.setVelocity(speedX, speedY);
    entity.addComponent(movement);

    entity.addComponent(new RadiusComponent(radius));

    CompositeShapeComponent shape = new CompositeShapeComponent();
    Color color = "STAR".equalsIgnoreCase(type) ? Color.YELLOW : Color.CYAN;
    shape.addShape(
        CompositeShapeComponent.SubShape.createCircle(0, 0, radius, color, true)
    );
    entity.addComponent(shape);

    // Optional but recommended for collision subsystem compatibility
    entity.addComponent(new CollisionComponent(radius * 2f, radius * 2f, true, false));

    return entity;
    }

    //Clamp helper method to restrict a value within a specified range
    private float clampMin(float value, float min) {
        return Math.max(value, min);
    }

}


