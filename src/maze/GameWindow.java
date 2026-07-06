package maze;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GameWindow extends JFrame {

    private static final Color BG       = new Color(15, 17, 26);
    private static final Color PANEL_BG = new Color(22, 26, 40);
    private static final Color ACCENT   = new Color(72, 199, 142);
    private static final Color BTN_BG   = new Color(35, 45, 70);
    private static final Color BTN_HOVER= new Color(55, 70, 110);
    private static final Color TEXT     = new Color(210, 215, 230);
    private static final Color SUBTEXT  = new Color(130, 145, 175);

    private MazePanel mazePanel;
    private JLabel statusLabel;
    private JComboBox<String> sizeCombo;
    private JComboBox<String> algoCombo;
    private Cell[][] currentGrid;

    public GameWindow() {
        setTitle("Maze Solver — DSA Game");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(BG);
        getContentPane().setBackground(BG);

        buildUI();
        generateNewMaze();

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(500, 400));
    }

    private void buildUI() {
        setLayout(new BorderLayout(0, 0));

        // ── Top header ──
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PANEL_BG);
        header.setBorder(new EmptyBorder(12, 18, 12, 18));

        JLabel title = new JLabel("Maze Solver");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(ACCENT);
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Navigate with arrow keys · Watch BFS/DFS solve it");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(SUBTEXT);
        header.add(subtitle, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── Maze panel (center) ──
        mazePanel = new MazePanel(new MazePanel.StatusCallback() {
            @Override public void onStatus(String msg) { statusLabel.setText(msg); }
            @Override public void onWin() {
                statusLabel.setText("You reached the exit! Press 'New Maze' to play again.");
            }
        });
        mazePanel.setBackground(BG);
        mazePanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JScrollPane scroll = new JScrollPane(mazePanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.setBackground(BG);
        add(scroll, BorderLayout.CENTER);

        // ── Bottom controls ──
        JPanel controls = new JPanel();
        controls.setBackground(PANEL_BG);
        controls.setBorder(new EmptyBorder(10, 16, 14, 16));
        controls.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 6));

        // Size selector
        JLabel sizeLabel = styledLabel("Size:");
        sizeCombo = new JComboBox<>(new String[]{"10×10", "15×15", "20×20", "25×25"});
        sizeCombo.setSelectedIndex(1);
        styleCombo(sizeCombo);

        // Algo selector
        JLabel algoLabel = styledLabel("Algorithm:");
        algoCombo = new JComboBox<>(new String[]{"BFS (Shortest)", "DFS (Deep)"});
        styleCombo(algoCombo);

        // Buttons
        JButton newBtn   = styledButton("New Maze", ACCENT);
        JButton solveBtn = styledButton("▶ Auto-Solve", new Color(100, 160, 255));
        JButton clearBtn = styledButton("✕ Clear", new Color(180, 80, 80));

        newBtn.addActionListener(e -> generateNewMaze());
        solveBtn.addActionListener(e -> {
            if (currentGrid == null) return;
            MazeSolver.Algorithm algo = algoCombo.getSelectedIndex() == 0
                    ? MazeSolver.Algorithm.BFS : MazeSolver.Algorithm.DFS;
            mazePanel.startSolve(algo);
        });
        clearBtn.addActionListener(e -> mazePanel.clearSolve());

        controls.add(sizeLabel);
        controls.add(sizeCombo);
        controls.add(Box.createHorizontalStrut(6));
        controls.add(algoLabel);
        controls.add(algoCombo);
        controls.add(Box.createHorizontalStrut(12));
        controls.add(newBtn);
        controls.add(solveBtn);
        controls.add(clearBtn);

        // ── Status bar ──
        statusLabel = new JLabel("Generating maze...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(SUBTEXT);
        statusLabel.setBorder(new EmptyBorder(0, 18, 8, 18));
        statusLabel.setBackground(PANEL_BG);
        statusLabel.setOpaque(true);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(PANEL_BG);
        bottom.add(controls, BorderLayout.NORTH);
        bottom.add(statusLabel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        // ── Legend ──
        JPanel legend = buildLegend();
        add(legend, BorderLayout.EAST);
    }

    private void generateNewMaze() {
        String sizeStr = (String) sizeCombo.getSelectedItem();
        int size = Integer.parseInt(sizeStr.split("×")[0]);
        MazeGenerator gen = new MazeGenerator(size, size);
        currentGrid = gen.generate();
        mazePanel.loadMaze(currentGrid);
        pack();
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(PANEL_BG);
        p.setBorder(new EmptyBorder(16, 10, 16, 16));

        p.add(legendTitle("Legend"));
        p.add(Box.createVerticalStrut(8));
        p.add(legendItem(new Color(255, 220, 50),   "You (player)"));
        p.add(Box.createVerticalStrut(4));
        p.add(legendItem(new Color(72, 199, 142),   "Start"));
        p.add(Box.createVerticalStrut(4));
        p.add(legendItem(new Color(255, 100, 100),  "Exit"));
        p.add(Box.createVerticalStrut(4));
        p.add(legendItem(new Color(30, 100, 180),   "Explored"));
        p.add(Box.createVerticalStrut(4));
        p.add(legendItem(new Color(255, 200, 60),   "Shortest path"));
        p.add(Box.createVerticalStrut(16));
        p.add(legendTitle("Controls"));
        p.add(Box.createVerticalStrut(6));
        p.add(controlHint("↑ ↓ ← →", "Move"));
        p.add(Box.createVerticalStrut(4));
        p.add(controlHint("▶ Solve", "Auto-solve"));
        p.add(Box.createVerticalStrut(4));
        p.add(controlHint("✕ Clear", "Reset paths"));
        return p;
    }

    private JLabel legendTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(SUBTEXT);
        return l;
    }

    private JPanel legendItem(Color color, String label) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setBackground(PANEL_BG);
        JPanel swatch = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, 12, 12);
            }
        };
        swatch.setPreferredSize(new Dimension(12, 12));
        swatch.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(TEXT);
        row.add(swatch);
        row.add(lbl);
        return row;
    }

    private JPanel controlHint(String key, String action) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        row.setBackground(PANEL_BG);
        JLabel k = new JLabel(key);
        k.setFont(new Font("Segoe UI", Font.BOLD, 10));
        k.setForeground(ACCENT);
        JLabel a = new JLabel("— " + action);
        a.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        a.setForeground(SUBTEXT);
        row.add(k); row.add(a);
        return row;
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(SUBTEXT);
        return l;
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setBackground(BTN_BG);
        cb.setForeground(TEXT);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBorder(BorderFactory.createLineBorder(BTN_HOVER));
        cb.setFocusable(false);
    }

    private JButton styledButton(String text, Color accent) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BTN_HOVER : BTN_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(accent);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(accent);
        btn.setFocusable(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 14, 6, 14));
        return btn;
    }
}
