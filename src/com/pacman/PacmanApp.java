package com.pacman;

import com.pacman.view.GameMainUI;

import java.awt.*;

public class PacmanApp {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            GameMainUI gameTitleUI = new GameMainUI();
        });
    }
}
