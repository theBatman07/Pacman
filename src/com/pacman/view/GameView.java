package com.pacman.view;

import com.pacman.controller.GhostManager;
import com.pacman.entity.*;
import com.pacman.utils.BufferedImageLoader;
import com.pacman.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GameView extends JPanel implements KeyListener {
    private Map map;
    private Pacman pacman;
    private GhostManager ghostManager;
    private PixelNumber pixelNumber;
    private SpriteSheet item;

    private int key;
    private int readyTimer;
    private int level;
    private int lastScore;

    private boolean isReady;
    private boolean isLoading;
    private boolean isDrawGhost;

    private static final int dX = Constants.CELL_SIZE * Constants.MAP_WIDTH;
    private static final int dY = Constants.CELL_SIZE * Constants.MAP_HEIGHT + Constants.SCREEN_TOP_MARGIN * 2;

    private GameMainUI mainUI;
    private GameState gameState;

    Container container;

    private boolean isWon;
    private final Object pauseLock;
    private int pauseMenuIndex;

    private Sound conSound;
    private Sound pacSound;

    public enum GameState {
        Running,
        Pause,
        End
    }

    ///////
    // JPanel methods override
    ///////
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (GameState.End == gameState) {
            this.setVisible(false);
            mainUI.initEndUI(pacman.getScore(), isWon);
        }
        try {
            if (isLoading) {
                loadingPacman.draw(g2d);
                loadingGhost.draw(GhostManager.GhostType.RED.ordinal(), g2d);
                this.drawNextLevelMenu(g2d);
            } else {
                this.drawScore(g2d);
                this.drawLives(g2d);
                this.drawLevel(g2d);
                map.drawMap(g2d);
                if (pacman.isDrawBonus()) {
                    this.drawBonus(g2d);
                    ghostManager.draw(g2d, true, pacman.getGhostKilled());
                } else {
                    pacman.draw(g2d);
                    if (isDrawGhost) {
                        ghostManager.draw(g2d, false, GhostManager.GhostType.FRIGHTENED);
                    }
                }
                if (!isReady) {
                    this.drawReady(g2d);
                }
            }
            if (GameState.Pause == gameState) {
                int middle = Constants.SCREEN_WIDTH / 2;
                int location = 0;
                switch (pauseMenuIndex) {
                    case 0:
                        location = 155;
                        break;
                    case 1:
                        location = 195;
                        break;
                    case 2:
                        location = 240;
                }
                g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\Pause.png"), middle - 300, 200, null);
                g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\arrowV.png"), middle - 300 + 180, 200 + location, null);
            }
        } catch (IOException e) {
        }
        g2d.dispose();
    }

    ///////
    // KeyListener method implements
    //////
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        key = e.getKeyCode();
        // stop
        if (KeyEvent.VK_P == key || KeyEvent.VK_ESCAPE == key && !isLoading) {
            if ( GameState.Running == gameState) {
                gameState = GameState.Pause;
            } else if (GameState.Pause == gameState) {
                gameState = GameState.Running;
                synchronized (pauseLock) {
                    pauseLock.notifyAll();
                }
            }
        }

        if (GameState.Pause == gameState) {
            if (KeyEvent.VK_UP == key) {
                if (pauseMenuIndex > 0) {
                    pauseMenuIndex--;
                    repaint();
                }
            }
            if (KeyEvent.VK_DOWN == key) {
                if (pauseMenuIndex < 2) {
                    pauseMenuIndex++;
                    repaint();
                }
            }
            if (KeyEvent.VK_ENTER == key) {
                switch (pauseMenuIndex) {
                    case 0: // back to the game... yeahhhhhh
                        gameState = GameState.Running;
                        synchronized (pauseLock) {
                            pauseLock.notifyAll();
                        }
                        break;
                    case 1:
                        while (pacman.getLive() > 0) {
                            pacman.decreaseLive(); // giam live de game dung lai
                        }
                        gameState = GameState.Running;
                        synchronized (pauseLock) {
                            pauseLock.notifyAll();
                        }

                        conSound.stop();
                        pacSound.stop();
                        this.setVisible(false);
                        container.remove(this);
                        mainUI.showMainUi();
                        break;
                    case 2: {
                        System.exit(0);
                        break;
                    }
                }
            }
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    ////////////////////////
    /////// GameView methods
    ////////////////////////
    public GameView(GameMainUI mainUI, Object pauseLock, Container container) throws IOException {
        this.mainUI = mainUI;
        this.pauseLock = pauseLock;
        pixelNumber = new PixelNumber();
        item = new SpriteSheet(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\Item.png"));
        this.container = container;
        initGame();
    }

    private void initGame() throws IOException {
        this.setOpaque(true);
        this.setBackground(Color.BLACK);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        isReady = false;
        isLoading = false;
        isWon = false;
        isDrawGhost = true;
        lastScore = 0;
        gameState = GameState.Running;
        pauseMenuIndex = 0;
        resetReadyTimer();
    }


    public void resetReadyTimer() {
        isReady = false;
        readyTimer = Constants.READY_TIME;
    }

    public void update(Pacman pacman, GhostManager ghostManager, Map map, int level, Sound conSound, Sound pacSound) {
        this.pacman = pacman;
        this.ghostManager = ghostManager;
        this.map = map;
        this.level = level;
        this.conSound = conSound;
        this.pacSound = pacSound;
        this.repaint();
    }

    public int getReadyTimer() {
        return readyTimer;
    }

    public int getKey() {
        return key;
    }

    public boolean getReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public void decreaseTimer() {
        readyTimer -= 1;
    }

    public void setEnd(boolean isWon) {
        gameState = GameState.End;
        this.isWon = isWon;
    }

    public GameState getGameState() {
        return gameState;
    }

    /////////////
    /// draws methods
    ////////////
    private void drawScore(Graphics2D g2d) throws IOException {
        g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\Score32.png"), 0, 0, null);
        pixelNumber.draw(g2d, pacman.getScore(), 86, 0, PixelNumber.FontType.MediumWhite);
    }

    private void drawBonus(Graphics2D g2d) {
        pixelNumber.draw(g2d, pacman.getBonus(), pacman.getPosition().x - Constants.CELL_SIZE / 4, pacman.getPosition().y, PixelNumber.FontType.Small);
    }

    private void drawLives(Graphics2D g2d) {
        int n = pacman.getLive();
        int dX = 100;
        int dY = Constants.SCREEN_HEIGHT - Constants.SCREEN_BOTTOM_MARGIN + Constants.CELL_SIZE / 3;
        for (int i = 0; i < n; i++) {
            g2d.drawImage(item.grabImage(0, 0), dX, dY, null);
            dX += Constants.CELL_SIZE + Constants.CELL_SIZE / 4;
        }
    }

    private void drawLevel(Graphics2D g2d) {
        int dX = Constants.SCREEN_WIDTH - Constants.CELL_SIZE;
        int dY = Constants.SCREEN_HEIGHT - Constants.SCREEN_BOTTOM_MARGIN + Constants.CELL_SIZE / 3;
        for (int i = 0; i < level; i++) {
            dX -= (Constants.CELL_SIZE + Constants.CELL_SIZE / 4);
            g2d.drawImage(item.grabImage(0, i + 1), dX, dY, null);

        }
    }

    private void drawReady(Graphics2D g2d) throws IOException {
        if (readyTimer > 1) {
            pixelNumber.draw(g2d, readyTimer - 1, (dX - 32) / 2, (dY + 32) / 2, PixelNumber.FontType.Large);
            return;
        }
        g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\Ready.png"), (dX - 128) / 2, (dY + 32) / 2, null);
    }

    private Pacman loadingPacman = new Pacman(false) {
        @Override
        public void update(int key, Map map) {
            if (KeyEvent.VK_LEFT == key) {
                this.setDirection(2);
                this.getPosition().x -= Constants.PACMAN_SPEED + 1;
                return;
            }
            if (KeyEvent.VK_RIGHT == key) {
                this.setDirection(0);
                this.getPosition().x += Constants.PACMAN_SPEED + 1;
            }
        }
    };

    private Ghost loadingGhost = new Ghost(GhostManager.GhostType.RED) {
        @Override
        public void update(Map map, Pacman pacman, Point redGhostPosition) {
            if (pacman.getDirection() == 2) {
                this.setDirection(2);
                this.setFrightenedMode(0); // 0: Normal
                this.getPosition().x -= Constants.GHOST_SPEED + 1;
                return;
            }
            if (pacman.getDirection() == 0) {
                this.setDirection(0);
                this.setFrightenedMode(1); // 1: Frightened
                this.getPosition().x += Constants.GHOST_SPEED + 1;
            }
        }
    };

    public void drawDeathAnimation() {
        isDrawGhost = false;
        while (!pacman.isAnimationOver()) {
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.repaint();
        }
        isDrawGhost = true;
    }

    public void updateLoadingScreen() {
        pacman.setAlive(false);
        drawDeathAnimation();
        pacman.setAlive(true);
        pacman.resetAnimationOver();

        int middleY = Constants.SCREEN_HEIGHT / 2;
        isLoading = true;
        loadingGhost.setPosition(Constants.SCREEN_WIDTH + Constants.CELL_SIZE * 2, middleY);
        loadingPacman.setPosition(Constants.SCREEN_WIDTH, middleY);
        loadingPacman.setAlive(true);

        // trai sang phai
        double drawInterval = 1000000000 / (double) Constants.FPS; // drawn 1 frame in 0.0166sec
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        while (-Constants.CELL_SIZE * 4 < loadingPacman.getPosition().x) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                loadingPacman.update(KeyEvent.VK_LEFT, null);
                loadingGhost.update(null, loadingPacman, null);
                this.repaint();
                delta--;
            }
        }

        // phai sang trai
        loadingGhost.setPosition(-Constants.CELL_SIZE, middleY);
        loadingPacman.setPosition(-Constants.CELL_SIZE * 3, middleY);
        lastTime = System.nanoTime();
        while (Constants.SCREEN_WIDTH + Constants.CELL_SIZE * 3 > loadingPacman.getPosition().x) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;
            if (delta >= 1) {
                loadingPacman.update(KeyEvent.VK_RIGHT, null);
                loadingGhost.update(null, loadingPacman, null);
                this.repaint();
                delta--;
            }
        }

        isLoading = false;
        // mat 9 sec
    }

    private void drawNextLevelMenu(Graphics2D g2d) throws IOException {
        int levelScore;
        if (lastScore < pacman.getScore()) {
            levelScore = lastScore;
            lastScore += 20;
        } else {
            levelScore = pacman.getScore();
        }
        g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\Score32.png"), Constants.SCREEN_WIDTH / 2 - 86, Constants.SCREEN_HEIGHT / 2 - Constants.SCREEN_BOTTOM_MARGIN, null);
        pixelNumber.draw(g2d, levelScore, Constants.SCREEN_WIDTH / 2, Constants.SCREEN_HEIGHT / 2 - Constants.SCREEN_BOTTOM_MARGIN, PixelNumber.FontType.MediumWhite);
    }
}
