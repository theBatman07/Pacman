package com.pacman.utils;

public interface Constants {
    public static final int CELL_SIZE = 32; // SPRITE size
    public static final int MAP_HEIGHT = 21;
    public static final int MAP_WIDTH = 21;
    public static final int FONT_SIZE = 18;

    public static final int SCREEN_TOP_MARGIN = CELL_SIZE;
    public static final int SCREEN_BOTTOM_MARGIN = CELL_SIZE * 3;
    public static final int SCREEN_WIDTH = (CELL_SIZE * MAP_WIDTH) + CELL_SIZE / 2;
    public static final int SCREEN_HEIGHT = (CELL_SIZE * MAP_HEIGHT) + SCREEN_TOP_MARGIN + SCREEN_BOTTOM_MARGIN;

    public static final int PACMAN_ANIMATION_FRAMES = 11;
    public static final int PACMAN_ANIMATION_SPEED = 3;
    public static final int PACMAN_DEATH_FRAMES = 14;
    public static final int PACMAN_SPEED = 2;

    public static final int GHOST_SPEED = 2;
    public static final int GHOST_FRIGHTENED_SPEED = 1;
    public static final int GHOST_ESCAPE_SPEED = 4;
    public static final int GHOST_ANIMATION_SPEED = 10;
    public static final int GHOST_ANIMATION_FRAMES = 2;
    public static final int IMPACT_RANGE = 2; // cang cao thi va cham cang gan

    public static final int GHOST_PINK_CHASE = 4;
    public static final int GHOST_BLUE_CHASE = 2;
    public static final int GHOST_ORANGE_CHASE = 5;

    public static final int FPS = 60;

    public static final int ENERGIZER_DURATION = 8;
    public static final int CHASE_DURATION = 20; //second
    public static final int LONG_SCATTER_DURATION = 7;
    public static final int SHORT_SCATTER_DURATION = 5;

    public static final int PACMAN_START_LIVES = 5;
    public static final int READY_TIME = 5;

    public static final int PELLET_SCORE = 10;
    public static final int ENERGIZER_SCORE = 50;
    public static final int GHOST_SCORE = 300;

    public static final boolean SOUND_DEFAULT = true;

    enum Cell {
        Door,
        Empty,
        Energizer,
        Pellet,
        Wall
    }

}
