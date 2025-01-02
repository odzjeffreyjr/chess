package org.cis1200.chess;

import javax.swing.*;
import java.awt.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class RunChess implements Runnable {

    /**
     * Initializes and runs the graphical user interface for the chess game.
     * This method sets up the main game window with its components including
     * the chess board, status panel, control panel, and evaluation bar.
     * It configures the primary interface elements and action listeners
     * for user interaction such as resetting the game, changing game modes,
     * resigning, and requesting a draw.
     *
     * The game starts in a predefined state, and the window is made visible
     * to start interaction with the players.
     */
    public void run() {
        // NOTE : recall that the 'final' keyword notes immutability even for
        // local variables.

        // Top-level frame in which game components live.
        // Be sure to change "TOP LEVEL FRAME" to the name of your game
        final JFrame frame = new JFrame("WELCOME TO JEFFREY CHESS");
        frame.setLocation(300, 20);
        frame.setResizable(false);

        // Status panel
        final JPanel status_panel = new JPanel();
        frame.add(status_panel, BorderLayout.SOUTH);
        final JLabel status = new JLabel("Game is fairly balanced!");
        status_panel.add(status);
        status_panel.add(Box.createRigidArea(new Dimension(ChessArena.BOARD_WIDTH / 16, 0)));
        final JLabel turn = new JLabel("White's turn!");
        status_panel.add(turn);
        status_panel.add(Box.createRigidArea(new Dimension(ChessArena.BOARD_WIDTH / 16, 0)));
        final JLabel error = new JLabel("No Error!");


        final JButton instructions = new JButton("Instructions");

        instructions.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                    frame,
                    "Instructions:\n\n" +
                            "0. This is a game of chess with the usual rules.\n" +
                            "0.1. Horse moves in an L. King moves one square all around. \n" +
                            "0.2. Bishop moves diagonally, Castle/Rook moves in straight lines. " +
                            "\n" +
                            "0.3. Queen moves like both bishop and rook.\n " +
                            "0.4. The game will assist in telling you what move is valid. \n" +
                            "1. White starts the game.\n" +
                            "2. Click on a piece to select it.\n" +
                            "3. Click on a valid square to move the piece.\n" +
                            "4. To resign, click the 'Resign' button.\n" +
                            "5. To request a draw, click the 'Draw' button.\n" +
                            "6. 'Change Mode' for AI mode not implemented yet.\n" +
                            "7. No drag functionality as of yet. Should be fine clicking squares." +
                            " \n" +
                            "8. Click 'Reset' to restart the game.\n\n" +
                            "Enjoy the game!",
                    "Game Instructions",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Evaluation bar panel
        EvalBarPanel evalBarPanel = new EvalBarPanel();
        frame.add(evalBarPanel, BorderLayout.EAST);

        // Main playing area
        final ChessArena chessArena = new ChessArena(status, turn, error, evalBarPanel);
        frame.add(chessArena, BorderLayout.CENTER);

        // Reset button
        final JPanel control_panel = new JPanel(new GridBagLayout());
        frame.add(control_panel, BorderLayout.NORTH);

        control_panel.add(instructions);
        control_panel.add(Box.createRigidArea(new Dimension(10, 0))); // Add spacing

        // Note here that when we add an action listener to the reset button, we
        // define it as an anonymous inner class that is an instance of
        // ActionListener with its actionPerformed() method overridden. When the
        // button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        final JButton changeMode = new JButton("Change Mode");
        final JLabel mode = new JLabel("PVP Mode...");
        final JButton resign = new JButton("Resign");
        final JButton draw = new JButton("Draw");

        reset.addActionListener(e -> chessArena.reset());
        changeMode.addActionListener(e -> {
            chessArena.changePlayerMode();
            if (mode.getText().equals("PVP Mode...")) {
                mode.setText("Vs Computer...");
            } else {
                mode.setText("PVP Mode...");
            }
        });
        resign.addActionListener(e -> {
            chessArena.resignGame();
        });
        draw.addActionListener(e -> {
            chessArena.drawGame();
        });

        control_panel.add(reset);
        control_panel.add(Box.createRigidArea(new Dimension(10, 0)));
        control_panel.add(changeMode);
        control_panel.add(Box.createRigidArea(new Dimension(10, 0)));
        control_panel.add(resign);
        control_panel.add(Box.createRigidArea(new Dimension(10, 0)));
        control_panel.add(draw);
        control_panel.add(Box.createRigidArea(new Dimension(10, 0)));
        status_panel.add(mode);
        status_panel.add(Box.createRigidArea(new Dimension(ChessArena.BOARD_WIDTH / 16, 0)));
        status_panel.add(error);

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game
        chessArena.reset();
    }
}
