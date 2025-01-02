package org.cis1200.chess;

import java.awt.*;

public class Dummy extends Piece {

    public Dummy(int xPos, int yPos) {
        super(
                xPos, yPos, 0, 0, ChessColor.EMPTY, 0,
                "", "", Pieces.EMPTY
        );
    }

    @Override
    public boolean validMove(BoardSquare destination) {
        return false;
    }
}
