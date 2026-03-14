package com.team6.arcadesim.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.events.CollisionEvent;
import com.team6.arcadesim.events.EventBus;
import com.team6.arcadesim.interfaces.CollisionListener;
import com.team6.arcadesim.interfaces.CollisionResolver;
import com.team6.arcadesim.logging.EngineLogger;
import com.team6.arcadesim.logging.NoOpEngineLogger;

public class CollisionManager {

    private final List<CollisionListener> listeners;
    private CollisionResolver resolver;
    private final Rectangle rectA;
    private final Rectangle rectB;
    private final Map<Long, CollisionPair> activeContacts;
    private EventBus eventBus;
    private EngineLogger logger;

    public CollisionManager() {
        this.listeners = new ArrayList<>();
        this.resolver = (a, b) -> {};
        this.rectA = new Rectangle();
        this.rectB = new Rectangle();
        this.activeContacts = new HashMap<>();
        this.eventBus = null;
        this.logger = new NoOpEngineLogger();
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void setLogger(EngineLogger logger) {
        this.logger = (logger == null) ? new NoOpEngineLogger() : logger;
    }

    public void setResolver(CollisionResolver resolver) {
        this.resolver = (resolver == null) ? (a, b) -> {} : resolver;
    }

    public void addCollisionListener(CollisionListener listener) {
        listeners.add(listener);
    }

    public void removeCollisionListener(CollisionListener listener) {
        listeners.remove(listener);
    }

    public void update(float dt, List<Entity> entities) {
        List<Entity> collidableEntities = new ArrayList<>();
        for (Entity entity : entities) {
            if (isValid(entity)) {
                collidableEntities.add(entity);
            }
        }

        Map<Long, CollisionPair> contactsThisFrame = new HashMap<>();

        for (int i = 0; i < collidableEntities.size(); i++) {
            Entity a = collidableEntities.get(i);
            CollisionComponent ca = a.getComponent(CollisionComponent.class);

            for (int j = i + 1; j < collidableEntities.size(); j++) {
                Entity b = collidableEntities.get(j);
                CollisionComponent cb = b.getComponent(CollisionComponent.class);

                if (!broadphaseOverlap(a, b)) {
                    continue;
                }
                if (!narrowphaseOverlap(a, b)) {
                    continue;
                }

                long pairKey = toPairKey(a, b);
                contactsThisFrame.put(pairKey, new CollisionPair(a, b));

                if (activeContacts.containsKey(pairKey)) {
                    notifyCollisionStay(a, b);
                } else {
                    notifyCollisionStart(a, b);
                }

                if (ca.isSolid() && cb.isSolid() && !ca.isTrigger() && !cb.isTrigger()) {
                    resolver.resolve(a, b);
                }
            }
        }

        for (Map.Entry<Long, CollisionPair> entry : activeContacts.entrySet()) {
            if (!contactsThisFrame.containsKey(entry.getKey())) {
                CollisionPair endedPair = entry.getValue();
                notifyCollisionEnd(endedPair.entityA, endedPair.entityB);
            }
        }

        activeContacts.clear();
        activeContacts.putAll(contactsThisFrame);
    }

    private void notifyCollisionStart(Entity a, Entity b) {
        for (CollisionListener listener : listeners) {
            listener.onCollisionStart(a, b);
        }
        if (eventBus != null) {
            eventBus.publish(new CollisionEvent(CollisionEvent.Type.START, a, b));
        }
    }

    private void notifyCollisionStay(Entity a, Entity b) {
        for (CollisionListener listener : listeners) {
            listener.onCollisionStay(a, b);
        }
        if (eventBus != null) {
            eventBus.publish(new CollisionEvent(CollisionEvent.Type.STAY, a, b));
        }
    }

    private void notifyCollisionEnd(Entity a, Entity b) {
        for (CollisionListener listener : listeners) {
            listener.onCollisionEnd(a, b);
        }
        if (eventBus != null) {
            eventBus.publish(new CollisionEvent(CollisionEvent.Type.END, a, b));
        }
    }

    private boolean broadphaseOverlap(Entity a, Entity b) {
        TransformComponent ta = a.getComponent(TransformComponent.class);
        CollisionComponent ca = a.getComponent(CollisionComponent.class);
        TransformComponent tb = b.getComponent(TransformComponent.class);
        CollisionComponent cb = b.getComponent(CollisionComponent.class);

        float aLeft = ta.getPosition().x - ca.getWidth() / 2f;
        float aBottom = ta.getPosition().y - ca.getHeight() / 2f;
        float bLeft = tb.getPosition().x - cb.getWidth() / 2f;
        float bBottom = tb.getPosition().y - cb.getHeight() / 2f;

        rectA.set(aLeft, aBottom, ca.getWidth(), ca.getHeight());
        rectB.set(bLeft, bBottom, cb.getWidth(), cb.getHeight());
        return rectA.overlaps(rectB);
    }

    private boolean narrowphaseOverlap(Entity a, Entity b) {
        // Default narrowphase is AABB overlap; shape-specific narrowphase can replace this later.
        return broadphaseOverlap(a, b);
    }

    private long toPairKey(Entity a, Entity b) {
        long idA = a.getId() & 0xffffffffL;
        long idB = b.getId() & 0xffffffffL;
        long min = Math.min(idA, idB);
        long max = Math.max(idA, idB);
        return (min << 32) | max;
    }

    private boolean isValid(Entity e) {
        return e.isActive()
            && e.hasComponent(TransformComponent.class)
            && e.hasComponent(CollisionComponent.class);
    }

    public void reset() {
        listeners.clear();
        this.resolver = (a, b) -> {};
        this.activeContacts.clear();
    }

    private static class CollisionPair {
        private final Entity entityA;
        private final Entity entityB;

        private CollisionPair(Entity entityA, Entity entityB) {
            this.entityA = entityA;
            this.entityB = entityB;
        }
    }
}
