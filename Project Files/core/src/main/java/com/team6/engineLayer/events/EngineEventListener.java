package com.team6.arcadesim.events;

@FunctionalInterface
public interface EngineEventListener<T extends EngineEvent> {
    void onEvent(T event);
}
