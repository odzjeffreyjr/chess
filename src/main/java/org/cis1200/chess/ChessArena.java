package org.cis1200.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The ChessArena class represents a graphical chess board where players
 * can interactively play chess games. It extends JPanel, providing an interface
 * to display the current state of a chess game and handle user interactions
 * such as mouse clicks to make moves.
 *
 * This class manages the chess board, the pieces on the board, and integrates
 * with a ChessLogicMachine for game logic processing. It also updates status,
 * mode, and error messages for display in a user interface.
 */
public class ChessArena extends JPanel {
    // state of the game
    private BoardSquare[][] squareMatrix;
    private Piece[][] pieceMatrix;
    private final JLabel status;
    private final JLabel mode;
    private final JLabel error;
    private final EvalBarPanel eval;
    private ChessLogicMachine chess;
    private PlayerMode playerMode = PlayerMode.PVP;

    // game constants
    public static final int BOARD_WIDTH = 640;
    public static final int BOARD_HEIGHT = 640;

    private King whiteKing;
    private King blackKing;
    private Board board;

    /**
     * Constructs a new ChessArena game instance with specified initial status,
     * mode, error labels,
     * and evaluation bar panel. This sets up the chess game logic and user
     * interface,
     * including mouse click interaction for playing the game.
     *
     * @param statusInit   the JLabel to display the current status of the game
     * @param modeInit     the JLabel to display the current mode of the game
     * @param errorInit    the JLabel to display any errors or messages during the game
     * @param evalBarPanel the panel that visually represents the evaluation of the game state
     */
    public ChessArena(
            JLabel statusInit, JLabel modeInit, JLabel errorInit, EvalBarPanel evalBarPanel
    ) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Enable keyboard focus on the court area. When this component has the
        // keyboard focus, key events are handled by its key listener.
        setFocusable(true);

        // initializes model for the game
        chess = new ChessLogicMachine(squareMatrix, pieceMatrix, whiteKing, blackKing, this);
        status = statusInit; // initializes the status JLabel
        mode = modeInit; // initializes the mode JLabel
        error = errorInit; // initializes the error JLabel
        eval = evalBarPanel;

