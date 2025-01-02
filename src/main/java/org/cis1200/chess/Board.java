package org.cis1200.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Board {

    public static final String IMG_FILE = "files/boardGrid.png";
    private final int width;
    private final int height;
    private static BufferedImage img;

    public Board(int wid, int hei) {
        width = wid;
        height = hei;
        try {
            if (img == null) {
                img = ImageIO.read(new File(IMG_FILE));
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
    }

    public int getX() {
        return 0;
    }

    public int getY() {
        return 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void draw(Graphics g) {
        g.drawImage(img, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
    }
}
