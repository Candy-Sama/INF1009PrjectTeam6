# Step 1 Report: Runtime Foundation Refactor

## Scope (Step 1 only)
This report covers only the Step 1 engine refactor:
1. Fixed-step simulation loop in `AbstractGameMaster`
2. Shared engine services added at the runtime root (`EventBus`, `EngineLogger`)
3. Service injection into subsystems (`SceneManager`, `CollisionManager`, `SoundManager`)

No Step 2+ details are included here.

## Files changed for Step 1
- `Project Files/core/src/main/java/com/team6/arcadesim/AbstractGameMaster.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/events/EngineEvent.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/events/EngineEventListener.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/events/EventBus.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/logging/EngineLogger.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/logging/ConsoleEngineLogger.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/logging/NoOpEngineLogger.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/managers/SceneManager.java` (service hooks)
- `Project Files/core/src/main/java/com/team6/arcadesim/managers/CollisionManager.java` (service hooks)
- `Project Files/core/src/main/java/com/team6/arcadesim/managers/SoundManager.java` (service hooks)

## Exact refactors

### 1) `AbstractGameMaster`: variable-step -> fixed-step simulation
Before:
- Frame delta was computed, clamped, and passed once to `sceneManager.update(deltaTime)`.
- This can produce unstable simulation when frame times vary.

After:
- Added constants:
  - `MAX_FRAME_TIME = 0.1f`
  - `FIXED_TIME_STEP = 1f / 60f`
  - `MAX_SIMULATION_STEPS = 5`
- Added accumulator state:
  - `private float accumulator;`
- New runtime flow in `render()`:
  1. Calculate `deltaTime`
  2. Clamp `deltaTime` to `MAX_FRAME_TIME`
  3. `accumulator += deltaTime`
  4. Run `update(deltaTime)` once (frame-level hook)
  5. Run `sceneManager.update(FIXED_TIME_STEP)` in a while-loop while accumulator permits
  6. Cap spiral-of-death with `MAX_SIMULATION_STEPS`
  7. Render once using current frame delta

Why needed:
- Fixed-step keeps simulation deterministic and more stable than variable-step.
- Clamp + max-steps prevent extreme lag spikes from exploding simulation workload.

### 2) `AbstractGameMaster`: runtime root now owns shared services
Before:
- No shared event bus at runtime root.
- No unified logger abstraction.
- Managers were created but not centrally wired with shared infrastructure.

After:
- Added fields:
  - `protected EventBus eventBus;`
  - `protected EngineLogger logger;`
- In `create()`:
  - `logger = new ConsoleEngineLogger();`
  - `eventBus = new EventBus();`
- Injected services:
  - `sceneManager.setLogger(logger);`
  - `sceneManager.setEventBus(eventBus);`
  - `collisionManager.setLogger(logger);`
  - `collisionManager.setEventBus(eventBus);`
  - `soundManager.setLogger(logger);`
- Added getters:
  - `getEventBus()`
  - `getLogger()`

Why needed:
- Centralized service ownership is a key engine abstraction pattern.
- Managers stop hardcoding direct console usage and can publish events through one shared bus.
- Subsystems become easier to test/mock/swap.

### 3) Added event system primitives
Added:
- `EngineEvent` marker interface
- `EngineEventListener<T>`
- `EventBus` with:
  - `subscribe(Class<T>, listener)`
  - `unsubscribe(Class<T>, listener)`
  - `publish(event)`

Important implementation details:
- Listener map is keyed by exact event class.
- `publish()` iterates a snapshot copy to avoid concurrent modification if listeners change during dispatch.

Why needed:
- Decouples subsystems from direct dependencies on each other.
- Enables manager-to-manager communication without tight coupling.

### 4) Added logging abstraction
Added:
- `EngineLogger` interface (`info`, `warn`, `error`)
- `ConsoleEngineLogger` (stdout/stderr implementation)
- `NoOpEngineLogger` (silent fallback)

Why needed:
- Removes hardwired console output from core manager logic.
- Allows swapping logger implementation later (file logger, UI logger, test logger).

### 5) Step 1 service hook adoption by subsystems
These were changed so Step 1 wiring in `AbstractGameMaster` can actually be consumed:
- `SceneManager`: `setLogger`, `setEventBus`
- `CollisionManager`: `setLogger`, `setEventBus`
- `SoundManager`: `setLogger`

Why needed:
- Without setter hooks, root-level service creation in `AbstractGameMaster` would not be usable by subsystems.

## How this is used by subsystems (runtime data flow)
1. App boot -> `AbstractGameMaster.create()`
2. Root services created (`EventBus`, `EngineLogger`)
3. Managers instantiated
4. Services injected into managers
5. Every frame:
   - `AbstractGameMaster.render()` accumulates frame time
   - Runs fixed-step updates in deterministic chunks
   - Renders once
6. Managers can now:
   - Log through `EngineLogger`
   - Publish/consume decoupled events via `EventBus` (where integrated)

## What this enables for later steps
- Stable simulation foundation for ECS/system scheduling
- Standardized instrumentation (logging/events)
- Cleaner dependency boundaries between engine root and subsystems

## What Step 1 intentionally did not do
- It did not redesign gameplay logic.
- It did not force all manager behavior into events yet.
- It did not add scene-specific UI/game logic changes.

---

If you want, the next report (`step-02-...`) will cover only the system pipeline refactor and nothing else.
