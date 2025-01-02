package org.cis1200.chess;

public class BoardSquare extends Coords {
    private int whiteCapturable;
    private int blackCapturable;
    private ChessColor pieceColor;

    public BoardSquare(int x, int y, int whiteCapturable, int blackCapturable, ChessColor col) {
        super(x, y);
        this.pieceColor = col;
        this.whiteCapturable = whiteCapturable;
        this.blackCapturable = blackCapturable;
    }

    public int getX() {
        return super.getX();
    }

    public int getY() {
        return super.getY();
    }

    public boolean isWhiteCapturable() {
        return whiteCapturable > 0;
    }

    public boolean isBlackCapturable() {
        return blackCapturable > 0;
    }

    public void increaseWhiteCapturable() {
        this.whiteCapturable += 1;
    }

    public void increaseBlackCapturable() {
        this.blackCapturable += 1;
    }

    public void decreaseWhiteCapturable() {
        if (this.whiteCapturable > 0) {
            this.whiteCapturable -= 1;
        }
    }

    public void decreaseBlackCapturable() {
        if (this.whiteCapturable > 0) {
            this.whiteCapturable -= 1;
        }
    }

    public void resetCapturable() {
        this.whiteCapturable = 0;
        this.blackCapturable = 0;
    }

    public ChessColor getPieceColour() {
        return pieceColor;
    }

    public void setPieceColor(ChessColor pieceColor) {
        this.pieceColor = pieceColor;
    }
}
