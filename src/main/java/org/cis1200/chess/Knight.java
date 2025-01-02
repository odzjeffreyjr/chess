package org.cis1200.chess;

public class Knight extends Piece {

    public Knight(int xPos, int yPos, int wid, int hei, ChessColor chessColor) {
        super(
                xPos, yPos, wid, hei, chessColor, 3,
                "files/whiteKnight.png", "files/blackKnight.png", Pieces.KNIGHT
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
        return ((targetX == this.getX() & targetY == this.getY()) ||
                ((dx == 2 * dy) && (dx - dy == dy)) || ((dy == 2 * dx) && (dy - dx == dx)));
    }
}
