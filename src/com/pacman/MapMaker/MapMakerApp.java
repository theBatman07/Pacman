package com.pacman.MapMaker;

import com.pacman.controller.GhostManager;
import com.pacman.utils.Constants;
import com.pacman.utils.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MapMakerApp {
    private static final String GAME_TITLE = "MapMakerApp";

    private JFrame window;
    private JPanel main;
    private Container con;
    private char text = ' ';
    private FileUtils data;
    private List<String[]> mapList;

    MapListener mapListener;
    ItemButtonListener itemButtonListener;
    JButton[] jButtonMap;

    public MapMakerApp() {
        window = new JFrame();
        mapListener = new MapListener();
        itemButtonListener = new ItemButtonListener();
        jButtonMap = new JButton[Constants.MAP_WIDTH * Constants.MAP_HEIGHT];
        data = new FileUtils();
        mapList = data.loadListMap();
        initTileUI();
        initFrame();
    }

    private void initFrame() {
        window.setTitle(GAME_TITLE);
        window.setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private void initTileUI() {
        main = new JPanel();
        main.setLayout(new BorderLayout());
        con = window.getContentPane();

        // sub title panel
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();

        main.add(leftPanel, BorderLayout.CENTER);
        main.add(rightPanel, BorderLayout.EAST);

        initMapButton(leftPanel);

        JPanel rightNorthPanel = new JPanel();
        JPanel rightSouthPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(rightNorthPanel, BorderLayout.NORTH);
        rightPanel.add(rightSouthPanel, BorderLayout.CENTER); // TODO load save

        rightNorthPanel.setLayout(new BoxLayout(rightNorthPanel, BoxLayout.Y_AXIS));
        initRightButton(rightNorthPanel, jButtonMap);

        initRightList(rightSouthPanel);

        // add to frame
        con.add(main);
        window.setContentPane(con);
    }

    private void initMapButton(JPanel panel) {
        JLabel[] northLb = new JLabel[21];
        JLabel[] westLb = new JLabel[21];
        for (int i = 0; i < Constants.MAP_HEIGHT; i++) {
            northLb[i] = new JLabel();
            westLb[i] = new JLabel();
            northLb[i].setPreferredSize(new Dimension(Constants.CELL_SIZE + 10, Constants.CELL_SIZE + 10));
            westLb[i].setPreferredSize(new Dimension(Constants.CELL_SIZE + 10, Constants.CELL_SIZE + 10));
            northLb[i].setText("     " + (i + 1));
            westLb[i].setText((i + 1) + " ");
        }

        panel.setLayout(new GridLayout(22, 22));

        JLabel zeroBtn = new JLabel();
        zeroBtn.setPreferredSize(new Dimension(Constants.CELL_SIZE + 10, Constants.CELL_SIZE + 10));
        zeroBtn.setText(0 + " ");
        panel.add(zeroBtn);

        for (int i = 0; i < Constants.MAP_WIDTH; i++) {
            panel.add(northLb[i]);
        }

        int c = 0;
        for (int i = 1; i < Constants.MAP_WIDTH + 1; i++) {
            for (int j = 0; j < Constants.MAP_HEIGHT + 1; j++) {
                if (j == 0) {
                    panel.add(westLb[i - 1]);
                }
                if (j != 0) {
                    jButtonMap[c] = new JButton(" ");
                    jButtonMap[c].setFocusPainted(false);
                    jButtonMap[c].setPreferredSize(new Dimension(Constants.CELL_SIZE + 10, Constants.CELL_SIZE + 10));
                    jButtonMap[c].setBackground(Color.BLACK);
                    jButtonMap[c].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    jButtonMap[c].setFont(new Font("Arial", Font.PLAIN, 25));
                    jButtonMap[c].setForeground(Color.WHITE);
                    jButtonMap[c].addActionListener(mapListener);
                    panel.add(jButtonMap[c]);
                    c++;
                }
            }
        }
    }

    private void initRightButton(JPanel panel, JButton[] jButtonMap) {
        JButton clearBtn = new JButton("Clear");
        clearBtn.setFocusPainted(false);
        clearBtn.setFont(new Font("Arial", Font.PLAIN, 25));

        JButton fillPelletBtn = new JButton("Fill With Pellet");
        fillPelletBtn.setFocusPainted(false);
        fillPelletBtn.setFont(new Font("Arial", Font.PLAIN, 25));

        JButton exportBtn = new JButton("Export to FILE");
        exportBtn.setFocusPainted(false);
        exportBtn.setFont(new Font("Arial", Font.PLAIN, 25));
        // left radio button
        int numberOfCells = Constants.Cell.values().length;
        int numberOfItemButtons = numberOfCells + GhostManager.NUMBER_OF_GHOSTS + 1;
        JRadioButton[] itemButtons = new JRadioButton[numberOfItemButtons];
        int c = 0;
        while (c < numberOfCells) {
            itemButtons[c] = new JRadioButton(Constants.Cell.values()[c].toString());
            itemButtons[c].setFocusPainted(false);
            itemButtons[c].setFont(new Font("Arial", Font.PLAIN, 25));
            itemButtons[c].addActionListener(itemButtonListener);
            panel.add(itemButtons[c]);
            c++;
        }
        int i = 0;
        while (c < numberOfCells + GhostManager.NUMBER_OF_GHOSTS) {
            itemButtons[c] = new JRadioButton(GhostManager.GhostType.values()[i].toString() + "-" + i);
            itemButtons[c].setFocusPainted(false);
            itemButtons[c].setFont(new Font("Arial", Font.PLAIN, 25));
            itemButtons[c].addActionListener(itemButtonListener);
            panel.add(itemButtons[c]);
            i++;
            c++;
        }
        itemButtons[c] = new JRadioButton("Pacman");
        itemButtons[c].setFocusPainted(false);
        itemButtons[c].setFont(new Font("Arial", Font.PLAIN, 25));
        itemButtons[c].addActionListener(itemButtonListener);


        panel.add(itemButtons[c]);
        panel.add(clearBtn);
        panel.add(fillPelletBtn);
        panel.add(exportBtn);

        ButtonGroup itemButtonGroup = new ButtonGroup();
        for (i = 0; i < numberOfItemButtons; i++) {
            itemButtonGroup.add(itemButtons[i]);
        }

        ////////
        //ACTION BUTTON
        ///////

        clearBtn.addActionListener(e -> {
            clearBtnText();
        });

        fillPelletBtn.addActionListener(e -> {
            for (int a = 0; a < Constants.MAP_WIDTH * Constants.MAP_HEIGHT; a++) {
                if (" ".equals(jButtonMap[a].getText())) {
                    jButtonMap[a].setText(".");
                    jButtonMap[a].setBackground(Color.BLACK);
                }
            }
        });

        exportBtn.addActionListener(e -> {
            data.writeMap(mapList, "src\\com\\pacman\\res\\data\\customMap.dat");
        });
    }

    private void initRightList(JPanel panel) {
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        DefaultListModel<Integer> defaultListModel = new DefaultListModel<Integer>();
        JLabel label = new JLabel("LEVEL LIST: ");
        label.setFont(new Font("Arial", Font.PLAIN, 25));

        for (int i = 0; i < mapList.size(); i++) {
            defaultListModel.addElement(i + 1);
        }

        JList list = new JList(defaultListModel);


        list.setVisibleRowCount(10);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFont(new Font("Arial", Font.PLAIN, 25));

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(150, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JButton loadBtn = new JButton("LOAD");
        JButton saveBtn = new JButton("SAVE");
        JButton deleteBtn = new JButton("DELETE");
        JButton insertBtn = new JButton("INSERT");
        loadBtn.setFont(new Font("Arial", Font.PLAIN, 25));
        saveBtn.setFont(new Font("Arial", Font.PLAIN, 25));
        deleteBtn.setFont(new Font("Arial", Font.PLAIN, 25));
        insertBtn.setFont(new Font("Arial", Font.PLAIN, 25));

        panel.add(label);
        panel.add(scrollPane);
        panel.add(insertBtn);
        panel.add(deleteBtn);
        panel.add(saveBtn);
        panel.add(loadBtn);

        insertBtn.addActionListener(e -> {
            int value = list.getSelectedIndex();
            if (value == -1) {
                return;
            }
            String[] ins = new String[Constants.MAP_HEIGHT];
            for (int i = 0; i < Constants.MAP_WIDTH; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                    sb.append(" ");
                }
                ins[i] = sb.toString();
            }
            mapList.add(value + 1, ins);
            defaultListModel.add(value + 1, value + 2);
            for (int i = 0; i < defaultListModel.size(); i++) {
                defaultListModel.set(i, i + 1);
            }
        });

        loadBtn.addActionListener(e -> {
            clearBtnText();
            int value = list.getSelectedIndex();
            if (value == -1) {
                return;
            }
            String[] level = mapList.get(value);
            for (int i = 0; i < Constants.MAP_WIDTH; i++) {
                String[] levelRow = level[i].split("");
                for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                    int index = i * Constants.MAP_WIDTH + j;
                    jButtonMap[index].setText(levelRow[j]);
                    if ("#".equals(levelRow[j])) {
                        jButtonMap[index].setBackground(Color.BLUE);
                    }
                }
            }
        });

        saveBtn.addActionListener(e -> {
            int value = list.getSelectedIndex();
            if (value == -1) {
                return;
            }
            String[] map = mapList.get(value);
            for (int i = 0; i < Constants.MAP_WIDTH; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < Constants.MAP_HEIGHT; j++) {
                    sb.append(jButtonMap[i * Constants.MAP_WIDTH + j].getText());
                }
                map[i] = sb.toString();
            }
        });

        deleteBtn.addActionListener(e -> {
            int value = list.getSelectedIndex();
            if (value == -1) {
                return;
            }
            defaultListModel.remove(value);
            mapList.remove(value);
        });
    }

    private void clearBtnText() {
        for (int i = 0; i < Constants.MAP_WIDTH * Constants.MAP_HEIGHT; i++) {
            jButtonMap[i].setText(" ");
            jButtonMap[i].setBackground(Color.BLACK);
        }
    }

    private class ItemButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JRadioButton btn = (JRadioButton) e.getSource();
            String type = btn.getText();
            switch (type) {
                case "Door":
                    text = '=';
                    break;
                case "Empty":
                    text = ' ';
                    break;
                case "Energizer":
                    text = 'o';
                    break;
                case "Pellet":
                    text = '.';
                    break;
                case "Wall":
                    text = '#';
                    break;
                case "RED-0":
                    text = '0';
                    break;
                case "PINK-1":
                    text = '1';
                    break;
                case "BLUE-2":
                    text = '2';
                    break;
                case "ORANGE-3":
                    text = '3';
                    break;
                case "Pacman":
                    text = 'P';
                    break;
            }
        }
    }

    private class MapListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton item = (JButton) e.getSource();
            item.setText(text + "");
            if ("#".equals(item.getText())) {
                item.setBackground(Color.BLUE);
            } else {
                item.setBackground(Color.BLACK);
            }
        }
    }

    public String[] getString() {
        String[] res = new String[Constants.MAP_HEIGHT];

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < Constants.MAP_HEIGHT; i++) {
            builder.setLength(0);
            for (int j = 0; j < Constants.MAP_WIDTH; j++) {
                builder.append(jButtonMap[i * Constants.MAP_WIDTH + j].getText());
            }
            res[i] = builder.toString();
        }

        return res;
    }

    public static void main(String[] args) {
        new MapMakerApp();
    }

}
