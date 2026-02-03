/* Mus Code */

package com.team6.arcadesim.ecs;

import com.badlogic.gdx.graphics.Color;
import java.util.List;

public abstract class Entity {
 
    private List<Component> listOfComponents;
    private int id;
    private String name;
    private boolean isActive;
    
    public void addComponent(Component component) {
        listOfComponents.add(component);
    }

    public Entity(int id, String name) {
        this.id = id;
        this.name = name;
        this.isActive = true;
    }

    //gets the component of the specified class type
    public Component get(Class<? extends Component> componentClass) { //get component of specified class type
        for (Component component : listOfComponents) { // for each component in listOfComponents
            if (component.getClass().equals(componentClass)) { 
                return component;
            }
        }
        return null;
    }

    public void update(float delta) { // update all components using their component update method
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


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }
}
