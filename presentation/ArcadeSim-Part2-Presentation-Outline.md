# ArcadeSim Gravity Sandbox

Suggested note placeholders use `Presenter 1` to `Presenter 4`. Swap these with actual names before recording.

## Slide 1: Title

- ArcadeSim Gravity Sandbox
- INF1009 Part 2
- Team 6
- Reusable engine + educational gravity simulation

Speaker notes:

[Suggested speaker: Presenter 1]
Hello everyone, we are Team 6, and this is our INF1009 Part 2 project, ArcadeSim Gravity Sandbox. Our goal was to prove that the engine we built in Part 1 could be reused for a richer application instead of staying as a one-off demo. We turned that engine into a space sandbox where users can create stars and planets, tune their properties, preview trajectories, and run gravity-based simulations live. In the next few minutes, we will cover the problem we chose, the architecture, the engine improvements, the key sandbox features, and the demo flow.

## Slide 2: Problem And Objectives

- Build a reusable engine, then apply it to a separate real-world style problem
- Chosen problem: make gravity and orbit behaviour easier to explore visually
- Target audience: beginners learning physics ideas through experimentation
- Focus areas: reusability, scalability, clean OOP design, and a strong live demo

Speaker notes:

[Suggested speaker: Presenter 1]
For Part 2, we chose to build a simulation instead of a traditional game. The problem we wanted to address was how to make gravitational motion easier to understand in a visual and interactive way. Rather than only reading formulas, users can create bodies, vary mass and velocity, and immediately see how those choices affect the simulation. This fits the project brief because it gives us a concrete application while still letting us showcase code quality, reusability, and good object-oriented design.

## Slide 3: Two-Layer Architecture

- `engineLayer` handles scenes, ECS, rendering, input, gravity, collision, audio, timing, and events
- `concreteLayer.sandbox` contains menus, sandbox scenes, UI, body creation, prediction, and sandbox audio
- The engine remains game-agnostic while the sandbox layer stays domain-specific
- The same engine structure can support other simulations or games later

Speaker notes:

[Suggested speaker: Presenter 1]
Our architecture is split into two clear layers. The abstract `engineLayer` contains reusable services such as scene management, input, rendering, physics-related managers, sound, timing, and the event bus. On top of that, the concrete sandbox layer implements only the domain-specific behaviour for our gravity simulation, such as the control panel, educational HUD, trajectory prediction, and body factory. This separation is important because it shows that the engine is not hard-coded to a single project idea.

## Slide 4: Engine Improvements Since Part 1

- Fixed timestep loop for more stable simulation updates
- Scene stack with `change`, `push`, and `pop` plus automatic scene input handling
- Event bus for decoupled scene, collision, and audio communication
- Rendering upgraded for sprite regions and fallback shapes
- Circle-aware collisions, radius-based bodies, and shared sound management
- More production-ready structure than the original demo scenes

Speaker notes:

[Suggested speaker: Presenter 2]
Compared with Part 1, the engine is now much more mature. We added a fixed timestep loop in the game master so updates stay stable even if frame rate changes. The scene manager now supports scene changes, overlays, and automatic attachment of scene-specific input processors. We also introduced an event bus so systems like collisions and audio can communicate without tight coupling. On the rendering and physics side, we improved support for radius-based circular bodies, sprite-region rendering, and shared sound services. These changes helped us move from a demo engine to a more reusable engine.

## Slide 5: OOP And Design Patterns

- ECS composition: entities gain behaviour through components such as transform, mass, radius, and movement
- Factory pattern: `CelestialEntityFactory` creates stars and planets consistently
- Strategy pattern: `CollisionResolver` swaps between merge and mutual destruction
- Observer pattern: `EventBus` publishes scene, collision, and audio events
- Adapter pattern: `SandboxNBodyAdapter` converts ECS entities into prediction states
- SOLID focus: lower coupling, clearer responsibilities, easier extension

Speaker notes:

[Suggested speaker: Presenter 2]
This slide highlights how we applied object-oriented programming and design patterns. We use composition heavily through an ECS style design, where entities are assembled from components like mass, movement, and collision data. The body factory centralises star and planet creation. Collision behaviour is swappable through the strategy pattern. The event bus gives us an observer-style system for decoupled events. We also use an adapter to convert engine entities into simpler N-body states for trajectory prediction. Altogether, these choices support SOLID principles and keep the design extensible.

## Slide 6: Key Sandbox Features

- Blueprint mode previews motion before the simulation starts
- Users can spawn stars and planets with custom mass, radius, position, and velocity
- Clicking a body loads it into the control panel for live editing
- The educational HUD shows speed, acceleration, nearest star distance, and orbit type
- The pause scene controls audio, collision mode, velocity vectors, and navigation

