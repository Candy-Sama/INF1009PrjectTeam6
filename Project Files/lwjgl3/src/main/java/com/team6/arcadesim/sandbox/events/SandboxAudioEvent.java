package com.team6.arcadesim.sandbox.events;

import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.events.EngineEvent;

public class SandboxAudioEvent implements EngineEvent {

    public enum Type {
        ENTITY_SPAWNED,
        MUTUAL_DESTRUCTION
    }

    private final Type type;
    private final Entity primaryEntity;
    private final Entity secondaryEntity;

    public SandboxAudioEvent(Type type, Entity primaryEntity, Entity secondaryEntity) {
        this.type = type;
        this.primaryEntity = primaryEntity;
        this.secondaryEntity = secondaryEntity;
    }

    public Type getType() {
        return type;
    }

    public Entity getPrimaryEntity() {
        return primaryEntity;
    }

    public Entity getSecondaryEntity() {
        return secondaryEntity;
    }
}
