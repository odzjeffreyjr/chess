package org.cis1200.chess;

public class King extends Piece {

    private boolean firstMoveMade = false;

    public King(int xPos, int yPos, int wid, int hei, ChessColor chessColor) {
        super(
                xPos, yPos, wid, hei, chessColor, 0,
                "files/whiteKing.png", "files/blackKing.png", Pieces.KING
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
        if (destination.isWhiteCapturable() && getColor() == ChessColor.BLACK) {
            return false;
        }
        if (destination.isBlackCapturable() && getColor() == ChessColor.WHITE) {
            return false;
        }
        int targetX = destination.getX();
        int targetY = destination.getY();
        int dx = Math.abs(targetX - getX());
        int dy = Math.abs(targetY - getY());
        if (!isFirstMoveMade()) {
            return (dy == 0) && (dx == 2 * ChessArena.getPieceSize())
                    || ((targetX == this.getX() & targetY == this.getY()) ||
                            ((dx == 0) && (dy == ChessArena.getPieceSize()))
                            || ((dy == 0) && (dx == ChessArena.getPieceSize())) ||
                            ((dx == dy) && (dx == ChessArena.getPieceSize())));
        }
        return ((targetX == this.getX() & targetY == this.getY()) ||
                ((dx == 0) && (dy == ChessArena.getPieceSize()))
                || ((dy == 0) && (dx == ChessArena.getPieceSize())) ||
                ((dx == dy) && (dx == ChessArena.getPieceSize())));
    }

    public boolean isFirstMoveMade() {
        return firstMoveMade;
    }
}
