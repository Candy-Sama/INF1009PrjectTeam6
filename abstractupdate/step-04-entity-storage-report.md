# Step 4 Report: Entity Storage and Safe Mutation

## Scope (Step 4 only)
This step upgrades ECS entity storage from copy-on-write behavior to frame-safe deferred mutation.

## Files changed
- `Project Files/core/src/main/java/com/team6/arcadesim/managers/EntityManager.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/ecs/Entity.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/managers/SceneManager.java` (calls `beginUpdate/endUpdate`)

## Exact refactors
1. `Entity` changed from abstract to concrete:
- `public abstract class Entity` -> `public class Entity`.
- ECS entities are now simple data containers by default.

2. `EntityManager` storage refactor:
- Removed `CopyOnWriteArrayList`.
- Added:
  - `entities` (main list)
  - `pendingAdditions`
  - `pendingRemovals`
  - `updateInProgress` flag

3. Added safe update boundaries:
- `beginUpdate()`
- `endUpdate()` -> flushes queued add/remove changes.

4. `addEntity/removeEntity/removeAll` now queue changes during update windows.

5. `getAllEntities()` now returns an unmodifiable view to prevent unsafe external mutation.

6. `createEntity()` now uses concrete `new Entity()`.

## Why this is necessary
- `CopyOnWriteArrayList` is expensive when spawning/removing entities often.
- Modifying active entity collections during iteration causes unstable behavior.
- Abstract engines need deterministic data mutation boundaries.

## How subsystems use it
1. `SceneManager.update()` calls `entityManager.beginUpdate()`.
2. Scene/systems run and can request add/remove operations.
3. `entityManager.endUpdate()` applies changes in one flush after logic completes.

## Resulting engine benefit
- Better scalability for dynamic simulations.
- Safer ECS lifecycle without accidental concurrent modification issues.

