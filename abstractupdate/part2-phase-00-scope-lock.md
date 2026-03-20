# ArcadeSim Gravity Sandbox - Phase 0 Scope Lock

Date locked: 2026-03-16
Owner: Team 6
Status: Approved baseline for Part 2 implementation

## Objective
Freeze all high-impact decisions before implementation so the team can build Part 2 without architecture drift.

## Locked Product Scope
In-scope baseline:
- Main menu scene with `Start Sandbox` and `Exit`.
- Sandbox scene with side control panel (Scene2D).
- Body placement via world click using panel values.
- Two body types: star (immovable) and planet (movable).
- Blueprint mode and Simulation mode.
- Deterministic fixed-step simulation in runtime mode.
- Phantom trajectory prediction in blueprint mode.
- Mutual destruction collision outcome.
- Clear board action.

Out-of-scope for this baseline:
- Presets and multi-scene campaign flow.
- Time multipliers (`2x`, `3x`, `4x`).
- Multiple collision strategies beyond mutual destruction.
- Advanced astrophysics realism.

## Architecture Boundary Rules
- All Part 2 game/simulation code must live under `com.team6.arcadesim.sandbox.*`.
- `core` remains abstract and reusable for non-sandbox projects.
- No context-specific classes or naming in `core` (no `Planet`, `Star`, `Sandbox` classes in `core`).
- Any `core` change must be generic and engine-safe.

## Locked Design Patterns
- Factory: `CelestialEntityFactory` creates all spawnable bodies.
- State: sandbox mode lifecycle (`BLUEPRINT`, `SIMULATION`) in `SandboxScene`.
- Observer: collision events and UI-to-scene callbacks/listeners.
- Adapter: `SandboxNBodyAdapter` transforms ECS entities to prediction states.

## Determinism Contract
- Single source of timestep truth: `gameMaster.getEngineTimingConfig().getFixedTimeStep()`.
- Runtime physics and phantom predictor must use the same fixed timestep value.
- Prediction service cannot run a different integration step than runtime simulation.

## Dependency Policy (Locked)
- No new third-party libraries for this baseline.
- Use existing LibGDX stack, including built-in Scene2D UI.

## Target Package/File Plan
Target root package:
- `Project Files/lwjgl3/src/main/java/com/team6/arcadesim/sandbox`

Planned files:
- `SandboxGameMaster.java`
- `BodyType.java`
- `scenes/MainMenuScene.java`
- `scenes/SandboxScene.java`
- `ui/SandboxControlPanel.java`
- `ui/SandboxSkinFactory.java`
- `factory/CelestialEntityFactory.java`
- `simulation/SandboxNBodyAdapter.java`
- `simulation/SandboxTrajectoryService.java`
- `simulation/MutualDestructionResolver.java`
- `render/TrajectoryRenderer.java`
- `config/SandboxConfig.java`
- `config/SandboxUiConfig.java`

## Phase 0 Definition of Done
- Scope and exclusions are written and agreed.
- Package boundary rules are written and agreed.
- Pattern commitments are explicit and mapped to target classes.
- Determinism rule is explicit and non-ambiguous.
- Dependency policy is explicit.
- Phase 1 can begin without revisiting architecture decisions.

## Immediate Next Action
Start Phase 1:
- Create package tree and scaffold `SandboxGameMaster`.
- Wire launcher to sandbox app entry.
