package com.team6.engineLayer.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {

    private final Map<Class<? extends EngineEvent>, List<EngineEventListener<? extends EngineEvent>>> listeners;

    public EventBus() {
        this.listeners = new HashMap<>();
    }

    public <T extends EngineEvent> void subscribe(Class<T> eventType, EngineEventListener<T> listener) {
        listeners.computeIfAbsent(eventType, key -> new ArrayList<>()).add(listener);
    }

    public <T extends EngineEvent> void unsubscribe(Class<T> eventType, EngineEventListener<T> listener) {
        List<EngineEventListener<? extends EngineEvent>> handlers = listeners.get(eventType);
        if (handlers != null) {
            handlers.remove(listener);
            if (handlers.isEmpty()) {
                listeners.remove(eventType);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends EngineEvent> void publish(T event) {
        List<EngineEventListener<? extends EngineEvent>> handlers = listeners.get(event.getClass());
        if (handlers == null) {
            return;
        }

        List<EngineEventListener<? extends EngineEvent>> snapshot = new ArrayList<>(handlers);
        for (EngineEventListener<? extends EngineEvent> handler : snapshot) {
            ((EngineEventListener<T>) handler).onEvent(event);
        }
    }
}
