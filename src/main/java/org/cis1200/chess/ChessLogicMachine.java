package org.cis1200.chess;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ChessLogicMachine is responsible for managing the logic of a chess game.
 * It handles piece movement, game state updates, evaluations, and user
 * interactions.
 * This class maintains the state of the chessboard, evaluates game conditions
 * such as check, checkmate, and stalemate,
 * and processes user inputs to update the game state accordingly.
 */
/**
 * ChessLogicMachine is responsible for managing the logic of a chess game.
 * It handles piece movement, game state updates, evaluations, and user
 * interactions.
 */
public class ChessLogicMachine {
    private BoardSquare[][] squareMatrixRef;
    private Piece[][] pieceMatrixRef;
    private Mode mode = Mode.WHITE_SELECT;
    private boolean checkMate = false;
    private Piece pieceSelected = null;
    private final King whiteKingRef;
    private final King blackKingRef;
    private King kingInCheck = null;
    private boolean gameEnded = false;
    private Status gameStatus = Status.NORMAL;
    private int whiteEval;
    private int blackEval;
    private ChessColor winner;
    private String errorMessage = "No error";
    private Clip clip = null;
    private SoundEffects sounds;
    private boolean kingInCheckSoundPlayed = false; // check Whether works well
    private ChessArena caller;

    /**
     * Constructs a ChessLogicMachine object with the specified board configuration
     * and kings.
     *
     * @param squareMatrix A 2D array representing the board squares.
     * @param pieceMatrix  A 2D array representing the pieces on the board.
     * @param theWhiteKing The white king piece.
     * @param theBlackKing The black king piece.
     * @param caller       The ChessArena instance that is calling this logic
     *                     machine.
     */
    public ChessLogicMachine(
            BoardSquare[][] squareMatrix, Piece[][] pieceMatrix, King theWhiteKing,
            King theBlackKing, ChessArena caller
    ) {
        whiteKingRef = theWhiteKing;
        blackKingRef = theBlackKing;
        squareMatrixRef = squareMatrix;
        pieceMatrixRef = pieceMatrix;
        sounds = new SoundEffects();
        this.caller = caller;
    }

