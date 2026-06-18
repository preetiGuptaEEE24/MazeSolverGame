# 🧩 Maze Solver Game — Java DSA Project

A fully playable maze game with animated BFS/DFS solver visualization.

## How to Run

**On Windows:**
```
run.bat
```

**On Linux/Mac:**
```
./run.sh
```

**Manual:**
```bash
mkdir -p out
javac -d out src/maze/*.java
java -cp out maze.Main
```

## Features

- **Random maze generation** using Recursive Backtracking (DFS on cells)
- **Player navigation** with arrow keys
- **BFS solver** — finds the SHORTEST path, explores layer by layer
- **DFS solver** — explores deeply before backtracking
- **Animated visualization** — watch the algorithm explore in real time
- **4 maze sizes**: 10×10, 15×15, 20×20, 25×25

## DSA Concepts Used

| Concept | Where |
|---|---|
| Stack (implicit recursion) | Maze generation |
| DFS | Maze generation + DFS solver |
| BFS (Queue) | BFS solver |
| 2D Arrays | Grid representation |
| HashMap | Parent tracking for path reconstruction |

## File Structure

```
src/maze/
├── Main.java          — Entry point
├── Cell.java          — Grid cell model (walls, state)
├── MazeGenerator.java — Recursive backtracking generator
├── MazeSolver.java    — BFS & DFS with step recording
├── MazePanel.java     — Swing rendering + player input
└── GameWindow.java    — Main window, controls, layout
```
