package maze;

public class Cell {
    public int row, col;

    // Walls: top, right, bottom, left
    public boolean[] walls = {true, true, true, true};
    public boolean visited = false;

    // For solve visualization
    public boolean inBFSPath = false;
    public boolean inDFSPath = false;
    public boolean inFinalPath = false;
    public boolean exploredBFS = false;
    public boolean exploredDFS = false;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void reset() {
        inBFSPath = false;
        inDFSPath = false;
        inFinalPath = false;
        exploredBFS = false;
        exploredDFS = false;
    }
}
