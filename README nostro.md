# BoardGame

## Group Members

| Student Name    | GitHub Username    |
|-----------------|--------------------|
| Zago Alessandro | @Alessandro1234567 |
| Davoodi Danial  | @Danial-BZ         |
| Chen Nicola     | @cennicola6        |

---

## Usage

1. Clone the repository:

```bash
   git clone git@github.com:Alessandro1234567/BoardProject.git
   ```

2. Run the Gradle wrapper (once is enough):

```bash
   gradle wrapper
   ```

### Starting the application

on macOS/Linux

```bash
   ./gradlew run
   ```

on Windows

```bash
   gradlew.bat run
   ```

### With an IDE

Open this repository as a Gradle project.

Then to start the application, run the
method [DesktopLauncher.main](https://github.com/Alessandro1234567/BoardProject/blob/main/desktop/src/it/unibz/inf/pp/clash/DesktopLauncher.java) (
in your running configuration, you may need to specify `assets` as the Java working directory).

---

## Project description

The game is a tactical RPG with match-3 mechanics

### Main gameplay mechanics:

- Match-3 combat: The battlefield is a grid where you combine 3 units of the same type to fuse them into a stronger
  version.
- Heroic units: Each unit has different health and attack countdowns
- Turn-based strategy: The game is turn-based. Each move requires strategy: you can choose to create barriers, call
  reinforcement or reposition your units.

---

## Design

### Game snapshot

The project is designed around the notion of game snapshot.
A snapshot is an object that intuitively stores all the information needed to resume an interrupted game (state of the
board, remaining health, active player, etc.).
In other words, you can think of a snapshot as a save state.

### Model-view-controller

In order to decouple the graphical interface from the mechanics of the game, the project (loosely) follows the
model-view-controller (MVC) pattern.
This means that the code is partitioned into three components called model, view and controller:

- The controller registers the user actions (click, hovering, etc.), and notifies the model of each action.
- The model is the core of the application. It keeps track of the state of the game and updates it after each action.
  The model takes its input from the controller, and outputs drawing instructions to the view.
- The view is in charge of drawing the game on screen. It takes its input from the model.

Note: This is not the most common interpretation of the MVC pattern: in many applications, the model remains passive,
whereas in this project the model gives instructions to the view.

### Our implementation

[RealEventHandler.java](core%2Fsrc%2Fmain%2Fjava%2Fit%2Funibz%2Finf%2Fpp%2Fclash%2Fmodel%2Fimpl%2FRealEventHandler.java)
is the core game logic controller that processes user interactions, updates game state (`Snapshot`), and delegates
display logic to the `DisplayManager`. It encapsulates behavior like starting a new game, handling turns, managing
reinforcements, and combat resolution.

### Libraries

We only used Java’s standard libraries.

---

## Human experience

### Work Distribution

- **Alessandro Zago**: I started by creating the `utils` folder, where I implemented the `UnitGenerator`class. This
  component is responsible for generating units and is used within `RealSnapshot`.  
  Additionally, I developed the `doAttack()` method inside `RealEventHandler`class, which handles the attack logic in the
  game.


- **Danial Davoodi**:
  I mainly worked on `RealEventHandler`class, where I implemented various methods for the game. In addition to the main
  methods, several auxiliary methods were created to ensure optimal code maintainability and to prevent potential bugs
  during gameplay.


- **Nicola Chen**: I created the `UnitMerger`class, which handles the mechanics of merging the units and rearranges the
  board correctly (i.e. the method `collapse()`, taking chain reactions into considerations.

### Use of Git

Each member of the group created their own Git branch, each one of us worked on different classes and method and merged
clean code into the Main branch regularly to avoid conflicts.
As a result, we had conflicts (which have been resolved fairly easily) only a limited amount of times

### Challenges faced by each member

#### Alessandro Zago

> During the development, I encountered several hurdles that affected efficiency and required focused analysis:
>
> - **Project Structure**:  
    Understanding the interdependencies between modules took significant time. I traced folder hierarchies and entry
    points to identify how to implement feature changes.
>
> #### Single Board Instance:
>
> - **Advantage**:  
    > Centralized state in `model/impl/RealEventHandler.doAttack()` made coordinating attack sequences straightforward
    and reduced data duplication.
>
>- **Disadvantage**:  
   > In `model/utils/UnitGenerator`, spawning units safely demanded extra synchronization and collision checks,
   increasing code complexity.

#### Danial Davoodi

> One of the difficulties I encountered while working on this project was the initial challenge of understanding how the
> game’s code works, and I had to carefully read through the code and all the declarations and usages of the various
> methods.

#### Nicola Chen

> The initial challenge was to become familiar with code written by another person, after that it was mainly about how to structure the class `UnitMerger`
> and write clean, factorized and easy to read code
---