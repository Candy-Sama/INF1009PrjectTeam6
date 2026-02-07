package com.team6.arcadesim.ecs;

import java.util.Map;

public abstract class Entity {
    private int id;
    private boolean active;
    private Map<Class<? extends Component>, Component> components;

    public void addComponent(Component c) {

    }

    public <T extends Component> T getComponent(Class<T> type) {
        return null;
    }

    public <T extends Component> boolean hasComponent(Class<T> type) {
        return false;
    }
}
