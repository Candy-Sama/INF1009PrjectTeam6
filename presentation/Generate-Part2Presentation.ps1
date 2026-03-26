param(
    [switch]$OpenWhenDone
)

Add-Type -AssemblyName System.Drawing

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$workspaceRoot = Split-Path -Parent $PSScriptRoot
$outputBase = Join-Path $PSScriptRoot 'INF1009-Team6-Part2-Presentation'
$outputPath = "$outputBase.pptx"
if (Test-Path $outputPath) {
    $outputPath = "$outputBase-$(Get-Date -Format 'yyyyMMdd-HHmmss').pptx"
}

$assets = @{
    SandboxBackground = Join-Path $workspaceRoot 'Project Files\assets\sandbox_bg.png'
    MainMenuBackground = Join-Path $workspaceRoot 'Project Files\assets\mainmenu_bg.png'
    SceneDiagram = Join-Path $workspaceRoot 'ArcadeSim-Scene Driven UML Diagram.drawio.png'
    UmlDiagram = Join-Path $workspaceRoot 'LAB-P6-Team 6_UMLDiagram-UML Diagram(Revised).png'
    SpriteSheet = Join-Path $workspaceRoot 'Project Files\assets\sprite\spritesheet.png'
}

function New-Rgb {
    param([int]$R, [int]$G, [int]$B)
    return ($R -bor ($G -shl 8) -bor ($B -shl 16))
}

$Colors = @{
    Navy = New-Rgb 12 23 43
    Panel = New-Rgb 23 38 67
    PanelSoft = New-Rgb 31 52 89
    Teal = New-Rgb 76 171 179
    Gold = New-Rgb 236 195 85
    White = New-Rgb 245 247 250
    SoftText = New-Rgb 200 210 228
    Green = New-Rgb 74 165 120
    Coral = New-Rgb 220 108 91
}

function Set-ShapeFill {
    param(
        [Parameter(Mandatory = $true)] $Shape,
        [Parameter(Mandatory = $true)] [int]$Rgb,
        [float]$Transparency = 0
    )
    $Shape.Fill.Visible = -1
    $Shape.Fill.Solid()
    $Shape.Fill.ForeColor.RGB = $Rgb
    $Shape.Fill.Transparency = $Transparency
}

function Set-ShapeLine {
    param(
        [Parameter(Mandatory = $true)] $Shape,
        [int]$Rgb = 0,
        [float]$Weight = 1.25,
        [switch]$Hide,
        [float]$Transparency = 0
    )
    if ($Hide) {
        $Shape.Line.Visible = 0
        return
    }
    $Shape.Line.Visible = -1
    $Shape.Line.ForeColor.RGB = $Rgb
    $Shape.Line.Weight = $Weight
    $Shape.Line.Transparency = $Transparency
}

function Add-TextBox {
    param(
        [Parameter(Mandatory = $true)] $Slide,
        [float]$Left,
        [float]$Top,
        [float]$Width,
        [float]$Height,
        [string]$Text,
        [float]$FontSize = 18,
        [string]$FontName = 'Aptos',
        [int]$Color = 0,
        [switch]$Bold,
        [int]$Alignment = 1
    )

    $shape = $Slide.Shapes.AddTextbox(1, $Left, $Top, $Width, $Height)
    $shape.TextFrame.TextRange.Text = $Text
    $shape.TextFrame.MarginLeft = 4
    $shape.TextFrame.MarginRight = 4
    $shape.TextFrame.MarginTop = 2
    $shape.TextFrame.MarginBottom = 2
    $shape.TextFrame.WordWrap = -1
    $shape.Fill.Visible = 0
    $shape.Line.Visible = 0

    $range = $shape.TextFrame.TextRange
    $range.Font.Name = $FontName
    $range.Font.Size = $FontSize
    $range.Font.Color.RGB = $Color
    $range.Font.Bold = $(if ($Bold) { -1 } else { 0 })
    $range.ParagraphFormat.Alignment = $Alignment
}

function Add-Panel {
    param(
        [Parameter(Mandatory = $true)] $Slide,
        [float]$Left,
        [float]$Top,
        [float]$Width,
        [float]$Height,
        [int]$FillRgb,
        [int]$LineRgb,
        [float]$Transparency = 0.05
    )
    $shape = $Slide.Shapes.AddShape(1, $Left, $Top, $Width, $Height)
    Set-ShapeFill -Shape $shape -Rgb $FillRgb -Transparency $Transparency
    Set-ShapeLine -Shape $shape -Rgb $LineRgb -Weight 1.2
    return $shape
}

