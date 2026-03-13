# Step 2 Report: Pluggable System Pipeline

## Scope (Step 2 only)
This step replaces hardcoded update order in playable scenes with a configurable system pipeline.

## Files changed
- `Project Files/core/src/main/java/com/team6/arcadesim/scenes/AbstractPlayableScene.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/systems/EngineSystem.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/systems/SystemPipeline.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/systems/GravitySystem.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/systems/MovementSystem.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/systems/CollisionSystem.java`
- `Project Files/lwjgl3/src/main/java/com/team6/arcadesim/Demoscene/Scenes/DemoSolar.java`
- `Project Files/lwjgl3/src/main/java/com/team6/arcadesim/Demoscene/Scenes/DemoScene.java`

## Exact refactors
1. Added `EngineSystem` interface:
- `update(float dt, AbstractGameMaster gameMaster, EntityManager entityManager)`
- `getPriority()` default for ordering.

2. Added `SystemPipeline`:
- Holds system list.
- Sorts systems by priority when added.
- Executes systems in deterministic order.

3. Refactored `AbstractPlayableScene`:
- Removed hardcoded manager chain (`gravity -> movement -> collision`).
- Added `SystemPipeline systemPipeline`.
- Added `configureSystems(SystemPipeline pipeline)` hook.
- `update()` now runs:
  - `processLevelLogic(dt)`
  - `systemPipeline.update(...)`

4. Added default system adapters:
- `GravitySystem`, `MovementSystem`, `CollisionSystem`.
- These call existing managers, but through generic system interface.

5. Demo compatibility updates:
- `DemoSolar.configureSystems(...)`: gravity + movement + collision.
- `DemoScene.configureSystems(...)`: movement + collision.

## Why this is necessary
- Hardcoded loops are not abstract: every playable scene had implied physics rules.
- A proper engine should let scenes/simulations define their own system composition.
- This design allows any future game/simulation to register different systems without editing core scene base classes.

## How subsystems use it
1. Scene subclass overrides `configureSystems`.
2. Scene registers only required systems.
3. Runtime calls scene update.
4. Scene logic runs first.
5. Pipeline executes systems in priority order.

## Resulting engine benefit
- Engine no longer assumes one fixed simulation model.
- System order and inclusion become data/config choices at scene level.

