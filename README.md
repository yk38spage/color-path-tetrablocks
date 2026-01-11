# Color-Path Tetrablocks - Project Documentation

## 1. Project Overview

**Color-Path Tetrablocks** is a Java Swing-based puzzle game that innovates on the classic falling-block formula. While players can still clear lines horizontally, the game introduces a strategic "Color Path" mechanic: connecting blocks of the same color from the left wall to the right wall clears them, adding a new layer of depth to the gameplay.

## 2. Gameplay Mechanics

### 2.1. Core Rules

* **Tetrablocks:** The game features 7 standard shapes (I, J, L, O, S, T, Z).
* **Color System:** Each piece is randomly assigned one of three colors: **Red**, **Green**, or **Blue**.
* **Classic Line Clearing:** Filling a horizontal row completely with blocks (regardless of color) clears that line, shifts blocks down, and awards points.
* **Game Over:** The game ends if the grid fills up and a new piece cannot spawn.

### 2.2. The "Color Path" Mechanic

This is the game's unique feature. The logic runs automatically every time a piece lands:

* **Goal:** Create a continuous chain of blocks of the **same color** connecting the far-left column (index 0) to the far-right column (index 9).
* **Algorithm:** The game uses a **Breadth-First Search (BFS)** algorithm to detect these paths. It scans from the left wall, checking adjacent neighbors (up, down, left, right) of the matching color.
* **Reward:** If a path is found, all blocks in that path are destroyed. Remaining blocks above them fall down (gravity is applied), potentially creating chain reactions.

### 2.3. Difficulty Levels

The Main Menu allows the player to select a starting difficulty, which dictates the initial game speed (tick delay):

* **Level 1 (Easy):** 700ms delay.
* **Level 2 (Medium):** 400ms delay.
* **Level 3 (Hard):** 200ms delay.

### 2.4. Progression System

* **Speed Up:** The game automatically speeds up (delay decreases by 50ms) for every **10 lines** cleared, capped at a maximum speed (minimum delay of 100ms).
* **Scoring:**
  * **Soft Drop:** 2 points per tick.
  * **Hard Drop:** 2 points per cell dropped.
  * **Line Clear:** 100 points per line.
  * **Color Path:** 50 points per block removed + 2 "lines cleared" credit.

## 3. Controls

The game features responsive keyboard controls handled via a `KeyAdapter`.

| Key             | Action     | Description                                    |
| --------------- | ---------- | ---------------------------------------------- |
| **Arrow Left**  | Move Left  | Moves the active piece one column left.        |
| **Arrow Right** | Move Right | Moves the active piece one column right.       |
| **Arrow Up**    | Rotate     | Rotates the piece 90° clockwise.               |
| **Arrow Down**  | Soft Drop  | Drops piece faster (awards points).            |
| **Space**       | Hard Drop  | Instantly drops piece to the bottom.           |
| **C**           | Hold Piece | Stores current piece or swaps with held piece. |
| **P**           | Pause      | Pauses/Resumes the game.                       |
| **ESC**         | Exit       | Returns to the Main Menu.                      |

## 4. Technical Architecture

### 4.1. Technology Stack

* **Language:** Java (JDK 21 recommended based on configuration).
* **GUI Framework:** Java Swing (JPanel, JFrame, Graphics).
* **Build Tool:** Apache Maven.

### 4.2. Class Structure

* **`Color_path_tetrablocks.java`**: The entry point. It sets up the `JFrame` and uses a `CardLayout` to switch between the **Main Menu** and the **Game Instance**.
* **`TetrablocksGame.java`**: The core engine. It manages:
* **Game Loop:** Uses `javax.swing.Timer` to handle gravity ticks.
* **Rendering:** Custom painting in `BoardPanel` (grid) and `SidePanel` (UI/Stats).
* **Logic:** Collision detection (`tryMove`), line clearing (`removeLines`), and pathfinding (`checkConnectedColorPath`).

## 5. How to Build and Run

Since this is a Maven project, you can easily build it into an executable JAR file.

### Prerequisites

* Java Development Kit (JDK) 21 installed.
* Maven installed (or use your IDE's built-in Maven).

### Step 1: Build the JAR

Open your terminal in the project root directory (where `pom.xml` is located) and run:

```bash
mvn clean package

```

* **clean:** Removes old build files.
* **package:** Compiles code and generates the JAR file in the `target` folder.

### Step 2: Run the Game

Navigate to the `target` directory and execute the generated JAR file:

```bash
cd target
java -jar color_path_tetrablocks-1.0-SNAPSHOT.jar
```

## 6. Licence

This project is licensed under the **GNU General Public License v3.0 (GPLv3).

For the conditions of the license, please see the [LICENSE](./LICENSE) file in the project repository or visit [gnu.org/licenses/gpl-3.0](https://www.gnu.org/licenses/gpl-3.0).