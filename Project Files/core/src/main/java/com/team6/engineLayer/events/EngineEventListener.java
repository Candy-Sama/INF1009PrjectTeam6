package com.team6.engineLayer.events;

@FunctionalInterface
public interface EngineEventListener<T extends EngineEvent> {
    void onEvent(T event);
}