function Add-InfoCard {
    param(
        [Parameter(Mandatory = $true)] $Slide,
        [float]$Left,
        [float]$Top,
        [float]$Width,
        [float]$Height,
        [string]$Heading,
        [string]$Body,
        [int]$AccentRgb
    )

    $card = Add-Panel -Slide $Slide -Left $Left -Top $Top -Width $Width -Height $Height -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
    $bar = $Slide.Shapes.AddShape(1, $Left, $Top, 8, $Height)
    Set-ShapeFill -Shape $bar -Rgb $AccentRgb
    Set-ShapeLine -Shape $bar -Hide
    Add-TextBox -Slide $Slide -Left ($Left + 20) -Top ($Top + 14) -Width ($Width - 32) -Height 22 -Text $Heading -FontSize 16 -FontName 'Aptos Display' -Color $Colors.White -Bold
    Add-TextBox -Slide $Slide -Left ($Left + 20) -Top ($Top + 42) -Width ($Width - 32) -Height ($Height - 50) -Text $Body -FontSize 11 -FontName 'Aptos' -Color $Colors.SoftText
}

function Add-ImageFit {
    param(
        [Parameter(Mandatory = $true)] $Slide,
        [string]$Path,
        [float]$Left,
        [float]$Top,
        [float]$Width,
        [float]$Height,
        [switch]$AddBorder
    )

    if (-not (Test-Path $Path)) {
        return $null
    }

    $image = [System.Drawing.Image]::FromFile($Path)
    try {
        $scale = [Math]::Min($Width / $image.Width, $Height / $image.Height)
        $drawWidth = [Math]::Round($image.Width * $scale, 2)
        $drawHeight = [Math]::Round($image.Height * $scale, 2)
        $drawLeft = $Left + (($Width - $drawWidth) / 2)
        $drawTop = $Top + (($Height - $drawHeight) / 2)
    } finally {
        $image.Dispose()
    }

    $picture = $Slide.Shapes.AddPicture($Path, 0, -1, $drawLeft, $drawTop, $drawWidth, $drawHeight)
    if ($AddBorder) {
        $picture.Line.Visible = -1
        $picture.Line.ForeColor.RGB = $Colors.PanelSoft
        $picture.Line.Weight = 1.2
    } else {
        $picture.Line.Visible = 0
    }
    return $picture
}

function Set-SpeakerNotes {
    param(
        [Parameter(Mandatory = $true)] $Slide,
        [Parameter(Mandatory = $true)] [string]$Notes
    )
    $noteBody = $Slide.NotesPage.Shapes.Placeholders.Item(2)
    $noteBody.TextFrame.TextRange.Text = $Notes.Trim()
    $noteBody.TextFrame.TextRange.Font.Name = 'Aptos'
    $noteBody.TextFrame.TextRange.Font.Size = 16
}

function Initialize-ContentSlide {
    param(
        [Parameter(Mandatory = $true)] $Presentation,
        [Parameter(Mandatory = $true)] [string]$Title
    )

    $slideNumber = $Presentation.Slides.Count + 1
    $slide = $Presentation.Slides.Add($slideNumber, 12)
    $background = $slide.Shapes.AddShape(1, 0, 0, 960, 540)
    Set-ShapeFill -Shape $background -Rgb $Colors.Navy
    Set-ShapeLine -Shape $background -Hide

    $topBar = $slide.Shapes.AddShape(1, 0, 0, 960, 14)
    Set-ShapeFill -Shape $topBar -Rgb $Colors.Teal
    Set-ShapeLine -Shape $topBar -Hide

    Add-TextBox -Slide $slide -Left 42 -Top 26 -Width 690 -Height 34 -Text $Title -FontSize 26 -FontName 'Aptos Display' -Color $Colors.White -Bold
    Add-TextBox -Slide $slide -Left 42 -Top 511 -Width 220 -Height 14 -Text 'INF1009 Part 2 | Team 6' -FontSize 9 -FontName 'Aptos' -Color $Colors.SoftText
    Add-TextBox -Slide $slide -Left 900 -Top 511 -Width 20 -Height 14 -Text ([string]$slideNumber) -FontSize 9 -FontName 'Aptos' -Color $Colors.SoftText -Alignment 3
    return $slide
}

