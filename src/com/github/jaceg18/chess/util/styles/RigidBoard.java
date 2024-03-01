package com.github.jaceg18.chess.util.styles;

import com.github.jaceg18.chess.ui.GamePanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static com.github.jaceg18.chess.util.Drawer.BOARD_SIZE;
import static com.github.jaceg18.chess.util.Drawer.SQUARE_SIZE;

public class RigidBoard implements BoardStyle {
    @Override
    public void drawBoard(Graphics g2d, GamePanel gamePanel) {
        Image boardImage = gamePanel.getDrawer().getBoardImage();
        if (boardImage == null) {
            boardImage = gamePanel.createImage(BOARD_SIZE, BOARD_SIZE);
            Graphics2D g2dBoard = (Graphics2D) boardImage.getGraphics();

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    int x = col * SQUARE_SIZE;
                    int y = row * SQUARE_SIZE;

                    Shape shape;
                    if ((row + col) % 2 == 0) {
                        shape = new Rectangle2D.Double(x, y, SQUARE_SIZE, SQUARE_SIZE);
                    } else {
                        shape = new Polygon(new int[]{x, x + SQUARE_SIZE, x + SQUARE_SIZE / 2}, new int[]{y, y, y + SQUARE_SIZE}, 3);
                    }
                    Color color = (row + col) % 2 == 0 ? Color.LIGHT_GRAY : Color.DARK_GRAY;
                    g2dBoard.setColor(color);
                    g2dBoard.fill(shape);
                }
            }
            g2dBoard.dispose();
        }
        g2d.drawImage(boardImage, 0, 0, null);
    }
}
