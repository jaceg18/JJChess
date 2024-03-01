package com.github.jaceg18.chess.util.styles;

import com.github.jaceg18.chess.ui.GamePanel;

import java.awt.*;

import static com.github.jaceg18.chess.util.Drawer.BOARD_SIZE;
import static com.github.jaceg18.chess.util.Drawer.SQUARE_SIZE;

public class GradientBoard implements BoardStyle {
    @Override
    public void drawBoard(Graphics g2d, GamePanel gamePanel) {
        Image boardImage = gamePanel.getDrawer().getBoardImage();
        if (boardImage == null) {
            boardImage = gamePanel.createImage(BOARD_SIZE, BOARD_SIZE);
            Graphics2D g2dBoard = (Graphics2D) boardImage.getGraphics();

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Color baseColor = ((row + col) % 2 == 0) ? new Color(80, 80, 80) : new Color(230, 230, 230); // Dark or light squares
                    GradientPaint gradient = new GradientPaint(
                            col * SQUARE_SIZE, row * SQUARE_SIZE, baseColor.darker(),
                            (col + 1) * SQUARE_SIZE, (row + 1) * SQUARE_SIZE, baseColor.brighter());
                    g2dBoard.setPaint(gradient);
                    g2dBoard.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
            g2dBoard.dispose();
        }
        g2d.drawImage(boardImage, 0, 0, null);
    }
}
