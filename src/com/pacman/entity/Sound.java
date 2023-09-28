package com.pacman.entity;

import javax.sound.sampled.*;
import java.io.File;


public class Sound {
    Clip clip;
    File[] files;
    private boolean isSoundOn;

    public enum MenuSound {
        Start,
        Loading,
        MenuSound
    }

    public enum GhostSound {
        Normal,
        Frightened,
        Eaten
    }

    public enum PacmanSound {
        Wakawaka,
        Death,
        EatGhost
    }

    public Sound(boolean isSoundOn) {
        files = new File[9];
        files[0] = new File("src\\com\\pacman\\res\\Sounds\\StartSound.wav");
        files[1] = new File("src\\com\\pacman\\res\\Sounds\\LoadingSound.wav");
        files[2] = new File("src\\com\\pacman\\res\\Sounds\\MenuSound.wav");

        files[3] = new File("src\\com\\pacman\\res\\Sounds\\Ghost\\Normal.wav");
        files[4] = new File("src\\com\\pacman\\res\\Sounds\\Ghost\\Frightened.wav");
        files[5] = new File("src\\com\\pacman\\res\\Sounds\\Ghost\\Eaten.wav");

        files[6] = new File("src\\com\\pacman\\res\\Sounds\\Pacman\\Wakawaka.wav");
        files[7] = new File("src\\com\\pacman\\res\\Sounds\\Pacman\\DeathSound.wav");
        files[8] = new File("src\\com\\pacman\\res\\Sounds\\Pacman\\EatGhost.wav");
        this.isSoundOn = isSoundOn;
    }

    public void setFile(MenuSound type) {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(files[type.ordinal()]);
            clip = AudioSystem.getClip();
            clip.open(ais);
        } catch (Exception e) {
        }
    }

    public void setFile(GhostSound type) {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(files[type.ordinal() + 3]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        } catch (Exception e) {
        }
    }

    public void setFile(PacmanSound type) {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(files[type.ordinal() + 6]);
            clip = AudioSystem.getClip();
            clip.open(ais);

        } catch (Exception e) {
        }
    }

    public void turnOnSound() {
        isSoundOn = true;
    }

    public void  turnOffSound() {
        isSoundOn = false;
    }

    public boolean isSoundOn() {
        return isSoundOn;
    }

    public void play() {
        if (isSoundOn && clip != null) {
            clip.start();
        }
    }

    public void loop() {
        if (isSoundOn) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}