        /*
         * Listens for mouseClicks. Updates the model, then updates the game
         * board based off of the updated model.
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                int x = rawToBoardCoord(p.x);
                int y = rawToBoardCoord(p.y);
                // updates the model given the coordinates of the mouseclick
                chess.handleClick(x, y);
                // System.out.println("Click at " + x + ", " + y);

                updateStatus(); // updates the status JLabel
                updateError();
                updateMode();
                updateEvalView();
                repaint(); // repaints the game board
            }
        });
    }

    private void updateEvalView() {
        eval.updateEvalBar(chess.getWhiteEval(), chess.getBlackEval());
    }

    public void updateStatus() {
        status.setText(chess.getStatusMessage());
    }

    private void updateError() {
        error.setText(chess.getErrorMessage());
    }

    private void updateMode() {
        mode.setText(chess.getTurn());
    }

    public void reset() {
        int size = getPieceSize();
        board = new Board(BOARD_WIDTH, BOARD_HEIGHT);
        squareMatrix = new BoardSquare[8][8];
        pieceMatrix = new Piece[8][8];

        pieceMatrix[0][0] = new Rook(0, 0, size, size, ChessColor.BLACK);
        pieceMatrix[0][1] = new Knight(size, 0, size, size, ChessColor.BLACK);
        pieceMatrix[0][2] = new Bishop(2 * size, 0, size, size, ChessColor.BLACK);
        pieceMatrix[0][3] = new Queen(3 * size, 0, size, size, ChessColor.BLACK);
        pieceMatrix[0][4] = blackKing = new King(4 * size, 0, size, size, ChessColor.BLACK);
        pieceMatrix[0][5] = new Bishop(5 * size, 0, size, size, ChessColor.BLACK);
        pieceMatrix[0][6] = new Knight(6 * size, 0, size, size, ChessColor.BLACK);
        pieceMatrix[0][7] = new Rook(7 * size, 0, size, size, ChessColor.BLACK);

        pieceMatrix[7][0] = new Rook(0, 7 * size, size, size, ChessColor.WHITE);
        pieceMatrix[7][1] = new Knight(size, 7 * size, size, size, ChessColor.WHITE);
        pieceMatrix[7][2] = new Bishop(2 * size, 7 * size, size, size, ChessColor.WHITE);
        pieceMatrix[7][3] = new Queen(3 * size, 7 * size, size, size, ChessColor.WHITE);
        pieceMatrix[7][4] = whiteKing = new King(4 * size, 7 * size, size, size, ChessColor.WHITE);
        pieceMatrix[7][5] = new Bishop(5 * size, 7 * size, size, size, ChessColor.WHITE);
        pieceMatrix[7][6] = new Knight(6 * size, 7 * size, size, size, ChessColor.WHITE);
        pieceMatrix[7][7] = new Rook(7 * size, 7 * size, size, size, ChessColor.WHITE);

        for (int j = 0; j < 8; j++) {
            pieceMatrix[1][j] = new Pawn(j * size, size, size, size, ChessColor.BLACK);
        }
        for (int j = 0; j < 8; j++) {
            pieceMatrix[6][j] = new Pawn(j * size, 6 * size, size, size, ChessColor.WHITE);
        }
        for (int r = 2; r < 6; r++) {
            for (int c = 0; c < 8; c++) {
                pieceMatrix[r][c] = new Dummy(c * size, r * size);
            }
        }

        squareMatrix[0][0] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[0][1] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[0][2] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[0][3] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[0][4] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[0][5] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[0][6] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[7][0] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[7][1] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[7][2] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[7][3] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);
        squareMatrix[7][4] = new BoardSquare(0, 0, 0, 0, ChessColor.BLACK);

        // Initialize all squares based on initial piece positions
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                ChessColor pieceColor;

                // Assign piece color based on initial setup
                if (r == 0) { // Black major pieces (first row)
                    pieceColor = ChessColor.BLACK;
                } else if (r == 1) { // Black pawns (second row)
                    pieceColor = ChessColor.BLACK;
                } else if (r == 6) { // White pawns (seventh row)
                    pieceColor = ChessColor.WHITE;
                } else if (r == 7) { // White major pieces (eighth row)
                    pieceColor = ChessColor.WHITE;
                } else {
                    pieceColor = ChessColor.EMPTY; // Empty squares
                }

                // Assign the square in the matrix with capturable counts set to 0
                squareMatrix[r][c] = new BoardSquare(
                        c * size, r * size,
                        0, 0, pieceColor
                );
            }
        }
        chess = new ChessLogicMachine(squareMatrix, pieceMatrix, whiteKing, blackKing, this);
        chess.runCaptureSequence();
        updateStatus();
        updateError();
        updateMode();
        updateEvalView();
        repaint();
    }

    public void changePlayerMode() {
        if (playerMode == PlayerMode.BOT) {
            playerMode = PlayerMode.PVP;
        } else {
            playerMode = PlayerMode.BOT;
        }
        reset();
    };

    public void animatePieceMovement(Piece piece, BoardSquare start, BoardSquare end) {
        // Get start and end coordinates in pixels
        int startX = start.getX();
        int startY = start.getY();
        int endX = end.getX();
        int endY = end.getY();

        int animationDuration = 75;
        int refreshRate = 25;

        int totalSteps = animationDuration / refreshRate;

        Timer timer = new Timer(refreshRate, new ActionListener() {
            int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                double progress = (double) step / totalSteps;
                int currentX = (int) (startX + progress * (endX - startX));
                int currentY = (int) (startY + progress * (endY - startY));
                piece.setPosition(new Coords(currentX, currentY));
                repaint();

                // Stop animation once completed
                if (step >= totalSteps) {
                    ((Timer) e.getSource()).stop();

                    // Finalize move
                    piece.setPosition(end);
                    repaint();
                }
            }
        });
        timer.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        // super.paintComponent(g);
        board.draw(g);
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                pieceMatrix[r][c].draw(g);
            }
        }
        if (chess.getPieceSelected() != null) {
            Graphics2D g2d = (Graphics2D) g;

            // Set the transparency level (0.0f for fully transparent, 1.0f for fully
            // opaque)
            float alpha = 0.4f;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            // Set the color for the square
            g2d.setColor(Color.orange);

            // Draw the filled square
            g2d.fillRect(
                    chess.getPieceSelected().getX(), chess.getPieceSelected().getY(),
                    ChessArena.getPieceSize(), ChessArena.getPieceSize()
            );
        }
        if (chess.getStatus() == Status.CHECKMATE || chess.getStatus() == Status.CHECK) {
            Graphics2D g2d = (Graphics2D) g;

            // Set the transparency level (0.0f for fully transparent, 1.0f for fully
            // opaque)
            float alpha = 0.4f;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            // Set the color for the square
            g2d.setColor(Color.RED);

            // Draw the filled square
            g2d.fillRect(
                    chess.getKingInCheck().getX(), chess.getKingInCheck().getY(),
                    ChessArena.getPieceSize(), ChessArena.getPieceSize()
            );
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    }

    public static int rawToBoardCoord(int number) {
        int divisor = getPieceSize();
        int result = (number / divisor) * divisor;
        if (result >= BOARD_HEIGHT) {
            return (7 * getPieceSize());
        } else {
            return result;
        }
    }

    public static int getPieceSize() {
        return (int) BOARD_HEIGHT / 8;
    }

    public void resignGame() {
        chess.resignGame();
    }

    public void drawGame() {
        chess.drawGame();
    }
}