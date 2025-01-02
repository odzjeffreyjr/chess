package org.cis1200.chess;

import java.awt.*;

public interface PieceInterface {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    int getWorth();

    Coords getPosition();

    void setPosition(Coords destination);

    void setWidth(int wid);

    void setHeight(int hei);

    boolean validMove(BoardSquare destination);

    Pieces getPieceType();

    ChessColor getColor();

    void draw(Graphics g);
}
