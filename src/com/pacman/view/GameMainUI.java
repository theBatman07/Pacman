package com.pacman.view;

import com.pacman.controller.GameController;
import com.pacman.entity.PixelNumber;
import com.pacman.entity.Sound;
import com.pacman.utils.BufferedImageLoader;
import com.pacman.utils.Constants;
import com.pacman.utils.DataBaseUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


public class GameMainUI {
    private static final String GAME_TITLE = "PACMAN";

    private JFrame window;
    private Container con;
    private ImagePanel titleUI;
    private ImagePanel scoreUI;
    private ImagePanel settingUI;
    private Sound sound;

    private GameView gameUI;
    private GameController controller;

    private EndUI endUI;
    private MenuState menuState;

    enum MenuState {
        Home,
        Score,
        Setting
    }

    public GameMainUI() {
        endUI = null;
        sound = new Sound(Constants.SOUND_DEFAULT);
        if (sound.isSoundOn()) {
            sound.setFile(Sound.MenuSound.MenuSound);
            sound.play();
            sound.loop();
        }

        menuState = MenuState.Home;
        initFrame();
        con = window.getContentPane();
        initTileUI();
    }

    private void initFrame() {
        window = new JFrame();
        window.setTitle(GAME_TITLE);
        window.setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    private void initTileUI() {
        titleUI = new ImagePanel("src\\com\\pacman\\res\\title-background.jpg");
        // Title panel config
        titleUI.setLayout(new BoxLayout(titleUI, BoxLayout.Y_AXIS));

        // sub title panel
        JPanel menuPanel = new JPanel();
        JPanel logoPanel = new JPanel();

        // Logo panel config
        JLabel logo = new JLabel();
        logo.setIcon(new ImageIcon("src\\com\\pacman\\res\\menu-logo.png"));
        logoPanel.add(logo);
        logoPanel.setBorder(new EmptyBorder(40, 0, 0, 0));
        logoPanel.setOpaque(false);

        // Menu panel config
        menuPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        menuPanel.setPreferredSize(new Dimension(300, 290));
        menuPanel.setMaximumSize(new Dimension(300, 290));

        // can le ben duoi
        menuPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        menuPanel.setLayout(new GridBagLayout());
        menuPanel.setOpaque(false); // trong suot

        MenuButton startBtn = new MenuButton("StartButton");
        MenuButton scoreBtn = new MenuButton("ScoreButton");
        MenuButton optionBtn = new MenuButton("OptionsButton");
        MenuButton quitBtn = new MenuButton("QuitButton");

        //add button to menu panel
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 0, 5, 0);
        c.gridy = 0;
        menuPanel.add(startBtn, c);
        c.gridy = 1;
        menuPanel.add(scoreBtn, c);
        c.gridy = 2;
        menuPanel.add(optionBtn, c);
        c.gridy = 3;
        menuPanel.add(quitBtn, c);

        // add to title ui
        titleUI.add(logoPanel);
        titleUI.add(menuPanel);

        // add to frame
        con.add(titleUI);
    }

    private void initScoreUI() {
        scoreUI = new ImagePanel("src\\com\\pacman\\res\\title-background.jpg");
        scoreUI.setLayout(new BoxLayout(scoreUI, BoxLayout.Y_AXIS));
        int page = 0;
        String[][] playerList = DataBaseUtils.getPlayerResult(page);
        String[] collum = {"DATE", "SCORE", "LEVEL", "WIN"};

        //sub panel
        JPanel tablePanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        // table panel config
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        DefaultTableModel defaultTableModel = new DefaultTableModel(playerList, collum);
        defaultTableModel.insertRow(0, collum);
        JTable playerTb = new JTable(defaultTableModel);
        playerTb.setFont(new Font("Arial", Font.BOLD, 18));
        playerTb.setForeground(Color.WHITE);
        playerTb.setRowHeight(50);
        playerTb.setOpaque(false);
        playerTb.setBackground(Color.DARK_GRAY);

        //table button panel
        JPanel tableButtonPanel = new JPanel();
        tableButtonPanel.add(new MenuButton("PreviousButton"));
        tableButtonPanel.add(new MenuButton("NextButton")); // TODO handle this
        tableButtonPanel.setOpaque(false);

        tablePanel.add(playerTb);
        tablePanel.add(tableButtonPanel);
        tablePanel.setBorder(new EmptyBorder(40, 100, 0, 100));
        tablePanel.setOpaque(false);

        // button panel config
        buttonPanel.add(new MenuButton("HomeButton"));
        buttonPanel.setBorder(new EmptyBorder(40, 0, 100, 0));
        buttonPanel.setOpaque(false);

        scoreUI.add(tablePanel);
        scoreUI.add(buttonPanel);

        //add to ui
        con.add(scoreUI);
    }

