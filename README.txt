=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================
    1. 2D Arrays
    (pieceMatrixRef) to represent the chess pieces and their respective states. Each element of the array corresponds to
     a chess piece on the board and holds a reference to the piece occupying that square or
     Dummy if the square is empty.
    (squareMatrixRef) to represent the effects that pieces have on their surroundings. This matrix stores how many
    pieces can be captured on a square, and which piece is on which square. Useful for finding check,
    checkmate, stalemate, and finding valid moves.
    2. Complex game logic
        The game implements advanced chess rules, including:
        Check and Checkmate: Logic to determine when a king is in check or checkmate.
        Pawn Promotion: Pawns are promoted when they reach the opposite end of the board.
        Castling: A special king and rook move that requires both pieces to be unmoved and no squares between them are
         attacked.
        Valid moves for each unique piece: Thus ensures that each piece can move in a unique manner.
        Others: These and other considerations count for complex logic

    3. JUnit testable component
        The ChessLogicMachine will be the class that handles all of the logic when a square is clicked. This component
        handles core game state and so I will write comprehensive unit tests for this class.

    4. Inheritance and subtyping
        A base Piece class is implemented, from which specific piece classes like Pawn, Rook, Bishop, Knight, Queen, and
         King inherit. Each subclass overrides methods like validMove() and setPosition() to define piece-specific
         behavior.
        Also, Coords are unique and act as parents to the BoardSquare class. Coords can specify position
        while BoardSquares can store additional data like piece references and effect counts.

=========================
=: My Implementation :=
=========================
  - PieceInterface.java
    - Defines the interface for chess pieces, including methods for getting and setting the piece's position,
      checking if a move is valid, and drawing the piece on a graphical component.
  - Coords.java
    - Holds x and Y position coordinates for the board.
  - BoardSquare.java
    - Inherits from Coords.java to provide positionality
    - Represents a square on the chess board.
    - Contains information about the square's position, color, and whether it contains a piece.
    - Stores how many pieces can capture onto it. This si useful for checkamte, stalemate, check,
    and determining legal moves.
  - ChessArena.java
    - Creates and manages the chess arena GUI components.
    - Draws pieces, and handles animation
    - Initializes the chess board, pieces, and other UI elements.
    - Handles user input events for selecting and moving pieces.
    - Updates the game state and displays relevant messages to the players.
    - Handles button clicks and displaying game state.
  - ChessColor.java
    - Enumerated list of colors for the chess pieces.
    - Includes WHITE, BLACK, and NONE constants.
    - Used to represent the color of a chess piece or square.
  - ChessLogicMachine.java
    - All chess rules and mechanics are handled here.
    - Manages the logic behind the chess game, including handling moves, validating moves, updating the board state,
      detecting checkmates and stalemates, and managing the game flow.
    - Ensures that only valid moves are allowed and updates the board accordingly.
    - Keeps track of the current player's turn and switches turns appropriately.
    - Detects checkmate conditions and declares a winner when necessary.
    - Handles promotion of pawns to higher rank pieces when reaching the opposite end of the board.
  - Effect.java
    - Enum class for sounds played when a move is made
    - Includes checkmate, castling, move, and check
  - Pieces.java
    - ENUM class for types of pieces
    - Includes PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING.
    - Used to differentiate between different types of chess pieces.
  - PlayerMode.java
    - Enum class for modes of play vs computer of player vs player.
    - Unimplemented yet.
  - Status.java
    - Enum class for game states.
    - Includes CHECKMATE, STALEMATE, ONGOING, RESIGN, DRAW.
    - Used to keep track of the current state of the game and update the UI accordingly.
  - RunChess.java
    - Starts the game by creating a new instance of ChessArena and calling its init() method.
    - Sets up the layout of the chess board.
    - Initializes the game loop and event listeners for user interactions.
  - Square.java
    - Represents a square on the chess board.
    - Contains information about the square's position, color, and whether it contains a piece.
  - Move.java
    - Represents a single move made by a chess piece.
    - Contains information about the source and destination positions of the move.
    - Implements equality checks based on source and destination positions.
  - Board.java
    - Crates and manages the size of the chess board background image.
    - Handles resizing and scaling of the image to fit different screen sizes.
    - Provides methods for drawing the board onto a JPanel.
  - EvalBarPanel.java
    - Displays the evaluation bar on the right side of the chess board.
    - Shows the material advantage of each player and highlights the winning player.
    - Provides a visual representation of the game's progress and outcome.
  - Mode.java
    - Enum class for modes of play.
    - Includes BLACK_SELECT, WHITE_SELECT, WHITE_PLACE, BLACK_PLACE.
    - Used to switch between different modes of play in the game.
  - SoundEffects.java
    - Plays sound effects associated with various game events.
    - Loads and plays audio clips for different sounds, such as capturing a piece or making a move.
    - Uses the javax.sound.sampled package to handle audio playback.
  - Game.java
    - Main class responsible for initializing and starting the game.
  - Piece.java
    - It is the base class representing the basic identity of a chess piece on the board.
    - Defines common attributes like type, color, and position.
    - Is overriden by subclasses to provide unique movement behaviors and rules for each piece type and unique looks.
  - Knight.java
    - Represents the Knight piece in chess.
    - Extends the Piece class and provides specific behavior related to the Knight's movements and actions.
  - Pawn.java
    - Represents the Pawn piece in chess.
    - Extends the Piece class and provides specific behavior related to the Pawn's movements and actions.
  -King.java
    - Represents the King piece in chess.
    - Extends the Piece class and provides specific behavior related to the King's movements and actions.
  - Queen.java
    - Represents the Queen piece in chess.
    - Extends the Piece class and provides specific behavior related to the Queen's movements and actions.
  - Rook.java
    - Represents the Rook piece in chess.
    - Extends the Piece class and provides specific behavior related to the Rook's movements and actions.
  - Bishop.java
    - Represents the Bishop piece in chess.
    - Extends the Piece class and provides specific behavior related
  - Dummy.java
    - A dummy subclass of Piece that represents a placeholder or empty space on the board.
    - Does not draw and has empty color.
    - Helps avoid null pointer exception issues.
  - Bishop.java
    - Represents the Bishop piece in chess.
    - Extends the Piece class and provides specific behavior related to the Bishop's movements and actions.
  - ChessLogicMachineTest.java
    - Unit testing for ChessLogicMachine.java
    - Tests the validity of moves, captures, checkmate, stalemate, and other game logic.
    - Ensures that the game behaves as expected and detects errors early in development.

