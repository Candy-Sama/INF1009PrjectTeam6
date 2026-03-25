package com.team6.engineLayer.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.team6.engineLayer.components.CollisionComponent;
import com.team6.engineLayer.components.RadiusComponent;
import com.team6.engineLayer.components.TransformComponent;
import com.team6.engineLayer.ecs.Entity;
import com.team6.engineLayer.events.CollisionEvent;
import com.team6.engineLayer.events.EventBus;
import com.team6.engineLayer.interfaces.CollisionListener;
import com.team6.engineLayer.interfaces.CollisionResolver;
import com.team6.engineLayer.logging.EngineLogger;
import com.team6.engineLayer.logging.NoOpEngineLogger;

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
            if (a == null || !a.isActive()) {
                continue;
            }
            CollisionComponent ca = a.getComponent(CollisionComponent.class);

            for (int j = i + 1; j < collidableEntities.size(); j++) {
                Entity b = collidableEntities.get(j);
                if (b == null || !b.isActive()) {
                    continue;
                }
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

                if (!a.isActive() || !b.isActive()) {
                    continue;
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
        TransformComponent tb = b.getComponent(TransformComponent.class);
        float halfWidthA = getHalfWidth(a);
        float halfHeightA = getHalfHeight(a);
        float halfWidthB = getHalfWidth(b);
        float halfHeightB = getHalfHeight(b);

        float aLeft = ta.getPosition().x - halfWidthA;
        float aBottom = ta.getPosition().y - halfHeightA;
        float bLeft = tb.getPosition().x - halfWidthB;
        float bBottom = tb.getPosition().y - halfHeightB;

        float aRight = aLeft + halfWidthA * 2f;
        float aTop = aBottom + halfHeightA * 2f;
        float bRight = bLeft + halfWidthB * 2f;
        float bTop = bBottom + halfHeightB * 2f;

        // Inclusive bounds to avoid missing edge-touching contacts.
        return aLeft <= bRight && aRight >= bLeft && aBottom <= bTop && aTop >= bBottom;
    }

    private boolean narrowphaseOverlap(Entity a, Entity b) {
        if (isCircle(a) && isCircle(b)) {
            return circleOverlap(a, b);
        }
        // Default narrowphase for non-circles remains AABB overlap.
        return broadphaseOverlap(a, b);
    }

    private boolean circleOverlap(Entity a, Entity b) {
        TransformComponent ta = a.getComponent(TransformComponent.class);
        TransformComponent tb = b.getComponent(TransformComponent.class);
        RadiusComponent ra = a.getComponent(RadiusComponent.class);
        RadiusComponent rb = b.getComponent(RadiusComponent.class);

        float dx = tb.getPosition().x - ta.getPosition().x;
        float dy = tb.getPosition().y - ta.getPosition().y;
        float combinedRadius = Math.max(0f, ra.getRadius()) + Math.max(0f, rb.getRadius());
        float distanceSq = dx * dx + dy * dy;
        return distanceSq <= combinedRadius * combinedRadius;
    }

    private boolean isCircle(Entity entity) {
        return entity.hasComponent(RadiusComponent.class);
    }

    private float getHalfWidth(Entity entity) {
        if (isCircle(entity)) {
            return Math.max(0f, entity.getComponent(RadiusComponent.class).getRadius());
        }
        return entity.getComponent(CollisionComponent.class).getWidth() / 2f;
    }

    private float getHalfHeight(Entity entity) {
        if (isCircle(entity)) {
            return Math.max(0f, entity.getComponent(RadiusComponent.class).getRadius());
        }
        return entity.getComponent(CollisionComponent.class).getHeight() / 2f;
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
