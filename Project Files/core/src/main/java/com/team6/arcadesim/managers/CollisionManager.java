package com.team6.arcadesim.managers;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.team6.arcadesim.components.CollisionComponent;
import com.team6.arcadesim.components.TransformComponent;
import com.team6.arcadesim.ecs.Entity;
import com.team6.arcadesim.interfaces.CollisionListener;
import com.team6.arcadesim.interfaces.CollisionResolver;

public class CollisionManager {

    private List<CollisionListener> listeners;
    private CollisionResolver resolver;
    
    // Reusable rectangles to avoid creating "garbage" every frame (Memory Optimization)
    private Rectangle rectA = new Rectangle();
    private Rectangle rectB = new Rectangle();

    public CollisionManager() {
        this.listeners = new ArrayList<>();
        // Default resolver: Stop the entity
        this.resolver = (a, b) -> { /* Default: Do nothing or implement simple stop */ };
    }

    public void setResolver(CollisionResolver resolver) {
        this.resolver = resolver;
    }

    public void addCollisionListener(CollisionListener listener) {
        listeners.add(listener);
    }

    public void removeCollisionListener(CollisionListener listener) {
        listeners.remove(listener);
    }

    public void update(float dt, List<Entity> entities) {
        // Simple N^2 check. For production games with 1000+ entities, use a QuadTree.
        for (int i = 0; i < entities.size(); i++) {
            Entity a = entities.get(i);
            if (!isValid(a)) continue;

            for (int j = i + 1; j < entities.size(); j++) {
                Entity b = entities.get(j);
                if (!isValid(b)) continue;

                CollisionComponent ca = a.getComponent(CollisionComponent.class);
                CollisionComponent cb = b.getComponent(CollisionComponent.class);


                if (checkCollision(a, b)) {
                    // 1. Notify Observers
                    for (CollisionListener listener : listeners) {
                        listener.onCollisionStart(a, b);
                    }
                    
                    // 2. Resolve Collision if both are solid and not triggers
                    if (ca.isSolid() && cb.isSolid() && !ca.isTrigger() && !cb.isTrigger()) {
                        resolver.resolve(a, b);
                    }
                }
                else {
                    // Notify Observers about collision end
                    for (CollisionListener listener : listeners) {
                        listener.onCollisionEnd(a, b);
                    }
                }
            }
        }
    }

    private boolean checkCollision(Entity a, Entity b) {
        TransformComponent ta = a.getComponent(TransformComponent.class);
        CollisionComponent ca = a.getComponent(CollisionComponent.class);
        
        TransformComponent tb = b.getComponent(TransformComponent.class);
        CollisionComponent cb = b.getComponent(CollisionComponent.class);

        // Update reusable rectangles with current positions
        rectA.set(ta.getPosition().x, ta.getPosition().y, ca.getWidth(), ca.getHeight());
        rectB.set(tb.getPosition().x, tb.getPosition().y, cb.getWidth(), cb.getHeight());

        return rectA.overlaps(rectB);
    }

    private boolean isValid(Entity e) {
        return e.hasComponent(TransformComponent.class) && 
               e.hasComponent(CollisionComponent.class);
    }
}