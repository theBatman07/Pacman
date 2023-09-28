package com.pacman.controller;

import com.pacman.entity.Ghost;
import com.pacman.entity.Map;
import com.pacman.entity.Pacman;
import com.pacman.entity.Sound;
import com.pacman.utils.Constants;

import java.awt.*;
import java.io.IOException;

public class GhostManager {
    private int sec = 0;
    private int phaseCount = 1;

    /* 4 Phase:
     * 1. Scatter for 7 seconds, then Chase for 20 seconds.
     * 2. Scatter for 7 seconds, then Chase for 20 seconds.
     * 3. Scatter for 5 seconds, then Chase for 20 seconds.
     * 4. Scatter for 5 seconds, then switch to Chase mode permanently.
     */ // source: https://gameinternals.com/understanding-pac-man-ghost-behavior

    public static final int NUMBER_OF_GHOSTS = 4;

    public enum GhostType {
        RED, //0
        PINK,//1
        BLUE,//2
        ORANGE,//3
        FRIGHTENED//4
    }

    private final Ghost[] ghosts;
    private final int[] startX; // vi tri tinh theo diem anh
    private final int[] startY;

    /////////////
    /// Methods
    ////////////

    public GhostManager() throws IOException {
        startX = new int[NUMBER_OF_GHOSTS];
        startY = new int[NUMBER_OF_GHOSTS];

        ghosts = new Ghost[NUMBER_OF_GHOSTS];
        ghosts[0] = new Ghost(GhostType.RED);
        ghosts[1] = new Ghost(GhostType.PINK);
        ghosts[2] = new Ghost(GhostType.BLUE);
        ghosts[3] = new Ghost(GhostType.ORANGE);
    }

    public void reset(boolean isNewGame) {
        for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
            if (isNewGame) {
                startX[i] = (ghosts[i].getPosition().x * Constants.CELL_SIZE);
                startY[i] = (ghosts[i].getPosition().y * Constants.CELL_SIZE) + Constants.SCREEN_TOP_MARGIN;
            }
            ghosts[i].setPosition(startX[i], startY[i]);
        }
        // blue la house, red la exit
        for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
            ghosts[i].reset(ghosts[1].getPosition(), ghosts[0].getPosition());
        }
        phaseCount = 1;
    }

    public Ghost getGhost(GhostType type) {
        return ghosts[type.ordinal()];
    }

    public void setPosition(GhostType type, int x, int y) {
        ghosts[type.ordinal()].setPosition(x, y);
    }

    public void phaseUpdate() {
        sec++;
        // phase 1, 2
        if (phaseCount <= 2) {
            // 1.1
            if (sec == Constants.LONG_SCATTER_DURATION) {
                for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
                    ghosts[i].switchMode();
                }
            }
            // 1.2
            if (sec == Constants.CHASE_DURATION) {
                phaseCount++;
                for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
                    ghosts[i].switchMode();
                }
                sec = 0;
            }
            return;
        }
        // phase 3
        if (phaseCount == 3) {
            // 1.1
            if (sec == Constants.SHORT_SCATTER_DURATION) {
                for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
                    ghosts[i].switchMode();
                }
            }
            // 1.2
            if (sec == Constants.CHASE_DURATION) {
                phaseCount++;
                for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
                    ghosts[i].switchMode();
                }
                sec = 0;
            }
            return;
        }

        // phase 4+
        if (sec == Constants.SHORT_SCATTER_DURATION) {
            for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
                ghosts[i].switchMode();
            }
        }

    }

    public void updateFrightened(Pacman pacman) {
        for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
            ghosts[i].updateFrightened(pacman);
        }
    }

    public void update(Map map, Pacman pacman, int gameTimer) {
        if (gameTimer <= 2) {
            ghosts[0].update(map, pacman, ghosts[0].getPosition());
            ghosts[1].update(map, pacman, ghosts[0].getPosition());
            return;
        }
        for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
            ghosts[i].update(map, pacman, ghosts[0].getPosition());
        }
    }

    public void makeBlinkEffect() {
        for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
            if (ghosts[i].getFrightenedMode() == 1) {
                ghosts[i].blinkSwitch();
            }
        }
    }

    public boolean isKillPacman() {
        for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
            if (ghosts[i].touchPacman()) {
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics2D g2d, boolean beingEat, GhostType type) {
        for (int i = 0; i < 4; i++) {
            if (beingEat && i == type.ordinal()) {
                continue;
            }
            ghosts[i].draw(i, g2d);
        }
    }
}
