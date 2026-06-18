package maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MazePanel extends JPanel {

    private static final int CELL_SIZE = 28;
    private static final int WALL_THICKNESS = 2;

    // Colors
    private static final Color BG          = new Color(15, 17, 26);
    private static final Color WALL_COLOR  = new Color(80, 100, 160);
    private static final Color START_COLOR = new Color(72, 199, 142);
    private static final Color END_COLOR   = new Color(255, 100, 100);
    private static final Color PLAYER_COLOR= new Color(255, 220, 50);
    private static final Color EXPLORED_BFS= new Color(30, 100, 180, 140);
    private static final Color EXPLORED_DFS= new Color(120, 40, 160, 140);
    private static final Color PATH_COLOR  = new Color(255, 200, 60, 220);
    private static final Color TRAIL_COLOR = new Color(72, 199, 142, 80);
    private static final Color WIN_OVERLAY = new Color(0, 0, 0, 160);

    private Cell[][] grid;
    private int rows, cols;

    // Player state
    private int playerRow = 0, playerCol = 0;
    private boolean[][] playerTrail;
    private boolean playerWon = false;

    // Solve animation
    private Timer animTimer;
    private List<int[]> solveSteps;
    private int stepIndex = 0;
    private boolean[][] exploredMark;
    private boolean[][] finalPathMark;
    private boolean isSolving = false;

    private StatusCallback statusCallback;

    public interface StatusCallback {
        void onStatus(String msg);
        void onWin();
    }

    public MazePanel(StatusCallback cb) {
        this.statusCallback = cb;
        setBackground(BG);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isSolving || playerWon) return;
                handlePlayerMove(e.getKeyCode());
            }
        });
    }

    public void loadMaze(Cell[][] grid) {
        this.grid = grid;
        this.rows = grid.length;
        this.cols = grid[0].length;
        this.playerRow = 0;
        this.playerCol = 0;
        this.playerWon = false;
        this.isSolving = false;
        this.playerTrail = new boolean[rows][cols];
        this.exploredMark = new boolean[rows][cols];
        this.finalPathMark = new boolean[rows][cols];
        playerTrail[0][0] = true;

        if (animTimer != null) animTimer.stop();

        setPreferredSize(new Dimension(cols * CELL_SIZE + 1, rows * CELL_SIZE + 1));
        revalidate();
        repaint();
        requestFocusInWindow();
        statusCallback.onStatus("Use arrow keys to navigate. Reach the 🔴 red cell to win!");
    }

    public void startSolve(MazeSolver.Algorithm algo) {
        if (grid == null) return;
        if (animTimer != null) animTimer.stop();

        // Clear previous solve marks
        exploredMark = new boolean[rows][cols];
        finalPathMark = new boolean[rows][cols];

        MazeSolver solver = new MazeSolver(grid);
        solveSteps = solver.solve(algo);
        stepIndex = 0;
        isSolving = true;

        String name = algo == MazeSolver.Algorithm.BFS ? "BFS" : "DFS";
        statusCallback.onStatus("Solving with " + name + "... watch the exploration!");

        animTimer = new Timer(18, e -> {
            if (stepIndex < solveSteps.size()) {
                int[] step = solveSteps.get(stepIndex++);
                int r = step[0], c = step[1], type = step[2];
                if (type == 0) exploredMark[r][c] = true;
                else finalPathMark[r][c] = true;
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
                isSolving = false;
                statusCallback.onStatus(name + " done! Green = path. Press New Maze to play again.");
                repaint();
            }
        });
        animTimer.start();
    }

    public void clearSolve() {
        if (animTimer != null) animTimer.stop();
        isSolving = false;
        exploredMark = new boolean[rows][cols];
        finalPathMark = new boolean[rows][cols];
        repaint();
    }

    private void handlePlayerMove(int keyCode) {
        if (grid == null) return;
        Cell cur = grid[playerRow][playerCol];
        int nr = playerRow, nc = playerCol;

        switch (keyCode) {
            case KeyEvent.VK_UP:    if (!cur.walls[0]) { nr--; } break;
            case KeyEvent.VK_RIGHT: if (!cur.walls[1]) { nc++; } break;
            case KeyEvent.VK_DOWN:  if (!cur.walls[2]) { nr++; } break;
            case KeyEvent.VK_LEFT:  if (!cur.walls[3]) { nc--; } break;
            default: return;
        }

        if (nr != playerRow || nc != playerCol) {
            playerRow = nr;
            playerCol = nc;
            playerTrail[playerRow][playerCol] = true;
            repaint();

            if (playerRow == rows - 1 && playerCol == cols - 1) {
                playerWon = true;
                repaint();
                statusCallback.onWin();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * CELL_SIZE;
                int y = r * CELL_SIZE;

                // Cell background
                g2.setColor(BG);
                g2.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                // Explored / path overlays
                if (finalPathMark[r][c]) {
                    g2.setColor(PATH_COLOR);
                    g2.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                } else if (exploredMark[r][c]) {
                    // BFS vs DFS color
                    g2.setColor(EXPLORED_BFS);
                    g2.fillRect(x + 1, y + 1, CELL_SIZE - 2, CELL_SIZE - 2);
                }

                // Player trail
                if (playerTrail[r][c] && !playerWon) {
                    g2.setColor(TRAIL_COLOR);
                    g2.fillRect(x + 4, y + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                }

                // Start
                if (r == 0 && c == 0) {
                    g2.setColor(START_COLOR);
                    g2.fillOval(x + 6, y + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                }

                // End
                if (r == rows - 1 && c == cols - 1) {
                    g2.setColor(END_COLOR);
                    g2.fillOval(x + 6, y + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                }

                // Player
                if (r == playerRow && c == playerCol) {
                    g2.setColor(PLAYER_COLOR);
                    g2.fillOval(x + 5, y + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                    // glow
                    g2.setColor(new Color(255, 220, 50, 60));
                    g2.fillOval(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
                }

                // Walls
                g2.setColor(WALL_COLOR);
                g2.setStroke(new BasicStroke(WALL_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Cell cell = grid[r][c];
                if (cell.walls[0]) g2.drawLine(x, y, x + CELL_SIZE, y);
                if (cell.walls[1]) g2.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE);
                if (cell.walls[2]) g2.drawLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
                if (cell.walls[3]) g2.drawLine(x, y, x, y + CELL_SIZE);
            }
        }

        // Win overlay
        if (playerWon) {
            g2.setColor(WIN_OVERLAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(START_COLOR);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
            String msg = "🎉 YOU WIN!";
            FontMetrics fm = g2.getFontMetrics();
            int mx = (getWidth() - fm.stringWidth(msg)) / 2;
            int my = getHeight() / 2;
            g2.drawString(msg, mx, my);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            String sub = "Press 'New Maze' to play again";
            fm = g2.getFontMetrics();
            g2.drawString(sub, (getWidth() - fm.stringWidth(sub)) / 2, my + 36);
        }
    }
}
