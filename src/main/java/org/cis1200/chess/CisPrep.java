package org.cis1200.chess;

import java.awt.*;

public class CisPrep implements PieceInterface, Runnable {
    @Override
    public void run() {

    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWorth() {
        return 0;
    }

    @Override
    public Coords getPosition() {
        return null;
    }

    @Override
    public void setPosition(Coords destination) {

    }

    @Override
    public void setWidth(int wid) {

    }

    @Override
    public void setHeight(int hei) {

    }

    @Override
    public boolean validMove(BoardSquare destination) {
        return false;
    }

    @Override
    public Pieces getPieceType() {
        return null;
    }

    @Override
    public ChessColor getColor() {
        return null;
    }

    @Override
    public void draw(Graphics g) {

    }
}
