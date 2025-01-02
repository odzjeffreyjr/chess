package org.cis1200.chess;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

abstract class Piece implements PieceInterface {

    private final String whiteImgFile;
    private final String blackImgFile;
    private int xPosition;
    private int yPosition;
    private int width;
    private int height;
    private final Pieces type;
    private final ChessColor chessColor;
    private final int worth;

    private BufferedImage img;

    public Piece(
            int xPos, int yPos, int wid, int hei, ChessColor chessColor, int worth,
            String whiteFile,
            String blackFile, Pieces type
    ) {
        width = wid;
        height = hei;
        xPosition = xPos;
        yPosition = yPos;
        whiteImgFile = whiteFile;
        blackImgFile = blackFile;
        this.chessColor = chessColor;
        this.worth = worth;
        this.type = type;
        try {
            if (chessColor.equals(ChessColor.BLACK)) {
                img = ImageIO.read(new File(blackImgFile));
            } else {
                img = ImageIO.read(new File(whiteImgFile));
            }
        } catch (IOException ignored) {
        }
    }

    @Override
    public Coords getPosition() {
        return new Coords(xPosition, yPosition);
    }

    @Override
    public int getX() {
        return xPosition;
    }

    @Override
    public int getY() {
        return yPosition;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWorth() {
        return worth;
    }

    @Override
    public ChessColor getColor() {
        return chessColor;
    }

    @Override
    public void setPosition(Coords destination) {
        xPosition = destination.getX();
        yPosition = destination.getY();
    }

    @Override
    public void setWidth(int wid) {
        width = wid;
    }

    @Override
    public void setHeight(int hei) {
        height = hei;
    }

    @Override
    public Pieces getPieceType() {
        return type;
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(img, this.getX(), this.getY(), this.getWidth(), this.getHeight(), null);
    }

    public String getWhiteImgFile() {
        return whiteImgFile;
    }

    public String getBlackImgFile() {
        return blackImgFile;
    }

    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public Pieces getType() {
        return type;
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    public ChessColor getChessColor() {
        return chessColor;
    }
}