========================
=: External Resources :=
========================
  - https://en.m.wikipedia.org/wiki/File:Chess_Board.svg for the chess board
  - https://www.chess.com/forum/view/general/chessboard-sound-files?page=2 For the chess sounds
  - https://www.pngegg.com/en/search?q=chess For the chess pieces

========================
=: Game instructions  :=
========================

Jeffrey Chess is Jeffrey Oduman's implementation of the game "Chess" in Java.
The goal of this project was to implement chess using Java Swing.
This game intends to mimic the classic board game chess, without the extra computational complexity of modern chess engines.
This game is played between 2 players by simply passing and sharing a mouse or touchpad.

To run, simply compile and execute Game.java. The main method in Game.Java will start the game.

Once the game is running, you can click on a piece to select it, then click again to move it.
The game starts automatically in White's turn and then white and black must alternate turns.
The game cannot be rotated and so black will always be up and white will be down
The game prevents invalid chess moves and makes sure that all moves are legal according to the rules of chess.
En passant special move is not implemented for this version yet.

Game status:
1. The game will show whose turn it is currently.
2. The game will highlight the selected piece
3. The game will highlight a king in check or checkmate and will show a status message saying which king is in check.
4. The game will animate the movement of pieces after a successful move.
6. The game will allow players to resign or offer draws during their turn with draw and resign buttons at the top.
7. The game has an evaluation bar on the right hand side that evaluates the current state of the game based on material advantage.
8. The game will display a message indicating whether the game is ongoing, has been resigned, or has ended in a draw.
9. The game will show a message for stalemate or checkmate.
10. The game will state if a move is invalid or if attempting to capture aKing or if attempting to make an irrelevant move while one's king is still in check.
9. The game will show the users how much material advantage one has over the other.

Game end Conditions:
1. Checkmate: When one player's king is under attack from another player's pieces and there is no way for them to escape or defend themselves.
   In this case, the other player wins the game.
2. Stalemate: When a player cannot make any more legal moves but their king is not in check (i.e., they are not being attacked).
3. Draw: If neither player can win the game through normal means, such as checkmate or stalemate, they must agree to a draw
as the game will not determine the impossibility of checkmate automatically.
4. The players can agree to a draw at anytime by clicking the "Draw" button. One can only offer a draw on their turn
of moving. In this case, the other player must accept or decline the draw.
5. The game ends when either player clicks the "Resign" button on their turn and the opponent wins.


Known Bugs:
1. The detection of checkmate may not be perfect and may sometimes incorrectly identify a position as checkmate even though it isn't.
2. Highlighting colours for visual cues may not be perfectly synchronised when a check or checkmate happens
and may take an extra click on the board before the highlighting colour changes correctly.
3. Edge cases when piece may refuse to make valid moves.
4. Animations may be clunky and board reset and response are quite slow due to iterative drawing of graphics for each piece.

Future additions:
1. Implementing Undo Functionality that allows users to undo their last move if the other user accepts the request.
2. Allowing the board to rotate at each turn so that both players have equal visibility of the board.
3. Adding a timer to end the game if neither player resigns nor agrees to a draw within a certain time limit.
4. Implementing artificial intelligence for computer opponents.
5. Adding drag capability for pieces so that players can drag pieces around the board instead of clicking on them.
6. En-passant move
7. Optimising code
8. Highlighting possible moves with arrows.
9. Allowing premoves