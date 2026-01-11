package yk38.color_path_tetrablocks;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JPanel;
import javax.swing.Timer;

public class TetrablocksGame extends JPanel implements ActionListener {
    private CardLayout cardLayout;

    private Timer timer;

    private int score = 0, gameSpeed, category, linesCleared = 0;
    private boolean isGameOver = false;
    private boolean hasWin = false;
    private boolean isPaused = false;

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final int TILE_SIZE = 30;

    private final int GAME_WIDTH = BOARD_WIDTH * TILE_SIZE;
    private final int GAME_HEIGHT = BOARD_HEIGHT * TILE_SIZE;
    private final int SIDE_PANEL_WIDTH = 200;

    private BoardPanel boardPanel;
    private SidePanel sidePanel;
    private int[][] board = new int[BOARD_HEIGHT][BOARD_WIDTH];

    private final Color[] COLORS = {
            Color.BLACK,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            // Color.MAGENTA
    };

    private final int[][][] SHAPES = {
            { { 1, 1, 1, 1 } },

            { { 1, 0, 0 },
                    { 1, 1, 1 } },

            { { 0, 0, 1 },
                    { 1, 1, 1 } },

            { { 1, 1 },
                    { 1, 1 } },

            { { 0, 1, 1 },
                    { 1, 1, 0 } },

            { { 0, 1, 0 },
                    { 1, 1, 1 } },

            { { 1, 1, 0 },
                    { 0, 1, 1 } }
    };

    private int[][] currentShape;
    private int currentColorIdx;
    private int currentX, currentY;

    private int[][] nextShape;
    private int nextColorIdx;

    private int[][] holdShape;
    private int holdColorIdx;
    private boolean hasHeld = false;

    private final SecureRandom RAND = new SecureRandom();

    private boolean speedIncreased = true;

    // add a field
    private int speedMilestone = 0;

    public TetrablocksGame(int category, int speedStep1, CardLayout cardLayout) {
        // Initialize game with the specified level
        this.gameSpeed = speedStep1;
        this.category = category;
        this.cardLayout = cardLayout;
        setLayout(new BorderLayout());
        boardPanel = new BoardPanel();
        add(boardPanel, BorderLayout.CENTER);

        sidePanel = new SidePanel();
        add(sidePanel, BorderLayout.EAST);

        setFocusable(true);
        addKeyListener(new GameAdapter());

        initializeNextPiece();
        spawnNewPiece();

        this.timer = new Timer(this.gameSpeed, this);
        this.timer.start();

    }

    private void initializeNextPiece() {
        int idx = RAND.nextInt(SHAPES.length);
        nextShape = SHAPES[idx];
        nextColorIdx = RAND.nextInt(3) + 1;
    }

    private void spawnNewPiece() {
        currentShape = nextShape;
        currentColorIdx = nextColorIdx;
        currentX = BOARD_WIDTH / 2 - currentShape[0].length / 2;
        currentY = 0;

        hasHeld = false;
        initializeNextPiece();

        if (!tryMove(currentShape, currentX, currentY)) {
            isGameOver = true;
            timer.stop();
        }
        repaintAll();
    }

    private void holdPiece() {
        if (isGameOver || isPaused || hasHeld)
            return;

        if (holdShape == null) {
            holdShape = currentShape.clone();
            holdColorIdx = currentColorIdx;
            spawnNewPiece();
        } else {
            int[][] tempShape = currentShape.clone();
            int tempColorIdx = currentColorIdx;

            currentShape = holdShape.clone();
            currentColorIdx = holdColorIdx;

            holdShape = tempShape;
            holdColorIdx = tempColorIdx;

            currentX = BOARD_WIDTH / 2 - currentShape[0].length / 2;
            currentY = 0;
        }
        hasHeld = true;
        repaintAll();
    }

    private void drawTile(Graphics g, int x, int y, Color color) {
        g.setColor(color);
        g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        g.setColor(color.darker());
        g.drawRect(x, y, TILE_SIZE, TILE_SIZE);
    }

    private void repaintAll() {
        boardPanel.repaint();
        sidePanel.repaint();
    }

    private boolean tryMove(int[][] shape, int newX, int newY) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int boardX = newX + col;
                    int boardY = newY + row;

