package pl.edu.pjwstk.jhalas.game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class Game extends JTable implements KeyListener {

    private static int CELL_SIZE = 30;
    private static int DOT_SIZE = CELL_SIZE / 4;
    private static final int WALL = 1;
    private static final int EMPTY = 0;
    private static final int PACMAN = 2;
    private static final int MONSTER = 3;
    private static final int DOT = 4;

    private BufferedImage pacmanImage;
    private BufferedImage ghostImage;
    private BufferedImage dotImage;

    private int[][] maze;
    private final int mazeWidth;
    private final int mazeHeight;
    private int pacmanX;
    private int pacmanY;
    private int[] monstersX;
    private int[] monstersY;
    private int score;
    private final Random random;

    public Game(int width, int height) {
        mazeWidth = width;
        mazeHeight = height;
        setPreferredSize(new Dimension(CELL_SIZE * mazeWidth, CELL_SIZE * mazeHeight));
        setBackground(Color.black);
        random = new Random();
        generateMaze();
        addExtraSpacesInWaLL();
        placePacMan();
        placeMovingMonster(3);
        placeDots();
        addKeyListener(this);
        setFocusable(true);

        try {
            pacmanImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("head.png")));
            ghostImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("monster.png")));
            dotImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("dot.png")));
            dotImage = resizeImage(dotImage, DOT_SIZE, DOT_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        score = 0;

        startMovingMonsters();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustCellSize();
            }
        });

    }

    private void generateMaze() {
        maze = new int[mazeHeight][mazeWidth];


        for (int i = 0; i < mazeHeight; i++) {
            for (int j = 0; j < mazeWidth; j++) {
                maze[i][j] = WALL;
            }
        }


        int startX = random.nextInt(mazeWidth);
        int startY = random.nextInt(mazeHeight);


        generateRecursive(startX, startY);
    }

    private void placeDots() {
        for (int i = 0; i < mazeHeight; i++) {
            for (int j = 0; j < mazeWidth; j++) {
                if (maze[i][j] == EMPTY) {
                    maze[i][j] = DOT;
                }
            }
        }
    }

    private void generateRecursive(int x, int y) {
        maze[y][x] = EMPTY;
        int[] directions = {0, 1, 2, 3};

        shuffleArray(directions);

        for (int direction : directions) {
            int newX = x;
            int newY = y;


            switch (direction) {
                case 0 -> newY -= 2;
                case 1 -> newX += 2;
                case 2 -> newY += 2;
                case 3 -> newX -= 2;
            }


            if (newX > 0 && newY > 0 && newX < mazeWidth - 1 && newY < mazeHeight - 1 && maze[newY][newX] == WALL) {
                maze[y + (newY - y) / 2][x + (newX - x) / 2] = EMPTY;
                generateRecursive(newX, newY);
            }
        }
    }

    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private void addExtraSpacesInWaLL() {
        for (int i = 0; i < mazeHeight; i++) {
            int extraSpacesCount = random.nextInt(2) + 1; //
            int extraSpacesAdded = 0;

            while (extraSpacesAdded < extraSpacesCount) {
                int x = random.nextInt(mazeWidth);
                if (maze[i][x] == WALL) {
                    maze[i][x] = EMPTY;
                    extraSpacesAdded++;
                }
            }
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        removeKeyListener(this);
    }

    private void placePacMan() {
        boolean pacmanPlaced = false;
        while (!pacmanPlaced) {
            int x = random.nextInt(mazeWidth);
            int y = random.nextInt(mazeHeight);
            if (maze[y][x] == EMPTY) {
                maze[y][x] = PACMAN;
                pacmanX = x;
                pacmanY = y;
                pacmanPlaced = true;
            }
        }
    }

    private void placeMovingMonster(int count) {
        monstersX = new int[count];
        monstersY = new int[count];

        for (int i = 0; i < count; i++) {
            boolean monsterPlaced = false;
            while (!monsterPlaced) {
                int x = random.nextInt(mazeWidth);
                int y = random.nextInt(mazeHeight);
                if (maze[y][x] == EMPTY && x != pacmanX && y != pacmanY) {
                    maze[y][x] = MONSTER;
                    monstersX[i] = x;
                    monstersY[i] = y;
                    monsterPlaced = true;
                }
            }
        }
    }

    private void movePacMan(int dx, int dy) {
        int newX = pacmanX + dx;
        int newY = pacmanY + dy;

        if (newX >= 0 && newY >= 0 && newX < mazeWidth && newY < mazeHeight && maze[newY][newX] != WALL) {
            if (maze[newY][newX] == DOT) {
                score++;
            }

            maze[pacmanY][pacmanX] = EMPTY;
            pacmanX = newX;
            pacmanY = newY;
            maze[pacmanY][pacmanX] = PACMAN;

            for (int i = 0; i < monstersX.length; i++)
                if (monstersX[i] == pacmanX && monstersY[i] == pacmanY) {
                    gameOver();
                }

            repaint();
        }
    }

    private void moveMovingMonsters() {
        for (int i = 0; i < monstersX.length; i++) {
            int direction = random.nextInt(4);

            int dx = 0;
            int dy = 0;

            switch (direction) {
                case 0 -> dy = -1;
                case 1 -> dx = 1;
                case 2 -> dy = 1;
                case 3 -> dx = -1;
            }

            int newX = monstersX[i] + dx;
            int newY = monstersY[i] + dy;

            if (newX >= 0 && newY >= 0 && newX < mazeWidth && newY < mazeHeight && maze[newY][newX] != WALL) {
                maze[monstersY[i]][monstersX[i]] = EMPTY;
                monstersX[i] = newX;
                monstersY[i] = newY;
                maze[monstersY[i]][monstersX[i]] = MONSTER;


            }
        }
        repaint();
    }

    private void startMovingMonsters() {
        Thread thread = new Thread(() -> {
            while (true) {
                moveMovingMonsters();
                repaint();

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    private void gameOver() {
        Thread.currentThread().interrupt();
        JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score, "Game Over", JOptionPane.PLAIN_MESSAGE);
        saveScoreToFile(score);
        resetGame();
    }

    private void resetGame() {
        score = 0;
        generateMaze();
        addExtraSpacesInWaLL();
        placePacMan();
        placeMovingMonster(3);
        placeDots();
        repaint();
        startMovingMonsters();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < mazeHeight; i++) {
            for (int j = 0; j < mazeWidth; j++) {
                int cellValue = maze[i][j];

                switch (cellValue) {
                    case WALL -> {
                        g.setColor(Color.BLUE);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                    case PACMAN -> g.drawImage(pacmanImage, j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE, null);
                    case MONSTER -> g.drawImage(ghostImage, j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE, null);
                    case DOT -> g.drawImage(dotImage, j * CELL_SIZE + (CELL_SIZE - DOT_SIZE) / 2, i * CELL_SIZE + (CELL_SIZE - DOT_SIZE) / 2, DOT_SIZE, DOT_SIZE, null);
                    case EMPTY -> {
                        g.setColor(Color.BLACK);
                        g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        }
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();


        if (e.isControlDown() && e.isShiftDown() && keyCode == KeyEvent.VK_Q) {

            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            frame.dispose();
            SwingUtilities.invokeLater(WelcomeWindow::new);
        }


        switch (keyCode) {
            case KeyEvent.VK_UP -> movePacMan(0, -1);
            case KeyEvent.VK_DOWN -> movePacMan(0, 1);
            case KeyEvent.VK_LEFT -> movePacMan(-1, 0);
            case KeyEvent.VK_RIGHT -> movePacMan(1, 0);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }


    private void saveScoreToFile(int score) {
        String fileName = "wynik.txt";

        try {
            FileWriter writer = new FileWriter(fileName, true);
            String playerName = JOptionPane.showInputDialog(null, "Tape your name:");
            writer.append(playerName + " : " + score + "\n");
            writer.close();
            JOptionPane.showMessageDialog(null, "score saved in : " + fileName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "saved error");
            e.printStackTrace();
        }
    }

    private void adjustCellSize() {
        int newCellSize = Math.min(getWidth() / mazeWidth, getHeight() / mazeHeight);
        if (newCellSize != CELL_SIZE) {
            CELL_SIZE = newCellSize;
            DOT_SIZE = CELL_SIZE / 4;
            pacmanImage = resizeImage(pacmanImage, CELL_SIZE, CELL_SIZE);
            ghostImage = resizeImage(ghostImage, CELL_SIZE, CELL_SIZE);
            dotImage = resizeImage(dotImage, DOT_SIZE, DOT_SIZE);
            revalidate();
            repaint();
        }
    }
}
