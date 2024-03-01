package com.github.jaceg18.chess.util.styles;

import com.github.jaceg18.chess.ui.GamePanel;

import java.awt.*;
import java.awt.geom.Point2D;

import static com.github.jaceg18.chess.util.Drawer.BOARD_SIZE;
import static com.github.jaceg18.chess.util.Drawer.SQUARE_SIZE;

public class RainbowBoard implements BoardStyle {
    @Override
    public void drawBoard(Graphics g2d, GamePanel gamePanel) {
        Image boardImage = gamePanel.getDrawer().getBoardImage();
        if (boardImage == null) {
            boardImage = gamePanel.createImage(BOARD_SIZE, BOARD_SIZE);
            Graphics2D g2dBoard = (Graphics2D) boardImage.getGraphics();

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    // Calculate the coordinates of the vertices of a polygon for each square
                    int[] xPoints = {col * SQUARE_SIZE, (col + 1) * SQUARE_SIZE, (col + 1) * SQUARE_SIZE, col * SQUARE_SIZE};
                    int[] yPoints = {row * SQUARE_SIZE, row * SQUARE_SIZE, (row + 1) * SQUARE_SIZE, (row + 1) * SQUARE_SIZE};

                    // Define gradient colors
                    Color startColor = Color.getHSBColor((float) col / 8, 0.9f, 0.9f);
                    Color endColor = Color.getHSBColor((float) (col + 1) / 8, 0.9f, 0.9f);

                    // Create and set gradient paint
                    Point2D start = new Point2D.Float(col * SQUARE_SIZE, row * SQUARE_SIZE);
                    Point2D end = new Point2D.Float((col + 1) * SQUARE_SIZE, (row + 1) * SQUARE_SIZE);
                    GradientPaint gradientPaint = new GradientPaint(start, startColor, end, endColor);
                    g2dBoard.setPaint(gradientPaint);

                    // Fill the polygon with the gradient paint
                    g2dBoard.fillPolygon(xPoints, yPoints, 4);
                }
            }
            g2dBoard.dispose();
        }
        g2d.drawImage(boardImage, 0, 0, null);
    }
}
