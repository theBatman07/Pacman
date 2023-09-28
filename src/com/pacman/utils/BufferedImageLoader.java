package com.pacman.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BufferedImageLoader {
    // load image
    public static BufferedImage loadImage(String path) throws IOException {
        BufferedImage image;
        image = ImageIO.read(new File(path)); //getClass().getResource(path)
        return image;
    }
}
