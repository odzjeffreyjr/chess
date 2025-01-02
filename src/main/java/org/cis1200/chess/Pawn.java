package org.cis1200.chess;

public class Pawn extends Piece {

    private boolean firstMoveMade = false;

    public Pawn(int xPos, int yPos, int wid, int hei, ChessColor chessColor) {
        super(
                xPos, yPos, wid, hei, chessColor, 1,
                "files/whitePawn.png", "files/blackPawn.png", Pieces.PAWN
        );
    }

    @Override
    public void setPosition(Coords destination) {
        super.setPosition(destination);
        firstMoveMade = true;
    }

    @Override
    public boolean validMove(BoardSquare destination) {
        int targetX = destination.getX();
        int targetY = destination.getY();
        int dx = targetX - getX();
        int dy = targetY - getY();
        if (targetX == this.getX() & targetY == this.getY()) {
            return true;
        }

        if (destination.getPieceColour().equals(getColor())
                || (Math.abs(dx) > ChessArena.getPieceSize()) ||
                (Math.abs(dy) > 3 * ChessArena.getPieceSize())) {
            return false;
        } else {
            boolean capture = ((destination.getPieceColour() == ChessColor.WHITE)
                    && (getColor() == ChessColor.BLACK) &&
                    (Math.abs(dx) == ChessArena.getPieceSize())
                    && (Math.abs(dy) == ChessArena.getPieceSize())) ||
                    ((destination.getPieceColour() == ChessColor.BLACK)
                            && (getColor() == ChessColor.WHITE) &&
                            (Math.abs(dx) == ChessArena.getPieceSize())
                            && (Math.abs(dy) == ChessArena.getPieceSize()));
            if (capture) {
                if (getColor() == ChessColor.WHITE) {
                    return (dy == (-1 * ChessArena.getPieceSize()));
                } else {
                    return (dy == (ChessArena.getPieceSize()));
                }
            } else {
                if (firstMoveMade) {
                    if (dx != 0) {
                        return false;
                    }
                    if (destination.getPieceColour().equals(ChessColor.EMPTY)) {
                        if (getColor() == ChessColor.WHITE) {
                            return (dy == (-1 * ChessArena.getPieceSize()));
                        } else {
                            return (dy == (ChessArena.getPieceSize()));
                        }
                    } else {
                        return false;
                    }
                } else {
                    if (dx != 0) {
                        return false;
                    }
                    if (destination.getPieceColour().equals(ChessColor.EMPTY)) {
                        if (getColor() == ChessColor.WHITE) {
                            return (dy == (-1 * ChessArena.getPieceSize()))
                                    || (dy == (-2 * ChessArena.getPieceSize()));
                        } else {
                            return (dy == (ChessArena.getPieceSize()))
                                    || (dy == (2 * ChessArena.getPieceSize()));
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    public boolean isFirstMoveMade() {
        return firstMoveMade;
    }
}
