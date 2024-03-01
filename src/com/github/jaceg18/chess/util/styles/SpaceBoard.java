package com.github.jaceg18.chess.util.styles;

import com.github.jaceg18.chess.ui.GamePanel;

import java.awt.*;
import java.awt.geom.Point2D;

import static com.github.jaceg18.chess.util.Drawer.BOARD_SIZE;
import static com.github.jaceg18.chess.util.Drawer.SQUARE_SIZE;

public class SpaceBoard implements BoardStyle {
    @Override
    public void drawBoard(Graphics g2d, GamePanel gamePanel) {
        Image boardImage = gamePanel.getDrawer().getBoardImage();
        if (boardImage == null) {
            boardImage = gamePanel.createImage(BOARD_SIZE, BOARD_SIZE);
            Graphics2D g2dBoard = (Graphics2D) boardImage.getGraphics();

            Color spaceColor1 = new Color(12, 20, 35);
            Color spaceColor2 = new Color(30, 40, 65);

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    int x = col * SQUARE_SIZE;
                    int y = row * SQUARE_SIZE;

                    Point2D start = new Point2D.Float(x, y);
                    Point2D end = new Point2D.Float(x + SQUARE_SIZE, y + SQUARE_SIZE);
                    GradientPaint gradient = new GradientPaint(start, spaceColor1, end, spaceColor2);

                    g2dBoard.setPaint(gradient);
                    g2dBoard.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
            g2dBoard.dispose();
        }
        g2d.drawImage(boardImage, 0, 0, null);
    }
}
