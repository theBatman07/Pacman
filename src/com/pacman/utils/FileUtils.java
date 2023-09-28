package com.pacman.utils;

import com.pacman.controller.GhostManager;
import com.pacman.entity.Pacman;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class FileUtils {
    private static final String gameDataPath = "src\\com\\pacman\\res\\data\\map.dat";

    public List<String[]> loadListMap() {
        List<String[]> mapList = new LinkedList<String[]>();

        File f = new File(gameDataPath);
        FileReader fr = null;
        BufferedReader br = null;

        // mapString
        String[] mapSketch;

        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            String level;

            while ((level = br.readLine()) != null) {
                mapSketch = level.split(",");
                mapList.add(mapSketch);
            }
        } catch (IOException e) {
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
            }
        }

        return mapList;
    }

    public void writeMap(List<String[]> mapList, String filePath) {
        File f = new File(filePath);
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
            for (String[] x : mapList) {
                for (int i = 0; i < Constants.MAP_WIDTH; i++) {
                    bw.write(x[i] + ",");
                }
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Constants.Cell[][] loadMap(Pacman pacman, GhostManager ghost, int level) {
        File f = new File(gameDataPath);
        FileReader fr = null;
        BufferedReader br = null;

        // mapString
        String[] mapSketch;

        Constants.Cell[][] mapOutput = null;
        level--;
        try {
            fr = new FileReader(f);
            br = new BufferedReader(fr);

            // dua den level can doc
            for (int i = 0; i < level; i++) {
                br.readLine();
            }

            // nhay gia tri
            mapSketch = br.readLine().split(",");
            mapOutput = getMap(pacman, ghost, mapSketch);
        } catch (IOException e) {
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
            }
        }

        return mapOutput;
    }

    private Constants.Cell[][] getMap(Pacman pacman, GhostManager ghost, String[] mapSketch) {
        Constants.Cell[][] mapOutput = new Constants.Cell[Constants.MAP_WIDTH][Constants.MAP_HEIGHT];

        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            String[] sub = mapSketch[i].split("");
            for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                mapOutput[i][j] = Constants.Cell.Empty;
                switch (sub[j]) {
                    case "#":
                        mapOutput[i][j] = Constants.Cell.Wall;
                        break;
                    case "=":
                        mapOutput[i][j] = Constants.Cell.Door;
                        break;
                    case ".":
                        mapOutput[i][j] = Constants.Cell.Pellet;
                        break;
                    case "P":
                        pacman.setPosition(j, i);
                        break;
                    case "0":
                        ghost.setPosition(GhostManager.GhostType.RED, j, i);
                        break;
                    case "1":
                        ghost.setPosition(GhostManager.GhostType.PINK, j, i);
                        break;
                    case "2":
                        ghost.setPosition(GhostManager.GhostType.BLUE, j, i);
                        break;
                    case "3":
                        ghost.setPosition(GhostManager.GhostType.ORANGE, j, i);
                        break;
                    case "o":
                        mapOutput[i][j] = Constants.Cell.Energizer;
                }
            }
        }
        return mapOutput;
    }
}
