package maze;

import java.util.*;

public class MazeGenerator {

    private final int rows, cols;
    private final Cell[][] grid;
    private final Random random = new Random();

    public MazeGenerator(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                grid[r][c] = new Cell(r, c);
    }

    public Cell[][] generate() {
        // Reset
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++) {
                grid[r][c].visited = false;
                grid[r][c].walls = new boolean[]{true, true, true, true};
                grid[r][c].reset();
            }

        // Recursive backtracking from top-left
        Stack<Cell> stack = new Stack<>();
        Cell start = grid[0][0];
        start.visited = true;
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Cell> neighbors = getUnvisitedNeighbors(current);

            if (neighbors.isEmpty()) {
                stack.pop();
            } else {
                Cell next = neighbors.get(random.nextInt(neighbors.size()));
                removeWall(current, next);
                next.visited = true;
                stack.push(next);
            }
        }

        return grid;
    }

    private List<Cell> getUnvisitedNeighbors(Cell cell) {
        List<Cell> list = new ArrayList<>();
        int r = cell.row, c = cell.col;
        if (r > 0 && !grid[r-1][c].visited) list.add(grid[r-1][c]);
        if (c < cols-1 && !grid[r][c+1].visited) list.add(grid[r][c+1]);
        if (r < rows-1 && !grid[r+1][c].visited) list.add(grid[r+1][c]);
        if (c > 0 && !grid[r][c-1].visited) list.add(grid[r][c-1]);
        return list;
    }

    private void removeWall(Cell a, Cell b) {
        int dr = b.row - a.row;
        int dc = b.col - a.col;
        if (dr == -1) { a.walls[0] = false; b.walls[2] = false; } // top
        if (dc == 1)  { a.walls[1] = false; b.walls[3] = false; } // right
        if (dr == 1)  { a.walls[2] = false; b.walls[0] = false; } // bottom
        if (dc == -1) { a.walls[3] = false; b.walls[1] = false; } // left
    }
}
