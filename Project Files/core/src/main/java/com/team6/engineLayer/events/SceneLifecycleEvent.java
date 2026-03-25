package com.team6.engineLayer.events;

public class SceneLifecycleEvent implements EngineEvent {

    public enum Type {
        CHANGED,
        PUSHED,
        POPPED
    }

    private final Type type;
    private final String fromScene;
    private final String toScene;

    public SceneLifecycleEvent(Type type, String fromScene, String toScene) {
        this.type = type;
        this.fromScene = fromScene;
        this.toScene = toScene;
    }

    public Type getType() {
        return type;
    }

    public String getFromScene() {
        return fromScene;
    }

    public String getToScene() {
        return toScene;
    }
}
