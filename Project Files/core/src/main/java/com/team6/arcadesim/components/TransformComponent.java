package com.team6.arcadesim.components;

import com.team6.arcadesim.ecs.Component;

public class TransformComponent extends Component {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void update(float deltaTime) {
        // Does nothing, the manager handles the transformation
    }


}
