## INF1009 Project — Abstract Engine & Simulation

### 1) Submission Logistics

- **Deadline:** Wednesday, 18 February 2026, by 23:59
- **Method:** Submit a single `.zip` to your lab group's xSITE Dropbox
- **Naming:** `[LabGroup_TeamNumber_ProjectPart1Final]`
- **Folder contents:**
  1. Report
  2. Presentation (slides)
  3. Project (all code files, including Abstract Engine and Simulation)
  4. Text file containing the presentation video link

#### Quick Submission Checklist

- [ ] Zip file named correctly
- [ ] Report included
- [ ] Slides included
- [ ] Code (Engine + Simulation) included
- [ ] Text file with video link included

---

### 2) Core Technical Requirements

The goal is to demonstrate solid OOP (classes, inheritance, polymorphism) and adherence to SOLID principles. The project uses **LibGDX**.

#### Abstract Engine (50% of grade)

- **Design:** Modular, reusable, and free of context-specific logic (no hardcoded game rules like "rockets" or "traffic lights" in the engine layer).
- **Must-have managers:**
  - **Scene Management:** Load, unload, and transition between scenes
  - **Entity Management:** Create, update, and manage entities using components (e.g., `Transform`, `Renderable`, `PhysicsBody`)
  - **Collision Management:** Detect and resolve interactions
  - **Movement Management:** Control movement for different entities
  - **Input/Output Management:** Handle user inputs (keyboard, mouse)

#### Simulation (15% of grade)

- Prototype that demonstrates the engine works
- Uses a general example to showcase the managers
- Initializes and ends without errors
- Demonstrates scalability/extensibility (e.g., handling 400 objects vs. 4 players)

---

### 3) Documentation and Presentation

#### Final Report (10% of grade)

- **Length:** Up to 15 pages
- **Content:**
  - Overall system design and purpose of each manager
  - Complete UML diagram for the Abstract Engine
  - Justification of OOP principles adopted
  - Reflection on engine design limitations
  - Clear description of each team member's contribution
- **Quality:** Professional, well-structured, clearly explains results and conclusions

#### Presentation and Video (5% + 5% of grade)

- **Video length:** 8–10 minutes
- **Slide deck:** 10–12 slides
- **Content:** Summarize the report, demo the engine simulation, highlight scalability/reusability, and discuss the must-have managers

---

### 4) Innovation and Individual Assessment

- **Innovation (10%):** Go beyond basics—creative features, smooth simulation transitions
- **Individual Reflection (5%):** Honest reflection of your contribution, including how AI was used
- **Peer Evaluation:** Peers assign a delta based on contribution

---

### Summary of Grading Criteria

| Component                      | Weight | Key Expectation                                                                 |
| ------------------------------ | :----: | ------------------------------------------------------------------------------- |
| Abstract Engine Implementation |  50%   | Good OOP practices, code quality, must-have managers, no context-specific logic |
| Abstract Engine Simulation     |  15%   | Showcases the engine, error-free, demonstrates scaling                          |
| Report                         |  10%   | Structured, grammatical, includes UML and design justification                  |
| Innovation                     |  10%   | Creative implementation and additional features                                 |
| Presentation                   |   5%   | Clear, logical structure                                                        |
| Video                          |   5%   | Engaging, within time limit, demos the simulation                               |
| Individual Reflection          |   5%   | Honest reflection and peer appraisal                                            |

---

### Tips for Clarity

- Keep the engine generic; push game-specific logic into the simulation or game layer
- Prefer components over deep inheritance; keep entities lightweight
- Document each manager's responsibilities and boundaries
- Add small load tests to demonstrate scalability (e.g., spawn many simple entities)

#### Example: Abstract Classes in Java

```java
// Abstract class declaration
public abstract class Animal {
        // Abstract method (no body, must be implemented by subclasses)
        public abstract void makeSound();

        // Concrete method (with implementation)
        public void eat() {
                System.out.println("This animal eats food.");
        }
}

// Concrete subclass
public class Dog extends Animal {
        // Must implement the abstract method
        @Override
        public void makeSound() {
                System.out.println("Woof woof");
        }
}

public class Main {
        public static void main(String[] args) {
                Dog myDog = new Dog();
                myDog.makeSound(); // Output: Woof woof
                myDog.eat();       // Output: This animal eats food.

                // Invalid: Cannot instantiate an abstract class
                // Animal genericAnimal = new Animal();
        }
}
```

Draw.io Link for UML: 