function Add-BulletList {
    param(
        [Parameter(Mandatory = $true)] $Slide,
        [float]$Left,
        [float]$Top,
        [float]$Width,
        [float]$Height,
        [string[]]$Items
    )

    $text = ($Items | ForEach-Object { [string]::Concat([char]0x2022, ' ', $_) }) -join "`r`n`r`n"
    Add-TextBox -Slide $Slide -Left $Left -Top $Top -Width $Width -Height $Height -Text $text -FontSize 17 -FontName 'Aptos' -Color $Colors.White
}

$notes = @{
    Slide1 = @'
[Suggested speaker: Presenter 1]
Hello everyone, we are Team 6, and this is our INF1009 Part 2 project, ArcadeSim Gravity Sandbox. Our goal was to prove that the engine we built in Part 1 could be reused for a richer application instead of staying as a one-off demo. We turned that engine into a space sandbox where users can create stars and planets, tune their properties, preview trajectories, and run gravity-based simulations live. In the next few minutes, we will cover the problem we chose, the architecture, the engine improvements, the key sandbox features, and the demo flow.
'@
    Slide2 = @'
[Suggested speaker: Presenter 1]
For Part 2, we chose to build a simulation instead of a traditional game. The problem we wanted to address was how to make gravitational motion easier to understand in a visual and interactive way. Rather than only reading formulas, users can create bodies, vary mass and velocity, and immediately see how those choices affect the simulation. This fits the project brief because it gives us a concrete application while still letting us showcase code quality, reusability, and good object-oriented design.
'@
    Slide3 = @'
[Suggested speaker: Presenter 1]
Our architecture is split into two clear layers. The abstract engine layer contains reusable services such as scene management, input, rendering, physics-related managers, sound, timing, and the event bus. On top of that, the concrete sandbox layer implements only the domain-specific behaviour for our gravity simulation, such as the control panel, educational HUD, trajectory prediction, and body factory. This separation is important because it shows that the engine is not hard-coded to a single project idea.
'@
    Slide4 = @'
[Suggested speaker: Presenter 2]
Compared with Part 1, the engine is now much more mature. We added a fixed timestep loop in the game master so updates stay stable even if frame rate changes. The scene manager now supports scene changes, overlays, and automatic attachment of scene-specific input processors. We also introduced an event bus so systems like collisions and audio can communicate without tight coupling. On the rendering and physics side, we improved support for radius-based circular bodies, sprite-region rendering, and shared sound services. These changes helped us move from a demo engine to a more reusable engine.
'@
    Slide5 = @'
[Suggested speaker: Presenter 2]
This slide highlights how we applied object-oriented programming and design patterns. We use composition heavily through an ECS style design, where entities are assembled from components like mass, movement, and collision data. The body factory centralises star and planet creation. Collision behaviour is swappable through the strategy pattern. The event bus gives us an observer-style system for decoupled events. We also use an adapter to convert engine entities into simpler N-body states for trajectory prediction. Altogether, these choices support SOLID principles and keep the design extensible.
'@
    Slide6 = @'
[Suggested speaker: Presenter 2]
The main user experience is built around a sandbox workflow. In blueprint mode, users can plan a setup before running the simulation. They can spawn stars and planets, choose values like mass and velocity, and click any entity to edit it live. While using the sandbox, the educational HUD displays information such as speed, acceleration, nearest star distance, and an interpreted orbit type. We also added a pause scene that lets the user adjust audio settings, toggle velocity vectors, and switch the collision behaviour between merge and mutual destruction.
'@
    Slide7 = @'
[Suggested speaker: Presenter 3]
This is the high-level logic flow of one simulation cycle. First, the scene processes user input and UI state. Then the engine pipeline runs gravity, movement, and collision in a fixed order. The exact order is configured through the system pipeline, so it is explicit and easy to adjust. In blueprint mode, we do not run the live simulation immediately. Instead, we copy the current bodies into simplified N-body states and generate trajectory predictions. This lets users compare their setup with the later live behaviour and makes the sandbox more educational.
'@
    Slide8 = @'
[Suggested speaker: Presenter 3]
For the actual video presentation, this is the demo flow we recommend. We begin by placing a star as the main gravitational anchor. Next, we place a planet and adjust its velocity while still in blueprint mode so the predicted path is visible. After that, we start the simulation and show how the real movement compares with the preview. Then we pause the simulation to highlight runtime controls such as velocity vectors, audio sliders, and collision mode. Finally, we demonstrate a collision so the audience can clearly see the difference between destruction and merge behaviour.
'@
    Slide9 = @'
[Suggested speaker: Presenter 4]
What makes this project interesting is not only the simulation itself, but how it showcases the engine. We are reusing the same abstract foundation instead of writing all behaviour directly inside one scene. The sandbox combines several ideas in one interface: body creation, live editing, trajectory prediction, educational statistics, and swappable collision behaviour. We also made operational limits explicit in configuration, such as active body caps and prediction limits, which makes the system easier to reason about and extend. Before packaging this deck, we also verified the project with a successful Gradle build.
'@
    Slide10 = @'
[Suggested speaker: Presenter 4]
We also want to be honest about the current limitations. Our trajectory prediction uses a straightforward numerical approach, which is enough for teaching and previewing but not as accurate as more advanced integrators over long durations. Collision checking is also pairwise, so scaling to much larger simulations would benefit from spatial partitioning or other optimisation. We also do not yet support save files or preset scenarios. If we continue this project, our next steps would be better numerical methods, more content, and stronger performance tooling.
'@
    Slide11 = @'
[Suggested speaker: Presenter 4]
To close, the main takeaway is that our Part 1 engine was successfully extended into a separate and more complete application for Part 2. We showed reusable architecture, stronger engine features, and a sandbox that makes gravitational behaviour easier to explore interactively. This gives us a deliverable that is not only functional, but also aligned with the grading focus on code quality, architecture, reusability, and clarity of presentation. Thank you for watching, and please replace the presenter placeholders in these notes with your actual team member names before recording.
'@
}

