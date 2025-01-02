package org.cis1200.chess;

import javax.swing.*;
import java.awt.*;

public class EvalBarPanel extends JPanel {
    private int whiteEval = 39;
    private int blackEval = 39;
    private final int maxEval = 39;
    private final int minEval = -39;

    public EvalBarPanel() {
        setPreferredSize(new Dimension(40, ChessArena.BOARD_HEIGHT));
    }

    public void updateEvalBar(int whiteEval, int blackEval) {
        this.whiteEval = whiteEval;
        this.blackEval = blackEval;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int evalDifference = blackEval - whiteEval;

        // Normalize the value between 0 and the height of the panel
        int normalizedEval = 3 * Math.max(minEval, Math.min(evalDifference, maxEval)); // play with
                                                                                       // the int *
        int evalHeight = (int) ((double) (normalizedEval - minEval) / (maxEval - minEval)
                * getHeight());

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), evalHeight);
    }
}
