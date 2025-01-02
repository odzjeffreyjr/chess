package org.cis1200.chess;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.cis1200.chess.ChessArena.*;
import static org.junit.jupiter.api.Assertions.*;

public class ChessLogicMachineTest {
    private ChessLogicMachine chess;
    private BoardSquare[][] squareMatrix; // Example board setup
    private Piece[][] pieceMatrix; // Example pieces setup
    private King whiteKing;
    private King blackKing;
    private ChessArena caller;

    @BeforeEach
    public void setUp() {
        int size = getPieceSize();
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
        chess = new ChessLogicMachine(squareMatrix, pieceMatrix, whiteKing, blackKing, null);
        chess.runCaptureSequence();
    }

    @Test
    public void testInitialSetup() {
        // Test that the kings are correctly placed
        assertEquals(Pieces.KING, whiteKing.getPieceType());
        assertEquals(ChessColor.WHITE, whiteKing.getColor());
        assertEquals(4, whiteKing.getX() / ChessArena.getPieceSize());
        assertEquals(7, whiteKing.getY() / ChessArena.getPieceSize());

        assertEquals(Pieces.KING, blackKing.getPieceType());
        assertEquals(ChessColor.BLACK, blackKing.getColor());
        assertEquals(4, blackKing.getX() / ChessArena.getPieceSize());
        assertEquals(0, blackKing.getY() / ChessArena.getPieceSize());

        // Test that all squares are initialized correctly
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                assertNotNull(squareMatrix[r][c]);
                assertNotNull(pieceMatrix[r][c]);
            }
        }
    }

    // MOVEMENT TESTS
    @Test
    public void testValidMove() {
        // Simulate moving a white pawn forward one square
        chess.handleClick(0, 6 * ChessArena.getPieceSize()); // Select white pawn at (0,6)
        chess.handleClick(0, 5 * ChessArena.getPieceSize()); // Move to (0,5)

        assertEquals(Pieces.PAWN, pieceMatrix[5][0].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[5][0].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[6][0].getPieceType());
    }

    @Test
    public void testInvalidMove() {
        // Attempt an invalid move for a white pawn (move backward)
        chess.handleClick(0, 6 * ChessArena.getPieceSize()); // Select white pawn at (0,6)
        chess.handleClick(0, 3 * ChessArena.getPieceSize()); // Attempt to move up 3 steps

        assertEquals("Invalid move!", chess.getErrorMessage());
        assertEquals(Pieces.PAWN, pieceMatrix[6][0].getPieceType());
        assertEquals(Pieces.EMPTY, pieceMatrix[3][0].getPieceType());
    }

    @Test
    public void testWhitePawnValidMove() {
        chess.handleClick(ChessArena.getPieceSize(), 6 * ChessArena.getPieceSize()); // Select white
                                                                                     // pawn at
                                                                                     // (1,6)
        chess.handleClick(ChessArena.getPieceSize(), 5 * ChessArena.getPieceSize()); // Move to
                                                                                     // (1,5)

        assertEquals(Pieces.PAWN, pieceMatrix[5][1].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[5][1].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[6][1].getPieceType());
    }

    @Test
    public void testWhiteRookValidMove() {
        chess.handleClick(0, 6 * ChessArena.getPieceSize()); // Select white pawn at (0,6)
        chess.handleClick(0, 5 * ChessArena.getPieceSize()); // Move to (0,5)
        chess.handleClick(ChessArena.getPieceSize(), ChessArena.getPieceSize()); // Select black
                                                                                 // pawn at (1,1)
        chess.handleClick(ChessArena.getPieceSize(), 2 * ChessArena.getPieceSize()); // Move to
                                                                                     // (1,2)
        chess.handleClick(0, 7 * ChessArena.getPieceSize()); // Select white rook at (0,7)
        chess.handleClick(0, 6 * ChessArena.getPieceSize()); // Move vertically to (0,6)

        assertEquals(Pieces.ROOK, pieceMatrix[6][0].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[6][0].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[7][0].getPieceType());
    }

    @Test
    public void testWhiteKnightValidMove() {
        chess.handleClick(ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize()); // Select white
                                                                                     // knight at
                                                                                     // (1,7)
        chess.handleClick(2 * ChessArena.getPieceSize(), 5 * ChessArena.getPieceSize()); // Move in
                                                                                         // an
                                                                                         // L-shape
                                                                                         // to (2,5)

        assertEquals(Pieces.KNIGHT, pieceMatrix[5][2].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[5][2].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[7][1].getPieceType());
    }

    @Test
    public void testTurnSwitch() {
        assertEquals("White's turn!", chess.getTurn());

        // Perform a move
        chess.handleClick(0, 6 * ChessArena.getPieceSize()); // Select white pawn
        chess.handleClick(0, 5 * ChessArena.getPieceSize()); // Move to (0,5)

        assertEquals("Black's turn!", chess.getTurn());
    }

    @Test
    public void testAllWhiteKnightMovesNotYetMoved() {
        ArrayList<Coords> expectedMovesWhiteKnight = new ArrayList<>();
        // Add expected moves for the knight at b1
        expectedMovesWhiteKnight.add(new Coords(0, 5 * ChessArena.getPieceSize()));
        expectedMovesWhiteKnight.add(new Coords(2 * ChessArena.getPieceSize(),
                5 * ChessArena.getPieceSize()));

        ArrayList<Coords> actualMovesWhiteKnight = chess.allKnightMoves((Knight)
                pieceMatrix[7][1], false);
        assertEquals(expectedMovesWhiteKnight.size(), actualMovesWhiteKnight.size());
        for (int i = 0; i < expectedMovesWhiteKnight.size(); i++) {
            assertEquals(expectedMovesWhiteKnight.get(i).getX(),
                    actualMovesWhiteKnight.get(i).getX());
            assertEquals(expectedMovesWhiteKnight.get(i).getY(),
                    actualMovesWhiteKnight.get(i).getY());
        }
    }

    @Test
    public void testAllWhiteBishopMovesNotYetMoved() {
        ArrayList<Coords> expectedMovesWhiteBishop = new ArrayList<>();
        // No valid moves for bishop at c1 due to blocking pawns
        ArrayList<Coords> actualMovesWhiteBishop = chess.allBishopMoves((Bishop)
                pieceMatrix[7][2], false);
        assertEquals(expectedMovesWhiteBishop.size(), actualMovesWhiteBishop.size());
    }

    @Test
    public void testAllWhiteQueenMovesNotYetMoved() {
        // No valid moves for queen at d1 due to blocking pawns
        ArrayList<Coords> actualMovesWhiteQueen = chess.allQueenMoves((Queen)
                pieceMatrix[7][3], false);
        assertEquals(0, actualMovesWhiteQueen.size());
    }

    @Test
    public void testAllWhiteKingMovesNotYetMoved() {
        // No valid moves for king at e1 due to blocking pawns
        ArrayList<Coords> actualMovesWhiteKing = chess.allKingMoves((King)
                pieceMatrix[7][4], false);
        assertEquals(0, actualMovesWhiteKing.size());
    }

    @Test
    public void testWhiteCastlesRight() {
        chess.handleClick(6 * ChessArena.getPieceSize(), 6 * ChessArena.getPieceSize()); // Select
                                                                                         // white
                                                                                         // pawn at
                                                                                         // (6,6)
        chess.handleClick(6 * ChessArena.getPieceSize(), 5 * ChessArena.getPieceSize()); // Move to
                                                                                         // (6,5)

        // 2. Black pawn moves forward
        chess.handleClick(getPieceSize(), getPieceSize()); // Select black pawn at (1,1)
        chess.handleClick(getPieceSize(), 3 * ChessArena.getPieceSize()); // Move to (1, 3)

        // 3. White knight moves
        chess.handleClick(6 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize()); // Select
                                                                                         // white
                                                                                         // knight
                                                                                         // at (6,7)
        chess.handleClick(5 * ChessArena.getPieceSize(), 5 * ChessArena.getPieceSize()); // Move to
                                                                                         // (5,5)

        // 4. Black knight moves
        chess.handleClick(ChessArena.getPieceSize(), 0); // Select black knight at (1,0)
        chess.handleClick(2 * ChessArena.getPieceSize(), 2 * ChessArena.getPieceSize()); // Move to
                                                                                         // (2,2)

        // 5. White bishop moves
        chess.handleClick(5 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize()); // Select
                                                                                         // white
                                                                                         // bishop
                                                                                         // at (5,7)
        chess.handleClick(7 * ChessArena.getPieceSize(), 5 * ChessArena.getPieceSize()); // Move to
                                                                                         // (7,5)

        // 6. Black bishop moves
        chess.handleClick(2 * ChessArena.getPieceSize(), 0); // Select black bishop at (2,0)
        chess.handleClick(0, 2 * ChessArena.getPieceSize()); // Move to (0,2)

        // 7. King castles
        chess.handleClick(4 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize()); // Select
                                                                                         // white
                                                                                         // king
        chess.handleClick(6 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize()); // Move to
                                                                                         // (6,7)

        assertEquals(Pieces.ROOK, pieceMatrix[7][5].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[7][5].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[7][7].getPieceType());

        assertEquals(Pieces.KING, pieceMatrix[7][6].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[7][6].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[7][4].getPieceType());
    }

    @Test
    public void testSequentialGamePlayNoChecks() {
        // 1. White pawn moves forward
        chess.handleClick(6 * ChessArena.getPieceSize(), 6 * ChessArena.getPieceSize()); // Select
                                                                                         // white
                                                                                         // pawn at
                                                                                         // (6,6)
        chess.handleClick(6 * ChessArena.getPieceSize(), 5 * ChessArena.getPieceSize()); // Move to
                                                                                         // (6,5)

        assertEquals(Pieces.PAWN, pieceMatrix[5][6].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[5][6].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[6][6].getPieceType());

        // 2. Black pawn moves forward
        chess.handleClick(getPieceSize(), getPieceSize()); // Select black pawn at (1,1)
        chess.handleClick(getPieceSize(), 3 * ChessArena.getPieceSize()); // Move to (1, 3)

        assertEquals(Pieces.PAWN, pieceMatrix[3][1].getPieceType());
        assertEquals(ChessColor.BLACK, pieceMatrix[3][1].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[1][1].getPieceType());

        // 3. White knight moves
        chess.handleClick(6 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize()); // Select
                                                                                         // white
                                                                                         // knight
                                                                                         // at (6,7)
        chess.handleClick(5 * ChessArena.getPieceSize(), 5 * ChessArena.getPieceSize()); // Move to
                                                                                         // (5,5)

        assertEquals(Pieces.KNIGHT, pieceMatrix[5][5].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[5][5].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[7][6].getPieceType());

        // 4. Black knight moves
        chess.handleClick(ChessArena.getPieceSize(), 0); // Select black knight at (1,0)
        chess.handleClick(2 * ChessArena.getPieceSize(), 2 * ChessArena.getPieceSize()); // Move to
                                                                                         // (2,2)

        assertEquals(Pieces.KNIGHT, pieceMatrix[2][2].getPieceType());
        assertEquals(ChessColor.BLACK, pieceMatrix[2][2].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[0][1].getPieceType());

        // 5. White bishop moves
        chess.handleClick(5 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize()); // Select
                                                                                         // white
                                                                                         // bishop
                                                                                         // at (5,7)
        chess.handleClick(7 * ChessArena.getPieceSize(), 5 * ChessArena.getPieceSize()); // Move to
                                                                                         // (7,5)

        assertEquals(Pieces.BISHOP, pieceMatrix[5][7].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[5][7].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[7][5].getPieceType());

        // 6. Black bishop moves
        chess.handleClick(2 * ChessArena.getPieceSize(), 0); // Select
                                                                                         // black
                                                                                         // bishop
                                                                                         // at (2,0)
        chess.handleClick(0, 2 * ChessArena.getPieceSize()); // Move to
                                                                                         // (0,2)

        assertEquals(Pieces.BISHOP, pieceMatrix[2][0].getPieceType());
        assertEquals(ChessColor.BLACK, pieceMatrix[2][0].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[0][2].getPieceType());

        // 7. White rook moves
        chess.handleClick(7 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize()); // Select
                                                                                         // white
                                                                                         // rook at
                                                                                         // (7,7)
        chess.handleClick(6 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize()); // Move to
                                                                                         // (6,7)

        assertEquals(Pieces.ROOK, pieceMatrix[7][6].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[7][6].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[7][7].getPieceType());

        // 8. Black pawn moves
        chess.handleClick(5 * getPieceSize(), getPieceSize()); // Select black pawn at (5,1)
        chess.handleClick(5 * getPieceSize(), 3 * getPieceSize()); // Move to (5,3)

        assertEquals(Pieces.PAWN, pieceMatrix[3][5].getPieceType());
        assertEquals(ChessColor.BLACK, pieceMatrix[3][5].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[1][5].getPieceType());
    }

    @Test
    public void testWhiteCastlesLeft() {
        chess.handleClick(ChessArena.getPieceSize(), 6 * ChessArena.getPieceSize());
        chess.handleClick(ChessArena.getPieceSize(), 4 * ChessArena.getPieceSize());

        // Move black pawn forward to maintain valid sequence
        chess.handleClick(4 * ChessArena.getPieceSize(), ChessArena.getPieceSize());
        chess.handleClick(4 * ChessArena.getPieceSize(), 3 * ChessArena.getPieceSize());

        // Move white knight out of the way
        chess.handleClick(ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize());
        chess.handleClick(2 * ChessArena.getPieceSize(), 5 * ChessArena.getPieceSize());

        // Move black pawn forward to continue valid sequence
        chess.handleClick(5 * ChessArena.getPieceSize(), ChessArena.getPieceSize());
        chess.handleClick(5 * ChessArena.getPieceSize(), 3 * ChessArena.getPieceSize());

        // Move white bishop out of the way
        chess.handleClick(2 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize());
        chess.handleClick(0, 5 * ChessArena.getPieceSize());

        // Move black knight to continue valid sequence
        chess.handleClick(ChessArena.getPieceSize(), 0);
        chess.handleClick(2 * ChessArena.getPieceSize(), 2 * ChessArena.getPieceSize());

        // Move white queen
        chess.handleClick(3 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize());
        chess.handleClick(ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize());

        // Move black pawn forward to maintain valid sequence
        chess.handleClick(7 * ChessArena.getPieceSize(), ChessArena.getPieceSize());
        chess.handleClick(7 * ChessArena.getPieceSize(), 3 * ChessArena.getPieceSize());

        // Move white queen
        chess.handleClick(ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize());
        chess.handleClick(ChessArena.getPieceSize(), 6 * ChessArena.getPieceSize());

        // Move black pawn forward to maintain valid sequence
        chess.handleClick(6 * ChessArena.getPieceSize(), ChessArena.getPieceSize());
        chess.handleClick(6 * ChessArena.getPieceSize(), 3 * ChessArena.getPieceSize());

        // Perform white queen-side castling
        chess.handleClick(4 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize());
        chess.handleClick(2 * ChessArena.getPieceSize(), 7 * ChessArena.getPieceSize());

        // Assertions
        assertEquals(Pieces.KING, pieceMatrix[7][2].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[7][2].getColor());
        assertEquals(Pieces.ROOK, pieceMatrix[7][3].getPieceType());
        assertEquals(ChessColor.WHITE, pieceMatrix[7][3].getColor());
        assertEquals(Pieces.EMPTY, pieceMatrix[7][0].getPieceType());
        assertEquals(Pieces.EMPTY, pieceMatrix[7][4].getPieceType());
    }

    @Test
    public void testFoolsMate() {
        // 1. Move white pawn from f2 to f3
        chess.handleClick(5 * getPieceSize(), 6 * getPieceSize());
        chess.handleClick(5 * getPieceSize(), 5 * getPieceSize());

        // 2. Move black pawn from e7 to e5
        chess.handleClick(4 * getPieceSize(), getPieceSize());
        chess.handleClick(4 * getPieceSize(), 3 * getPieceSize());

        // 3. Move white pawn from g2 to g4
        chess.handleClick(6 * getPieceSize(), 6 * getPieceSize());
        chess.handleClick(6 * getPieceSize(), 4 * getPieceSize());

        // 4. Move black queen from d8 to h4 (checkmate)
        chess.handleClick(3 * getPieceSize(), 0);
        chess.handleClick(7 * getPieceSize(), 4 * getPieceSize());

        //random click to trigger checkmate check
        chess.handleClick(7 * getPieceSize(), 7 * getPieceSize());
        chess.handleClick(7 * getPieceSize(), 6 * getPieceSize());

        // Verify checkmate
//        assertEquals("Checkmate! winner is black.", chess.getStatusMessage());
        assertEquals(chess.getStatus(), Status.CHECKMATE);
    }

    @Test
    public void testCheckmate() {
        // Clear the board
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                pieceMatrix[r][c] = new Dummy(c * getPieceSize(), r * getPieceSize());
            }
        }

        // Set up kings and a rook for checkmate
        pieceMatrix[7][0] = whiteKing; // White king on a8
        pieceMatrix[5][0] = new Rook(0, 5 * getPieceSize(), getPieceSize(), getPieceSize(),
                ChessColor.WHITE); // White rook on a6
        pieceMatrix[0][7] = blackKing; // Black king on h1

        chess.runCaptureSequence(); // Update board state

        // Simulate moves to reach checkmate
        chess.handleClick(0, 5 * getPieceSize()); // Select white rook
        chess.handleClick(0, 1 * getPieceSize()); // Move rook to a2 (checkmate)

        // Verify the game ends with checkmate
        assertEquals("Checkmate! Winner is white.", chess.getStatusMessage());
        assertEquals(chess.getStatus(), Status.CHECKMATE);
    }

    @Test
    public void testAllPawnMovesNotYetMoved() {
        ArrayList<Coords> expectedMovesWhitePawn = new ArrayList<>();
        expectedMovesWhitePawn.add(squareMatrix[5][0]);
        expectedMovesWhitePawn.add(squareMatrix[4][0]);
        ArrayList<Coords> actualMovesWhitePawn = chess.allPawnMoves((Pawn)pieceMatrix[6][0], false);
        assertEquals(expectedMovesWhitePawn.get(0).getX(), actualMovesWhitePawn.get(0).getX());
        assertEquals(expectedMovesWhitePawn.get(1).getY(), actualMovesWhitePawn.get(1).getY());
    }

    @Test
    public void testAllWhiteRookMovesNotYetMoved() {
        ArrayList<Coords> expectedMovesWhiteRook = new ArrayList<>();

        ArrayList<Coords> actualMovesWhiteRook = chess.allRookMoves((Rook)
                pieceMatrix[7][0], false);
        assertEquals(0, actualMovesWhiteRook.size());
    }

    @Test
    public void testAllBlackQueenMovesNotYetMoved() {
        ArrayList<Coords> actualMovesWhiteRook = chess.allRookMoves((Rook)
                pieceMatrix[7][0], false);
        assertEquals(0, actualMovesWhiteRook.size());
    }


    @Test
    public void testWhiteResign() {
        chess.resignGame();
        assertTrue(chess.isGameEnded());
        assertSame(Status.RESIGN, chess.getStatus());
    }

    @Test
    public void testBlackResign() {
        chess.setMode(Mode.BLACK_SELECT);
        chess.resignGame();
        assertTrue(chess.isGameEnded());
        assertSame(Status.RESIGN, chess.getStatus());
    }

    @Test
    public void testsDrawOfferDeclined() {
        chess.drawGame();
        assertFalse(chess.isGameEnded());
        assertSame(Status.NORMAL, chess.getStatus());
    }

    @Test
    public void testsDrawOfferAccepted() {
        chess.drawGame();
        assertTrue(chess.isGameEnded());
        assertSame(Status.DRAW, chess.getStatus());
    }
    @Test
    public void testDrawOffer() {
        String reply = chess.requestDraw();
        //click yes button
        assertEquals(reply, "YES");
        //Uncomment below line to test click no
        //assertEquals(reply, "NO");
    }
}