                    if (boardX < 0 || boardX >= BOARD_WIDTH || boardY >= BOARD_HEIGHT) {
                        return false;
                    }
                    if (boardY >= 0 && board[boardY][boardX] != 0) {
                        return false;
                    }
                }
            }
        }
        currentX = newX;
        currentY = newY;
        repaintAll();
        return true;
    }

    private void rotatePiece() {
        int rows = currentShape.length;
        int cols = currentShape[0].length;
        int[][] rotated = new int[cols][rows];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                rotated[col][rows - 1 - row] = currentShape[row][col];
            }
        }

        if (tryMove(rotated, currentX, currentY)) {
            currentShape = rotated;
        }
    }

    private void pieceLanded() {
        for (int row = 0; row < currentShape.length; row++) {
            for (int col = 0; col < currentShape[row].length; col++) {
                if (currentShape[row][col] != 0) {
                    int boardX = currentX + col;
                    int boardY = currentY + row;
                    if (boardY >= 0 && boardY < BOARD_HEIGHT && boardX >= 0 && boardX < BOARD_WIDTH) {
                        board[boardY][boardX] = currentColorIdx;
                    }
                }
            }
        }

        removeLines();
        checkConnectedColorPath();
        spawnNewPiece();
    }

    private void moveDown() {
        if (!tryMove(currentShape, currentX, currentY + 1)) {
            pieceLanded();
        }
    }

    private void removeLines() {
        int linesRemoved = 0;
        for (int row = BOARD_HEIGHT - 1; row >= 0; row--) {
            boolean isFull = true;
            for (int col = 0; col < BOARD_WIDTH; col++) {
                if (board[row][col] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                linesRemoved++;
                for (int r = row; r > 0; r--) {
                    board[r] = board[r - 1].clone();
                }
                board[0] = new int[BOARD_WIDTH];
                row++; // Recheck the same row
            }
        }

        // System.out.println("Total lines cleared: " + linesCleared + ", Timer delay: "
        // + timer.getDelay());

        if (linesRemoved > 0) {
            linesCleared += linesRemoved;
            score += linesRemoved * 100;
            repaintAll();
        }

        int milestones = linesCleared / 10;
        if (milestones > speedMilestone && gameSpeed > 100) {
            int steps = milestones - speedMilestone; // how many new 10-line blocks since last time
            gameSpeed = Math.max(100, gameSpeed - steps * 50);
            timer.setDelay(gameSpeed);
            speedMilestone = milestones;
        }

        if (linesCleared % 10 != 0 && linesCleared > 0) {
            speedIncreased = false;
        }
        // System.out.println("New game speed: " + gameSpeed + ", lines removed: " +
        // linesCleared + ", decrease: "
        // + (linesCleared / 10));

        // if (linesCleared >= 12) {
        // gameSpeed = speedStep2;
        // timer.setDelay(gameSpeed);
        // }

        // if (linesCleared >= 24) {
        // gameSpeed = speedStep3;
        // timer.setDelay(gameSpeed);
        // }

        // if (linesCleared >= 36) {
        // isGameOver = true;
        // timer.stop();
        // }
    }

    private void checkConnectedColorPath() {
        boolean[][] visited = new boolean[BOARD_HEIGHT][BOARD_WIDTH];
        ArrayList<Point> blocksToRemove = new ArrayList<>();
        boolean foundPath = false;

        for (int row = 0; row < BOARD_HEIGHT; row++) {
            int color = board[row][0];

            if (color != 0 && !visited[row][0]) {
                ArrayList<Point> currentPath = new ArrayList<>();
                Queue<Point> queue = new LinkedList<>();
                boolean reachedRight = false;

                Point start = new Point(0, row);
                queue.add(start);
                visited[row][0] = true;
                currentPath.add(start);

                while (!queue.isEmpty()) {
                    Point current = queue.poll();
                    if (current.x == BOARD_WIDTH - 1) {
                        reachedRight = true;
                    }

                    int[] dx = { 0, 0, -1, 1 };
                    int[] dy = { -1, 1, 0, 0 };

                    for (int i = 0; i < 4; i++) {
                        int newX = current.x + dx[i];
                        int newY = current.y + dy[i];

                        if (newX >= 0 && newX < BOARD_WIDTH && newY >= 0 && newY < BOARD_HEIGHT) {
                            if (board[newY][newX] == color && !visited[newY][newX]) {
                                visited[newY][newX] = true;
                                Point nextPoint = new Point(newX, newY);
                                queue.add(nextPoint);
                                currentPath.add(nextPoint);
                            }
                        }
                    }
                }
                if (reachedRight) {
                    foundPath = true;
                    blocksToRemove.addAll(currentPath);
                }
            }
        }

        if (foundPath) {
            for (Point p : blocksToRemove) {
                board[p.y][p.x] = 0;
            }

            score += blocksToRemove.size() * 50;
            linesCleared += 2;
            applyGravity();
            repaintAll();
        }
    }

    private void applyGravity() {
        for (int col = 0; col < BOARD_WIDTH; col++) {
            for (int row = BOARD_HEIGHT - 1; row > 0; row--) {
                if (board[row][col] == 0) {
                    for (int k = row - 1; k >= 0; k--) {
                        if (board[k][col] != 0) {
                            board[row][col] = board[k][col];
                            board[k][col] = 0;
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isGameOver || isPaused)
            return;

        moveDown();
    }

    private class BoardPanel extends JPanel {
        public BoardPanel() {
            setBackground(Color.BLACK);
            setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int row = 0; row < BOARD_HEIGHT; row++) {
                for (int col = 0; col < BOARD_WIDTH; col++) {
                    if (board[row][col] > 0) {
                        drawTile(g, col * TILE_SIZE, row * TILE_SIZE, COLORS[board[row][col]]);
                    }
                }
            }

            g.setColor(Color.DARK_GRAY);
            for (int i = 0; i <= BOARD_WIDTH; i++) {
                g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, GAME_HEIGHT);
            }
            for (int i = 0; i <= BOARD_HEIGHT; i++) {
                g.drawLine(0, i * TILE_SIZE, GAME_WIDTH, i * TILE_SIZE);
            }

            if (currentShape != null & !isGameOver) {
                for (int row = 0; row < currentShape.length; row++) {
                    for (int col = 0; col < currentShape[row].length; col++) {
                        if (currentShape[row][col] != 0) {
                            int x = (currentX + col) * TILE_SIZE;
                            int y = (currentY + row) * TILE_SIZE;
                            drawTile(g, x, y, COLORS[currentColorIdx]);
                        }
                    }
                }
            }

            if (isPaused) {
                drawCenterText(g, "PAUSED", Color.YELLOW, 48, GAME_HEIGHT / 2);
            } else if (isGameOver) {
                drawCenterText(g, "GAME OVER", Color.YELLOW, 48, GAME_HEIGHT / 2);
                drawCenterText(g, "Score: " + score, Color.WHITE, 24, GAME_HEIGHT / 2 + 50);
            }
        }

        private void drawCenterText(Graphics g, String text, Color color, int size, int posY) {
            g.setColor(color);
            g.setFont(new Font("Arial", Font.BOLD, size));
            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(text);
            g.drawString(text, (GAME_WIDTH - textWidth) / 2, posY);
        }
    }

    private class SidePanel extends JPanel {
        public SidePanel() {
            setBackground(Color.DARK_GRAY);
            setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, GAME_HEIGHT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String levelText;
            switch (category) {
                case 1:
                    levelText = "Easy";
                    break;
                case 2:
                    levelText = "Medium";
                    break;
                case 3:
                    levelText = "Hard";
                    break;
                default:
                    levelText = "Error";
                    break;
            }

            g.drawString("Level: " + levelText, 20, 40);
            g.drawString("Score: " + score, 20, 70);
            g.drawString("Speed: " + gameSpeed + " ms", 20, 100);
            g.drawString("Lines: " + linesCleared, 20, 130);

            g.drawString("NEXT:", 20, 160);
            drawShape(g, nextShape, COLORS[nextColorIdx], 20, 180);

            g.setColor(Color.WHITE);
            g.drawString("HOLD:", 20, 300);
            if (holdShape != null)
                drawShape(g, holdShape, holdColorIdx > 0 ? COLORS[holdColorIdx] : Color.GRAY, 20, 320);

            g.setFont(new Font("Arial", Font.PLAIN, 14));
            g.setColor(Color.LIGHT_GRAY);
            g.drawString("Controls:", 20, 450);
            g.drawString("Left/Right: Move", 20, 470);
            g.drawString("Up: Rotate", 20, 490);
            g.drawString("Down: Soft Drop", 20, 510);
            g.drawString("Space: Hard Drop", 20, 530);
            g.drawString("C: Hold", 20, 550);
            g.drawString("P: Pause", 20, 570);
            g.drawString("Esc: Exit", 20, 590);
        }

        private void drawShape(Graphics g, int[][] shape, Color color, int x, int y) {
            if (shape == null)
                return;

            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] != 0) {
                        g.setColor(color);
                        g.fillRect(x + col * 20, y + row * 20, 20, 20);
                        g.setColor(Color.BLACK);
                        g.drawRect(x + col * 20, y + row * 20, 20, 20);
                    }
                }
            }
        }
    }

    private class GameAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (isGameOver) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cardLayout.show(getParent(), "main");
                }
                return;
            }

            if (isPaused) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    isPaused = false;
                    repaintAll();
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cardLayout.show(getParent(), "main");
                }
                return;
            }

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    tryMove(currentShape, currentX - 1, currentY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(currentShape, currentX + 1, currentY);
                    break;
                case KeyEvent.VK_DOWN:
                    tryMove(currentShape, currentX, currentY + 1);
                    score += 2;
                    break;
                case KeyEvent.VK_UP:
                    rotatePiece();
                    break;
                case KeyEvent.VK_SPACE:
                    while (tryMove(currentShape, currentX, currentY + 1)) {
                        score += 2;
                    }
                    pieceLanded();
                    break;
                case KeyEvent.VK_C:
                    holdPiece();
                    break;
                case KeyEvent.VK_P:
                    isPaused = true;
                    repaintAll();
                    break;
                case KeyEvent.VK_ESCAPE:
                    cardLayout.show(getParent(), "main");
                    break;
            }

            // TODO: Score calculation
        }
    }
}
