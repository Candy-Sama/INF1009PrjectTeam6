# Step 3 Report: Scene Lifecycle Contract

## Scope (Step 3 only)
This step standardizes scene transitions and lifecycle behavior (`onEnter/onPause/onResume/onExit/dispose`).

## Files changed
- `Project Files/core/src/main/java/com/team6/arcadesim/managers/SceneManager.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/events/SceneLifecycleEvent.java`

## Exact refactors
1. Replaced `Stack` with `Deque` (`ArrayDeque`) in `SceneManager`.

2. Added lifecycle-safe transition behavior:
- `changeScene(route)`:
  - dispose existing scenes properly (`onExit` + `dispose`)
  - enter new scene (`onEnter`)
- `pushScene(route)`:
  - pause current top scene (`onPause`)
  - push new overlay (`onEnter`)
- `popScene()`:
  - remove top (`onExit` + `dispose`)
  - resume next (`onResume`)

3. Added lifecycle-safe shutdown:
- `dispose()` now exits/disposes every stacked scene.

4. Added scene lifecycle event publication:
- `SceneLifecycleEvent` (`CHANGED`, `PUSHED`, `POPPED`)
- Published through shared `EventBus`.

5. Added logger integration:
- errors/warnings now flow through `EngineLogger` instead of direct console calls.

## Why this is necessary
- Before this step, scene transitions were partially cleaned up and inconsistent.
- Without strict lifecycle guarantees, scenes leak memory/resources and pause/resume logic is unreliable.
- Abstract engines require clear lifecycle contracts regardless of game context.

## How subsystems use it
1. Runtime triggers transition call in `SceneManager`.
2. Lifecycle methods fire in deterministic order.
3. Optional listeners can observe transitions via `SceneLifecycleEvent`.
4. Scene resources are cleaned consistently when scenes are removed.

## Resulting engine benefit
- Predictable, reusable scene lifecycle behavior for any simulation/game built on top.

