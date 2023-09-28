package com.pacman.controller;

import com.pacman.entity.Map;
import com.pacman.entity.Pacman;
import com.pacman.entity.Sound;
import com.pacman.utils.Constants;
import com.pacman.utils.DataBaseUtils;
import com.pacman.utils.FileUtils;
import com.pacman.view.GameView;

import java.io.IOException;
import java.sql.Date;

public class GameController implements Runnable {
    private FileUtils data;
    private Pacman pacman;
    private GhostManager ghostManager;
    private Map map;
    private GameView view;

    private boolean isWinLevel;
    private boolean isFinish;
    private int level;

    Thread gameThread; //https://stackoverflow.com/questions/27593900

    private final Object pauseLock;

    ///SOUND
    private Sound sound;
    private Sound soundPacman;

    ///////
    // Runnable method implements
    //////

    @Override
    public void run() {
        // 1 sec = 1000000000 nanosec
        // draw 60fps
        double drawInterval = 1000000000 / (double) Constants.FPS; // drawn 1 frame in 0.0166sec
        double delta = 0;
        long lastTime = 0;
        long currentTime;
        // count fps
        long fpsTimer = 0;
        int drawCount = 0;
        // hieu ung nhap nhay
        long energizerTimer = 0; // energizer nhap nhay
        long blinkTimer = 0; // ghost nhap nhay khi gan het thoi gian frightened
        // thoi gian game, theo sec
        int gameTimerFirst2Sec = 0;

        countDownReady(false);
        lastTime = System.nanoTime();

        sound.setFile(Sound.GhostSound.Normal);
        sound.play();
        sound.loop();

        while (!isFinish) {

            // draw 60fps
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;

            fpsTimer += currentTime - lastTime; // thoi gian reset khac nhau, khong the gan
            energizerTimer += currentTime - lastTime;
            blinkTimer += currentTime - lastTime;
            lastTime = currentTime;

            // delta >= 1 mean past 0.0166 sec
            if (delta >= 1) {
                // 1. update pacman position
                pacman.update(view.getKey(), map);

                // get pacman position in map
                int x = (int) Math.round(pacman.getPosition().x / (double) (Constants.CELL_SIZE));
                int y = (int) Math.round(pacman.getPosition().y / (double) (Constants.CELL_SIZE)) - Constants.SCREEN_TOP_MARGIN / Constants.CELL_SIZE;

                // kiem tra xem co o trong map khong...
                if (0 <= x && Constants.MAP_WIDTH > x && 0 <= y && Constants.MAP_HEIGHT > y) {
                    // 2. check collectItem
                    pacman.updateCollectItem(map.getMapItem(x, y));
                    // neu pacman an energizer thi ghost bi frightened
                    ghostManager.updateFrightened(pacman);
                    // 3. update map
                    map.mapUpdate(x, y);
                }

                // 4. update ghost
                ghostManager.update(map, pacman, gameTimerFirst2Sec);

                // 5. update view
                view.update(pacman, ghostManager, map, level, sound, soundPacman);

                // check pause
                if (view.getGameState() == GameView.GameState.Pause) {
                    synchronized (pauseLock) {
                        try {
                            pauseLock.wait();
                            lastTime = System.nanoTime();
                        } catch (InterruptedException e) {
                        }
                    }
                }

                // 6. check kill pacman
                if (ghostManager.isKillPacman()) {
                    gameTimerFirst2Sec = 0;
                    pacman.decreaseLive();
                    if (pacman.getLive() != 0) {
                        pacman.reset(false, sound, soundPacman);
                        ghostManager.reset(false);
                        view.resetReadyTimer(); // 2 second
                    }
                }

                // 7. Check lose
                if (pacman.getLive() == 0) {
                    pacman.setAlive(false);
                    // draw death animation
                    sound.stop();
                    view.drawDeathAnimation();
                    view.setEnd(false);
                    DataBaseUtils.savePlayerResult(new Date(System.currentTimeMillis()), pacman.getScore(), level, isFinish);
                    break; // out vong lap
                }

                // 8. Check win
                isWinLevel = isWinLevel();
                if (isWinLevel) {
                    if (level < 8) {
                        level += 1;

                        sound.stop();
                        soundPacman.stop();

                        sound.setFile(Sound.MenuSound.Loading);
                        sound.play();

                        view.updateLoadingScreen();
                        view.resetReadyTimer(); // 2 second
                        this.initGame();
                        lastTime = System.nanoTime();
                    } else {
                        pacman.setAlive(false);
                        sound.stop();
                        soundPacman.stop();
                        view.drawDeathAnimation();
                        isFinish = true;
                        view.setEnd(true);
                        view.update(pacman, ghostManager, map, level, sound, soundPacman);
                        break;
                    }
                }

                // reset delta
                delta--;
                // count frame
                drawCount++;
            }

            // khong co gi de giai thich
            if (!view.getReady()) {
                sound.stop();
                countDownReady(true);
                sound.setFile(Sound.GhostSound.Normal);
                sound.play();
                sound.loop();

                lastTime = System.nanoTime();
            }

            // ghost nhap nhay
            if (blinkTimer >= 100000000) {
                if (pacman.getEnergizerTimer() <= 3 && pacman.getEnergizerTimer() > 0) {
                    ghostManager.makeBlinkEffect();
                }
                blinkTimer = 0;
            }

            // energizer nhap nhay
            if (energizerTimer >= 200000000) {
                map.energizerSwitch();
                energizerTimer = 0;
            }

            // print fps and update phase
            if (fpsTimer >= 1000000000) {
                if (pacman.getEnergizerTimer() > 0) {
                    pacman.reduceEnergizerTimer();
                }
                //System.out.println("FPS:" + drawCount);
                ghostManager.phaseUpdate();
                if (gameTimerFirst2Sec <= 3) {
                    gameTimerFirst2Sec += 1;
                }
                drawCount = 0;
                fpsTimer = 0;
            }

            // an duoc ghost thi draw diem bonus trong 1 sec
            if (pacman.isDrawBonus()) {
                view.update(pacman, ghostManager, map, level, sound, soundPacman);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pacman.setIsDrawBonus(false);
                lastTime = System.nanoTime();
            }
        }
    }

