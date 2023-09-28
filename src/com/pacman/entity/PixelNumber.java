package com.pacman.entity;

import com.pacman.utils.BufferedImageLoader;
import com.pacman.utils.Constants;

import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PixelNumber {
    public static final String path = "src\\com\\pacman\\res\\Number.png";

    public enum FontType {
        Small,
        MediumWhite,
        MediumBlack,
        Large
    }

    private SpriteSheet numberSheet;

    public PixelNumber() throws IOException {
        numberSheet = new SpriteSheet(BufferedImageLoader.loadImage(path));
    }

    private static Object[] toArray(int value) {
        List<Integer> list = new LinkedList<Integer>();
        while (value > 0) {
            list.add(value % 10);
            value /= 10;
        }
        Collections.reverse(list);
        return list.toArray();
    }

    public int getSize(int value, FontType type) {
        int space = 0;
        switch (type) {
            case Small:
                space = Constants.CELL_SIZE / 3;
                break;
            case MediumWhite:
            case MediumBlack:
                space = Constants.CELL_SIZE / 2 + 2;
                break;
            case Large:
                space = Constants.CELL_SIZE;
                break;
        }

        Object[] number = toArray(value);

        return number.length * space;
    }

    public void draw(Graphics2D g2d, int value, int x, int y, FontType type) {
        int space = 0;
        if (value < 0) {
            return;
        }

        int row = 0;
        switch (type) {
            case Small:
                space = Constants.CELL_SIZE / 3;
                break;
            case MediumWhite:
                space = Constants.CELL_SIZE / 2 + 2;
                row = 1;
                break;
            case MediumBlack:
                space = Constants.CELL_SIZE / 2 + 2;
                row = 2;
                break;
            case Large:
                space = Constants.CELL_SIZE;
                row = 3;
                break;
        }

        if (value < 10) {
            g2d.drawImage(numberSheet.grabImage(row, value), x, y, null);
            return;
        }
        Object[] number = toArray(value);
        int n = number.length;
        for (Object o : number) {
            g2d.drawImage(numberSheet.grabImage(row, (int) o), x, y, null);
            x += space;
        }
    }
}
