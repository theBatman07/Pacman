package com.pacman.entity;

import com.pacman.utils.Constants;

import java.awt.image.BufferedImage;

public class SpriteSheet implements Constants {

    private BufferedImage image;

    public SpriteSheet(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage grabImage(int row, int col) {
        row += 1;
        col += 1;
        BufferedImage img = image.getSubimage((col * CELL_SIZE) - CELL_SIZE, (row * CELL_SIZE) - CELL_SIZE, CELL_SIZE, CELL_SIZE);
        return img;
    }
}