    ///////////////
    // Controller methods
    ///////////////
    public GameController(GameView view, Object pauseLock, boolean isSoundOn) throws IOException {
        data = new FileUtils();
        pacman = new Pacman(isSoundOn);
        ghostManager = new GhostManager();
        map = new Map();
        sound = new Sound(isSoundOn);
        soundPacman = new Sound(isSoundOn);

        // defaults values
        this.view = view;
        this.pauseLock = pauseLock;
        this.level = 1;
        initGame();
    }

    public void initGame() {
        // set map
        map.setMap(data.loadMap(pacman, ghostManager, level));

        // update view
        view.update(pacman, ghostManager, map, level, sound, soundPacman);

        // move to right pos in map
        pacman.reset(true, sound, soundPacman);

        // move to right pos in map
        ghostManager.reset(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        isWinLevel = false;
        isFinish = false;
        gameThread.start();
    }

    public void killThread() {
        if (gameThread != null) {
            isFinish = true;
            gameThread.stop();
        }
    }

    public synchronized void countDownReady(boolean isMapBlink) {
        long lastTime = System.nanoTime();
        long currentTime;
        long readyTimer = 0; // dem ready time
        long numberTimer = 0; // dem nguoc..
        long mapColorTimer = 0;

        sound.setFile(Sound.MenuSound.Start);
        sound.play();

        while (view.getReadyTimer() > 0) {
            currentTime = System.nanoTime();
            readyTimer += (currentTime - lastTime);
            numberTimer += (currentTime - lastTime);
            mapColorTimer += (currentTime - lastTime);
            lastTime = currentTime;

            if (isMapBlink && mapColorTimer >= 500000000) {
                map.switchColor();
                view.update(pacman, ghostManager, map, level, sound, soundPacman);
                mapColorTimer = 0;
            }

            if (numberTimer >= 1000000000) {
                view.decreaseTimer();
                view.update(pacman, ghostManager, map, level, sound, soundPacman);
                numberTimer = 0;
            }
            if (readyTimer >= 1000000000L * Constants.READY_TIME) {
                view.setReady(true);
                break;
            }
            // kiem tra xem co nhan pause khong...
            if (view.getGameState() == GameView.GameState.Pause) {
                synchronized (pauseLock) {
                    try {
                        view.update(pacman, ghostManager, map, level, sound, soundPacman);
                        pauseLock.wait();
                        lastTime = System.nanoTime();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        map.resetColor();
    }

    public boolean isWinLevel() {
        return map.isClear();
    }
}
