# Step 5 Report: Collision Contact Tracking and Events

## Scope (Step 5 only)
This step replaces frame-spam collision notifications with proper contact-state tracking and event publication.

## Files changed
- `Project Files/core/src/main/java/com/team6/arcadesim/managers/CollisionManager.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/events/CollisionEvent.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/interfaces/CollisionListener.java`

## Exact refactors
1. Rewrote `CollisionManager` update flow:
- Filters valid collidable entities first.
- Tracks active contact pairs across frames (`activeContacts` map).
- Detects transitions:
  - Start: not active before, colliding now
  - Stay: active before, still colliding
  - End: active before, missing this frame

2. Added pair keying:
- Stable pair key generated from entity IDs (`toPairKey`).

3. Notification contract changes:
- `onCollisionStart`
- `onCollisionStay`
- `onCollisionEnd`

4. `CollisionListener` updated:
- Added default method `onCollisionStay(...)` for backward compatibility.

5. Added event publication:
- `CollisionEvent` type with `START/STAY/END`.
- Published via shared `EventBus`.

6. Added manager service hooks:
- `setEventBus(...)`
- `setLogger(...)`

## Why this is necessary
- Previous model called start/end every frame per pair, which is semantically incorrect.
- Contact-state correctness is required for professional engine behavior (sound triggers, damage windows, sensors, etc.).
- Event publication decouples collision reactions from collision manager internals.

## How subsystems use it
1. Collision manager computes contacts.
2. Listeners receive callback transitions.
3. Other systems can independently subscribe to `CollisionEvent` through event bus.

## Resulting engine benefit
- Correct, reusable collision signaling independent of any specific game rules.

