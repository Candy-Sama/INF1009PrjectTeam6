package com.team6.arcadesim.ecs;

import java.util.HashMap;
import java.util.Map;

public abstract class Entity {
    private static int nextId = 0;

    private int id;
    private boolean active = true;

    // Map of Component type to Component instance
    private Map<Class<? extends Component>, Component> components = new HashMap<>();

    public Entity()
    {
        this.id = nextId++;
        this.active = true;
        this.components = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void addComponent(Component c) {
        components.put(c.getClass(), c);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return type.cast(components.get(type));
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void dispose() {
        this.active = false;
        this.components.clear();
    }
}
