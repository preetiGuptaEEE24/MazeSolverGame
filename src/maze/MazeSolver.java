package maze;

import java.util.*;

public class MazeSolver {

    public enum Algorithm { BFS, DFS }

    private final Cell[][] grid;
    private final int rows, cols;

    public MazeSolver(Cell[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
    }

    /**
     * Returns ordered list of (row,col) cells visited during exploration,
     * followed by final path cells marked separately.
     * Each int[] = {row, col, type}  type: 0=explored, 1=final path
     */
    public List<int[]> solve(Algorithm algo) {
        // Reset visualization state
        for (Cell[] row : grid)
            for (Cell c : row)
                c.reset();

        Cell start = grid[0][0];
        Cell end = grid[rows-1][cols-1];

        Map<Cell, Cell> parent = new LinkedHashMap<>();
        List<int[]> steps = new ArrayList<>();

        if (algo == Algorithm.BFS) {
            bfs(start, end, parent, steps);
        } else {
            dfs(start, end, parent, steps, new boolean[rows][cols]);
        }

        // Trace final path
        List<int[]> pathSteps = new ArrayList<>();
        Cell cur = end;
        while (cur != null) {
            pathSteps.add(0, new int[]{cur.row, cur.col, 1});
            cur = parent.get(cur);
        }

        steps.addAll(pathSteps);
        return steps;
    }

    private void bfs(Cell start, Cell end, Map<Cell, Cell> parent, List<int[]> steps) {
        Queue<Cell> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            steps.add(new int[]{cur.row, cur.col, 0});
            if (cur == end) break;

            for (Cell nb : getPassableNeighbors(cur)) {
                if (!visited.contains(nb)) {
                    visited.add(nb);
                    parent.put(nb, cur);
                    queue.add(nb);
                }
            }
        }
    }

    private boolean dfs(Cell cur, Cell end, Map<Cell, Cell> parent, List<int[]> steps, boolean[][] visited) {
        visited[cur.row][cur.col] = true;
        steps.add(new int[]{cur.row, cur.col, 0});
        if (cur == end) return true;

        for (Cell nb : getPassableNeighbors(cur)) {
            if (!visited[nb.row][nb.col]) {
                parent.put(nb, cur);
                if (dfs(nb, end, parent, steps, visited)) return true;
            }
        }
        return false;
    }

    private List<Cell> getPassableNeighbors(Cell cell) {
        List<Cell> list = new ArrayList<>();
        int r = cell.row, c = cell.col;
        // top
        if (!cell.walls[0] && r > 0) list.add(grid[r-1][c]);
        // right
        if (!cell.walls[1] && c < cols-1) list.add(grid[r][c+1]);
        // bottom
        if (!cell.walls[2] && r < rows-1) list.add(grid[r+1][c]);
        // left
        if (!cell.walls[3] && c > 0) list.add(grid[r][c-1]);
        return list;
    }
}