Speaker notes:

[Suggested speaker: Presenter 2]
The main user experience is built around a sandbox workflow. In blueprint mode, users can plan a setup before running the simulation. They can spawn stars and planets, choose values like mass and velocity, and click any entity to edit it live. While using the sandbox, the educational HUD displays information such as speed, acceleration, nearest star distance, and an interpreted orbit type. We also added a pause scene that lets the user adjust audio settings, toggle velocity vectors, and switch the collision behaviour between merge and mutual destruction.

## Slide 7: How A Simulation Step Works

- Input and scene logic happen first
- `GravityManager` computes acceleration from massive bodies
- `MovementManager` updates velocity and position
- `CollisionManager` detects overlap and applies the active resolver
- In blueprint mode, the trajectory service predicts sampled future paths without running the live simulation
- The HUD classifies outcomes such as free drift, near circular, elliptical, or escape

Speaker notes:

[Suggested speaker: Presenter 3]
This is the high-level logic flow of one simulation cycle. First, the scene processes user input and UI state. Then the engine pipeline runs gravity, movement, and collision in a fixed order. The exact order is configured through the system pipeline, so it is explicit and easy to adjust. In blueprint mode, we do not run the live simulation immediately. Instead, we copy the current bodies into simplified N-body states and generate trajectory predictions. This lets users compare their setup with the later live behaviour and makes the sandbox more educational.

## Slide 8: Demo Walkthrough For The Video

- Step 1: place a star to act as the anchor body
- Step 2: place a planet and tune its velocity until the predicted path looks reasonable
- Step 3: start the simulation and observe the orbit or drift behaviour
- Step 4: pause to show audio controls, vector toggle, and collision mode switching
- Step 5: trigger a collision to compare mutual destruction and merge behaviour

Speaker notes:

[Suggested speaker: Presenter 3]
For the actual video presentation, this is the demo flow we recommend. We begin by placing a star as the main gravitational anchor. Next, we place a planet and adjust its velocity while still in blueprint mode so the predicted path is visible. After that, we start the simulation and show how the real movement compares with the preview. Then we pause the simulation to highlight runtime controls such as velocity vectors, audio sliders, and collision mode. Finally, we demonstrate a collision so the audience can clearly see the difference between destruction and merge behaviour.

## Slide 9: Innovation, Scalability, And Verification

- Reuses one engine instead of rewriting logic for a single simulation
- Combines live editing, trajectory prediction, orbit interpretation, and runtime mode switches
- Uses explicit configuration values such as body caps and speed multipliers
- Keeps engine services reusable while sandbox logic remains isolated
- Verified with a successful `gradlew.bat build`

Speaker notes:

[Suggested speaker: Presenter 4]
What makes this project interesting is not only the simulation itself, but how it showcases the engine. We are reusing the same abstract foundation instead of writing all behaviour directly inside one scene. The sandbox combines several ideas in one interface: body creation, live editing, trajectory prediction, educational statistics, and swappable collision behaviour. We also made operational limits explicit in configuration, such as active body caps and prediction limits, which makes the system easier to reason about and extend. Before packaging this deck, we also verified the project with a successful Gradle build.

## Slide 10: Limitations And Next Steps

- Prediction currently uses a simple forward Euler approach, so long future paths can drift
- Collision checks are pairwise and would need optimisation for very large simulations
- The project does not yet support save/load or scenario presets
- Future work could add more body types, better tutorials, and deeper performance profiling

Speaker notes:

[Suggested speaker: Presenter 4]
We also want to be honest about the current limitations. Our trajectory prediction uses a straightforward numerical approach, which is enough for teaching and previewing but not as accurate as more advanced integrators over long durations. Collision checking is also pairwise, so scaling to much larger simulations would benefit from spatial partitioning or other optimisation. We also do not yet support save files or preset scenarios. If we continue this project, our next steps would be better numerical methods, more content, and stronger performance tooling.

## Slide 11: Closing Takeaways

- Part 2 demonstrates that our Part 1 engine can support a separate simulation cleanly
- The project showcases reusable architecture, OOP principles, and multiple design patterns
- The sandbox adds educational value through prediction, statistics, and interactive experimentation
- The final deliverable is a concise 10-minute deck-and-demo presentation

Speaker notes:

[Suggested speaker: Presenter 4]
To close, the main takeaway is that our Part 1 engine was successfully extended into a separate and more complete application for Part 2. We showed reusable architecture, stronger engine features, and a sandbox that makes gravitational behaviour easier to explore interactively. This gives us a deliverable that is not only functional, but also aligned with the grading focus on code quality, architecture, reusability, and clarity of presentation. Thank you for watching, and please replace the presenter placeholders in these notes with your actual team member names before recording.
