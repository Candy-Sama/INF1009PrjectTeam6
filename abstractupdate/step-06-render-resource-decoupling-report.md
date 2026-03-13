# Step 6 Report: Render Resource Decoupling

## Scope (Step 6 only)
This step removes direct texture ownership from ECS sprite components and centralizes texture mapping in the render subsystem.

## Files changed
- `Project Files/core/src/main/java/com/team6/arcadesim/components/SpriteComponent.java`
- `Project Files/core/src/main/java/com/team6/arcadesim/managers/RenderManager.java`
- `Project Files/lwjgl3/src/main/java/com/team6/arcadesim/Demoscene/Scenes/DemoScene.java`
- `Project Files/lwjgl3/src/main/java/com/team6/arcadesim/Demoscene/Scenes/DemoGravity.java`

## Exact refactors
1. `SpriteComponent` redesign:
- Before: stored direct `Texture`.
- After: stores `spriteId`, dimensions, flip flags.
- Rendering data component no longer owns graphics resource object.

2. `RenderManager` resource registry:
- Added `Map<String, Texture> textureRegistry`.
- Added APIs:
  - `registerTexture(id, texture)`
  - `unregisterTexture(id)`
  - `getTexture(id)`
  - `clearTextureRegistry()`
- Render path now resolves texture using `spriteId`.

3. Demo compatibility updates:
- Registered textures by IDs.
- Entities now use `new SpriteComponent("id", w, h)`.
- Scenes unregister/dispose textures on exit.

## Why this is necessary
- ECS components should stay data-oriented and abstract.
- Direct texture references in components tightly couple engine data model to rendering backend.
- ID-based indirection improves portability and runtime asset control.

## How subsystems use it
1. Scene loads/creates texture resource.
2. Scene registers resource in `RenderManager` under an ID.
3. Entity uses `SpriteComponent(spriteId, ...)`.
4. Render system looks up texture by ID at draw time.

## Resulting engine benefit
- Cleaner separation between ECS data and platform-specific resources.
- Easier to swap asset loading strategies without changing component contracts.

