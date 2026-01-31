package com.team6.arcadesim.ecs;

import com.badlogic.gdx.graphics.Color;
import java.util.List;

public abstract class Entity {
 
    private List<Component> listOfComponents;
    private int id;
    private boolean isActive;
    
    public void addComponent(Component component) {
        listOfComponents.add(component);
    }

    //gets the component of the specified class type
    public Component get(Class<? extends Component> componentClass) {
        for (Component component : listOfComponents) {
            if (component.getClass().equals(componentClass)) {
                return component;
            }
        }
        return null;
    }

    public void update(float delta) {
        for (Component component : listOfComponents) {
            component.update(delta);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
