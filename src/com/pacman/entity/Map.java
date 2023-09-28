package com.pacman.entity;

import com.pacman.utils.BufferedImageLoader;
import com.pacman.utils.Constants;

import java.awt.*;
import java.io.IOException;

public class Map {
    private Constants.Cell[][] map;
    private boolean isEnergizerOff;
    private SpriteSheet mapSprite;
    private boolean isBlueColor;

    public Map() throws IOException {
        mapSprite = new SpriteSheet(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\Entity\\Map32.png"));
        isEnergizerOff = false;
        isBlueColor = true;
    }

    public void setMap(Constants.Cell[][] map) {
        this.map = map;
        isEnergizerOff = false;
    }

    public Constants.Cell getMapItem(int x, int y) {
        return map[y][x];
    }

    public boolean isClear() {
        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                if (map[i][j] == Constants.Cell.Pellet || map[i][j] == Constants.Cell.Energizer) {
                    return false;
                }
            }
        }
        return true;
    }

    public void energizerSwitch() {
        isEnergizerOff = !isEnergizerOff;
    }

    public boolean mapCollision(boolean iUseDoor, int iX, int iY) {
        boolean output = false;

        double cellX = iX / (double) (Constants.CELL_SIZE);
        double cellY = iY / (double) (Constants.CELL_SIZE) - Constants.SCREEN_TOP_MARGIN / (double) Constants.CELL_SIZE;

        for (int i = 0; i < 4; i++) {
            int x = 0;
            int y = 0;

            switch (i) {
                case 0://TOP LEFT CELL
                {
                    x = (int) Math.floor(cellX);
                    y = (int) Math.floor(cellY);
                    break;
                }
                case 1: //TOP RIGHT
                {
                    x = (int) Math.ceil(cellX);
                    y = (int) Math.floor(cellY);
                    break;
                }
                case 2: {
                    x = (int) Math.floor(cellX);
                    y = (int) Math.ceil(cellY);
                    break;
                }
                case 3: {
                    x = (int) Math.ceil(cellX);
                    y = (int) Math.ceil(cellY);
                }
            }

            // kiem tra xem vi tri co trong map khong
            if (x >= 0 && y >= 0 && Constants.MAP_HEIGHT > y && Constants.MAP_WIDTH > x) {
                if (Constants.Cell.Wall == map[y][x]) {
                    output = true;
                } else if (iUseDoor == false && Constants.Cell.Door == map[y][x]) {
                    output = true;
                }
            }
        }

        return output;
    }

    public void drawMap(Graphics2D g2d) throws IOException {
        for (int a = 0; a < Constants.MAP_WIDTH; a++) {
            for (int b = 0; b < Constants.MAP_HEIGHT; b++) {

                int xPos = ((b * Constants.CELL_SIZE));
                int yPos = ((a * Constants.CELL_SIZE)) + Constants.SCREEN_TOP_MARGIN;

                switch (map[a][b]) {
                    case Door: {
                        g2d.drawImage(mapSprite.grabImage(2, 2), xPos, yPos, null);
                        break;
                    }
                    case Energizer: {
                        if (isEnergizerOff) {
                            g2d.drawImage(mapSprite.grabImage(2, 3), xPos, yPos, null);
                            break;
                        }
                        g2d.drawImage(mapSprite.grabImage(2, 1), xPos, yPos, null);
                        break;
                    }
                    case Pellet: {
                        g2d.drawImage(mapSprite.grabImage(2, 0), xPos, yPos, null);
                        break;
                    }
                    case Wall: {

                        int up = 0;
                        int left = 0;
                        int down = 0;
                        int right = 0;

                        // check xem co tuong, hoac khong, neu co thi phai noi vao voi nhau

                        if (a > 0) {
                            if (Constants.Cell.Wall == map[a - 1][b]) {
                                up = 1; // up
                            }
                        }

                        if (Constants.MAP_WIDTH - 1 > b) {
                            if (Constants.Cell.Wall == map[a][b + 1]) {
                                right = 1; // right
                            }
                        }

                        if (b > 0) {
                            if (Constants.Cell.Wall == map[a][b - 1]) {
                                left = 1; // left
                            }
                        }

                        if (Constants.MAP_HEIGHT - 1 > a) {
                            if (Constants.Cell.Wall == map[a + 1][b]) {
                                down = 1; // dow
                            }
                        }

                        //-------------------<         DISTRIBUTIVE PROPERTY!         >-----------------------
                        int pos = (down + 2 * (left + 2 * (right + 2 * up)));

                        if (isBlueColor) {
                            g2d.drawImage(mapSprite.grabImage(1, pos), xPos, yPos, null);
                        } else {
                            g2d.drawImage(mapSprite.grabImage(0, pos), xPos, yPos, null);
                        }

                        // fix tuong
                        if (Constants.MAP_HEIGHT - 1 > a && b > 0) {
                            if (Constants.Cell.Wall == map[a + 1][b - 1] && down == 1 && left == 1) { // down left
                                g2d.drawImage(mapSprite.grabImage(2, 4), xPos, yPos, null);
                            }
                        }

                        if (Constants.MAP_HEIGHT - 1 > a && Constants.MAP_WIDTH - 1 > b) {
                            if (Constants.Cell.Wall == map[a + 1][b + 1] && down == 1 && right == 1) { // down right
                                g2d.drawImage(mapSprite.grabImage(2, 5), xPos, yPos, null);
                            }
                        }

                        if (a > 0 && b > 0) {
                            if (Constants.Cell.Wall == map[a - 1][b - 1] && up == 1 && left == 1) { // up left
                                g2d.drawImage(mapSprite.grabImage(2, 6), xPos, yPos, null);
                            }
                        }

                        if (a > 0 && Constants.MAP_WIDTH - 1 > b) {
                            if (Constants.Cell.Wall == map[a - 1][b + 1] && up == 1 && right == 1) { // up right
                                g2d.drawImage(mapSprite.grabImage(2, 7), xPos, yPos, null);
                            }
                        }
                    }
                }
            }
        }
    }

    public void switchColor() {
        isBlueColor = !isBlueColor;
    }

    public void resetColor() {
        isBlueColor = true;
    }

    public void mapUpdate(int x, int y) {
        if (Constants.Cell.Energizer == map[y][x]) {
            map[y][x] = Constants.Cell.Empty;
            return;
        }
        if (Constants.Cell.Pellet == map[y][x]) {
            map[y][x] = Constants.Cell.Empty;
        }
    }
}
