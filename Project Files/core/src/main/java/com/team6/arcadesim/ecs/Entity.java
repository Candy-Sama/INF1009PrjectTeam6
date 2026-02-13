package com.team6.arcadesim.ecs;

import java.util.HashMap;
import java.util.Map;

public abstract class Entity {

    // Static counter shared by ALL entities to ensure unique IDs
    private static int nextId = 0;

    private int id;
    private boolean active;
    
    // The "Bag" of components. Key = Class Type, Value = The Component Instance
    private Map<Class<? extends Component>, Component> components;

    public Entity() {
        this.id = nextId++;
        this.active = true;
        this.components = new HashMap<>();
    }

    public int getId() {
        return id;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
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
}