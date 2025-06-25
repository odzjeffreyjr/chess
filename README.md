Jeffrey Chess

---

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

---

========================
=: Game status :=
========================
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

---

========================
=: Known bugs :=
========================
1. The detection of checkmate may not be perfect and may sometimes incorrectly identify a position as checkmate even though it isn't.
2. Highlighting colours for visual cues may not be perfectly synchronised when a check or checkmate happens
   and may take an extra click on the board before the highlighting colour changes correctly.
3. Edge cases when piece may refuse to make valid moves.
4. Animations may be clunky and board reset and response are quite slow due to iterative drawing of graphics for each piece.

---

========================
=: Future additions :=
========================
1. Implementing Undo Functionality that allows users to undo their last move if the other user accepts the request.
2. Allowing the board to rotate at each turn so that both players have equal visibility of the board.
3. Adding a timer to end the game if neither player resigns nor agrees to a draw within a certain time limit.
4. Implementing artificial intelligence for computer opponents.
5. Adding drag capability for pieces so that players can drag pieces around the board instead of clicking on them.
6. En-passant move
7. Optimising code
8. Highlighting possible moves with arrows.
9. Allowing premoves

---

========================
=: External Resources :=
========================
- https://en.m.wikipedia.org/wiki/File:Chess_Board.svg for the chess board
- https://www.chess.com/forum/view/general/chessboard-sound-files?page=2 For the chess sounds
- https://www.pngegg.com/en/search?q=chess For the chess pieces
