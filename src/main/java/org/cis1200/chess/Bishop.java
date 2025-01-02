package org.cis1200.chess;

public class Bishop extends Piece {

    public Bishop(int xPos, int yPos, int wid, int hei, ChessColor chessColor) {
        super(
                xPos, yPos, wid, hei, chessColor, 3,
                "files/whiteBishop.png", "files/blackBishop.png", Pieces.BISHOP
        );
    }

    @Override
    public boolean validMove(BoardSquare destination) {
        if (destination.getPieceColour() == getColor()) {
            return false;
        }
        int targetX = destination.getX();
        int targetY = destination.getY();
        int dx = Math.abs(targetX - getX());
        int dy = Math.abs(targetY - getY());
        return ((targetX == this.getX() & targetY == this.getY()) || (dx == dy));
    }
}
