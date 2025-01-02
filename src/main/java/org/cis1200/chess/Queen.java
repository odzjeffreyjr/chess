package org.cis1200.chess;

public class Queen extends Piece {

    public Queen(int xPos, int yPos, int wid, int hei, ChessColor chessColor) {
        super(
                xPos, yPos, wid, hei, chessColor, 9,
                "files/whiteQueen.png", "files/blackQueen.png", Pieces.QUEEN
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
        return ((targetX == this.getX() & targetY == this.getY()) || (dx == 0) || (dy == 0)
                || (dx == dy));
    }
}