    public void handleClick(int x, int y) {
        if (getKingInCheck() != null) {
            checkMateCheck(getKingInCheck());
            setGameEnded();
            getCaller().updateStatus();
        }
        if (!gameEnded) {
            int xCoord = x / ChessArena.getPieceSize();
            int yCoord = y / ChessArena.getPieceSize();
            Piece piece = pieceMatrixRef[yCoord][xCoord];
            BoardSquare square = squareMatrixRef[yCoord][xCoord];
            if (mode == Mode.WHITE_PLACE) {
                if (piece.getPieceType() == Pieces.KING && piece.getColor() != ChessColor.WHITE) {
                    setErrorMessage("Cannot capture King!");
                    mode = Mode.WHITE_SELECT;
                    pieceSelected = null;
                } else if (piece.getColor() == ChessColor.WHITE) {
                    mode = Mode.WHITE_PLACE;
                    pieceSelected = piece;
                } else if ((pieceSelected != null)
                        && (pieceSelected.validMove(squareMatrixRef[yCoord][xCoord]))) {
                    switch (pieceSelected.getPieceType()) {
                        case PAWN:
                            if (isPawnPathClear((Pawn) pieceSelected, square)) {
                                tryMove(
                                        piece, square, ChessColor.WHITE, Mode.WHITE_SELECT, true,
                                        false
                                );
                                stalemateCheck(ChessColor.BLACK);
                            } else {
                                setErrorMessage("Invalid move!");
                                mode = Mode.WHITE_SELECT;
                                pieceSelected = null;
                            }
                            break;
                        case ROOK:
                            if (isRookPathClear((Rook) pieceSelected, square)) {
                                tryMove(
                                        piece, square, ChessColor.WHITE, Mode.WHITE_SELECT, false,
                                        false
                                );
                                stalemateCheck(ChessColor.BLACK);
                            } else {
                                setErrorMessage("Invalid move!");
                                mode = Mode.WHITE_SELECT;
                                pieceSelected = null;
                            }
                            break;
                        case KNIGHT, KING:
                            if (pieceSelected.getPieceType() == Pieces.KING) {
                                try {
                                    int targetX = square.getX();
                                    int targetY = square.getY();
                                    int dx = Math.abs(targetX - pieceSelected.getX());
                                    int dy = Math.abs(targetY - pieceSelected.getY());

                                    if (dx == 2 * ChessArena.getPieceSize() && dy == 0) {
                                        King king = (King) pieceSelected;

                                        if (!king.isFirstMoveMade()) {
                                            Rook rook = null;
                                            BoardSquare rookDest = null;

                                            // Check if castling to the right
                                            if (targetX > pieceSelected.getX()) {
                                                rook = (Rook) pieceMatrixRef[targetY
                                                        / ChessArena.getPieceSize()][7];
                                                rookDest = squareMatrixRef[targetY / ChessArena
                                                        .getPieceSize()][(pieceSelected.getX()
                                                                + ChessArena.getPieceSize())
                                                                / ChessArena.getPieceSize()];
                                            } else if (targetX < pieceSelected.getX()) {
                                                rook = (Rook) pieceMatrixRef[targetY
                                                        / ChessArena.getPieceSize()][0];
                                                rookDest = squareMatrixRef[targetY / ChessArena
                                                        .getPieceSize()][(pieceSelected.getX()
                                                                - ChessArena.getPieceSize())
                                                                / ChessArena.getPieceSize()];
                                            }

                                            if (rook != null && !rook.isFirstMoveMade() &&
                                                    rook.getColor() == king.getColor() &&
                                                    rookDest.getPieceColour() == ChessColor.EMPTY &&
                                                    square.getPieceColour() == ChessColor.EMPTY &&
                                                    !square.isBlackCapturable()
                                                    && !rookDest.isBlackCapturable()) {

                                                // Move the king
                                                tryMove(
                                                        piece, square, king.getColor(),
                                                        Mode.WHITE_SELECT, false, false
                                                );

                                                // Move the rook
                                                int oldRookX = rook.getX();
                                                int oldRookY = rook.getY();
                                                rook.setPosition(rookDest);
                                                pieceMatrixRef[rookDest.getY()
                                                        / ChessArena.getPieceSize()][rookDest.getX()
                                                                / ChessArena.getPieceSize()] = rook;
                                                squareMatrixRef[rookDest.getY()
                                                        / ChessArena.getPieceSize()][rookDest.getX()
                                                                / ChessArena.getPieceSize()]
                                                                        .setPieceColor(
                                                                                rook.getColor()
                                                        );

                                                pieceMatrixRef[oldRookY / ChessArena
                                                        .getPieceSize()][oldRookX / ChessArena
                                                                .getPieceSize()] = new Dummy(
                                                                        oldRookX, oldRookY
                                                                );
                                                squareMatrixRef[oldRookY
                                                        / ChessArena.getPieceSize()][oldRookX
                                                                / ChessArena.getPieceSize()]
                                                                        .setPieceColor(
                                                                                ChessColor.EMPTY
                                                        );
                                                playSound(Effect.CASTLE);
                                            } else {
                                                setErrorMessage("Invalid move!");
                                                mode = Mode.WHITE_SELECT;
                                                pieceSelected = null;
                                            }
                                        } else {
                                            setErrorMessage("Invalid move!");
                                            mode = Mode.WHITE_SELECT;
                                            pieceSelected = null;
                                        }
                                    } else {
                                        tryMove(
                                                piece, square, ChessColor.WHITE, Mode.WHITE_SELECT,
                                                false, false
                                        );
                                    }
                                } catch (Exception e) {
                                    setErrorMessage("Invalid move!");
                                    mode = Mode.WHITE_SELECT;
                                    pieceSelected = null;
                                }
                            } else {
                                tryMove(
                                        piece, square, ChessColor.WHITE, Mode.WHITE_SELECT, false,
                                        false
                                );
                            }
                            stalemateCheck(ChessColor.BLACK);
                            break;
                        case BISHOP:
                            if (isBishopPathClear((Bishop) pieceSelected, square)) {
                                tryMove(
                                        piece, square, ChessColor.WHITE, Mode.WHITE_SELECT, false,
                                        false
                                );
                                stalemateCheck(ChessColor.BLACK);
                            } else {
                                setErrorMessage("Invalid move!");
                                mode = Mode.WHITE_SELECT;
                                pieceSelected = null;
                            }
                            break;
                        case QUEEN:
                            if (isQueenPathClear((Queen) pieceSelected, square)) {
                                tryMove(
                                        piece, square, ChessColor.WHITE, Mode.WHITE_SELECT, false,
                                        false
                                );
                                stalemateCheck(ChessColor.BLACK);
                            } else {
                                setErrorMessage("Invalid move!");
                                mode = Mode.WHITE_SELECT;
                                pieceSelected = null;
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    setErrorMessage("Invalid move!");
                    mode = Mode.WHITE_SELECT;
                    pieceSelected = null;
                }
            } else if (mode == Mode.WHITE_SELECT) {
                if (piece.getColor() == ChessColor.WHITE) {
                    pieceSelected = piece;
                    changeMode();
                }
            } else if (mode == Mode.BLACK_PLACE) {
                if (piece.getPieceType() == Pieces.KING && piece.getColor() != ChessColor.BLACK) {
                    setErrorMessage("Cannot capture King!");
                    mode = Mode.BLACK_SELECT;
                    pieceSelected = null;
                } else if (piece.getColor() == ChessColor.BLACK) {
                    mode = Mode.BLACK_PLACE;
                    pieceSelected = piece;
                } else if ((pieceSelected != null)
                        && (pieceSelected.validMove(squareMatrixRef[yCoord][xCoord]))) {
                    switch (pieceSelected.getPieceType()) {
                        case PAWN:
                            if (isPawnPathClear((Pawn) pieceSelected, square)) {
                                tryMove(
                                        piece, square, ChessColor.BLACK, Mode.BLACK_SELECT, true,
                                        false
                                );
                                stalemateCheck(ChessColor.WHITE);
                            } else {
                                setErrorMessage("Invalid move!");
                                mode = Mode.BLACK_SELECT;
                                pieceSelected = null;
                            }
                            break;
                        case ROOK:
                            if (isRookPathClear((Rook) pieceSelected, square)) {
                                tryMove(
                                        piece, square, ChessColor.BLACK, Mode.BLACK_SELECT, false,
                                        false
                                );
                                stalemateCheck(ChessColor.WHITE);
                            } else {
                                setErrorMessage("Invalid move!");
                                mode = Mode.BLACK_SELECT;
                                pieceSelected = null;
                            }
                            break;
                        case KNIGHT, KING:
                            if (pieceSelected.getPieceType() == Pieces.KING) {
                                try {
                                    int targetX = square.getX();
                                    int targetY = square.getY();
                                    int dx = Math.abs(targetX - pieceSelected.getX());
                                    int dy = Math.abs(targetY - pieceSelected.getY());

                                    if (dx == 2 * ChessArena.getPieceSize() && dy == 0) {
                                        King king = (King) pieceSelected;

                                        if (!king.isFirstMoveMade()) {
                                            Rook rook = null;
                                            BoardSquare rookDest = null;

                                            // Check if castling to the right
                                            if (targetX > pieceSelected.getX()) {
                                                rook = (Rook) pieceMatrixRef[targetY
                                                        / ChessArena.getPieceSize()][7];
                                                rookDest = squareMatrixRef[targetY / ChessArena
                                                        .getPieceSize()][(pieceSelected.getX()
                                                                + ChessArena.getPieceSize())
                                                                / ChessArena.getPieceSize()];
                                            } else if (targetX < pieceSelected.getX()) {
                                                rook = (Rook) pieceMatrixRef[targetY
                                                        / ChessArena.getPieceSize()][0];
                                                rookDest = squareMatrixRef[targetY / ChessArena
                                                        .getPieceSize()][(pieceSelected.getX()
                                                                - ChessArena.getPieceSize())
                                                                / ChessArena.getPieceSize()];
                                            }

                                            if (rook != null && !rook.isFirstMoveMade() &&
                                                    rook.getColor() == king.getColor() &&
                                                    rookDest.getPieceColour() == ChessColor.EMPTY &&
                                                    square.getPieceColour() == ChessColor.EMPTY &&
                                                    !square.isWhiteCapturable()
                                                    && !rookDest.isWhiteCapturable()) {

                                                // Move the king
                                                tryMove(
                                                        piece, square, king.getColor(),
                                                        Mode.BLACK_SELECT, false, false
                                                );

                                                // Move the rook
                                                int oldRookX = rook.getX();
                                                int oldRookY = rook.getY();
                                                rook.setPosition(rookDest);
                                                pieceMatrixRef[rookDest.getY()
                                                        / ChessArena.getPieceSize()][rookDest.getX()
                                                                / ChessArena.getPieceSize()] = rook;
                                                squareMatrixRef[rookDest.getY()
                                                        / ChessArena.getPieceSize()][rookDest.getX()
                                                                / ChessArena.getPieceSize()]
                                                                        .setPieceColor(
                                                                                rook.getColor()
                                                        );

                                                pieceMatrixRef[oldRookY / ChessArena
                                                        .getPieceSize()][oldRookX / ChessArena
                                                                .getPieceSize()] = new Dummy(
                                                                        oldRookX, oldRookY
                                                                );
                                                squareMatrixRef[oldRookY
                                                        / ChessArena.getPieceSize()][oldRookX
                                                                / ChessArena.getPieceSize()]
                                                                        .setPieceColor(
                                                                                ChessColor.EMPTY
                                                        );
                                                playSound(Effect.CASTLE);
                                            } else {
                                                setErrorMessage("Invalid move!");
                                                mode = Mode.BLACK_SELECT;
                                                pieceSelected = null;
                                            }
                                        } else {
                                            setErrorMessage("Invalid move!");
                                            mode = Mode.BLACK_SELECT;
                                            pieceSelected = null;
                                        }
                                    } else {
                                        tryMove(
                                                piece, square, ChessColor.BLACK, Mode.BLACK_SELECT,
                                                false, false
                                        );
                                    }
                                } catch (Exception e) {
                                    setErrorMessage("Invalid move!");
                                    mode = Mode.BLACK_SELECT;
                                    pieceSelected = null;
                                }
                            } else {
                                tryMove(
                                        piece, square, ChessColor.BLACK, Mode.BLACK_SELECT, false,
                                        false
                                );
                            }
                            stalemateCheck(ChessColor.WHITE);
                            break;
                        case BISHOP:
                            if (isBishopPathClear((Bishop) pieceSelected, square)) {
                                tryMove(
                                        piece, square, ChessColor.BLACK, Mode.BLACK_SELECT, false,
                                        false
                                );
                                stalemateCheck(ChessColor.WHITE);
                            } else {
                                setErrorMessage("Invalid move!");
                                mode = Mode.BLACK_SELECT;
                                pieceSelected = null;
                            }
                            break;
                        case QUEEN:
                            if (isQueenPathClear((Queen) pieceSelected, square)) {
                                tryMove(
                                        piece, square, ChessColor.BLACK, Mode.BLACK_SELECT, false,
                                        false
                                );
                                stalemateCheck(ChessColor.WHITE);
                                runCaptureSequence();
                            } else {
                                setErrorMessage("Invalid move!");
                                mode = Mode.BLACK_SELECT;
                                pieceSelected = null;
                            }
                            break;
                        default:
                            // System.out.println("Dummy");
                            break;
                    }
                } else {
                    setErrorMessage("Invalid move!");
                    mode = Mode.BLACK_SELECT;
                    pieceSelected = null;
                }
            } else if (mode == Mode.BLACK_SELECT) {
                if (piece.getColor() == ChessColor.BLACK) {
                    pieceSelected = piece;
                    changeMode();
                }
            }
            runCaptureSequence();
            updateKingsInCheck();
            setGameEnded();
        } else {
            setErrorMessage("Game already ended!");
        }
    }

    public int getBlackEval() {
        return blackEval;
    }

    public int getWhiteEval() {
        return whiteEval;
    }

    public boolean tryMove(
            Piece paramPiece, BoardSquare square, ChessColor paramColor, Mode paramMode,
            boolean isPawn, boolean testMode
    ) {
        Piece oldPieceSelected = pieceSelected;
        int oldPieceX = oldPieceSelected.getX();
        int oldPieceY = oldPieceSelected.getY();
        Piece displacedPiece = paramPiece;
        int destinationX = displacedPiece.getX();
        int destinationY = displacedPiece.getY();
        BoardSquare oldSquare = squareMatrixRef[oldPieceY / ChessArena.getPieceSize()][oldPieceX
                / ChessArena.getPieceSize()];
        movePiece(pieceSelected, square, pieceSelected.getX(), pieceSelected.getY());
        if (isPawn) {
            String promo = promotionSequence(square, (Pawn) pieceSelected);
            if (promo != null) {
                switch (promo) {
                    case "Rook":
                        pieceMatrixRef[destinationY / ChessArena.getPieceSize()][destinationX
                                / ChessArena.getPieceSize()] = new Rook(
                                        destinationX, destinationY, ChessArena.getPieceSize(),
                                        ChessArena.getPieceSize(), pieceSelected.getColor()
                                );
                        break;
                    case "Bishop":
                        pieceMatrixRef[destinationY / ChessArena.getPieceSize()][destinationX
                                / ChessArena.getPieceSize()] = new Bishop(
                                        destinationX, destinationY, ChessArena.getPieceSize(),
                                        ChessArena.getPieceSize(), pieceSelected.getColor()
                                );
                        break;
                    case "Queen":
                        pieceMatrixRef[destinationY / ChessArena.getPieceSize()][destinationX
                                / ChessArena.getPieceSize()] = new Queen(
                                        destinationX, destinationY, ChessArena.getPieceSize(),
                                        ChessArena.getPieceSize(), pieceSelected.getColor()
                                );
                        break;
                    case "Knight":
                        pieceMatrixRef[destinationY / ChessArena.getPieceSize()][destinationX
                                / ChessArena.getPieceSize()] = new Knight(
                                        destinationX, destinationY, ChessArena.getPieceSize(),
                                        ChessArena.getPieceSize(), pieceSelected.getColor()
                                );
                        break;
                    default:
                        break;
                }
            }
        }
        pieceSelected = null;
        runCaptureSequence();
        King formerKingInCheck = getKingInCheck();
        updateKingsInCheck();
        boolean isKingInCheck = (getKingInCheck() != null
                && getKingInCheck().getColor() == paramColor);

        if (testMode || isKingInCheck) {
            movePiece(
                    oldPieceSelected, oldSquare, oldPieceSelected.getX(),
                    oldPieceSelected.getY()
            );
            pieceMatrixRef[destinationY / ChessArena.getPieceSize()][destinationX
                    / ChessArena.getPieceSize()] = displacedPiece;
            squareMatrixRef[destinationY / ChessArena.getPieceSize()][destinationY
                    / ChessArena.getPieceSize()].setPieceColor(
                            displacedPiece.getColor()
            );
            runCaptureSequence();
            if (testMode) {
                kingInCheck = formerKingInCheck;
            }
            setErrorMessage("King in check!");
            mode = paramMode;
            pieceSelected = null;
            return isKingInCheck;
        } else {
            oldPieceSelected.setPosition(oldSquare);
            if (caller != null) {
                caller.animatePieceMovement(oldPieceSelected, oldSquare, square); // TODO: Figure
                                                                                  // out smoother
                                                                                  // animation

            }
            sounds.playEffect(Effect.MOVE);
            return false;
        }
    }

    // Helper method to convert (x, y) to chess coordinates like a1, b2, etc.
    private String convertToChessCoordinates(int x, int y) {
        int boardX = x / ChessArena.getPieceSize();
        int boardY = y / ChessArena.getPieceSize();

        char file = (char) ('a' + boardX); // Convert 0-7 to a-h
        int rank = 8 - boardY; // Convert to chess-style rank (1-8)

        return "" + file + rank;
    }

    public String getTurn() {
        return switch (mode) {
            case WHITE_SELECT -> "White's turn!";
            case BLACK_SELECT -> "Black's turn!";
            case BLACK_PLACE, WHITE_PLACE -> {
                String position = convertToChessCoordinates(
                        pieceSelected.getX(), pieceSelected.getY()
                );
                yield pieceSelected.getColor() + " " +
                        pieceSelected.getPieceType() + " at " + position + ".";
            }
        };
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Status getStatus() {
        return gameStatus;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Piece getPieceSelected() {
        return pieceSelected;
    }

    public King getKingInCheck() {
        return kingInCheck;
    }

    public String getStatusMessage() {
        switch (gameStatus) {
            case CHECKMATE:
                if (winner == ChessColor.BLACK) {
                    return "Checkmate! winner is black.";
                } else {
                    return "Checkmate! winner is white.";
                }
            case STALEMATE:
                return "Draw by stalemate";
            case DRAW:
                return "Draw agreed!";
            case CHECK:
                if (kingInCheck.getColor() == ChessColor.BLACK) {
                    return "Black king in check!";
                } else {
                    return "White king in check!";
                }
            case FLAG:
                if (winner == ChessColor.BLACK) {
                    return "Time up! White loses on time!";
                } else {
                    return "Time up! Black loses on time!";
                }
            case RESIGN:
                if (winner == ChessColor.BLACK) {
                    return "White resigns! Black wins!";
                } else {
                    return "Black resigns, White wins!";
                }
            case NORMAL:
                if (getBlackEval() > getWhiteEval()) {
                    return "Black is ahead by " + (getBlackEval() - getWhiteEval())
                            + " points of material.";
                } else if (getWhiteEval() > getBlackEval()) {
                    return "White is ahead by " + (getWhiteEval() - getBlackEval())
                            + " points of material.";
                } else {
                    return "Game is fairly balanced!";
                }
            default:
                return "Game going on!";
        }
    }

    public void setGameEnded() {
        if (gameStatus == Status.CHECKMATE || gameStatus == Status.STALEMATE
                || gameStatus == Status.DRAW
                || gameStatus == Status.FLAG || gameStatus == Status.RESIGN) {
            gameEnded = true;
        }
    }

    private void changeMode() {
        if (mode == Mode.WHITE_SELECT) {
            mode = Mode.WHITE_PLACE;
        } else if (mode == Mode.WHITE_PLACE) {
            mode = Mode.BLACK_SELECT;
        } else if (mode == Mode.BLACK_SELECT) {
            mode = Mode.BLACK_PLACE;
        } else if (mode == Mode.BLACK_PLACE) {
            mode = Mode.WHITE_SELECT;
        }
    }

    private String promotionSequence(BoardSquare destination, Pawn pawn) {
        // Ensure promotion is valid (reaching the last row for black or first row for
        // white)
        int promotionRow = (pawn.getColor() == ChessColor.WHITE) ? 0 : 7;
        if (destination.getY() / ChessArena.getPieceSize() != promotionRow) {
            return null; // Not a valid promotion scenario
        }

        // Create a modal dialog for selecting the promotion piece
        javax.swing.JDialog dialog = new javax.swing.JDialog(
                (java.awt.Frame) null, "Select Promotion Piece", true
        );
        dialog.setLayout(new java.awt.GridLayout(1, 4));
        dialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);

        String[] options = { "Queen", "Rook", "Bishop", "Knight" };
        javax.swing.JButton[] buttons = new javax.swing.JButton[options.length];
        AtomicReference<String> piecePromoted = new AtomicReference<>(""); // Store the selected
                                                                           // piece

        for (int i = 0; i < options.length; i++) {
            buttons[i] = new javax.swing.JButton(options[i]);
            final String pieceType = options[i];
            buttons[i].addActionListener(e -> {
                switch (pieceType) {
                    case "Queen":
                        piecePromoted.set("Queen");
                        break;
                    case "Rook":
                        piecePromoted.set("Rook");
                        break;
                    case "Bishop":
                        piecePromoted.set("Bishop");
                        break;
                    case "Knight":
                        piecePromoted.set("Knight");
                        break;
                    default:
                        piecePromoted.set("Queen");
                        break;
                }
                dialog.dispose(); // Close the dialog after a selection
            });
            dialog.add(buttons[i]);
        }

        // Configure and display the dialog
        dialog.setSize(400, 100);
        dialog.setLocationRelativeTo(null); // Center on screen
        dialog.setVisible(true);

        return piecePromoted.get();
    }

    private void checkMateCheck(King king) {
        Piece oldPiece = pieceSelected;
        Mode oldMode = mode;
        pieceSelected = king;
        mode = (king.getColor() == ChessColor.WHITE) ? Mode.WHITE_PLACE : Mode.BLACK_PLACE;
        boolean moveAvailable = false;
        runCaptureSequence();
        if (gameStatus == Status.CHECK) {
            ArrayList<Coords> kingMoves = allKingMoves(king, false);
            for (Coords move : kingMoves) {
                int xCoord = move.getX() / ChessArena.getPieceSize();
                int yCoord = move.getY() / ChessArena.getPieceSize();
                Piece piece = pieceMatrixRef[yCoord][xCoord];
                BoardSquare square = squareMatrixRef[yCoord][xCoord];
                if (!tryMove(
                        piece, square, king.getColor(),
                        (king.getColor() == ChessColor.WHITE) ? Mode.WHITE_SELECT
                                : Mode.BLACK_SELECT,
                        false, true
                )) {
                    checkMate = false;
                    moveAvailable = true;
                }
                if (moveAvailable) {
                    break;
                }
                pieceSelected = king;
                mode = (king.getColor() == ChessColor.WHITE) ? Mode.WHITE_PLACE : Mode.BLACK_PLACE;
            }

            pieceSelected = oldPiece;
            mode = oldMode;
            if (moveAvailable) {
                return;
            }

            outer: for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece piece = pieceMatrixRef[r][c];
                    if (piece.getColor() == king.getColor()
                            && piece.getPieceType() != Pieces.KING) {
                        ArrayList<Coords> moves = getAllValidMoves(piece);
                        for (Coords move : moves) {
                            int xCoord = move.getX() / ChessArena.getPieceSize();
                            int yCoord = move.getY() / ChessArena.getPieceSize();
                            BoardSquare thisSquare = squareMatrixRef[yCoord][xCoord];
                            Piece pieceDestination = pieceMatrixRef[yCoord][xCoord];
                            pieceSelected = piece;
                            mode = (piece.getColor() == ChessColor.WHITE) ? Mode.WHITE_PLACE
                                    : Mode.BLACK_PLACE;
                            if (!tryMove(
                                    pieceDestination, thisSquare, piece.getColor(),
                                    (piece.getColor() == ChessColor.WHITE) ? Mode.WHITE_SELECT
                                            : Mode.BLACK_SELECT,
                                    piece.getPieceType() == Pieces.PAWN, true
                            )) {
                                checkMate = false;
                                gameEnded = false;
                                moveAvailable = true;
                            }
                            if (moveAvailable) {
                                break outer;
                            }
                        }
                    }
                }
            }

            if (!moveAvailable) {
                // No legal moves, it's checkmate
                checkMate = true;
                gameEnded = true;
                winner = (king.getColor() == ChessColor.WHITE) ? ChessColor.BLACK
                        : ChessColor.WHITE;
                gameStatus = Status.CHECKMATE;
                setErrorMessage("Checkmate!");
                playSound(Effect.CHECKMATE);
            } else {
                pieceSelected = oldPiece;
                mode = oldMode;
            }
        } else {
            checkMate = false;
            gameEnded = false;
        }
    }

    private ArrayList<Coords> getAllValidMoves(Piece piece) {
        return switch (piece.getPieceType()) {
            case PAWN -> allPawnMoves((Pawn) piece, false);
            case KING -> allKingMoves((King) piece, false);
            case QUEEN -> allQueenMoves((Queen) piece, false);
            case ROOK -> allRookMoves((Rook) piece, false);
            case KNIGHT -> allKnightMoves((Knight) piece, false);
            case BISHOP -> allBishopMoves((Bishop) piece, false);
            default -> new ArrayList<>();
        };
    }

    public void stalemateCheck(ChessColor color) {
        if (gameStatus == Status.NORMAL) {
            int numValidMoves = 0;
            for (int r = 0; r < 8; r++) {
                for (int c = 0; c < 8; c++) {
                    Piece piece = pieceMatrixRef[r][c];
                    if (piece.getColor() == color) {
                        switch (piece.getPieceType()) {
                            case PAWN:
                                numValidMoves += allPawnMoves((Pawn) piece, false).size();
                                break;
                            case ROOK:
                                numValidMoves += allRookMoves((Rook) piece, false).size();
                                break;
                            case KNIGHT:
                                numValidMoves += allKnightMoves((Knight) piece, false).size();
                                break;
                            case BISHOP:
                                numValidMoves += allBishopMoves((Bishop) piece, false).size();
                                break;
                            case QUEEN:
                                numValidMoves += allQueenMoves((Queen) piece, false).size();
                                break;
                            case KING:
                                numValidMoves += allKingMoves((King) piece, false).size();
                                break;
                            default:
                                // System.out.println("Dummy");
                                break;
                        }
                    }
                }
            }
            if (numValidMoves <= 0) {
                gameStatus = Status.STALEMATE;
                gameEnded = true;
            }
        } else {
            return;
        }
    }

    public void updateKingsInCheck() {
        BoardSquare whiteSquare = squareMatrixRef[whiteKingRef.getY()
                / ChessArena.getPieceSize()][whiteKingRef.getX() / ChessArena.getPieceSize()];
        BoardSquare blackSquare = squareMatrixRef[blackKingRef.getY()
                / ChessArena.getPieceSize()][blackKingRef.getX() / ChessArena.getPieceSize()];
        if (!(gameStatus == Status.STALEMATE || gameStatus == Status.CHECKMATE
                || gameStatus == Status.DRAW ||
                gameStatus == Status.FLAG || gameStatus == Status.RESIGN)) {
            if (whiteSquare.isBlackCapturable()) {
                gameStatus = Status.CHECK;
                kingInCheck = whiteKingRef;
                if (!kingInCheckSoundPlayed) {
                    playSound(Effect.CHECK);
                    kingInCheckSoundPlayed = true;
                }
            } else if (blackSquare.isWhiteCapturable()) {
                gameStatus = Status.CHECK;
                kingInCheck = blackKingRef;
                if (!kingInCheckSoundPlayed) {
                    playSound(Effect.CHECK);
                    kingInCheckSoundPlayed = true;
                }
            } else {
                gameStatus = Status.NORMAL;
                kingInCheck = null;
                kingInCheckSoundPlayed = false;
            }
        }
    }

    public void runCaptureSequence() {
        // update check and checkmate
        whiteEval = 0;
        blackEval = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                squareMatrixRef[r][c].resetCapturable();
            }
        }
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = pieceMatrixRef[r][c];
                switch (piece.getPieceType()) {
                    case PAWN:
                        changeAllCapturable(allPawnMoves((Pawn) piece, true), piece.getColor(),
                                1);
                        break;
                    case ROOK:
                        changeAllCapturable(allRookMoves((Rook) piece, true), piece.getColor(),
                                1);
                        break;
                    case KNIGHT:
                        changeAllCapturable(
                                allKnightMoves((Knight) piece, true), piece.getColor(),
                                1
                        );
                        break;
                    case BISHOP:
                        changeAllCapturable(
                                allBishopMoves((Bishop) piece, true), piece.getColor(),
                                1
                        );
                        break;
                    case QUEEN:
                        changeAllCapturable(
                                allQueenMoves((Queen) piece, true), piece.getColor(),
                                1
                        );
                        break;
                    case KING:
                        changeAllCapturable(allKingMoves((King) piece, true), piece.getColor(),
                                1);
                        break;
                    default:
                        // System.out.println("Dummy");
                        break;
                }
                updateEval(piece);
            }
        }
        // System.out.println("White: " + whiteEval + " Black: " + blackEval);
    }

    private void updateEval(Piece piece) {
        switch (piece.getColor()) {
            case WHITE:
                whiteEval += piece.getWorth();
                break;
            case BLACK:
                blackEval += piece.getWorth();
                break;
            default:
                // System.out.println("Dummy");
                break;
        }
    }

    private void movePiece(Piece piece, BoardSquare destination, int xCoord, int yCoord) {
        piece.setPosition(destination);
        pieceMatrixRef[destination.getY() / ChessArena.getPieceSize()][destination.getX()
                / ChessArena.getPieceSize()] = piece;
        squareMatrixRef[destination.getY() / ChessArena.getPieceSize()][destination.getX()
                / ChessArena.getPieceSize()].setPieceColor(piece.getColor());
        pieceMatrixRef[yCoord / ChessArena.getPieceSize()][xCoord
                / ChessArena.getPieceSize()] = new Dummy(xCoord, yCoord);
        squareMatrixRef[yCoord / ChessArena.getPieceSize()][xCoord / ChessArena.getPieceSize()]
                .setPieceColor(ChessColor.EMPTY);
        changeMode();
    }

    public boolean isBishopPathClear(Bishop bishop, Coords destination) {
        int destX = destination.getX();
        int destY = destination.getY();
        int startX = bishop.getX();
        int startY = bishop.getY();
        int dx = destX - startX;
        int dy = destY - startY;
        if (startX == destX && startY == destY) {
            return true;
        }

        int stepX = dx > 0 ? ChessArena.getPieceSize() : -1 * ChessArena.getPieceSize();
        int stepY = dy > 0 ? ChessArena.getPieceSize() : -1 * ChessArena.getPieceSize();

        int x = startX + stepX;
        int y = startY + stepY;

        while (x != destX && y != destY) {
            if (pieceMatrixRef[y / ChessArena.getPieceSize()][x / ChessArena.getPieceSize()]
                    .getPieceType() != Pieces.EMPTY) {
                return false; // Obstruction found
            }
            x += stepX;
            y += stepY;
        }

        return true;
    }

    public boolean isRookPathClear(Rook rook, Coords destination) {
        int destX = destination.getX();
        int destY = destination.getY();
        int startX = rook.getX();
        int startY = rook.getY();
        int dx = destX - startX;
        int dy = destY - startY;

        if (startX == destX && startY == destY) {
            return true;
        }

        int stepX = dx != 0 ? (dx > 0 ? ChessArena.getPieceSize() : -1 * ChessArena.getPieceSize())
                : 0;
        int stepY = dy != 0 ? (dy > 0 ? ChessArena.getPieceSize() : -1 * ChessArena.getPieceSize())
                : 0;

        int x = startX + stepX;
        int y = startY + stepY;

        while (x != destX || y != destY) {
            if (pieceMatrixRef[y / ChessArena.getPieceSize()][x / ChessArena.getPieceSize()]
                    .getPieceType() != Pieces.EMPTY) {
                return false;
            }
            x += stepX;
            y += stepY;
        }

        return true;
    }

    public boolean isQueenPathClear(Queen queen, Coords destination) {
        int destX = destination.getX();
        int destY = destination.getY();
        int startX = queen.getX();
        int startY = queen.getY();
        int dx = destX - startX;
        int dy = destY - startY;

        if (startX == destX && startY == destY) {
            return true;
        }

        int stepX = dx != 0 ? (dx > 0 ? ChessArena.getPieceSize() : -1 * ChessArena.getPieceSize())
                : 0;
        int stepY = dy != 0 ? (dy > 0 ? ChessArena.getPieceSize() : -1 * ChessArena.getPieceSize())
                : 0;

        int x = startX + stepX;
        int y = startY + stepY;

        while (x != destX || y != destY) {
            if (pieceMatrixRef[y / ChessArena.getPieceSize()][x / ChessArena.getPieceSize()]
                    .getPieceType() != Pieces.EMPTY) {
                return false;
            }
            x += stepX;
            y += stepY;
        }

        return true; // Path is clear
    }

    public boolean isPawnPathClear(Pawn pawn, Coords destination) {
        boolean firstMove = pawn.isFirstMoveMade();
        int destX = destination.getX();
        int destY = destination.getY();
        int startX = pawn.getX();
        int startY = pawn.getY();
        int dx = destX - startX;
        int dy = destY - startY;

        int forwardStep = pawn.getColor() == ChessColor.WHITE ? -ChessArena.getPieceSize()
                : ChessArena.getPieceSize();

        if (firstMove || (dy == 1) || (dx != 0)) {
            return true;
        } else {
            int midY = startY + forwardStep; // Square between start and destination
            int scaledStartX = startX / ChessArena.getPieceSize();
            int scaledMidY = midY / ChessArena.getPieceSize();
            // System.out.println("ScaledStartX " + scaledStartX + " ScaledStartY " +
            // scaledMidY);
            return ((pieceMatrixRef[scaledMidY][scaledStartX]).getPieceType() == Pieces.EMPTY) &&
                    pieceMatrixRef[destY / ChessArena.getPieceSize()][destX
                            / ChessArena.getPieceSize()].getPieceType() == Pieces.EMPTY;
        }
    }

    public ArrayList<Coords> allBishopMoves(Bishop bishop, boolean captures) {
        ArrayList<Coords> movableSquares = new ArrayList<>();
        int x = bishop.getX();
        int y = bishop.getY();

        // Directions: top-left, top-right, bottom-left, bottom-right
        int[] dx = { -ChessArena.getPieceSize(), ChessArena.getPieceSize(),
            -ChessArena.getPieceSize(), ChessArena.getPieceSize() };
        int[] dy = { -ChessArena.getPieceSize(), -ChessArena.getPieceSize(),
            ChessArena.getPieceSize(), ChessArena.getPieceSize() };

        for (int direction = 0; direction < 4; direction++) {
            int i = x;
            int j = y;

            // Move along the diagonal in each direction until bounds are exceeded or an
            // obstruction is found
            while (true) {
                i += dx[direction];
                j += dy[direction];

                // Check if we're out of bounds
                if (i < 0 || i >= ChessArena.getPieceSize() * 8 || j < 0
                        || j >= ChessArena.getPieceSize() * 8) {
                    break;
                }
                // Add the square to the list if the destination is empty or occupied by an
                // enemy piece
                BoardSquare destinationSquare = squareMatrixRef[j / ChessArena.getPieceSize()][i
                        / ChessArena.getPieceSize()];
                if (destinationSquare.getPieceColour() != ChessColor.EMPTY) {
                    if (captures) {
                        movableSquares.add(new Coords(i, j));
                    } else {
                        if (destinationSquare.getPieceColour() != bishop.getColor()) {
                            movableSquares.add(new Coords(i, j));
                        }
                    }
                    break;
                }
                if (destinationSquare.getPieceColour() == ChessColor.EMPTY) {
                    movableSquares.add(new Coords(i, j)); // Move to an empty square
                }
            }
        }
        return movableSquares;
    }

    public ArrayList<Coords> allRookMoves(Rook rook, boolean captures) {
        ArrayList<Coords> movableSquares = new ArrayList<>();
        int x = rook.getX();
        int y = rook.getY();

        // Directions: left, right, up, down
        int[] dx = { -ChessArena.getPieceSize(), ChessArena.getPieceSize(), 0, 0 };
        int[] dy = { 0, 0, -ChessArena.getPieceSize(), ChessArena.getPieceSize() };

        // Traverse each of the 4 directions
        for (int direction = 0; direction < 4; direction++) {
            int i = x;
            int j = y;

            // Move along the line in the current direction until bounds are exceeded or an
            // obstruction is found
            while (true) {
                i += dx[direction];
                j += dy[direction];

                // Check if we're out of bounds
                if (i < 0 || i >= ChessArena.getPieceSize() * 8 || j < 0
                        || j >= ChessArena.getPieceSize() * 8) {
                    break;
                }

                // Check if the square contains an opponent's piece (for capture)
                BoardSquare destinationSquare = squareMatrixRef[j / ChessArena.getPieceSize()][i
                        / ChessArena.getPieceSize()];
                if (destinationSquare.getPieceColour() != ChessColor.EMPTY) {
                    if (captures) {
                        movableSquares.add(new Coords(i, j));
                    } else {
                        if (destinationSquare.getPieceColour() != rook.getColor()) {
                            movableSquares.add(new Coords(i, j));
                        }
                    }
                    break;
                } else {
                    movableSquares.add(new Coords(i, j));
                }
            }
        }
        return movableSquares;
    }

    public ArrayList<Coords> allQueenMoves(Queen queen, boolean captures) {
        ArrayList<Coords> movableSquares = new ArrayList<>();
        int x = queen.getX();
        int y = queen.getY();

        // Directions: left, right, up, down, top-left, top-right, bottom-left,
        // bottom-right
        int[] dx = { -ChessArena.getPieceSize(), ChessArena.getPieceSize(), 0, 0,
            -ChessArena.getPieceSize(),
            ChessArena.getPieceSize(), -ChessArena.getPieceSize(), ChessArena.getPieceSize() }; //
        int[] dy = { 0, 0, -ChessArena.getPieceSize(), ChessArena.getPieceSize(),
            -ChessArena.getPieceSize(),
            -ChessArena.getPieceSize(), ChessArena.getPieceSize(), ChessArena.getPieceSize() };

        // Traverse each of the 8 directions
        for (int direction = 0; direction < 8; direction++) {
            int i = x;
            int j = y;

            // Move along the line in the current direction until bounds are exceeded or an
            // obstruction is found
            while (true) {
                i += dx[direction];
                j += dy[direction];

                // Check if we're out of bounds
                if (i < 0 || i >= ChessArena.getPieceSize() * 8 || j < 0
                        || j >= ChessArena.getPieceSize() * 8) {
                    break;
                }

                BoardSquare destinationSquare = squareMatrixRef[j / ChessArena.getPieceSize()][i
                        / ChessArena.getPieceSize()];
                if (destinationSquare.getPieceColour() != ChessColor.EMPTY) {
                    if (captures) {
                        movableSquares.add(new Coords(i, j));
                    } else {
                        if (destinationSquare.getPieceColour() != queen.getColor()) {
                            movableSquares.add(new Coords(i, j));
                        }
                    }
                    break;
                } else {
                    movableSquares.add(new Coords(i, j));
                }
            }
        }
        return movableSquares;
    }

    public ArrayList<Coords> allKingMoves(King king, boolean captures) {
        ArrayList<Coords> movableSquares = new ArrayList<>();
        int x = king.getX();
        int y = king.getY();

        // Directions: up, down, left, right, top-left, top-right, bottom-left,
        // bottom-right
        int[] dx = { -ChessArena.getPieceSize(), ChessArena.getPieceSize(), 0, 0,
            -ChessArena.getPieceSize(),
            ChessArena.getPieceSize(), -ChessArena.getPieceSize(), ChessArena.getPieceSize() }; //
        int[] dy = { 0, 0, -ChessArena.getPieceSize(), ChessArena.getPieceSize(),
            -ChessArena.getPieceSize(),
            -ChessArena.getPieceSize(), ChessArena.getPieceSize(), ChessArena.getPieceSize() };

        // Traverse each of the 8 possible directions (all adjacent squares)
        for (int direction = 0; direction < 8; direction++) {
            int i = x + dx[direction];
            int j = y + dy[direction];

            // Check if we're out of bounds
            if (i < 0 || i >= ChessArena.getPieceSize() * 8 || j < 0
                    || j >= ChessArena.getPieceSize() * 8) {
                continue;
            }
            BoardSquare destinationSquare = squareMatrixRef[j / ChessArena.getPieceSize()][i
                    / ChessArena.getPieceSize()];
            switch (king.getColor()) {
                case WHITE:
                    if (!destinationSquare.isBlackCapturable()) {
                        if (captures) {
                            movableSquares.add(new Coords(i, j));
                        } else {
                            if (destinationSquare.getPieceColour() != king.getColor()) {
                                movableSquares.add(new Coords(i, j));
                            }
                        }
                    }
                    break;
                case BLACK:
                    if (!destinationSquare.isWhiteCapturable()) {
                        if (captures) {
                            movableSquares.add(new Coords(i, j));
                        } else {
                            if (destinationSquare.getPieceColour() != king.getColor()) {
                                movableSquares.add(new Coords(i, j));
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
            // if (!((destinationSquare.isBlackCapturable() && king.getColor() ==
            // ChessColor.WHITE)
            // || (destinationSquare.isWhiteCapturable() && king.getColor() ==
            // ChessColor.BLACK))) {
            // if (captures) {
            // movableSquares.add(new Coords(i, j));
            // } else {
            // if (destinationSquare.getPieceColour() != king.getColor() &&
            // destinationSquare.getPieceColour() !=ChessColor.EMPTY) {
            // movableSquares.add(new Coords(i, j));
            // }
            // }
            // }
        }
        return movableSquares;
    }

    public ArrayList<Coords> allKnightMoves(Knight knight, boolean captures) {
        ArrayList<Coords> movableSquares = new ArrayList<>();
        int x = knight.getX();
        int y = knight.getY();

        // Directions: all 8 possible "L" shaped moves
        int[] dx = { -2 * ChessArena.getPieceSize(), 2 * ChessArena.getPieceSize(),
            -2 * ChessArena.getPieceSize(),
            2 * ChessArena.getPieceSize(), -1 * ChessArena.getPieceSize(),
            ChessArena.getPieceSize(),
            -1 * ChessArena.getPieceSize(), ChessArena.getPieceSize() };
        int[] dy = { -1 * ChessArena.getPieceSize(), -1 * ChessArena.getPieceSize(),
            ChessArena.getPieceSize(),
            ChessArena.getPieceSize(), -2 * ChessArena.getPieceSize(),
            -2 * ChessArena.getPieceSize(),
            2 * ChessArena.getPieceSize(), 2 * ChessArena.getPieceSize() };

        for (int direction = 0; direction < 8; direction++) {
            int i = x + dx[direction];
            int j = y + dy[direction];

            // Check if we're out of bounds
            if (i < 0 || i >= ChessArena.getPieceSize() * 8 || j < 0
                    || j >= ChessArena.getPieceSize() * 8) {
                continue;
            }
            BoardSquare destinationSquare = squareMatrixRef[j / ChessArena.getPieceSize()][i
                    / ChessArena.getPieceSize()];
            if (captures) {
                movableSquares.add(new Coords(i, j));
            } else {
                if (destinationSquare.getPieceColour() != knight.getColor()) {
                    movableSquares.add(new Coords(i, j));
                }
            }
        }

        return movableSquares;
    }

    public ArrayList<Coords> allPawnMoves(Pawn pawn, boolean captures) {
        ArrayList<Coords> movableSquares = new ArrayList<>();
        int x = pawn.getX();
        int y = pawn.getY();

        int forwardStep = pawn.getColor() == ChessColor.WHITE ? -ChessArena.getPieceSize()
                : ChessArena.getPieceSize();

        // Diagonal directions for capturing
        int[] dx = { -ChessArena.getPieceSize(), ChessArena.getPieceSize() }; // left and right
        int[] dy = { forwardStep, forwardStep }; // pawn captures diagonally one square forward

        for (int i = 0; i < 2; i++) {
            int targetX = x + dx[i];
            int targetY = y + dy[i];

            // Check if the destination is within bounds
            if (targetX >= 0 && targetX < ChessArena.getPieceSize() * 8 &&
                    targetY >= 0 && targetY < ChessArena.getPieceSize() * 8) {
                BoardSquare destinationSquare = squareMatrixRef[targetY
                        / ChessArena.getPieceSize()][targetX / ChessArena.getPieceSize()];
                if (captures) {
                    movableSquares.add(new Coords(targetX, targetY));
                } else {
                    if ((destinationSquare.getPieceColour() == ChessColor.WHITE
                            && pawn.getColor() == ChessColor.BLACK)
                            || (destinationSquare.getPieceColour() == ChessColor.BLACK
                                    && pawn.getColor() == ChessColor.WHITE)) {
                        movableSquares.add(new Coords(targetX, targetY));
                    }
                }
            }
        }
        if (!captures) {
            for (int i = 0; i < 2; i++) {
                int targetY = y + (i + 1) * dy[i];
                if (x >= 0 && x < ChessArena.getPieceSize() * 8 &&
                        targetY >= 0 && targetY < ChessArena.getPieceSize() * 8) {
                    BoardSquare destinationSquare = squareMatrixRef[targetY
                            / ChessArena.getPieceSize()][x / ChessArena.getPieceSize()];
                    if (destinationSquare.getPieceColour() == ChessColor.EMPTY) {
                        movableSquares.add(new Coords(x, targetY));
                    }
                    if (pawn.isFirstMoveMade()
                            || (destinationSquare.getPieceColour() != ChessColor.EMPTY)) {
                        break;
                    }
                }
            }
        }
        return movableSquares;
    }

    public void changeAllCapturable(
            ArrayList<Coords> capturables, ChessColor color, int zeroForMinusElsePlus
    ) {
        for (Coords coord : capturables) {
            if (zeroForMinusElsePlus != 0) {
                if (color == ChessColor.WHITE) {
                    squareMatrixRef[coord.getY() / ChessArena.getPieceSize()][coord.getX()
                            / ChessArena.getPieceSize()].increaseWhiteCapturable();
                } else if (color == ChessColor.BLACK) {
                    squareMatrixRef[coord.getY() / ChessArena.getPieceSize()][coord.getX()
                            / ChessArena.getPieceSize()].increaseBlackCapturable();
                }
            } else {
                if (color == ChessColor.WHITE) {
                    squareMatrixRef[coord.getY() / ChessArena.getPieceSize()][coord.getX()
                            / ChessArena.getPieceSize()].decreaseWhiteCapturable();
                } else if (color == ChessColor.BLACK) {
                    squareMatrixRef[coord.getY() / ChessArena.getPieceSize()][coord.getX()
                            / ChessArena.getPieceSize()].decreaseBlackCapturable();
                }
            }
        }
    }

    public void playSound(Effect soundType) {
        sounds.playEffect(soundType);
    }

    public void resignGame() {
        if (!gameEnded) {
            gameEnded = true;
            gameStatus = Status.RESIGN;
            if (mode == Mode.WHITE_PLACE || mode == Mode.WHITE_SELECT) {
                winner = ChessColor.BLACK;
            } else {
                winner = ChessColor.WHITE;
            }
            if (caller != null) {
                caller.updateStatus();
            }
        }
    }

    public void drawGame() {
        if (!gameEnded) {
            String reply = requestDraw();
            if (Objects.equals(reply, "YES")) {
                gameEnded = true;
                gameStatus = Status.DRAW;
                if (caller != null) {
                    caller.updateStatus();
                }
            }
        }
    }

    public String requestDraw() {
        javax.swing.JDialog dialog = new javax.swing.JDialog(
                (java.awt.Frame) null, "Accept draw?", true
        );
        dialog.setLayout(new java.awt.GridLayout(1, 2));
        dialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);

        String[] options = { "YES", "NO" };
        javax.swing.JButton[] buttons = new javax.swing.JButton[options.length];
        AtomicReference<String> answer = new AtomicReference<>(""); // Store the selected piece

        for (int i = 0; i < options.length; i++) {
            buttons[i] = new javax.swing.JButton(options[i]);
            final String reply = options[i];
            buttons[i].addActionListener(e -> {
                switch (reply) {
                    case "NO":
                        answer.set("NO");
                        break;
                    case "YES":
                        answer.set("YES");
                        break;
                    default:
                        answer.set("NO");
                        break;
                }
                dialog.dispose(); // Close the dialog after a selection
            });
            dialog.add(buttons[i]);
        }

        // Configure and display the dialog
        dialog.setSize(400, 100);
        dialog.setLocationRelativeTo(null); // Center on screen
        dialog.setVisible(true);

        return answer.get();
    }

    public SoundEffects getSounds() {
        return sounds;
    }

    public void setSounds(SoundEffects sounds) {
        this.sounds = sounds;
    }

    public ChessArena getCaller() {
        return caller;
    }

    public void setCaller(ChessArena caller) {
        this.caller = caller;
    }

    public boolean isKingInCheckSoundPlayed() {
        return kingInCheckSoundPlayed;
    }

    public void setKingInCheckSoundPlayed(boolean kingInCheckSoundPlayed) {
        this.kingInCheckSoundPlayed = kingInCheckSoundPlayed;
    }

    public Clip getClip() {
        return clip;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public ChessColor getWinner() {
        return winner;
    }

    public void setWinner(ChessColor winner) {
        this.winner = winner;
    }

    public void setBlackEval(int blackEval) {
        this.blackEval = blackEval;
    }

    public void setWhiteEval(int whiteEval) {
        this.whiteEval = whiteEval;
    }

    public Status getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(Status gameStatus) {
        this.gameStatus = gameStatus;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(boolean gameEnded) {
        this.gameEnded = gameEnded;
    }

    public void setKingInCheck(King kingInCheck) {
        this.kingInCheck = kingInCheck;
    }

    public King getWhiteKingRef() {
        return whiteKingRef;
    }

    public King getBlackKingRef() {
        return blackKingRef;
    }

    public void setPieceSelected(Piece pieceSelected) {
        this.pieceSelected = pieceSelected;
    }

    public boolean isCheckMate() {
        return checkMate;
    }

    public void setCheckMate(boolean checkMate) {
        this.checkMate = checkMate;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Piece[][] getPieceMatrixRef() {
        return pieceMatrixRef;
    }

    public void setPieceMatrixRef(Piece[][] pieceMatrixRef) {
        this.pieceMatrixRef = pieceMatrixRef;
    }

    public BoardSquare[][] getSquareMatrixRef() {
        return squareMatrixRef;
    }

    public void setSquareMatrixRef(BoardSquare[][] squareMatrixRef) {
        this.squareMatrixRef = squareMatrixRef;
    }
}