$powerPoint = $null
$presentation = $null

try {
    $powerPoint = New-Object -ComObject PowerPoint.Application
    $powerPoint.Visible = -1

    $presentation = $powerPoint.Presentations.Add()
    $presentation.PageSetup.SlideWidth = 960
    $presentation.PageSetup.SlideHeight = 540

    # Slide 1
    $slide = $presentation.Slides.Add(1, 12)
    $bg = $slide.Shapes.AddShape(1, 0, 0, 960, 540)
    Set-ShapeFill -Shape $bg -Rgb $Colors.Navy
    Set-ShapeLine -Shape $bg -Hide
    $null = Add-ImageFit -Slide $slide -Path $assets.SandboxBackground -Left 0 -Top 0 -Width 960 -Height 540
    $overlay = $slide.Shapes.AddShape(1, 0, 0, 960, 540)
    Set-ShapeFill -Shape $overlay -Rgb $Colors.Navy -Transparency 0.22
    Set-ShapeLine -Shape $overlay -Hide
    $pill = $slide.Shapes.AddShape(1, 58, 70, 320, 28)
    Set-ShapeFill -Shape $pill -Rgb $Colors.Teal -Transparency 0.08
    Set-ShapeLine -Shape $pill -Hide
    Add-TextBox -Slide $slide -Left 72 -Top 76 -Width 292 -Height 18 -Text 'Reusable Engine + Educational Simulation' -FontSize 12 -FontName 'Aptos' -Color $Colors.White -Bold
    Add-TextBox -Slide $slide -Left 58 -Top 140 -Width 520 -Height 74 -Text 'ArcadeSim Gravity Sandbox' -FontSize 28 -FontName 'Aptos Display' -Color $Colors.White -Bold
    Add-TextBox -Slide $slide -Left 58 -Top 220 -Width 500 -Height 82 -Text 'INF1009 Part 2 presentation for Team 6. Built with a reusable libGDX engine and extended into an interactive space sandbox.' -FontSize 17 -FontName 'Aptos' -Color $Colors.SoftText
    Add-TextBox -Slide $slide -Left 58 -Top 474 -Width 200 -Height 16 -Text 'Team 6' -FontSize 11 -FontName 'Aptos' -Color $Colors.Gold -Bold
    Add-TextBox -Slide $slide -Left 58 -Top 495 -Width 320 -Height 16 -Text 'Slides + speaker notes generated from the project repo and Part 2 guidelines' -FontSize 9 -FontName 'Aptos' -Color $Colors.SoftText
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide1

    # Slide 2
    $slide = Initialize-ContentSlide -Presentation $presentation -Title 'Problem And Objectives'
    Add-BulletList -Slide $slide -Left 48 -Top 92 -Width 470 -Height 270 -Items @(
        'Build a reusable engine, then apply it to a separate real-world style problem.',
        'Chosen problem: make gravity and orbit behaviour easier to explore visually.',
        'Target audience: beginners learning physics ideas through experimentation.',
        'Focus areas: reusability, scalability, clean OOP design, and a strong live demo.'
    )
    $null = Add-Panel -Slide $slide -Left 560 -Top 104 -Width 330 -Height 230 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
    Add-TextBox -Slide $slide -Left 582 -Top 124 -Width 286 -Height 22 -Text 'Why this simulation fits the brief' -FontSize 18 -FontName 'Aptos Display' -Color $Colors.Gold -Bold
    Add-TextBox -Slide $slide -Left 582 -Top 160 -Width 276 -Height 140 -Text 'It uses the abstract engine from Part 1, but applies it to a different problem domain. Instead of focusing on content complexity alone, the project emphasises reusable components, clear architecture, and a simulation that can be demonstrated live within ten minutes.' -FontSize 14 -FontName 'Aptos' -Color $Colors.White
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide2

    # Slide 3
    $slide = Initialize-ContentSlide -Presentation $presentation -Title 'Two-Layer Architecture'
    Add-BulletList -Slide $slide -Left 48 -Top 92 -Width 360 -Height 290 -Items @(
        'engineLayer handles scenes, ECS, rendering, input, gravity, collision, audio, timing, and events.',
        'concreteLayer.sandbox contains menus, sandbox scenes, UI, body creation, prediction, and sandbox audio.',
        'The engine stays game-agnostic while the sandbox layer stays domain-specific.',
        'The same engine structure can support other simulations or games later.'
    )
    $null = Add-Panel -Slide $slide -Left 454 -Top 92 -Width 438 -Height 352 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
    Add-TextBox -Slide $slide -Left 476 -Top 108 -Width 394 -Height 20 -Text 'Scene-driven architecture overview' -FontSize 16 -FontName 'Aptos Display' -Color $Colors.Gold -Bold
    $null = Add-ImageFit -Slide $slide -Path $assets.SceneDiagram -Left 470 -Top 138 -Width 404 -Height 286 -AddBorder
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide3

    # Slide 4
    $slide = Initialize-ContentSlide -Presentation $presentation -Title 'Engine Improvements Since Part 1'
    Add-InfoCard -Slide $slide -Left 48 -Top 92 -Width 262 -Height 112 -Heading 'Stable updates' -Body 'Fixed timestep logic in AbstractGameMaster makes simulation timing more predictable and protects against runaway frame spikes.' -AccentRgb $Colors.Teal
    Add-InfoCard -Slide $slide -Left 336 -Top 92 -Width 262 -Height 112 -Heading 'Scene stack' -Body 'SceneManager now supports change, push, and pop with automatic scene-owned input processor attachment.' -AccentRgb $Colors.Gold
    Add-InfoCard -Slide $slide -Left 624 -Top 92 -Width 262 -Height 112 -Heading 'Event bus' -Body 'Scenes, collision handling, and audio can communicate through decoupled event publication and subscription.' -AccentRgb $Colors.Green
    Add-InfoCard -Slide $slide -Left 48 -Top 226 -Width 262 -Height 112 -Heading 'Flexible rendering' -Body 'RenderManager now supports sprite regions for abstract assets and shape fallbacks when no texture is available.' -AccentRgb $Colors.Coral
    Add-InfoCard -Slide $slide -Left 336 -Top 226 -Width 262 -Height 112 -Heading 'Better physics support' -Body 'Radius-based bodies and circle-aware overlap checks fit the sandbox domain better than box-only behaviour.' -AccentRgb $Colors.Teal
    Add-InfoCard -Slide $slide -Left 624 -Top 226 -Width 262 -Height 112 -Heading 'Shared audio services' -Body 'SoundManager centralises music, SFX, and runtime volume control across scenes and sandbox events.' -AccentRgb $Colors.Gold
    Add-TextBox -Slide $slide -Left 48 -Top 372 -Width 838 -Height 50 -Text 'Together, these changes move the engine from a Part 1 demo foundation toward a reusable application framework.' -FontSize 16 -FontName 'Aptos' -Color $Colors.SoftText
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide4

    # Slide 5
    $slide = Initialize-ContentSlide -Presentation $presentation -Title 'OOP And Design Patterns'
    Add-InfoCard -Slide $slide -Left 48 -Top 92 -Width 272 -Height 96 -Heading 'ECS composition' -Body 'Entities gain behaviour through components such as transform, mass, radius, movement, and collision.' -AccentRgb $Colors.Teal
    Add-InfoCard -Slide $slide -Left 336 -Top 92 -Width 272 -Height 96 -Heading 'Factory pattern' -Body 'CelestialEntityFactory builds stars and planets from shared validation and creation rules.' -AccentRgb $Colors.Gold
    Add-InfoCard -Slide $slide -Left 624 -Top 92 -Width 272 -Height 96 -Heading 'Strategy pattern' -Body 'CollisionResolver swaps between merge mode and mutual destruction without changing the manager.' -AccentRgb $Colors.Green
    Add-InfoCard -Slide $slide -Left 48 -Top 206 -Width 272 -Height 96 -Heading 'Observer pattern' -Body 'EventBus publishes scene, collision, and audio events without tight dependencies between services.' -AccentRgb $Colors.Coral
    Add-InfoCard -Slide $slide -Left 336 -Top 206 -Width 272 -Height 96 -Heading 'Adapter pattern' -Body 'SandboxNBodyAdapter converts ECS entities into lightweight prediction states for trajectory previews.' -AccentRgb $Colors.Teal
    $null = Add-Panel -Slide $slide -Left 624 -Top 206 -Width 272 -Height 160 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
    Add-TextBox -Slide $slide -Left 646 -Top 224 -Width 228 -Height 20 -Text 'SOLID in practice' -FontSize 16 -FontName 'Aptos Display' -Color $Colors.Gold -Bold
    Add-TextBox -Slide $slide -Left 646 -Top 252 -Width 220 -Height 98 -Text 'Single-purpose managers, swappable behaviours, and a clean abstract-versus-concrete package split help keep the system easier to extend and test.' -FontSize 13 -FontName 'Aptos' -Color $Colors.White
    $null = Add-ImageFit -Slide $slide -Path $assets.UmlDiagram -Left 48 -Top 326 -Width 560 -Height 156 -AddBorder
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide5

    # Slide 6
    $slide = Initialize-ContentSlide -Presentation $presentation -Title 'Key Sandbox Features'
    Add-BulletList -Slide $slide -Left 48 -Top 92 -Width 410 -Height 310 -Items @(
        'Blueprint mode previews motion before the live simulation starts.',
        'Users can spawn stars and planets with custom mass, radius, position, and velocity.',
        'Clicking a body loads it into the control panel for live editing.',
        'The educational HUD shows speed, acceleration, nearest star distance, and orbit type.',
        'The pause scene controls audio, collision mode, velocity vectors, and navigation.'
    )
    $null = Add-Panel -Slide $slide -Left 500 -Top 92 -Width 360 -Height 164 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
    Add-TextBox -Slide $slide -Left 522 -Top 108 -Width 316 -Height 20 -Text 'Visual assets used by the sandbox' -FontSize 15 -FontName 'Aptos Display' -Color $Colors.Gold -Bold
    $null = Add-ImageFit -Slide $slide -Path $assets.SpriteSheet -Left 516 -Top 136 -Width 328 -Height 100 -AddBorder
    $null = Add-Panel -Slide $slide -Left 500 -Top 274 -Width 360 -Height 170 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
    Add-TextBox -Slide $slide -Left 522 -Top 290 -Width 316 -Height 20 -Text 'Sandbox atmosphere and presentation' -FontSize 15 -FontName 'Aptos Display' -Color $Colors.Gold -Bold
    $null = Add-ImageFit -Slide $slide -Path $assets.SandboxBackground -Left 516 -Top 318 -Width 328 -Height 108 -AddBorder
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide6

    # Slide 7
    $slide = Initialize-ContentSlide -Presentation $presentation -Title 'How A Simulation Step Works'
    Add-InfoCard -Slide $slide -Left 48 -Top 128 -Width 190 -Height 104 -Heading '1. Input + scene logic' -Body 'Process UI events, edits, mode switches, and pause controls before the systems run.' -AccentRgb $Colors.Teal
    Add-InfoCard -Slide $slide -Left 270 -Top 128 -Width 190 -Height 104 -Heading '2. GravityManager' -Body 'Compute acceleration contributions from all active massive bodies.' -AccentRgb $Colors.Gold
    Add-InfoCard -Slide $slide -Left 492 -Top 128 -Width 190 -Height 104 -Heading '3. MovementManager' -Body 'Apply updated velocity and move entities to their next positions.' -AccentRgb $Colors.Green
    Add-InfoCard -Slide $slide -Left 714 -Top 128 -Width 190 -Height 104 -Heading '4. CollisionManager' -Body 'Detect overlap and hand resolution to the currently active collision strategy.' -AccentRgb $Colors.Coral
    foreach ($x in @(242, 464, 686)) {
        $arrow = $slide.Shapes.AddShape(1, $x, 168, 18, 4)
        Set-ShapeFill -Shape $arrow -Rgb $Colors.SoftText
        Set-ShapeLine -Shape $arrow -Hide
    }
    $null = Add-Panel -Slide $slide -Left 96 -Top 282 -Width 768 -Height 118 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
    Add-TextBox -Slide $slide -Left 122 -Top 302 -Width 716 -Height 76 -Text 'In blueprint mode, the trajectory service copies the current entities into simplified N-body states and predicts sampled future paths. At the same time, the educational HUD interprets outcomes like Free Drift, Near Circular, Elliptical, and Escape to help users understand what they are seeing.' -FontSize 15 -FontName 'Aptos' -Color $Colors.White
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide7

    # Slide 8
    $slide = Initialize-ContentSlide -Presentation $presentation -Title 'Demo Walkthrough For The Video'
    $demoSteps = @(
        @{ Num = '01'; Title = 'Place a star'; Body = 'Create the anchor body that will provide the main gravity source.'; Accent = $Colors.Teal; Top = 98 },
        @{ Num = '02'; Title = 'Add a planet'; Body = 'Tune its mass and velocity until the predicted path looks reasonable.'; Accent = $Colors.Gold; Top = 170 },
        @{ Num = '03'; Title = 'Start simulation'; Body = 'Compare the live motion against the blueprint prediction.'; Accent = $Colors.Green; Top = 242 },
        @{ Num = '04'; Title = 'Pause and inspect'; Body = 'Show vectors, audio controls, and the collision mode toggle.'; Accent = $Colors.Coral; Top = 314 },
        @{ Num = '05'; Title = 'Trigger a collision'; Body = 'Demonstrate the difference between destruction and merge behaviour.'; Accent = $Colors.Teal; Top = 386 }
    )
    foreach ($step in $demoSteps) {
        $null = Add-Panel -Slide $slide -Left 64 -Top $step.Top -Width 828 -Height 56 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
        $badge = $slide.Shapes.AddShape(1, 78, ($step.Top + 10), 54, 36)
        Set-ShapeFill -Shape $badge -Rgb $step.Accent
        Set-ShapeLine -Shape $badge -Hide
        Add-TextBox -Slide $slide -Left 90 -Top ($step.Top + 16) -Width 30 -Height 18 -Text $step.Num -FontSize 15 -FontName 'Aptos Display' -Color $Colors.Navy -Bold -Alignment 2
        Add-TextBox -Slide $slide -Left 154 -Top ($step.Top + 12) -Width 182 -Height 18 -Text $step.Title -FontSize 16 -FontName 'Aptos Display' -Color $Colors.White -Bold
        Add-TextBox -Slide $slide -Left 350 -Top ($step.Top + 12) -Width 520 -Height 28 -Text $step.Body -FontSize 13 -FontName 'Aptos' -Color $Colors.SoftText
    }
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide8

    # Slide 9
    $slide = Initialize-ContentSlide -Presentation $presentation -Title 'Innovation, Scalability, And Verification'
    Add-InfoCard -Slide $slide -Left 48 -Top 92 -Width 196 -Height 90 -Heading '500 active bodies' -Body 'Configured cap keeps the sandbox bounded and easier to manage.' -AccentRgb $Colors.Teal
    Add-InfoCard -Slide $slide -Left 260 -Top 92 -Width 196 -Height 90 -Heading '1x / 2x / 3x speed' -Body 'Runtime speed controls let the same simulation be explored at different pacing.' -AccentRgb $Colors.Gold
    Add-InfoCard -Slide $slide -Left 472 -Top 92 -Width 196 -Height 90 -Heading 'Swappable collisions' -Body 'One manager, two behaviours: mutual destruction or merge strategy.' -AccentRgb $Colors.Green
    Add-InfoCard -Slide $slide -Left 684 -Top 92 -Width 196 -Height 90 -Heading 'Build verified' -Body 'The project passed a successful Gradle build before this deck was generated.' -AccentRgb $Colors.Coral
    Add-BulletList -Slide $slide -Left 64 -Top 232 -Width 816 -Height 210 -Items @(
        'Reuses one engine instead of rewriting logic for a single simulation.',
        'Combines live editing, trajectory prediction, orbit interpretation, and runtime mode switches.',
        'Uses explicit configuration values such as body caps and prediction limits.',
        'Keeps engine services reusable while sandbox logic remains isolated.'
    )
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide9

    # Slide 10
    $slide = Initialize-ContentSlide -Presentation $presentation -Title 'Limitations And Next Steps'
    $null = Add-Panel -Slide $slide -Left 48 -Top 104 -Width 392 -Height 272 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
    Add-TextBox -Slide $slide -Left 70 -Top 122 -Width 348 -Height 20 -Text 'Current limitations' -FontSize 18 -FontName 'Aptos Display' -Color $Colors.Gold -Bold
    Add-BulletList -Slide $slide -Left 70 -Top 158 -Width 340 -Height 184 -Items @(
        'Trajectory prediction uses a simple forward Euler approach.',
        'Long future paths can drift from more accurate orbital behaviour.',
        'Pairwise collision checks are expensive for much larger simulations.',
        'There is no save/load system or preset scenario library yet.'
    )
    $null = Add-Panel -Slide $slide -Left 500 -Top 104 -Width 392 -Height 272 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.02
    Add-TextBox -Slide $slide -Left 522 -Top 122 -Width 348 -Height 20 -Text 'Next improvements' -FontSize 18 -FontName 'Aptos Display' -Color $Colors.Gold -Bold
    Add-BulletList -Slide $slide -Left 522 -Top 158 -Width 340 -Height 184 -Items @(
        'Use more advanced integration and prediction methods.',
        'Introduce spatial partitioning or other collision optimisations.',
        'Add body presets, tutorials, and scenario save/load.',
        'Profile the project further and expand the educational analytics.'
    )
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide10

    # Slide 11
    $slide = $presentation.Slides.Add($presentation.Slides.Count + 1, 12)
    $bg = $slide.Shapes.AddShape(1, 0, 0, 960, 540)
    Set-ShapeFill -Shape $bg -Rgb $Colors.Navy
    Set-ShapeLine -Shape $bg -Hide
    $null = Add-ImageFit -Slide $slide -Path $assets.MainMenuBackground -Left 0 -Top 0 -Width 960 -Height 540
    $overlay = $slide.Shapes.AddShape(1, 0, 0, 960, 540)
    Set-ShapeFill -Shape $overlay -Rgb $Colors.Navy -Transparency 0.28
    Set-ShapeLine -Shape $overlay -Hide
    Add-TextBox -Slide $slide -Left 60 -Top 62 -Width 520 -Height 34 -Text 'Closing Takeaways' -FontSize 28 -FontName 'Aptos Display' -Color $Colors.White -Bold
    Add-BulletList -Slide $slide -Left 60 -Top 138 -Width 500 -Height 236 -Items @(
        'Part 2 demonstrates that our Part 1 engine can support a separate simulation cleanly.',
        'The project showcases reusable architecture, OOP principles, and multiple design patterns.',
        'The sandbox adds educational value through prediction, statistics, and interactive experimentation.',
        'The final deck is designed to support a concise 10-minute video presentation.'
    )
    $null = Add-Panel -Slide $slide -Left 612 -Top 168 -Width 260 -Height 120 -FillRgb $Colors.Panel -LineRgb $Colors.PanelSoft -Transparency 0.08
    Add-TextBox -Slide $slide -Left 636 -Top 194 -Width 212 -Height 46 -Text 'Replace the speaker placeholders in the notes with your real team member names before recording.' -FontSize 14 -FontName 'Aptos' -Color $Colors.White
    Add-TextBox -Slide $slide -Left 60 -Top 486 -Width 220 -Height 16 -Text 'INF1009 Part 2 | Team 6' -FontSize 9 -FontName 'Aptos' -Color $Colors.SoftText
    Add-TextBox -Slide $slide -Left 900 -Top 486 -Width 20 -Height 16 -Text ([string]($presentation.Slides.Count)) -FontSize 9 -FontName 'Aptos' -Color $Colors.SoftText -Alignment 3
    Set-SpeakerNotes -Slide $slide -Notes $notes.Slide11

    $presentation.SaveAs($outputPath)
    Write-Output "Created presentation: $outputPath"

    if ($OpenWhenDone) {
        Start-Process $outputPath
    }
}
finally {
    if ($presentation -ne $null) {
        $presentation.Close()
    }
    if ($powerPoint -ne $null) {
        $powerPoint.Quit()
    }
}
