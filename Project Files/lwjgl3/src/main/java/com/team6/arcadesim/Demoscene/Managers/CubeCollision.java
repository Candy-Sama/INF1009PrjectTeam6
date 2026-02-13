package com.team6.arcadesim.Demoscene.Managers;
import com.team6.arcadesim.managers.CollisionManager;

public class CubeCollision extends CollisionManager {
    @Override
    public void update(float dt, java.util.List<com.team6.arcadesim.ecs.Entity> entities) {
        // Custom collision logic for cubes can be implemented here
        super.update(dt, entities); // Call the base class update for default behavior

        
    }

}
