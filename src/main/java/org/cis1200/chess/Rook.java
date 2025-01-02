package org.cis1200.chess;

public class Rook extends Piece {

    private boolean firstMoveMade = false;

    public Rook(int xPos, int yPos, int wid, int hei, ChessColor chessColor) {
        super(
                xPos, yPos, wid, hei, chessColor, 5,
                "files/whiteRook.png", "files/blackRook.png", Pieces.ROOK
        );
    }

    @Override
    public void setPosition(Coords destination) {
        super.setPosition(destination);
        firstMoveMade = true;
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
        return ((targetX == this.getX() & targetY == this.getY()) || ((dx == 0) || (dy == 0)));
    }

    public boolean isFirstMoveMade() {
        return firstMoveMade;
    }
}