    private void initSettingUI() {
        JPanel panel = new JPanel();
        settingUI = new ImagePanel("src\\com\\pacman\\res\\title-background.jpg");

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(350, 0, 0, 0));
        panel.setOpaque(false);
        MenuButton homeBtn = new MenuButton("HomeButton");
        homeBtn.setBorder(BorderFactory.createEmptyBorder(40, 160, 0, 0));

        panel.add(new MenuButton("SoundButton"));
        panel.add(homeBtn);

        // add to ui
        settingUI.add(panel);
        con.add(settingUI);
    }

    // initGameUI and new game
    private void initGame() {
        // stop music
        if (sound.isSoundOn()){
            sound.stop();
        }

        if (controller != null) { // KILL THREAD GAME CU
            controller.killThread();
        }

        Object pauseLock = new Object();
        try {
            gameUI = new GameView(this, pauseLock, con);
            controller = new GameController(gameUI, pauseLock, sound.isSoundOn());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // sub menu ui
    public void initEndUI(int score, boolean isWon) {
        if (endUI != null) {
            endUI.setVisible(true);
            endUI.setWon(isWon);
            window.removeKeyListener(gameUI);
            con.remove(gameUI);
            return;
        }

        try {
            endUI = new EndUI(score, isWon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        endUI.setBackground(Color.BLACK);
        endUI.setOpaque(true);

        window.removeKeyListener(gameUI);
        con.remove(gameUI);

        con.add(endUI);
        window.addKeyListener(endUI);
        window.setFocusable(true);
    }

    public void showMainUi() {
        sound.play();
        titleUI.setVisible(true);
        window.removeKeyListener(gameUI);
        con.repaint();
    }

    private void playMusic() {
        if (!sound.isSoundOn()){
            sound.stop();
            return;
        }
        sound.setFile(Sound.MenuSound.MenuSound);
        sound.play();
    }
    //////////////
    //INNER CLASS
    //////////////
    private class EndUI extends JPanel implements KeyListener {
        private PixelNumber pixelNumber;
        private int score;
        private boolean yes;
        private boolean isWon;

        private EndUI(int score, boolean isWon) throws IOException {
            pixelNumber = new PixelNumber();
            yes = true;
            this.score = score;
            this.isWon = isWon;
        }

        public void setWon(boolean isWon) {
            this.isWon = isWon;
            this.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = window.getWidth() / 2;
            int scoreSize = pixelNumber.getSize(this.score, PixelNumber.FontType.MediumBlack);
            int highestScore = DataBaseUtils.getHighestScore();
            int highestScoreSize = pixelNumber.getSize(highestScore, PixelNumber.FontType.MediumBlack);

            try {
                g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\BackGround.jpg"), 0, 0, null);
                g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\YourScore.png"), width - (173 + scoreSize) / 2, 455, null);
                g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\YourHighestScore.png"), width - (292 + highestScoreSize) / 2, 505, null);

                if (this.isWon) {
                    g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\GameWon.png"), width - 300, -50, null);
                } else {
                    g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\GameOver.png"), width - 300, -50, null);
                }

                if (this.yes) {
                    g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\Yes.png"), width - 300, -50, null);
                } else {
                    g2d.drawImage(BufferedImageLoader.loadImage("src\\com\\pacman\\res\\No.png"), width - 300, -50, null);
                }
            } catch (IOException e) {
            }

            pixelNumber.draw(g2d, this.score, width - (scoreSize) / 2 + 173 / 2, 450, PixelNumber.FontType.MediumBlack);
            pixelNumber.draw(g2d, highestScore, width - (highestScoreSize) / 2 + 292 / 2, 500, PixelNumber.FontType.MediumBlack);
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (KeyEvent.VK_LEFT == e.getKeyCode()) {
                this.yes = true;
                this.repaint();
                return;
            }
            if (KeyEvent.VK_RIGHT == e.getKeyCode()) {
                this.yes = false;
                this.repaint();
            }
            if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                if (yes) {
                    controller.killThread();
                    initGame();
                    endUI.setVisible(false);
                    window.addKeyListener(gameUI);
                    con.add(gameUI);
                    con.repaint();
                    gameUI.setVisible(true);
                    controller.startGameThread();
                } else {
                    endUI.setVisible(false);
                    titleUI.setVisible(true);
                    con.repaint();
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public class MenuButton extends JLabel implements MouseListener {
        private static final String iconFolPath = "src\\com\\pacman\\res\\MenuButton\\";
        String buttonName;
        String iconName;
        String activeIconName;

        ImageIcon norIcon;
        ImageIcon actIcon;

        public MenuButton(String iconName) {
            super();
            this.buttonName = iconName;
            this.iconName = iconFolPath + iconName + ".png";
            this.activeIconName = iconFolPath + "Active" + iconName + ".png";
            norIcon = new ImageIcon(this.iconName);
            actIcon = new ImageIcon(activeIconName);
            this.addMouseListener(this);

            if ("SoundButton".equals(iconName)) {
                if (sound.isSoundOn()) {
                    this.setActIcon();
                } else {
                    this.setNorIcon();
                }
                return;
            }

            this.setIcon(norIcon);
        }

        public String getButtonName() {
            return buttonName;
        }

        public void setActIcon() {
            this.setIcon(actIcon);
        }

        public void setNorIcon() {
            this.setIcon(norIcon);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            MenuButton button = (MenuButton) e.getSource();
            String buttonName = button.getButtonName();

            if ("StartButton".equals(buttonName)) {
                titleUI.setVisible(false);
                con.repaint();
                initGame();

                window.addKeyListener(gameUI);
                window.setFocusable(true); // for key listener
                con.add(gameUI);

                controller.startGameThread();
                return;
            }

            if ("ScoreButton".equals(buttonName)) {
                initScoreUI();
                titleUI.setVisible(false);
                scoreUI.setVisible(true);
                menuState = MenuState.Score;
                return;
            }

            if ("OptionsButton".equals(buttonName)) {
                initSettingUI();
                titleUI.setVisible(false);
                settingUI.setVisible(true);
                menuState = MenuState.Setting;
                return;
            }

            if ("HomeButton".equals(buttonName)) {
                if (MenuState.Setting == menuState) {
                    settingUI.setVisible(false);
                    con.remove(settingUI);
                } else if (MenuState.Score == menuState) {
                    scoreUI.setVisible(false);
                    con.remove(scoreUI);
                }

                titleUI.setVisible(true);
                menuState = MenuState.Home;
                return;
            }

            // quit button
            if ("QuitButton".equals(buttonName)) {
                System.exit(0);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            MenuButton btn = (MenuButton) e.getSource();

            if ("SoundButton".equals(buttonName)) {
                if (sound.isSoundOn()) {
                    btn.setNorIcon();
                    sound.turnOffSound();
                } else {
                    btn.setActIcon();
                    sound.turnOnSound();
                }
                playMusic();
                return;
            }

            btn.setActIcon();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if ("SoundButton".equals(buttonName)) {
                return;
            }

            MenuButton btn = (MenuButton) e.getSource();
            btn.setNorIcon();
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }


}
