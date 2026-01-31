package com.team6.arcadesim.ecs;

import com.badlogic.gdx.scenes.scene2d.ui.List;

public class EntityManager {

    private List<Entity> entityList;

    public EntityManager() {
     entityList = new List<Entity>();
    }
}
