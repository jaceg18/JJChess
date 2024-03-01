package com.github.jaceg18.chess.util.styles;

import com.github.jaceg18.chess.ui.GamePanel;

import java.awt.*;
import java.awt.geom.Point2D;

import static com.github.jaceg18.chess.util.Drawer.BOARD_SIZE;
import static com.github.jaceg18.chess.util.Drawer.SQUARE_SIZE;

public class CopperIllusionBoard implements BoardStyle {
    @Override
    public void drawBoard(Graphics g2d, GamePanel gamePanel) {
        Image boardImage = gamePanel.getDrawer().getBoardImage();
        if (boardImage == null) {
            boardImage = gamePanel.createImage(BOARD_SIZE, BOARD_SIZE);
            Graphics2D g2dBoard = (Graphics2D) boardImage.getGraphics();
            g2dBoard.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2dBoard.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2dBoard.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            Color lightWood = new Color(205, 133, 63);
            Color darkWood = new Color(139, 69, 19);

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    boolean isLightSquare = (row + col) % 2 == 0;
                    Color baseColor = isLightSquare ? lightWood : darkWood;

                    Point2D start = new Point2D.Float(col * SQUARE_SIZE, row * SQUARE_SIZE);
                    Point2D end = new Point2D.Float((col + 1) * SQUARE_SIZE, (row + 1) * SQUARE_SIZE);
                    float[] dist = {0.0f, 0.5f, 1.0f};
                    Color[] colors = {baseColor.darker(), baseColor.brighter(), baseColor.darker()};
                    LinearGradientPaint paint = new LinearGradientPaint(start, end, dist, colors);

                    g2dBoard.setPaint(paint);
                    g2dBoard.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                }
            }
            g2dBoard.dispose();
        }
        g2d.drawImage(boardImage, 0, 0, null);
    }
}
